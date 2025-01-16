package com.example.roomy.ui.ViewModels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.roomy.db.ItemRepository
import com.example.roomy.db.data.Item
import com.example.roomy.ui.States.ItemsUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ItemViewModel(private val itemRepository: ItemRepository) : ViewModel() {

    // item error
    sealed class ItemErrorState {
        data class Error(val message: String)
    }

    private val _allGroupsItemsCount = MutableStateFlow<Map<Int, Int>>(emptyMap())
    val allGroupsItemsCount = _allGroupsItemsCount.asStateFlow()


    private val _finishedFetching = mutableStateOf<Boolean>(false)
    val finishedFetching: State<Boolean> = _finishedFetching
    fun resetFinishedFetching() {
        _finishedFetching.value = false
    }


    private val _allShoppingListItems = MutableStateFlow(ItemsUiState(emptyList()))
    private val _allInventoryItems = MutableStateFlow(ItemsUiState(emptyList()))

    // item error handling
    private val _itemError = MutableStateFlow(ItemErrorState.Error(""))
    val itemError = _itemError.asStateFlow()


    val allShoppingListItems = _allShoppingListItems.asStateFlow()
    val allInventoryItems = _allInventoryItems.asStateFlow()


    // move item to the inventroy
    fun moveToInventory(item: Item) {

        val newItem = Item(item.id, item.name, item.groupId, "inventory", item.quantity, item.icon)

        viewModelScope.launch {

            _allShoppingListItems.update { it ->
                it.copy(
                    items = it.items.filter { it.id != newItem.id }
                )
            }

            _allInventoryItems.update {
                it.copy(
                    items = it.items + newItem
                )
            }

            itemRepository.updateItem(newItem)

        }
    }

    // move Item to the shopping list
    fun moveToShoppingList(item: Item) {

        val newItem =
            Item(item.id, item.name, item.groupId, "shoppingList", item.quantity, item.icon)

        viewModelScope.launch {

            _allInventoryItems.update { it ->
                it.copy(
                    items = it.items.filter { it.id != newItem.id }
                )
            }

            _allShoppingListItems.update {
                it.copy(
                    items = it.items + newItem
                )
            }
            itemRepository.updateItem(newItem)

        }
    }

    // get All Items of a group
    suspend fun getAllItems(groupId: Int) {
        viewModelScope.launch {
            val result = itemRepository.getAllItems(groupId)

            if (result != null) {
                val (shoppingListItems, inventoryItems) = result

                _allShoppingListItems.update {
                    it.copy(
                        items = shoppingListItems
                    )
                }

                _allInventoryItems.update {
                    it.copy(
                        items = inventoryItems
                    )
                }
            } else {

                _itemError.update { oldState ->
                    oldState.copy("Could not get Items")
                }

                _allShoppingListItems.update {
                    it.copy(
                        items = emptyList()
                    )
                }

                _allInventoryItems.update {
                    it.copy(
                        items = emptyList()
                    )
                }
            }

            _finishedFetching.value = true
        }
    }


    // Function to get itemCounts for each group
    fun getAllItemCountsForGroups(groupIds: List<Int>) {
        viewModelScope.launch {
            val groupItemsCountMap = mutableMapOf<Int, Int>()

            // Iterate through each groupId and fetch the items for that group
            groupIds.forEach { groupId ->
                try {
                    // Fetch items for the group
                    val result = itemRepository.getAllItems(groupId)
                    if (result != null) {
                        val (shoppingListItems, inventoryItems) = result

                        //Only Shoppinglist items for now
                        val totalItemsForGroup = shoppingListItems.size
//                        + inventoryItems.size

                        groupItemsCountMap[groupId] = totalItemsForGroup
                    }
                } catch (e: Exception) {
                    groupItemsCountMap[groupId] = -1
                    _itemError.update { oldState ->
                        oldState.copy("Failed to get ItemCounts")
                    }
                }
            }

            // Update the state with the new item counts
            _allGroupsItemsCount.update { groupItemsCountMap }
        }
    }

    // add an Item to your List
    fun addItem(item: Item) {
        viewModelScope.launch {


            _allShoppingListItems.update {
                it.copy(
                    items = it.items + item
                )
            }

            val newItem = itemRepository.addItem(item)

            if (newItem == null) {
                _itemError.update { oldState ->
                    oldState.copy("Failed to add new Item")
                }
            } else {
                _allShoppingListItems.update {
                    it.copy(
                        items = it.items.map { existingItem ->
                            if (existingItem == item) newItem else existingItem
                        }
                    )
                }

            }
        }
    }

    // delete an item
    fun deleteItem(item: Item) {
        viewModelScope.launch {
            if (item.status == "shoppingList") {
                _allShoppingListItems.update {
                    it.copy(
                        items = it.items - item
                    )
                }
            } else if (item.status == "inventory") {
                _allInventoryItems.update {
                    it.copy(
                        items = it.items - item
                    )
                }
            }

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