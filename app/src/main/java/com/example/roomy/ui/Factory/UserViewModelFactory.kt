package com.example.roomy.ui.Factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.roomy.db.UserRepository
import com.example.roomy.ui.ViewModels.StateViewModel
import com.example.roomy.ui.ViewModels.UserViewModel

class UserViewModelFactory(private val userRepository: UserRepository, private val stateViewModel: StateViewModel) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {

            return UserViewModel(userRepository = userRepository, stateViewModel = stateViewModel) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}