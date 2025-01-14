package com.example.roomy.ui.Factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.roomy.db.ItemRepository
import com.example.roomy.ui.ViewModels.ItemViewModel

class ItemViewModelFactory(private val itemRepository: ItemRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(ItemViewModel::class.java)) {

            return ItemViewModel(itemRepository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }


}