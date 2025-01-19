package com.example.roomy.ui.ViewModels

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.example.roomy.db.GroupRepository
import androidx.lifecycle.ViewModel
import com.example.roomy.db.BalanceRepository
import com.example.roomy.db.UserRepository
import com.example.roomy.db.data.GroupInformation
import com.example.roomy.db.data.Groups
import com.example.roomy.db.data.UserInformation
import com.example.roomy.ui.States.GroupMembersUiState
import com.example.roomy.ui.States.GroupState
import com.example.roomy.ui.States.GroupsUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


sealed class AddGroupState {
    object Idle : AddGroupState()
    object Success : AddGroupState()
    data class Error(val message: String) : AddGroupState()
}

sealed class GroupError {
    data class Error(val message: String)
}


class GroupViewModel(
    private val groupRepository: GroupRepository,
    private val userRepository: UserRepository,
    private val itemViewModel: ItemViewModel,
    private val balanceRepository: BalanceRepository
) : ViewModel() {


    private val _addGroupState = mutableStateOf<AddGroupState>(AddGroupState.Idle)
    val addGroupState: State<AddGroupState> = _addGroupState
    fun resetGroupState() {
        _addGroupState.value = AddGroupState.Idle
    }

    private val _groups = MutableStateFlow(GroupState(emptyList()))
    private val _groupsInformation = MutableStateFlow(GroupsUiState(emptyList()))
    private val _allGroupsMembers = MutableStateFlow<List<GroupMembersUiState>>(emptyList())
    private val _groupMembers = MutableStateFlow(GroupMembersUiState(emptyList()))

    // error State
    private val _groupError = MutableStateFlow(GroupError.Error(""))
    val error = _groupError.asStateFlow()

    val groups = _groups.asStateFlow()
    val groupsInformation = _groupsInformation.asStateFlow()
    val allGroupsMembers = _allGroupsMembers.asStateFlow()

    val groupMembers = _groupMembers.asStateFlow()

    private val _currentGroup =
        MutableStateFlow(GroupInformation(id = -1, name = "Placeholder", creatorId = "-1"))
    val currentGroup: StateFlow<GroupInformation> get() = _currentGroup

    fun setCurrentGroup(group: GroupInformation) {
        _currentGroup.value = group
    }

    // get the Groups for the user to display them
    //Updated: this now also fetches the group members for each group and the number of shoppingListItems in each group to display in the Home UI
    fun getGroupsByUserId(userId: String) {
        viewModelScope.launch {
            val groups = groupRepository.getGroupsByUserId(userId)
            if (groups == null) {
                _groupError.update { oldState ->
                    oldState.copy("No groups found")
                }
            } else {
                _groups.update { oldState ->
                    oldState.copy(
                        groups = groups
                    )
                }
                getGroupInformationByGroupId(groups)

//              Addition by Jakob - getALlGroupMembers for Home Page to display them and all itemcounts
                getAllGroupsMembers(groups)

                val groupIds = groups.map { it.groupId }
                itemViewModel.getAllItemCountsForGroups(groupIds)
            }
        }
    }


    // Function to get all members for all groups
    private fun getAllGroupsMembers(groups: List<Groups>) {
        viewModelScope.launch {
//               For each group, fetch its members
            val allMembers = mutableListOf<GroupMembersUiState>()
            for (group in groups) {
                // Step 3: Fetch members for each group
                val groupMembers = groupRepository.getGroupMembers(group.groupId)

                if (groupMembers != null) {
                    val groupMemberInfo = groupMembers.map { member ->
                        // Assuming userRepository.getUserById returns a UserInformation object
                        userRepository.getUserById(member.userId)
                    }.filterNotNull()

                    // Step 4: Add the group members to the list
                    allMembers.add(GroupMembersUiState(groupMemberInfo))
                } else {
                    _groupError.update { oldState ->
                        oldState.copy("Could not get All Group Members")
                    }
                }
            }
            // Step 5: Update _allGroupsMembers with the fetched members
            _allGroupsMembers.update {
                allMembers
            }
        }
    }


    // get the Group information by the group Id
    private fun getGroupInformationByGroupId(groups: List<Groups>) {
        viewModelScope.launch {
            val household = groups.map { group ->
                Log.d("IDS", "${group.groupId}")
                groupRepository.getGroupInformationById(group.groupId)
            }
//            val houseHoldMembers = groups.map{group ->
//                val groups = groupRepository.getGroupMembers(group.groupId)
//            }

            _groupsInformation.update {
                it.copy(groupsInformation = household.filterNotNull())
            }
        }
    }

    // create a new group with information needed
    fun createNewGroup(groupName: String, userId: String, addedUsers: List<String>) {
        viewModelScope.launch {
            val newGroup = groupRepository.createGroup(groupName, userId)
            if (newGroup == null) {
                _groupError.update { oldState ->
                    oldState.copy("Could not create the group")
                }
            } else {
                newGroup.id?.let { groupId ->
                    val addMember = groupRepository.addMemberToGroup(userId, groupId)
                    if (!addMember) {
                        _groupError.update { oldState ->
                            oldState.copy("Failed to add Member")
                        }
                    }
                    try {

                        addedUsers.forEach { item ->
                            addMemberByNameToGroup(item, groupId)

                        }
                    } catch (e: Exception) {
                        _groupError.update { oldState ->
                            oldState.copy("Failed to add Members")
                        }
                    }

                    setCurrentGroup(newGroup)
                    _addGroupState.value = AddGroupState.Success


                } ?: run {
                    Log.d("TAG", "Group ID is null. Cannot add member.")
                }
            }
        }
    }

    // get all the group members and their information for a group by groupid
    fun getGroupMembers(groupId: Int) {
        viewModelScope.launch {
            val users = groupRepository.getGroupMembers(groupId)
            if (users == null) {
                _groupError.update { oldState ->
                    oldState.copy("Could not fetch group Members")
                }
            } else {
                val usersInformation = users.map { users ->
                    Log.d("IDS FOR INFO", users.userId)
                    userRepository.getUserById(users.userId)

                }
                _groupMembers.update { it.copy(memberInformation = usersInformation.filterNotNull()) }
                Log.d("User INFO", "$usersInformation")
            }
        }
    }

    // delete a group by groupId
    fun deleteGroup(groupId: Int) {
        viewModelScope.launch {
            val delete = groupRepository.deleteGroup(groupId)
            if (!delete) {
                _groupError.update { oldState ->
                    oldState.copy("Group could not be deleted")
                }
            }
        }
    }

    // kick a user
    fun kickUser(userId: String, groupId: Int) {
        viewModelScope.launch {
            val kicked = groupRepository.kickMemberFromGroup(userId, groupId)
            if (!kicked) {
                _groupError.update { oldState ->
                    oldState.copy("Kick user failed")
                }
            } else {
                _groupMembers.update { oldState ->
                    val updatedMembers = oldState.memberInformation.filter { it.id != userId }
                    oldState.copy(memberInformation = updatedMembers)
                }
            }
        }
    }

    //add a member to a group by his username
    fun addMemberByNameToGroup(username: String, groupId: Int) {
        viewModelScope.launch {

            val user = userRepository.getUserByName(username)
            if (user == null) {
                _groupError.update { oldState ->
                    oldState.copy("No user found")
                }
            } else {
                val adduser = groupRepository.addMemberToGroup(user.id, groupId)
                if (!adduser) {
                    _groupError.update { oldState ->
                        oldState.copy("User already in the group")
                    }
                } else {
                    _groupMembers.update { oldState ->
                        val updatedMembers = oldState.memberInformation.toMutableList()
                        updatedMembers.add(user)
                        oldState.copy(memberInformation = updatedMembers)
                    }
//                    Also update the current AllGroupMembers here if a new group is created
                    _allGroupsMembers.update { oldGroups ->
                        oldGroups + _groupMembers.value
                    }

                }
            }

        }

    }

    // clear the group error state
    fun clearGroupError() {
        _groupError.update { oldState ->
            oldState.copy("")
        }
    }


}
