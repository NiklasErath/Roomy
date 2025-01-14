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

    fun addItem(itemName:String, groupId: Int, status:String, quantity:Int, icon:String) {
        val item = Item(
            groupId = groupId,
            name = itemName,
            status = status,
//                quantity = quantity,
//                icon = icon
        )
        viewModelScope.launch {

            _allShoppingListItems.update {
                it.copy(
                    items = it.items + item
                )
            }
            itemRepository.addItem(item)

        }
    }
}