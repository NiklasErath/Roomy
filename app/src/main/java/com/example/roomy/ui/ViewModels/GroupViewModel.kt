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
import com.example.roomy.ui.States.GroupsUiState
import com.example.roomy.ui.States.PaymentsUiState
import com.example.roomy.ui.States.newGroupState
import kotlinx.coroutines.async
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

//    private val _allGroupsState = MutableStateFlow<List<newGroupState>>(emptyList())
//    val allGroupsState = _allGroupsState.asStateFlow()


    private val _addGroupState = mutableStateOf<AddGroupState>(AddGroupState.Idle)
    val addGroupState: State<AddGroupState> = _addGroupState
    fun resetGroupState() {
        _addGroupState.value = AddGroupState.Idle
    }

    private val _groupsInformation = MutableStateFlow(GroupsUiState(emptyList()))
    private val _allGroupsMembers = MutableStateFlow<List<GroupMembersUiState>>(emptyList())
    private val _groupMembers = MutableStateFlow(GroupMembersUiState(emptyList()))

    private val _fetchingAllGroups = MutableStateFlow(true)
    val fetchingAllGroups = _fetchingAllGroups.asStateFlow()


    // error State
    private val _groupError = MutableStateFlow(GroupError.Error(""))
    val error = _groupError.asStateFlow()

    val groupsInformation = _groupsInformation.asStateFlow()
    val allGroupsMembers = _allGroupsMembers.asStateFlow()

    val groupMembers = _groupMembers.asStateFlow()

    private val _currentGroupInformation =
        MutableStateFlow(GroupInformation(id = -1, name = "Placeholder", creatorId = "-1"))
    val currentGroupInformation: StateFlow<GroupInformation> get() = _currentGroupInformation

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

                val groupIds = groups.map{group ->
                    group.groupId
                }


//                Fetch all needed data from supabase with asynchronous requests
                val groupInformationDeferred = async { groupRepository.getGroupInformationByIds(groupIds) ?: emptyList() }
                val groupMembersDeferred = async { getAllGroupsMembers(groupIds) }
                val itemsDeferred = async { itemViewModel.getAllItemsForGroups(groupIds) }
                val paymentsDeferred = async { paymentsRepository.getPaymentsByGroupIds(groupIds) ?: emptyList()}


                // Wait for all async tasks to complete
                val groupInformation = groupInformationDeferred.await()
                val groupMembers = groupMembersDeferred.await()
                val items = itemsDeferred.await()
                val allPayments = paymentsDeferred.await()

//                Create a combined Group State containing all Necessary Information for UI Rendering
                val combinedGroupState = groupIds.map { groupId ->
                    val groupInfo = groupInformation.find { it.id == groupId }
                    val membersUiState = groupMembers.find { it.groupId == groupId }
                    val members = membersUiState?.memberInformation ?: emptyList()
                    val (shoppingListItems, inventoryItems) = items[groupId] ?: Pair(emptyList(), emptyList())
                    val groupPayments = allPayments.filter { it.groupId == groupId }

                    newGroupState(
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

//                Set the new GroupState
                stateViewModel.setAllGroupsState(combinedGroupState)

//                Used to display the loader in the Home screen while fetching
                _fetchingAllGroups.update { false }


            }

        }
    }


    // Function to get all members for all groups
//    private fun getAllGroupsMembers(groups: List<Groups>) {
//        viewModelScope.launch {
////               For each group, fetch its members
//            val allMembers = mutableListOf<GroupMembersUiState>()
//            for (group in groups) {
//                // Step 3: Fetch members for each group
//                val groupMembers = groupRepository.getGroupMembers(group.groupId)
//
//                if (groupMembers != null) {
//                    val groupMemberInfo = groupMembers.map { member ->
//                        // Assuming userRepository.getUserById returns a UserInformation object
//                        userRepository.getUserById(member.userId)
//                    }.filterNotNull()
//
//                    // Step 4: Add the group members to the list
//                    allMembers.add(GroupMembersUiState(groupMemberInfo))
//                } else {
//                    _groupError.update { oldState ->
//                        oldState.copy("Could not get All Group Members")
//                    }
//                }
//            }
//            // Step 5: Update _allGroupsMembers with the fetched members
//            _allGroupsMembers.update {
//                allMembers
//            }
//        }
//    }


//    private fun getAllGroupsMembers(groupIds: List<Int>) {
//        viewModelScope.launch {
//            val usersInGroups = userRepository.getUsersByGroupIds(groupIds) ?: emptyList()
//
//            Log.d("llllllllllllllllllllllllllllllllllllllllll", "$usersInGroups")
//
//
//
//            val userIds = usersInGroups.map { it.userId }
//
//            val userInformation = userRepository.getUserInformationByIds(userIds) ?: emptyList()
//
//
//            mapToGroupMembersUiState(usersInGroups, userInformation)
//
//        }
//    }
//


//Get all GroupMembers the user is in, returns a List of GroupmemberUIStates
    private suspend fun getAllGroupsMembers(groupIds: List<Int>): List<GroupMembersUiState> {
        val usersInGroups = userRepository.getUsersByGroupIds(groupIds) ?: emptyList()
        val userIds = usersInGroups.map { it.userId }
        val userInformation = userRepository.getUserInformationByIds(userIds) ?: emptyList()

        // Map the fetched UserInformations and map them to a GroupUIState with groupID to use them for filling the Global UI State
        // Return group member UI state for each group
        return mapToGroupMembersUiState(usersInGroups, userInformation)
    }


////    With all the fetched data we create one groupMember UI State with all groups containing all members
//    private fun mapToGroupMembersUiState(groups: List<Groups>, allUserInformation: List<UserInformation>) {
//        // Step 1: Create a map of user ID to UserInformation for quick lookup
//        val userInfoMap = allUserInformation.associateBy { it.id }
//
//        // Step 2: Group the users by their groupId
//        val groupedByGroupId = groups.groupBy { it.groupId }  // Group by groupId
//
//        // Step 3: For each groupId, fetch the corresponding user information and create GroupMembersUiState
//        val groupMembersUiStateList = groupedByGroupId.map { (groupId, usersInGroup) ->
//            // Fetch all user information for the current group
//            val members = usersInGroup.mapNotNull { group ->
//                userInfoMap[group.userId]  // Lookup user information by userId
//            }
//
//            // Step 4: Create GroupMembersUiState for the group
//            GroupMembersUiState(
//                memberInformation = members
//            )
//        }
//
//        // Update the state with the newly created list
//        _allGroupsMembers.value = groupMembersUiStateList
//
//        Log.d("allGroupMembers", "$_allGroupsMembers")
//
//    }

//Takes a List of Users/Group Entities from the database and maps it to a List of GroupUIStated to further process
    private fun mapToGroupMembersUiState(groups: List<Groups>, allUserInformation: List<UserInformation>): List<GroupMembersUiState> {
        val userInfoMap = allUserInformation.associateBy { it.id }
        val groupedByGroupId = groups.groupBy { it.groupId }

        return groupedByGroupId.map { (groupId, usersInGroup) ->
            val members = usersInGroup.mapNotNull { group -> userInfoMap[group.userId] }
            GroupMembersUiState(groupId = groupId, memberInformation = members)
        }
    }





//
//    // get the Group information by the group Id
//    private fun getGroupInformationByGroupId(groups: List<Groups>) {
//        viewModelScope.launch {
//            val household = groups.map { group ->
//                Log.d("IDS", "${group.groupId}")
//                groupRepository.getGroupInformationById(group.groupId)
//            }
////            val houseHoldMembers = groups.map{group ->
////                val groups = groupRepository.getGroupMembers(group.groupId)
////            }
//
//            _groupsInformation.update {
//                it.copy(groupsInformation = household.filterNotNull())
//            }
//        }
//    }


    // get all the Group information by the group Id
    private fun getGroupInformationByGroupId(groupIds: List<Int>) {
        viewModelScope.launch {

            val households =
                groupRepository.getGroupInformationByIds(groupIds) ?: emptyList()

//            val houseHoldMembers = groups.map{group ->
//                val groups = groupRepository.getGroupMembers(group.groupId)
//            }

            _groupsInformation.update {
                it.copy(groupsInformation = households)
            }
        }
    }


    // create a new group with information needed
    fun createNewGroup(groupName: String, userId: String, addedUsers: List<String>) {
//        Set this state to true here to give the allow loader rendering while UIState has to update
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

//                    stateViewModel.addNewGroup(newGroupState(creatorId = newGroup.creatorId, groupId = newGroup.id, groupMembers = ))

                    setCurrentGroupInformation(newGroup)
                    _addGroupState.value = AddGroupState.Success


                } ?: run {
                    Log.d("TAG", "Group ID is null. Cannot add member.")
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
                stateViewModel.deleteGroup(groupId)
                balanceRepository.deleteBalanceByGroupId(groupId)
                paymentsRepository.deleteGroupPayments(groupId)
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
                stateViewModel.addUser(groupId, user)

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

    fun setGroupError(newError:String){
        _groupError.update { oldState->
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
