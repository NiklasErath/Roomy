package com.example.roomy.ui.ViewModels

import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.roomy.db.ItemRepository
import com.example.roomy.db.data.Item
import com.example.roomy.ui.States.ItemsUiState
import com.example.roomy.ui.States.newGroupState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ItemViewModel(
    private val itemRepository: ItemRepository, private val stateViewModel: StateViewModel
) : ViewModel() {


    // item error
    sealed class ItemErrorState {
        data class Error(val message: String)
    }


    private val _finishedFetching = mutableStateOf<Boolean>(false)
    val finishedFetching: State<Boolean> = _finishedFetching

    fun resetFinishedFetching() {
        _finishedFetching.value = false
    }

    // item error handling
    private val _itemError = MutableStateFlow(ItemErrorState.Error(""))
    val itemError = _itemError.asStateFlow()


    // move item to the inventory
    fun moveToInventory(item: Item, groupId: Int) {

        val newItem = Item(item.id, item.name, item.groupId, "inventory", item.quantity, item.icon)

        viewModelScope.launch {

            val success =itemRepository.updateItem(newItem)
            if (success) {
                stateViewModel.moveItemToInventory(groupId, newItem)
            } else {
                _itemError.update { oldState ->
                    oldState.copy("Oops, something went wrong")
                }
            }

        }
    }

    // move Item to the shopping list
    fun moveToShoppingList(item: Item, groupId: Int) {

        val newItem = Item(item.id, item.name, item.groupId, "shoppingList", item.quantity, item.icon)

        viewModelScope.launch {
            val success = itemRepository.updateItem(newItem)

            if (success) {
                stateViewModel.moveItemToShoppingList(groupId, newItem)
            } else {
                _itemError.update { oldState ->
                    oldState.copy("Oops, something went wrong")
                }
            }
        }
    }

    // get all the items for all the groups the user is a member of - map them
    suspend fun getAllItemsForGroups(groupIds: List<Int>): Map<Int, Pair<List<Item>, List<Item>>> {
        val groupItemsMap = mutableMapOf<Int, Pair<List<Item>, List<Item>>>()


        try {
            // get all the items for all the groups
            val allItems: Pair<List<Item>, List<Item>>? =
                itemRepository.getAllItemsForGroups(groupIds)

            if (allItems == null) {
                groupIds.forEach { groupId ->
                    groupItemsMap[groupId] = Pair(emptyList(), emptyList())
                }
                _itemError.update { oldState -> oldState.copy("Failed to get Items") }
            } else {

                val (shoppingListItems, inventoryItems) = allItems

                // filter the items by groupId
                groupIds.forEach { groupId ->

                    val itemsForGroup = shoppingListItems.filter { it.groupId == groupId }
                    val inventoryForGroup = inventoryItems.filter { it.groupId == groupId }

                    // pair the items and map them
                    groupItemsMap[groupId] = Pair(itemsForGroup, inventoryForGroup)
                }
            }
        } catch (e: Exception) {

            groupIds.forEach { groupId ->
                groupItemsMap[groupId] = Pair(emptyList(), emptyList())
            }
            _itemError.update { oldState -> oldState.copy("Failed to get Items") }
        }

        return groupItemsMap
    }


    // add an item
    fun addItem(item: Item, groupId: Int) {
        viewModelScope.launch {
            val newItem = itemRepository.addItem(item)

            if (newItem == null) {
                _itemError.update { oldState ->
                    oldState.copy("Failed to add new Item")
                }
            } else {
                stateViewModel.addItemToGroup(groupId, newItem)
            }
        }
    }

    // delete an item
    fun deleteItem(item: Item, groupId: Int) {

        viewModelScope.launch {
            val delete = itemRepository.deleteItem(item)

            if (!delete) {
                _itemError.update { oldState ->
                    oldState.copy("Failed to delete Item")
                }
            } else {
                stateViewModel.deleteItemFromGroup(groupId, item)
            }
        }
    }

    // clear the item error state
    fun clearItemError() {
        _itemError.update { oldState ->
            oldState.copy("")
        }
    }
}