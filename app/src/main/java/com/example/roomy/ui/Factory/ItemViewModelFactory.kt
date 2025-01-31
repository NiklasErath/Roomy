package com.example.roomy.ui.Factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.roomy.db.ItemRepository
import com.example.roomy.ui.ViewModels.GroupViewModel
import com.example.roomy.ui.ViewModels.ItemViewModel
import com.example.roomy.ui.ViewModels.StateViewModel

class ItemViewModelFactory(
    private val itemRepository: ItemRepository,
    private val stateViewModel: StateViewModel
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(ItemViewModel::class.java)) {

            return ItemViewModel(itemRepository = itemRepository, stateViewModel = stateViewModel) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }


}