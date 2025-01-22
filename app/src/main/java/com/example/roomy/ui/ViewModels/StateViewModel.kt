package com.example.roomy.ui.ViewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.roomy.db.data.Balance
import com.example.roomy.db.data.Item
import com.example.roomy.db.data.Payments
import com.example.roomy.db.data.UserInformation
import com.example.roomy.ui.States.GroupState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


//Holds global state of all groups the user is part of and updates it accordingly when things change
//This removes the need to always refetch data when something changes and lets us update the state directly
class StateViewModel(
) : ViewModel() {

    // contains all data from all groups the user is in and allows seamless UI Rendering without the need to fetch data again
    private val _allGroupsState = MutableStateFlow<List<GroupState>>(emptyList())
    val allGroupsState = _allGroupsState.asStateFlow()

    //    Initially set the new global group state frm the group viewmodel
    fun setAllGroupsState(GroupState: List<GroupState>) {
        _allGroupsState.update { GroupState }
    }


    // update the state when an item gets moved to the inventory
    fun moveItemToInventory(groupId: Int, item: Item) {
        _allGroupsState.update { groups ->
            groups.map { group ->
                if (group.groupId == groupId) {

                    // move the item from shopping list to inventory
                    val updatedShoppingList = group.shoppingListItems.filter { it.id != item.id }
                    val updatedInventoryList = group.inventoryItems + item

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

    // update the state when an item get moved to the shopping list
    fun moveItemToShoppingList(groupId: Int, item: Item) {
        _allGroupsState.update { groups ->
            groups.map { group ->
                if (group.groupId == groupId) {
                    // move the item from shopping list to inventory
                    val updatedInventoryList = group.inventoryItems.filter { it.id != item.id }
                    val updatedShoppingList = group.shoppingListItems + item

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

    // update the state when deleting an item from a group
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

    // update the state when adding a new item to a group
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

    // delete a grpup from the state
    fun deleteGroup(groupId: Int) {
        _allGroupsState.update { groups ->
            groups.filterNot { it.groupId == groupId }
        }
    }

    // update state when kicking a user out of the group or a user leaves
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


    // update the state when adding a new User to a group
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

    // update the state when changing the username
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

    // update the State when adding a new Payment
    fun addNewPayment(addedPayment: Payments, groupId: Int) {
        _allGroupsState.update { groups ->
            groups.map { group ->
                if (group.groupId == groupId) {
                    group.copy(payments = group.payments + addedPayment)
                } else {
                    group
                }
            }
        }
    }

    // clear the Group state if necessary - e.g. when logout
    fun clearGroupsState() {

        _allGroupsState.value = emptyList()

    }

}



