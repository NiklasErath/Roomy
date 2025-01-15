package com.example.roomy.ui

import android.widget.Toast
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.example.roomy.ui.Composables.ExpandingAddItemElement
import com.example.roomy.ui.Composables.UserProfileCircle
import com.example.roomy.ui.States.GroupMembersUiState
import com.example.roomy.ui.States.ItemsUiState
import com.example.roomy.ui.ViewModels.GroupViewModel
import com.example.roomy.ui.ViewModels.ItemViewModel


@Composable
fun Home(
    groupViewModel: GroupViewModel,
    itemViewModel: ItemViewModel,
    navController: NavController,
) {

    val context = LocalContext.current


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

    val currentGroupIdInt: Int = currentGroup.id?.let { it } ?: 0


    var isExpanded by remember { mutableStateOf(false) }

    val animatedHeight by animateDpAsState(
        targetValue = if (isExpanded) (0.5 * LocalConfiguration.current.screenHeightDp).dp else 56.dp
    )

    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    var isFocused by remember { mutableStateOf(false) }




    LaunchedEffect(Unit) {

        currentGroup.id?.let { itemViewModel.getAllItems(it) }
        currentGroup.id?.let { groupViewModel.getGroupMembers(it) }

    }

    LaunchedEffect(finishedFetching) {
        if (finishedFetching) {
            renderAfterFetching = true
            itemViewModel.resetFinishedFetching()

        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {


        Column(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
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

        Button(onClick = {groupViewModel.deleteGroup(currentGroupIdInt)}) {
            Text(text = "Delete Group")
        }
        Button(onClick = { navController.navigate(Screens.GroupMembers.route) }) {
            Text(text = "GroupMembers")
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
            Button(onClick = {
                groupViewModel.addMemberToGroup(
                    "0f35ef14-7c8c-4dd8-9a63-2f14190eec8d",
                    currentGroupIdInt
                )
            }) {
                Text(text = "Add Member")
            }

            Button(onClick = {
                groupViewModel.kickUser(
                    "0f35ef14-7c8c-4dd8-9a63-2f14190eec8d",
                    currentGroupIdInt
                )
            }) {
                Text(text = "Delete Member")
            }

            Button(onClick = { groupViewModel.deleteGroup(currentGroupIdInt) }) {
                Text(text = "Delete Group")
            }

            if (shoppingListItems.items.isEmpty() && renderAfterFetching) {
                Text(text = "Nothing here yet, add a new Item to your Shopping List")
            } else if (renderAfterFetching) {

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

            if (inventoryItems.items.isEmpty() && renderAfterFetching) {
                Text(text = "Nothing here yet, add a new Item to your Shopping List")
            } else if (renderAfterFetching) {

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


        // Overlay
        if (isExpanded) {
            Toast.makeText(context, "clickeeed", Toast.LENGTH_SHORT).show()
            Box(
                Modifier
                    .fillMaxSize()
                    .clickable {
                        Toast
                            .makeText(context, "clicked the box to close", Toast.LENGTH_SHORT)
                            .show()
                        isExpanded = false
                        focusManager.clearFocus()
                        keyboardController?.hide()

                    } // Dismiss on click outside
                    .background(Color.Black.copy(alpha = 0.3f))
                    .zIndex(1f) // Ensure it is layered on top
            )
        }

        ExpandingAddItemElement(
            animatedHeight,
            isExpanded,
            isFocused,
            focusRequester,
            itemName,
            updateExpandState = { isExpanded = it },
            updateFocusState = { isFocused = it },
            updtaeItemName = { itemName = it })


    }
}




//ExpandingItemELement no as composable but in Group/Home Composable - leave here for future testing
//
//Column(
//Modifier
//.fillMaxWidth()
//.height(animatedHeight)
//.align(Alignment.BottomCenter)
//.background(MaterialTheme.colorScheme.background)
//.border(1.dp, Color.White)
//.zIndex(3f)
//.clickable { }, // Ensure this column is rendered above other elements
//verticalArrangement = if (isExpanded || isFocused) Arrangement.SpaceBetween else Arrangement.Bottom
//) {
////            Spacer(Modifier.height(0.dp))
//    // Additional Content at the top
//    if (isExpanded) {
//        Text(
//            text = "Additional Content Shown in Overlay",
//            modifier = Modifier.padding(16.dp),
//            style = MaterialTheme.typography.bodyLarge
//        )
//    }
//
//    // TextField at the bottom
//    Box(
//        Modifier
//            .fillMaxWidth()
//            .zIndex(3f)
//            .clickable { }// Ensure the TextField appears above the overlay
//    ) {
//        OutlinedTextField(
//            modifier = Modifier
//                .fillMaxWidth()
//                .focusRequester(focusRequester)
//                .onFocusChanged { focusState ->
//                    isFocused = focusState.isFocused
//                    if (isFocused) isExpanded = true
//                },
//            value = itemName,
//            onValueChange = { newValue -> itemName = newValue },
//            placeholder = { Text(text = "We could use ...") },
//            trailingIcon = {
//                Icon(
//                    imageVector = Icons.Filled.AddCircle,
//                    contentDescription = "Add Item",
//                    Modifier.clickable {
//                        isExpanded = true
//                        // Logic to add items
//                    }
//                )
//            }
//        )
//    }
//}






