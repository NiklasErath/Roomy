package com.example.roomy.ui

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.example.roomy.R
import com.example.roomy.db.api.getSuggestedRecipe
import com.example.roomy.ui.Composables.ExpandingAddItemElement
import com.example.roomy.ui.Composables.UserProfileCircle
import com.example.roomy.ui.States.GroupMembersUiState
import com.example.roomy.ui.States.ItemsUiState
import com.example.roomy.ui.States.newGroupState
import com.example.roomy.ui.ViewModels.GroupViewModel
import com.example.roomy.ui.ViewModels.ItemViewModel


@Composable
fun Group(
    groupViewModel: GroupViewModel,
    itemViewModel: ItemViewModel,
    navController: NavController,
    previousScreen: String
) {



    val context = LocalContext.current

    var itemName by remember { mutableStateOf("") }

    val finishedFetching by itemViewModel.finishedFetching

    var renderAfterFetching by remember { mutableStateOf(true) }


    val currentGroup by groupViewModel.currentGroup.collectAsState()

    // Step 2: Collect all groups state
    val allGroupsState by groupViewModel.allGroupsState.collectAsState(
        initial = emptyList()
    )

    // Step 3: Find the corresponding GroupState based on the currentGroup.id
    val group by remember(currentGroup.id, allGroupsState) {
        derivedStateOf {
            // Find the corresponding GroupState based on the currentGroup.id
            allGroupsState.find { it.groupId == currentGroup.id }
                ?: newGroupState(
                    groupId = -1,
                    groupName = "Unknown",
                    creatorId = "Unknown",
                    groupMembers = emptyList(),
                    shoppingListItems = emptyList(),
                    inventoryItems = emptyList(),
                    itemCount = 0
                )  // Fallback default group state
        }
    }

    Log.d("llllllllllllllllllllllllllllllllllllllllll", "$group")


    val inventoryItems by itemViewModel.allInventoryItems.collectAsState(
        initial = ItemsUiState(emptyList())
    )

    val shoppingListItems by itemViewModel.allShoppingListItems.collectAsState(
        initial = ItemsUiState(emptyList())
    )
//    val groupMemberInformation by groupViewModel.groupMembers.collectAsState(
//        initial = GroupMembersUiState(
//            emptyList()
//        )
//    )

//    val currentGroupIdInt: Int = currentGroup.id?.let { it } ?: 0


    var isExpanded by remember { mutableStateOf(false) }

    val animatedHeight by animateDpAsState(
        targetValue = if (isExpanded) (0.6 * LocalConfiguration.current.screenHeightDp).dp else 56.dp
    )

    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    var isFocused by remember { mutableStateOf(false) }


    var triggerNavCheck by remember { mutableStateOf(true) }


//    LaunchedEffect(previousScreen) {
//        if (triggerNavCheck) {
//            when (previousScreen) {
//                Screens.Groups.name, Screens.Balance.name, Screens.Profile.name -> renderAfterFetching =
//                    true
//
//                else -> renderAfterFetching = false
//            }
//            triggerNavCheck = false
//        }
//    }


    LaunchedEffect(Unit) {

//        Fix later - this doesnt have to be done on launch, just acces the newGroupsState
        itemViewModel.setAllItems(group)
//        groupViewModel.getGroupMembers(currentGroupIdInt)

    }

//    Still needed? Im not sure
    LaunchedEffect(finishedFetching) {
        if (finishedFetching) {
            renderAfterFetching = true
            itemViewModel.resetFinishedFetching()

        }
    }
    Log.d("ShoppingListssssssssssssssssssss", "Shopping List Items: ${group.shoppingListItems}")



    //            Note change this to our errorhandling process later on
    Box(
        Modifier
            .fillMaxSize()
            .zIndex(20F)
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(vertical = 80.dp, horizontal = 20.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.End
        ) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .size(80.dp) // Adjusted size for better padding visibility
                    .border(
                        border = BorderStroke(
                            3.dp, Brush.linearGradient(
                                colors = listOf(Color.Cyan, Color.Magenta, Color.Yellow),
                                start = Offset(0f, 0f),
                                end = Offset(100f, 100f)
                            )
                        ),
                        shape = CircleShape
                    )
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable {
                        if (group.inventoryItems.isEmpty()) {
                            Toast
                                .makeText(
                                    context,
                                    "Add some Items to your Inventory first",
                                    Toast.LENGTH_SHORT
                                )
                                .show()

                        } else {
                            navController.navigate(Screens.RecipeSuggestion.name)
                        }
                    }
                    .padding(10.dp)
            ) {
                Icon(
                    painterResource(R.drawable.recipe),
                    contentDescription = "Group",

                    )
                Text(text = "Recipe", fontSize = 12.sp)

            }

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
                .padding(bottom = 50.dp)
                .verticalScroll(rememberScrollState())
        ) {

            Text(text = "ShoppingList")

            if (shoppingListItems.items.isEmpty() && renderAfterFetching) {
                Text(text = "Nothing here yet, add a new Item to your Shopping List")
            } else {



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
                                    onClick = {
                                        itemViewModel.moveToInventory(item)
                                    },
                                    onLongClick = {
                                        itemViewModel.deleteItem(item)
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
            } else {

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
                                    onClick = {
                                        itemViewModel.moveToShoppingList(item)
                                    },
                                    onLongClick = {
                                        itemViewModel.deleteItem(item)

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
            Box(
                Modifier
                    .fillMaxSize()
                    .clickable {
                        isExpanded = false
                        focusManager.clearFocus()
                        keyboardController?.hide()

                    } // Dismiss on click outside
                    .background(Color.Black.copy(alpha = 0.3f))
                    .zIndex(1f) // Ensure it is layered on top
            )
        }

        fun resetAfterItemAdded() {
            isExpanded = false
            isFocused = false
            itemName = ""
            focusManager.clearFocus()
            keyboardController?.hide()

        }

        ExpandingAddItemElement(
            animatedHeight,
            isExpanded,
            isFocused,
            focusRequester,
            itemName,
            updateExpandState = { isExpanded = it },
            updateFocusState = { isFocused = it },
            updateItemName = { itemName = it },
            currentGroupId = group.groupId,
            itemViewModel = itemViewModel,
            resetAfterItemAdded = { resetAfterItemAdded() }
        )


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






