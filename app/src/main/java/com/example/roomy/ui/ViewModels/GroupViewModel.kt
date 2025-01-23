package com.example.roomy.ui.ViewModels

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.example.roomy.db.GroupRepository
import androidx.lifecycle.ViewModel
import com.example.roomy.db.BalanceRepository
import com.example.roomy.db.PaymentsRepository
import com.example.roomy.db.UserRepository
import com.example.roomy.db.data.GroupInformation
import com.example.roomy.db.data.Groups
import com.example.roomy.db.data.UserInformation
import com.example.roomy.ui.States.GroupMembersUiState
import com.example.roomy.ui.States.GroupState
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


sealed class AddGroupState {
    data object Idle : AddGroupState()
    data object Success : AddGroupState()
    data class Error(val message: String) : AddGroupState()
}

sealed class GroupError {
    data class Error(val message: String)
}

class GroupViewModel(
    private val groupRepository: GroupRepository,
    private val userRepository: UserRepository,
    private val itemViewModel: ItemViewModel,
    private val balanceRepository: BalanceRepository,
    private val paymentsRepository: PaymentsRepository,
    private val stateViewModel: StateViewModel

) : ViewModel() {

    // update state when a group gets added
    private val _addGroupState = mutableStateOf<AddGroupState>(AddGroupState.Idle)
    val addGroupState: State<AddGroupState> = _addGroupState


    private val _pullDownRefreshState = MutableStateFlow(false)
    val pullDownRefreshState = _pullDownRefreshState.asStateFlow()

    fun updatePullDownRefreshState(state: Boolean) {
        _pullDownRefreshState.value = state
    }

    // reset the groupState
    fun resetGroupState() {
        _addGroupState.value = AddGroupState.Idle
    }

    // state for fetching all the information from the database - for the loading screen
    private val _fetchingAllGroups = MutableStateFlow(true)
    val fetchingAllGroups = _fetchingAllGroups.asStateFlow()

    // error State
    private val _groupError = MutableStateFlow(GroupError.Error(""))
    val error = _groupError.asStateFlow()

    // current group information to display whe selected group information
    private val _currentGroupInformation =
        MutableStateFlow(GroupInformation(id = -1, name = "Placeholder", creatorId = "-1"))
    val currentGroupInformation: StateFlow<GroupInformation> get() = _currentGroupInformation

    // set current group information
    fun setCurrentGroupInformation(group: GroupInformation) {
        _currentGroupInformation.value = group
    }

    // get the Groups for the user to display them
    //Updated: this now also fetches the group members for each group and the number of shoppingListItems in each group to display in the Home UI
    fun getGroupsByUserId(userId: String) {
        viewModelScope.launch {
//            List of Groups the user is in
            val groups = groupRepository.getGroupIdsByUserId(userId)

            if (groups == null) {
                _groupError.update { oldState ->
                    oldState.copy("No groups found")
                }
            } else {

                val groupIds = groups.map { group ->
                    group.groupId
                }


                // fetch all the necessary data for a group with an async
                val groupInformationDeferred = async { groupRepository.getGroupInformationByIds(groupIds) ?: emptyList() }
                val groupMembersDeferred = async { getAllGroupsMembers(groupIds) }
                val itemsDeferred = async { itemViewModel.getAllItemsForGroups(groupIds) }
                val paymentsDeferred = async { paymentsRepository.getPaymentsByGroupIds(groupIds) ?: emptyList() }


                // wait for all functions to be done
                val groupInformation = groupInformationDeferred.await()
                val groupMembers = groupMembersDeferred.await()
                val items = itemsDeferred.await()
                val allPayments = paymentsDeferred.await()

                // combine the data to have all data in one state
                val combinedGroupState = groupIds.map { groupId ->
                    val groupInfo = groupInformation.find { it.id == groupId }
                    val membersUiState = groupMembers.find { it.groupId == groupId }
                    val members = membersUiState?.memberInformation ?: emptyList()
                    val (shoppingListItems, inventoryItems) = items[groupId] ?: Pair(
                        emptyList(),
                        emptyList()
                    )
                    val groupPayments = allPayments.filter { it.groupId == groupId }

                    // set the values for GroupState with the fetched data
                    GroupState(
                        groupId = groupId,
                        groupName = groupInfo?.name ?: "Unknown",
                        creatorId = groupInfo?.creatorId ?: "Unknown",
                        groupMembers = members,
                        shoppingListItems = shoppingListItems,  // Shopping list items
                        inventoryItems = inventoryItems,  // Inventory items
                        itemCount = shoppingListItems.size,
                        payments = groupPayments
                    )
                }

                // set the new GroupState
                stateViewModel.setAllGroupsState(combinedGroupState)

                // update the state that the fetch process is done
                _fetchingAllGroups.update { false }

//              Reset Pull to Refresh Indicator
                _pullDownRefreshState.update { false }

            }

        }
    }


    // get all GroupMembers for the groups the user is in, returns a List of GroupMemberUIStates
    private suspend fun getAllGroupsMembers(groupIds: List<Int>): List<GroupMembersUiState> {
        val usersInGroups = userRepository.getUsersByGroupIds(groupIds) ?: emptyList()
        val userIds = usersInGroups.map { it.userId }
        val userInformation = userRepository.getUserInformationByIds(userIds) ?: emptyList()

        // return group member UI state for each group
        return mapToGroupMembersUiState(usersInGroups, userInformation)
    }


    // map a list of groups and user information to a list of GroupMembersUiState
    private fun mapToGroupMembersUiState(
        groups: List<Groups>,
        allUserInformation: List<UserInformation>
    ): List<GroupMembersUiState> {

        // create a map where the key is the userId and the value is the user information
        val userInfoMap = allUserInformation.associateBy { it.id }

        // create a map where the key is the groupId and the value is the list of user information
        val groupedByGroupId = groups.groupBy { it.groupId }


        return groupedByGroupId.map { (groupId, usersInGroup) ->
            val members = usersInGroup.mapNotNull { group -> userInfoMap[group.userId] }
            // create an object with the groupId and the list of the member information
            GroupMembersUiState(groupId = groupId, memberInformation = members)
        }
    }


    // create a new group with all the information needed
    fun createNewGroup(groupName: String, userId: String, addedUsers: List<String>) {

        _fetchingAllGroups.update { true }

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
                    // update the currentGroup state and the state for all groups
                    setCurrentGroupInformation(newGroup)
                    _addGroupState.value = AddGroupState.Success
                }
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
            } else {
                // delete everything related to the group
                stateViewModel.deleteGroup(groupId)
                balanceRepository.deleteBalanceByGroupId(groupId)
                paymentsRepository.deleteGroupPayments(groupId)
            }
        }
    }

    // kick a user by the userId and groupId
    fun kickUser(userId: String, groupId: Int) {
        viewModelScope.launch {
            val kicked = groupRepository.kickMemberFromGroup(userId, groupId)
            if (!kicked) {
                _groupError.update { oldState ->
                    oldState.copy("Kick user failed")
                }
            } else {
                // delete everything from the group that is related to the kicked user
                stateViewModel.kickUser(groupId, userId)
                balanceRepository.deleteBalanceByUserId(groupId, userId)
                paymentsRepository.deleteUserPayments(userId, groupId)
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
                    stateViewModel.addUser(groupId, user)
                }
            }
        }
    }

    // update the group error state
    fun setGroupError(newError: String) {
        _groupError.update { oldState ->
            oldState.copy(newError)
        }
    }

    // clear the group error state
    fun clearGroupError() {
        _groupError.update { oldState ->
            oldState.copy("")
        }
    }
}
