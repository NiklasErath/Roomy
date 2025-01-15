package com.example.roomy.ui.ViewModels

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.roomy.db.GroupRepository
import androidx.lifecycle.ViewModel
import com.example.roomy.db.Supabase.supabase
import com.example.roomy.db.UserRepository
import com.example.roomy.db.data.GroupInformation
import com.example.roomy.db.data.Groups
import com.example.roomy.ui.States.GroupMembersUiState
import com.example.roomy.ui.States.GroupState
import com.example.roomy.ui.States.GroupsUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


sealed class AddGroupState {
    object Idle : AddGroupState()
    object Success : AddGroupState()
    data class Error(val message: String) : AddGroupState()
}


class GroupViewModel(
    private val groupRepository: GroupRepository,
    private val userRepository: UserRepository
) : ViewModel() {


    private val _addGroupState = mutableStateOf<AddGroupState>(AddGroupState.Idle)
    val addGroupState: State<AddGroupState> = _addGroupState
    fun resetGroupState() {
        _addGroupState.value = AddGroupState.Idle
    }

    private val _groups = MutableStateFlow(GroupState(emptyList()))
    private val _groupsInformation = MutableStateFlow(GroupsUiState(emptyList()))
    private val _groupMembers = MutableStateFlow(GroupMembersUiState(emptyList()))

    val groups = _groups.asStateFlow()
    val groupsInformation = _groupsInformation.asStateFlow()
    val groupMembers = _groupMembers.asStateFlow()

    private val _currentGroup =
        MutableStateFlow(GroupInformation(id = -1, name = "Placeholder", creatorId = "-1"))
    val currentGroup: StateFlow<GroupInformation> get() = _currentGroup

    fun setCurrentGroup(group: GroupInformation) {
        _currentGroup.value = group
    }

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

    fun createNewGroup(groupName: String, userId: String) {
        viewModelScope.launch {
            val newGroup = groupRepository.createGroup(groupName, userId)
            Log.d("HAHHAHAHAHAHA", "$newGroup")
            newGroup.id?.let { groupId ->
                Log.d("GROUP ID", "$groupId and $userId")
                groupRepository.addMemberToGroup(userId, groupId)

                setCurrentGroup(newGroup)
                _addGroupState.value = AddGroupState.Success


            } ?: run {
                Log.d("TAG", "Group ID is null. Cannot add member.")
            }
        }
    }

    fun getGroupMembers(groupId: Int) {
        viewModelScope.launch {
            val users = groupRepository.getGroupMembers(groupId)
            val usersInformation = users.map { users ->
                Log.d("IDS FOR INFO", "${users.userId}")
                userRepository.getUserById(users.userId)

            }
            _groupMembers.update { it.copy(memberInformation = usersInformation) }
            Log.d("User INFO", "$usersInformation")
        }
    }

    fun deleteGroup(groupId: Int) {
        viewModelScope.launch {
            groupRepository.deleteGroup(groupId)
        }
    }

    fun kickUser(userId: String, groupId: Int) {
        viewModelScope.launch {
            groupRepository.kickMemberFromGroup(userId, groupId)
        }
    }

    fun addMemberToGroup(userId: String, groupId: Int) {
        viewModelScope.launch {
            groupRepository.addMemberToGroup(userId, groupId)
        }
    }
}