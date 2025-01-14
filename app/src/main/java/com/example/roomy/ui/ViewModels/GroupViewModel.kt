package com.example.roomy.ui.ViewModels

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.roomy.db.GroupRepository
import androidx.lifecycle.ViewModel
import com.example.roomy.db.data.Groups
import com.example.roomy.ui.States.GroupState
import com.example.roomy.ui.States.GroupsUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class GroupViewModel(private val groupRepository: GroupRepository) : ViewModel() {

    private val _groups = MutableStateFlow(GroupState(emptyList()))
    private val _groupsInformation = MutableStateFlow(GroupsUiState(emptyList()))

    val groups = _groups.asStateFlow()
    val groupsInformation = _groupsInformation.asStateFlow()

    fun getGroupsByUserId(userId: String) {
        viewModelScope.launch {
            val groups = groupRepository.getGroupsByUserId(userId)
            _groups.update { oldState ->
                oldState.copy(
                    groups = groups
                )
            }
            getGroupInformationByGroupId(groups)
        }
    }


    fun getGroupInformationByGroupId(groups: List<Groups>) {
        Log.d("IDS", "$groups")
        viewModelScope.launch {
            val household = groups.map { group ->
                Log.d("IDS", "${group.groupId}")
                groupRepository.getGroupInformationById(group.groupId)
            }
            _groupsInformation.update {
                it.copy(groupsInformation = household)
            }
        }
    }


}