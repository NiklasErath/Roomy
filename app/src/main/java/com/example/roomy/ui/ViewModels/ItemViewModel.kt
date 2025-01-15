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


    private val _finishedFetching = mutableStateOf<Boolean>(false)
    val finishedFetching: State<Boolean> = _finishedFetching
    fun resetFinishedFetching(){
        _finishedFetching.value = false
    }


    private val _allShoppingListItems = MutableStateFlow(ItemsUiState(emptyList()))
    private val _allInventoryItems = MutableStateFlow(ItemsUiState(emptyList()))



    val allShoppingListItems = _allShoppingListItems.asStateFlow()
    val allInventoryItems = _allInventoryItems.asStateFlow()

    fun moveToInventory(item:Item){

        val newItem = Item(item.id, item.name, item.groupId, "inventory", item.quantity, item.icon)

        viewModelScope.launch {

            _allShoppingListItems.update { it ->
                it.copy(
                    items = it.items.filter { it.id != newItem.id}
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


    fun moveToShoppingList(item:Item){

        val newItem = Item(item.id, item.name, item.groupId, "shoppingList", item.quantity, item.icon)

        viewModelScope.launch {

            _allInventoryItems.update { it ->
                it.copy(
                    items = it.items.filter { it.id != newItem.id}
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


    suspend fun getAllItems(groupId: Int) {
        viewModelScope.launch {
            val (shoppingListItems:List<Item>, inventoryItems: List<Item>) = itemRepository.getAllItems(groupId)

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

            _finishedFetching.value = true
        }
    }

    fun addItem(item: Item) {
        viewModelScope.launch {


            _allShoppingListItems.update {
                it.copy(
                    items = it.items + item
                )
            }

            val newItem = itemRepository.addItem(item)

            _allShoppingListItems.update {
                it.copy(
                    items = it.items.map { existingItem ->
                        if (existingItem == item) newItem else existingItem
                    }
                )
            }


        }
    }

    fun deleteItem(item: Item) {
        viewModelScope.launch {

            if(item.status == "shoppingList"){
                _allShoppingListItems.update {
                    it.copy(
                        items = it.items - item
                    )
                }
            }else if(item.status == "inventory"){
                _allInventoryItems.update {
                    it.copy(
                        items = it.items - item
                    )
                }
            }

            itemRepository.deleteItem(item)




        }
    }
}