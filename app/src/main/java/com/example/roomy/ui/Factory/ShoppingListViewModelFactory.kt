package com.example.roomy.ui.Factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.roomy.db.ShoppingListRepository
import com.example.roomy.ui.ViewModels.ShoppingListViewModel

class ShoppingListViewModelFactory(private val shoppingListRepository: ShoppingListRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ShoppingListViewModel::class.java)) {
            return ShoppingListViewModel(shoppingListRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

