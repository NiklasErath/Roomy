package com.example.roomy.ui.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.roomy.db.ItemRepository
import com.example.roomy.ui.States.ItemsUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ItemViewModel(private val itemRepository: ItemRepository) : ViewModel() {

    private val _allItems = MutableStateFlow(ItemsUiState(emptyList()))

    val allItems = _allItems.asStateFlow()

    suspend fun getItemsByGroupId(groupId:Int) {
        viewModelScope.launch {
            val fetchedItems = itemRepository.getItemsByGroupId(groupId)

            _allItems.update {
                it.copy(
                    items = fetchedItems
                )
            }
        }
    }
}