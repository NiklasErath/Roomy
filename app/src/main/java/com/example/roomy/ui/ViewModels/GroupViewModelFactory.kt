package com.example.roomy.ui.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.roomy.db.GroupRepository
import com.example.roomy.db.UserRepository


class GroupViewModelFactory(
    private val groupRepository: GroupRepository,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GroupViewModel::class.java)) {
            return GroupViewModel(
                groupRepository = groupRepository,
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}