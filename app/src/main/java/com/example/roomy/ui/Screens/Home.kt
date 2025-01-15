package com.example.roomy.ui

import androidx.annotation.DrawableRes
import androidx.compose.animation.Animatable
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.currentRecomposeScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.roomy.R
import com.example.roomy.ui.Composables.UserProfileCircle
import com.example.roomy.ui.States.GroupMembersUiState
import com.example.roomy.ui.States.GroupsUiState
import com.example.roomy.ui.States.ItemsUiState
import com.example.roomy.ui.ViewModels.GroupViewModel
import com.example.roomy.ui.ViewModels.ItemViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch





@Composable
fun Home(
    groupViewModel: GroupViewModel,
    itemViewModel: ItemViewModel,
    navController: NavController,
    currentGroupId: String?
) {

    val currentGroupIdInt = currentGroupId?.toIntOrNull() ?: 0


    var itemName by remember { mutableStateOf("") }


    val finishedFetching by itemViewModel.finishedFetching

    var renderAfterFetching by remember { mutableStateOf(false) }

    val currentGroup by groupViewModel.currentGroup.collectAsState()


    val inventoryItems by itemViewModel.allInventoryItems.collectAsState(
        initial = ItemsUiState(emptyList())
    )

    val shoppingListItems by itemViewModel.allShoppingListItems.collectAsState(
        initial = ItemsUiState(emptyList())
    )
    val groupMemberInformation by groupViewModel.groupMembers.collectAsState(
        initial = GroupMembersUiState(
            emptyList()
        )
    )



    LaunchedEffect(Unit) {

         itemViewModel.getAllItems(currentGroupIdInt)
         groupViewModel.getGroupMembers(currentGroupIdInt)

    }

    LaunchedEffect(finishedFetching){
        if(finishedFetching){
            renderAfterFetching = true
            itemViewModel.resetFinishedFetching()

        }
    }


    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(text = currentGroup.name)
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            groupMemberInformation.memberInformation.forEach { groupMembers ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    UserProfileCircle(groupMembers.username, 50.dp, Color.Blue)
                    Text(
                        text = groupMembers.username,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }

        Button(onClick = { navController.navigate(Screens.Groups.name) }) {
            Text(text = "Back to Groups")
        }
        Button(onClick = {groupViewModel.addMemberToGroup("0f35ef14-7c8c-4dd8-9a63-2f14190eec8d", currentGroupIdInt)}) {
            Text(text = "Add Member")
        }

        if(shoppingListItems.items.isEmpty() && renderAfterFetching){
            Text(text = "Nothing here yet, add a new Item to your Shopping List")
        }else if(renderAfterFetching) {

            shoppingListItems.items.chunked(3).forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    rowItems.forEach { item ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                        ) {
                            Item(
                                item,
                                updateItem = {
                                    itemViewModel.moveToInventory(item)
                                })
                        }
                    }

                    repeat(3 - rowItems.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
        }
        Spacer(Modifier.height(50.dp))
        Text(text = "Inventory")

        if(inventoryItems.items.isEmpty() && renderAfterFetching){
            Text(text = "Nothing here yet, add a new Item to your Shopping List")
        }else if(renderAfterFetching) {

            inventoryItems.items.chunked(3).forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    rowItems.forEach { item ->


                        Box(
                            modifier = Modifier
                                .weight(1f)
                        ) {
                            Item(
                                item,
                                updateItem = {
                                    itemViewModel.moveToShoppingList(item)
                                })

                        }



                    }

                    repeat(3 - rowItems.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
        }
    }

    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom
    ) {
        Row(Modifier.fillMaxWidth()) {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .border(width = 1.dp, color = Color.White)
                    .clip(
                        shape = RoundedCornerShape(
                            topStart = 10.dp,
                            topEnd = 10.dp
                        )
                    ),
                value = itemName,
                onValueChange = { newValue -> itemName = newValue },
                placeholder = { Text(text = "We could use ...") },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Filled.AddCircle,
                        contentDescription = "Add Item",
                        Modifier.clickable {

                            currentGroup.id?.let {
                                itemViewModel.addItem(
                                    itemName,
                                    it, "shoppingList", 1, "iconString"
                                )
                            }

                        })
                }
            )
        }

    }


}




