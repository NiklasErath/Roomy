package com.example.roomy.ui.Factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.roomy.db.GroupRepository
import com.example.roomy.db.UserRepository
import com.example.roomy.ui.ViewModels.GroupViewModel
import com.example.roomy.ui.ViewModels.ItemViewModel


class GroupViewModelFactory(
    private val groupRepository: GroupRepository,
    private  val userRepository: UserRepository,
    private  val itemViewModel: ItemViewModel

) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GroupViewModel::class.java)) {
            return GroupViewModel(
                groupRepository = groupRepository,
                userRepository = userRepository,
                itemViewModel = itemViewModel
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}