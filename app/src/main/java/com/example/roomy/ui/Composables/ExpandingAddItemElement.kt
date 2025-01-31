package com.example.roomy.ui.Composables

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.roomy.R
import com.example.roomy.ui.CustomOutlinedTextField
import com.example.roomy.ui.Item
import com.example.roomy.ui.States.GroupState
import com.example.roomy.ui.ViewModels.ItemViewModel

@Composable
fun ExpandingAddItemElement(
    context: Context,
    animatedHeight: Dp,
    isExpanded: Boolean,
    isFocused: Boolean,
    focusRequester: FocusRequester,
    itemName: String,
    updateFocusState: (Boolean) -> Unit,
    updateExpandState: (Boolean) -> Unit,
    updateItemName: (String) -> Unit,
    currentGroup: GroupState,
    itemViewModel: ItemViewModel,
    resetAfterItemAdded: () -> Unit
) {


    val filteredItems by remember(itemName) {
        derivedStateOf {
            if (itemName.isBlank()) emptyList() else {
                suggestedItems.filter { it.contains(itemName, ignoreCase = true) }
            }
        }
    }
    val placeHolderItem = com.example.roomy.db.data.Item(
        name = itemName,
        groupId = currentGroup.groupId,
        status = "shoppingList",
        icon = null
    )
    Box(
        Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))

            .height(animatedHeight)
            .zIndex(3f)

    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                .height(animatedHeight)
                .align(Alignment.BottomCenter)
                .background(MaterialTheme.colorScheme.background)
                .zIndex(3f)
                .clickable { }, // Ensure this column is rendered above other elements
            verticalArrangement = if (isExpanded || isFocused) Arrangement.SpaceBetween else Arrangement.Bottom
        ) {

            if (isExpanded) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))

                        .weight(1f, false)
                        .padding(horizontal = 20.dp)
                        .padding(top = 12.dp)


                ) {

                    if (itemName == "") {
                        Text(
                            text = "Start typing to get some suggestions",
                            modifier = Modifier.padding(top=12.dp).align(Alignment.Center),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                    } else {

                        LazyVerticalGrid(
                            columns = GridCells.Fixed(3), // Three columns
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(bottom = 4.dp, top = 8.dp)
                        ) {
                            items(filteredItems.size) { index ->
                                val suggestedItemName = filteredItems[index]
                                val iconResId = ItemSuggestionMap.nameToIconId[suggestedItemName]
                                    ?: R.drawable.placeholder
                                val suggestedItem = com.example.roomy.db.data.Item(
                                    name = suggestedItemName,
                                    groupId = currentGroup.groupId,
                                    status = "shoppingList",
                                    icon = iconResId
                                )
                                Item(item = suggestedItem,
                                    onClick = {
                                        itemViewModel.addItem(
                                            suggestedItem, currentGroup.groupId
                                        )
                                        resetAfterItemAdded()

                                    },
                                    onLongClick = {},
                                    context

                                )
                            }
                            item {
                                Item(item = placeHolderItem,
                                    onClick = {
                                        itemViewModel.addItem(
                                            placeHolderItem, currentGroup.groupId
                                        )
                                        resetAfterItemAdded()

                                    },
                                    onLongClick = {},
                                    context


                                )

                            }
                        }
                    }
                }

            }

            // TextField at the bottom
            Box(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                    .height(64.dp)
                    .zIndex(4f)
                    .clickable { }
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.Center

// Ensure the TextField appears above the overlay
            ) {
                CustomOutlinedTextField(
                    modifier = Modifier

                        .fillMaxWidth()
                        .border(
                            width = 0.dp, // Thicker border
                            color = Color.Transparent,
                        )
                        .background(MaterialTheme.colorScheme.background)
                        .focusRequester(focusRequester)
                        .onFocusChanged { focusState ->
                            updateFocusState(focusState.isFocused)
                            if (focusState.isFocused) updateExpandState(true)
                        },

                    value = itemName,
                    onValueChange = { newValue -> if (newValue.length < 50) updateItemName(newValue) },
                    placeholder = { Text(text = "We could use ...") },
                )
            }
        }
    }
}

