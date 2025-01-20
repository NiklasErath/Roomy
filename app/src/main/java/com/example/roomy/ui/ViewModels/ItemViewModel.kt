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

        stateViewModel.moveItemToInventory(groupId, newItem)

        viewModelScope.launch {

            itemRepository.updateItem(newItem)

        }
    }

    // move Item to the shopping list
    fun moveToShoppingList(item: Item, groupId: Int) {

        val newItem =
            Item(item.id, item.name, item.groupId, "shoppingList", item.quantity, item.icon)

        stateViewModel.moveItemToShoppingList(groupId, newItem)

        viewModelScope.launch {
            itemRepository.updateItem(newItem)

        }
    }

    // Get all Items for all groups the user is in, return them in a Map of GroupId to Pair of(ShoppingListItems, InventoryItems)
    suspend fun getAllItemsForGroups(groupIds: List<Int>): Map<Int, Pair<List<Item>, List<Item>>> {
        val groupItemsMap = mutableMapOf<Int, Pair<List<Item>, List<Item>>>()


        try {
//            Fetch all items for the groups at once
            val allItems: Pair<List<Item>, List<Item>>? =
                itemRepository.getAllItemsForGroups(groupIds)

//            If the result is null, return empty lists for each group
            if (allItems == null) {
                groupIds.forEach { groupId ->
                    groupItemsMap[groupId] = Pair(emptyList(), emptyList())
                }
                _itemError.update { oldState -> oldState.copy("Failed to get Items") }
            } else {
                //Split shopping list items and inventory items for further processing
                val (shoppingListItems, inventoryItems) = allItems

                // Process shopping list and inventory items, and map them to each groupId
                groupIds.forEach { groupId ->
                    // Filter shopping list items for the current groupId
                    val itemsForGroup = shoppingListItems.filter { it.groupId == groupId }
                    // Filter inventory items for the current groupId
                    val inventoryForGroup = inventoryItems.filter { it.groupId == groupId }

                    // Add the filtered items as a pair (shopping list items, inventory items) to the map
                    groupItemsMap[groupId] = Pair(itemsForGroup, inventoryForGroup)
                }
            }
        } catch (e: Exception) {
            // Return empty lists for each group in casde of errors for now
            groupIds.forEach { groupId ->
                groupItemsMap[groupId] = Pair(emptyList(), emptyList())
            }
            _itemError.update { oldState -> oldState.copy("Failed to get Items") }
        }

        // Return the map containing the list of items for each group split into shopping and inventory
        return groupItemsMap
    }


    // add an Item to your List
//    Not this still requires a call to first set the item in supabase to work
//    Otherwise we dont have the itemid yet which is used to handle updates
//    Create dummy random id maybe later on
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

        stateViewModel.deleteItemFromGroup(groupId, item)

        viewModelScope.launch {
            val delete = itemRepository.deleteItem(item)
            if (!delete) {
                _itemError.update { oldState ->
                    oldState.copy("Failed to delete Item")
                }
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