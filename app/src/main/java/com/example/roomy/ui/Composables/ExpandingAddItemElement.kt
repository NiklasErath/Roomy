package com.example.roomy.ui.Composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.roomy.R
import com.example.roomy.ui.Item
import com.example.roomy.ui.ViewModels.ItemViewModel

@Composable
fun ExpandingAddItemElement(
    animatedHeight: Dp,
    isExpanded: Boolean,
    isFocused: Boolean,
    focusRequester: FocusRequester,
    itemName: String,
    updateFocusState: (Boolean) -> Unit,
    updateExpandState: (Boolean) -> Unit,
    updtaeItemName: (String) -> Unit,
    currentGroupId: Int,
    itemViewModel: ItemViewModel
) {


    val filteredItems by remember(itemName) {
        derivedStateOf {
            if (itemName.isBlank()) emptyList() else {
                suggestedItems.filter { it.contains(itemName, ignoreCase = true) }
            }
        }
    }

    Box(
        Modifier
            .fillMaxSize()
            .height(animatedHeight)
            .zIndex(3f)
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .height(animatedHeight)
                .align(Alignment.BottomCenter)
                .background(MaterialTheme.colorScheme.background)
                .border(1.dp, Color.White)
                .zIndex(3f)
                .clickable { }, // Ensure this column is rendered above other elements
            verticalArrangement = if (isExpanded || isFocused) Arrangement.SpaceBetween else Arrangement.Bottom
        ) {

//            Spacer(Modifier.height(0.dp))
            // Additional Content at the top
            if (isExpanded) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, false) // Let the content grow but limit to max available space
//                        .padding(horizontal = 16.dp)
                ) {

                    if(filteredItems.isEmpty()){
                        Text(text = "Type something to get some suggestions",
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                    }
                    else {

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
                                    groupId = currentGroupId,
                                    status = "shoppingList",
                                    icon = iconResId
                                )
                                Item(item = suggestedItem,
                                    updateItem = {
                                        itemViewModel.addItem(
                                            suggestedItem
                                        )
                                    }

                                )
                            }
                        }
                    }
//                    Text(
//                        text = "Additional Content Shown in Overlay",
//                        modifier = Modifier.padding(16.dp),
//                        style = MaterialTheme.typography.bodyLarge
//                    )
                }

            }

            // TextField at the bottom
            Box(
                Modifier
                    .fillMaxWidth()
                    .zIndex(4f)
                    .clickable { }// Ensure the TextField appears above the overlay
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                        .onFocusChanged { focusState ->
                            updateFocusState(focusState.isFocused)
                            if (focusState.isFocused) updateExpandState(true)
                        },
                    value = itemName,
                    onValueChange = { newValue -> updtaeItemName(newValue) },
                    placeholder = { Text(text = "We could use ...") },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Filled.AddCircle,
                            contentDescription = "Add Item",
                            Modifier.clickable {
                                updateExpandState(true)                            // Logic to add items
                            }
                        )
                    }
                )
            }
        }
    }
}


//currentGroup.id?.let {
//                                itemViewModel.addItem(
//                                    itemName,
//                                    it, "shoppingList", 1, "iconString"
//                                )
//                            }
//
//                        })