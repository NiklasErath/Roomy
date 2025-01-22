package com.example.roomy.ui

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.example.roomy.R
import com.example.roomy.ui.Composables.ExpandingAddItemElement
import com.example.roomy.ui.Composables.RecipeButton
import com.example.roomy.ui.States.GroupState
import com.example.roomy.ui.ViewModels.GroupViewModel
import com.example.roomy.ui.ViewModels.ItemViewModel


@Composable
fun Group(
    groupViewModel: GroupViewModel,
    itemViewModel: ItemViewModel,
    navController: NavController,
    previousScreen: String,
    currentGroup: GroupState
) {


    val context = LocalContext.current

    var itemName by remember { mutableStateOf("") }

    val finishedFetching by itemViewModel.finishedFetching

//    var renderAfterFetching by remember { mutableStateOf(true) }

//    val inventoryItems by itemViewModel.allInventoryItems.collectAsState(
//        initial = ItemsUiState(emptyList())
//    )
//
//    val shoppingListItems by itemViewModel.allShoppingListItems.collectAsState(
//        initial = ItemsUiState(emptyList())
//    )
//    val groupMemberInformation by groupViewModel.groupMembers.collectAsState(
//        initial = GroupMembersUiState(
//            emptyList()
//        )
//    )

//    val currentGroupIdInt: Int = currentGroup.id?.let { it } ?: 0


    var isExpanded by remember { mutableStateOf(false) }

    val animatedHeight by animateDpAsState(
        targetValue = if (isExpanded) (0.6 * LocalConfiguration.current.screenHeightDp).dp else 64.dp
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


//    LaunchedEffect(Unit) {
//
////        Fix later - this doesnt have to be done on launch, just acces the newGroupsState
////        itemViewModel.setAllItems(currentGroup)
////        groupViewModel.getGroupMembers(currentGroupIdInt)
//
//    }

//    Still needed? Im not sure
//    LaunchedEffect(finishedFetching) {
//        if (finishedFetching) {
//            renderAfterFetching = true
//            itemViewModel.resetFinishedFetching()
//
//        }
//    }


    //            Note change this to our errorhandling process later on


    RecipeButton(currentGroup, context, navController, groupViewModel)




    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primaryContainer)
    ) {

        if (currentGroup.shoppingListItems.isEmpty() && currentGroup.inventoryItems.isEmpty()) {

            Column(
                Modifier
                    .padding(bottom = 200.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Image(
                    modifier = Modifier.padding(horizontal = 30.dp),
                    painter = painterResource(id = R.drawable.grocery),
                    contentDescription = "Groceries",
                    contentScale = ContentScale.FillWidth,
                )
                Spacer(Modifier.height(20.dp))

                Text(
                    text = "Add your first Articles",
                    textAlign = TextAlign.Center,
                    fontSize = integerResource(id = R.integer.heading3).sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Type them freely into the search bar below and select what you need!",
                    textAlign = TextAlign.Center,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer

                )

            }

        } else {


            Column(
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 50.dp)
                    .verticalScroll(rememberScrollState())
            ) {

                Text(
                    text = "ShoppingList",
                    fontSize = integerResource(id = R.integer.heading3).sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(Modifier.height(8.dp))


                if (currentGroup.shoppingListItems.isEmpty()) {
                    Column(
                        Modifier
                            .padding(horizontal = 20.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {


                        Image(
                            modifier = Modifier.size(60.dp),
                            painter = painterResource(id = R.drawable.emptycart),
                            contentDescription = "Groceries",
                            contentScale = ContentScale.FillWidth,
                        )
                        Spacer(Modifier.height(10.dp))
                        Text(
                            text = "Nothing here yet, add a new Item to your Shopping List",
                            textAlign = TextAlign.Center,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer

                        )
                    }


                } else {


                    currentGroup.shoppingListItems.chunked(3).forEach { rowItems ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(3.dp),
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
                                            itemViewModel.moveToInventory(
                                                item,
                                                currentGroup.groupId
                                            )
                                        },
                                        onLongClick = {
                                            itemViewModel.deleteItem(item, currentGroup.groupId)
                                        },
                                        context
                                    )

                                }
                            }

                            repeat(3 - rowItems.size) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                        Spacer(Modifier.height(3.dp))
                    }
                }
                Spacer(Modifier.height(30.dp))
                Text(
                    text = "Inventory",
                    fontSize = integerResource(id = R.integer.heading3).sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(Modifier.height(8.dp))

                if (currentGroup.inventoryItems.isEmpty()) {
                    Column(
                        Modifier
                            .padding(horizontal = 20.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {


                        Image(
                            modifier = Modifier.size(60.dp),
                            painter = painterResource(id = R.drawable.emptycart),
                            contentDescription = "Groceries",
                            contentScale = ContentScale.FillWidth,
                        )
                        Spacer(Modifier.height(10.dp))
                        Text(
                            text = "Nothing here yet, add a new Item to your Inventory",
                            textAlign = TextAlign.Center,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer

                        )
                    }
                } else {

                    currentGroup.inventoryItems.chunked(3).forEach { rowItems ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(3.dp),
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
                                            itemViewModel.moveToShoppingList(
                                                item,
                                                currentGroup.groupId
                                            )
                                        },
                                        onLongClick = {
                                            itemViewModel.deleteItem(item, currentGroup.groupId)

                                        },
                                        context
                                    )

                                }


                            }

                            repeat(3 - rowItems.size) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                        Spacer(Modifier.height(3.dp))
                    }
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
            context,
            animatedHeight,
            isExpanded,
            isFocused,
            focusRequester,
            itemName,
            updateExpandState = { isExpanded = it },
            updateFocusState = { isFocused = it },
            updateItemName = { itemName = it },
            currentGroup = currentGroup,
            itemViewModel = itemViewModel,
            resetAfterItemAdded = { resetAfterItemAdded() }
        )


    }
}






