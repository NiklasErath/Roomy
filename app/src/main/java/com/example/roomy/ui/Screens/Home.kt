package com.example.roomy.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.roomy.R
import com.example.roomy.ui.Composables.AddGroupButton
import com.example.roomy.ui.States.GroupState
import com.example.roomy.ui.ViewModels.AddGroupState
import com.example.roomy.ui.ViewModels.GroupViewModel
import com.example.roomy.ui.ViewModels.ItemViewModel
import com.example.roomy.ui.ViewModels.LoginState
import com.example.roomy.ui.ViewModels.UserViewModel
import dev.materii.pullrefresh.PullRefreshIndicator
import dev.materii.pullrefresh.PullRefreshLayout
import dev.materii.pullrefresh.rememberPullRefreshState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(
    navController: NavController,
    groupViewModel: GroupViewModel,
    userViewModel: UserViewModel,
    itemViewModel: ItemViewModel,
    allGroupsState: List<GroupState>,
    previousScreen: String
) {

    val context = LocalContext.current

    val addGroupState by groupViewModel.addGroupState

    val currentUserId = userViewModel.currentUserSession.collectAsState().value.userId

    var fetchingAllGroups = groupViewModel.fetchingAllGroups.collectAsState()

    val pullDownRefreshState = groupViewModel.pullDownRefreshState.collectAsState()



    val loginState by userViewModel.loginState


//    var triggerNavCheck by remember { mutableStateOf(true) }

//    This allows to controll rerenders only when necessary, note because of db and uistate mismatches we disable rerenders wehn coming from Groupmembers page
//    Triggernavchek maybe neccessary to make sure previousscreen can be used correctly
    LaunchedEffect(previousScreen) {
//        if (triggerNavCheck) {
        when (previousScreen) {
            Screens.Login.name, Screens.Groups.name, Screens.Profile.name, Screens.Balance.name, Screens.Home.name -> groupViewModel.getGroupsByUserId(
                currentUserId
            )

        }
//            triggerNavCheck = false
//        }
    }


    LaunchedEffect(loginState) {
        if (loginState is LoginState.Success) {
            groupViewModel.getGroupsByUserId(
                currentUserId
            )
        }
    }


//    Launcheffect for adding a new Group,
//    Basically makers sure we navigate to the Groups Screen after the Group is created ... necessary?
    LaunchedEffect(addGroupState) {
        if (addGroupState is AddGroupState.Success) {
//            navController.navigate(Screens.Groups.name)
            groupViewModel.getGroupsByUserId(currentUserId)
            groupViewModel.resetGroupState()

        }
    }

    var newGroupName by remember { mutableStateOf("Home") }
    var usernameAdd by remember { mutableStateOf("") }

    val addedUsers = remember { mutableStateListOf<String>() }


    var addGroupPopUp by remember { mutableStateOf(false) }


    var pullRefreshState = rememberPullRefreshState(refreshing = pullDownRefreshState.value, onRefresh = {
        groupViewModel.updatePullDownRefreshState(true)
        groupViewModel.getGroupsByUserId(currentUserId)
        groupViewModel.resetGroupState()})


    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Text(text = "My Groups", fontSize = integerResource(id = R.integer.heading1).sp)
            Spacer(Modifier.height(40.dp))

            if (fetchingAllGroups.value) {
                LoadingIndicator(modifier = Modifier.padding(bottom = 300.dp))
            } else {

                PullRefreshLayout(
                    state = pullRefreshState,
                    indicator = {
                        PullRefreshIndicator(
                            state = pullRefreshState,
                            backgroundColor = MaterialTheme.colorScheme.surfaceContainer,
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    }
                ){
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(20.dp),
                        modifier = Modifier.weight(1f) // Ensures the LazyColumn takes up remaining space
                    ) {
                        if (allGroupsState.isEmpty()) {
                            item {
                                Text(text = "No groups")
                            }
                        } else {
                            itemsIndexed(allGroupsState) { index, group ->
                                GroupCard(groupViewModel, group, navController)
                            }
                        }

                    }

                }





            }
        }
    }

    if (!addGroupPopUp) {
        AddGroupButton(onClick = { addGroupPopUp = true })
    }


    // Button positioned at the bottom center
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .background(Color.Transparent)
//                .padding(bottom = 40.dp) // Optional padding
//                .align(Alignment.BottomCenter),
//            horizontalArrangement = Arrangement.End
//        ) {
//            Button(onClick = { addGroupPopUp = true }) {
//                Icon(imageVector = Icons.Filled.Add, contentDescription = "Add Group")
//            }
//        }
//    }


    if (addGroupPopUp) {
        Column(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .clickable { }
                .shadow(0.dp)
            ,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {

            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Return",
                    Modifier.clickable { addGroupPopUp = false })

                Spacer(Modifier.width(20.dp))
                Text(text = "Add Group", fontSize = integerResource(id = R.integer.heading1).sp)
            }

            CustomOutlinedTextField(
                value = newGroupName,
                onValueChange = { newValue -> newGroupName = newValue },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "GroupName"
                    )
                },
                label = { Text("Groupname") },
            )

            CustomOutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = usernameAdd,
                onValueChange = { newValue -> usernameAdd = newValue },
                label = { Text("Invite someone New") },
                placeholder = { Text("username") },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add",
                        Modifier.clickable {


                            if (addedUsers.contains(usernameAdd) || usernameAdd.isEmpty()) {
                                return@clickable
                            }
                            userViewModel.getUserByUsername(usernameAdd) { isUserFound ->
                                if (!isUserFound) {
                                    usernameAdd = ""
                                    groupViewModel.clearGroupError()
                                } else {
                                    addedUsers.add(usernameAdd)
                                    usernameAdd = ""
                                }
                            }
                        })

                }


            )





            if (addedUsers.isNotEmpty()) {
                LazyColumn() {
                    itemsIndexed(addedUsers) { index, item ->
                        OutlinedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Row(modifier = Modifier.padding(12.dp)) {
                                Text(text = item)
                                Button(onClick = { addedUsers.remove(item) }) {
                                    Text(text = "remove")
                                }
                            }
                        }
                    }
                }
            }
            Button(
                onClick = {
                    groupViewModel.createNewGroup(newGroupName, currentUserId, addedUsers)
                    addGroupPopUp = false
                },
            ) {
                Text(text = "Add Group")
            }
        }
    }
}