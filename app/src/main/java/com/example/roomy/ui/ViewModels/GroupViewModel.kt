package com.example.roomy.ui.ViewModels

import androidx.lifecycle.viewModelScope
import com.example.roomy.db.GroupRepository
import androidx.lifecycle.ViewModel
import com.example.roomy.ui.States.GroupState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch



class GroupViewModel(private val groupRepository: GroupRepository) : ViewModel() {

    private val _groups = MutableStateFlow(GroupState(0))

    val groups = _groups.asStateFlow()

    fun getGroupsByUserId(userId: String) {
        viewModelScope.launch {
            groupRepository.getGroupsByUserId(userId)
        }
    }
}