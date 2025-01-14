package com.example.roomy.ui.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.roomy.db.ShoppingListRepository
import com.example.roomy.ui.States.ShoppingListState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ShoppingListViewModel(private val shoppingListRepository: ShoppingListRepository) : ViewModel() {

    private val _shoppingList = MutableStateFlow(ShoppingListState(0, "", emptyList(), 0, 0))

    val shoppingList = _shoppingList.asStateFlow()

    fun getShoppingListForGroup(groupId: Int) {
        viewModelScope.launch {
            val list = shoppingListRepository.getShoppingListByGroupId(groupId)
            _shoppingList.update { oldState ->
                oldState.copy(
                    id = list.id,
                    created = list.created,
                    itemIds = list.itemId,
                    groupId = list.groupId,
                    quantity = list.quantity
                )
            }
        }
    }
}