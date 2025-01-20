package com.example.roomy.ui.ViewModels

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.roomy.db.ItemRepository
import com.example.roomy.db.data.Item
import com.example.roomy.db.data.UserInformation
import com.example.roomy.ui.States.ItemsUiState
import com.example.roomy.ui.States.newGroupState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


//Holds globalstate of all groups the user is part of and updates it accordingly when things change
//This removes the need to always refetch data when something changes and lets us update the state directly
class StateViewModel(
) : ViewModel() {

    //Contains all data from all groups the user is in and allows seamless UI Rendering without the need to fetch data again
    private val _allGroupsState = MutableStateFlow<List<newGroupState>>(emptyList())
    val allGroupsState = _allGroupsState.asStateFlow()

    //    Initially set the new global Groupstate frm the groupviewmodel
    fun setAllGroupsState(newGroupState: List<newGroupState>) {

        _allGroupsState.update { newGroupState }


    }


    // Function to update the shopping list and inventory for the current group
    fun moveItemToInventory(groupId: Int, item: Item) {
        _allGroupsState.update { groups ->
            groups.map { group ->
                if (group.groupId == groupId) {
                    Log.d(
                        "ShoppingListssssssssssssssssssss",
                        "Shopping List Items: ${group.shoppingListItems}"
                    )

                    // Move the item from shopping list to inventory
                    val updatedShoppingList = group.shoppingListItems.filter { it.id != item.id }
                    val updatedInventoryList = group.inventoryItems + item

                    // Return the updated group with the new item lists
                    group.copy(
                        shoppingListItems = updatedShoppingList,
                        inventoryItems = updatedInventoryList
                    )
                } else {
                    group
                }
            }
        }
    }

    // Function to update the shopping list and inventory for the current group
    fun moveItemToShoppingList(groupId: Int, item: Item) {
        _allGroupsState.update { groups ->
            groups.map { group ->
                if (group.groupId == groupId) {
                    // Move the item from shopping list to inventory
                    val updatedInventoryList = group.inventoryItems.filter { it.id != item.id }
                    val updatedShoppingList = group.shoppingListItems + item

                    // Return the updated group with the new item lists
                    group.copy(
                        shoppingListItems = updatedShoppingList,
                        inventoryItems = updatedInventoryList
                    )
                } else {
                    group
                }
            }
        }
    }

//    Delete an Item from the current Group
    fun deleteItemFromGroup(groupId: Int, item: Item) {
        _allGroupsState.update { groups ->
            groups.map { group ->
                if (group.groupId == groupId) {
                    // Delete the item from the correct list
                    val updatedShoppingList = if (item.status == "shoppingList") {
                        group.shoppingListItems.filter { it.id != item.id }
                    } else {
                        group.shoppingListItems
                    }

                    val updatedInventoryList = if (item.status == "inventory") {
                        group.inventoryItems.filter { it.id != item.id }
                    } else {
                        group.inventoryItems
                    }

                    // Return the updated group with the modified lists
                    group.copy(
                        shoppingListItems = updatedShoppingList,
                        inventoryItems = updatedInventoryList
                    )
                } else {
                    group
                }
            }
        }
    }

//    Add a new item to the group
    fun addItemToGroup(groupId: Int, item: Item) {
        _allGroupsState.update { groups ->
            groups.map { group ->

                if (group.groupId == groupId) {
                    val updatedShoppingList = group.shoppingListItems + item


                    group.copy(
                        shoppingListItems = updatedShoppingList
                    )
                } else {
                    group
                }
            }
        }
    }

//Delete a group from the global state
    fun deleteGroup(groupId: Int) {
        _allGroupsState.update { groups ->
            groups.filterNot { it.groupId == groupId }  // Exclude the group with matching groupId
        }
    }

//    Kick out a user
    fun kickUser(groupId: Int, userId: String) {

        _allGroupsState.update { groups ->
            groups.map { group ->

                if (group.groupId == groupId) {
                    val updatedGroupMembers = group.groupMembers.filter { it.id != userId }


                    group.copy(
                        groupMembers = updatedGroupMembers
                    )
                } else {
                    group
                }
            }
        }

    }


//    Add a new user to a group
    fun addUser(groupId: Int, user: UserInformation) {

        _allGroupsState.update { groups ->
            groups.map { group ->

                if (group.groupId == groupId) {
                    val updatedGroupMembers = group.groupMembers + user


                    group.copy(
                        groupMembers = updatedGroupMembers
                    )
                } else {
                    group
                }
            }
        }


    }

//    Update the username
    fun updateUserName(userName: String, userId: String) {

        _allGroupsState.update { groups ->
            groups.map { group ->


                val updatedGroupMembers = group.groupMembers.map { groupMember ->
                    if (groupMember.id == userId) {
                        groupMember.copy(username = userName)
                    } else {
                        groupMember
                    }
                }
                group.copy(
                    groupMembers = updatedGroupMembers
                )
            }
        }
    }

    fun clearGroupsState(){

        _allGroupsState.value = emptyList()

    }

}



