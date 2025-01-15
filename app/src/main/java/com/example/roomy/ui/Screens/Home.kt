package com.example.roomy.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.roomy.R
import com.example.roomy.ui.States.GroupsUiState
import com.example.roomy.ui.ViewModels.AddGroupState
import com.example.roomy.ui.ViewModels.GroupViewModel
import com.example.roomy.ui.ViewModels.UserViewModel

@Composable
fun Home(
    navController: NavController,
    groupViewModel: GroupViewModel,
    userViewModel: UserViewModel,
) {


    val addGroupState by groupViewModel.addGroupState

    val context = LocalContext.current

    val currentUserId = userViewModel.currentUserSession.collectAsState().value.userId

    val groupInformationState by groupViewModel.groupsInformation.collectAsState(
        initial = GroupsUiState(
            emptyList()
        )
    )

    LaunchedEffect(Unit) {
        groupViewModel.getGroupsByUserId(currentUserId)
        groupViewModel.getGroupMembers(2)
    }

    LaunchedEffect(addGroupState) {
        if (addGroupState is AddGroupState.Success) {
            navController.navigate(Screens.Groups.name)
            groupViewModel.resetGroupState()

        }
    }

    var newGroupName by remember { mutableStateOf("Home") }
    var usernameAdd by remember { mutableStateOf("") }

    val addedUsers = remember { mutableStateListOf<String>() }


    var addGroupPopUp by remember { mutableStateOf(false) }

    Column(
        Modifier.fillMaxSize()
    ) {
        Text(text = "Groups Page")
        Button(onClick = { navController.navigate(Screens.Groups.name) }) {
            Text(text = "Select and enter Group")
        }

        LazyColumn {
            if (groupInformationState.groupsInformation.isEmpty()) {
                item {
                    Text(text = "No groups")
                }
            } else {
                itemsIndexed(groupInformationState.groupsInformation) { index, groupInformation ->


                        OutlinedCard(
                            Modifier.clickable {
                                groupViewModel.setCurrentGroup(groupInformation)
                                navController.navigate(Screens.Groups.name)
                            }.fillMaxWidth(),

                        ) {
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp), horizontalArrangement = Arrangement.Center) {

                            Text(text = " ${groupInformation.name}")
                        }

                    }


                }


            }
            item {
                Spacer(Modifier.height(100.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center){

                    Button(onClick = {
                        addGroupPopUp = true

                    }) {
                        Icon(imageVector = Icons.Filled.Add, contentDescription = "Add Group")
                    }

                }

            }
        }


    }

    if (addGroupPopUp) {
        Column(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
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

            OutlinedTextField(
                value = newGroupName,
                onValueChange = { newValue -> newGroupName = newValue },
                Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "GroupName"
                    )
                },
            )

            Row {

            }
            OutlinedTextField(
                value = usernameAdd,
                onValueChange = { newValue -> usernameAdd = newValue },
                Modifier.fillMaxWidth(),
//                leadingIcon = {Icon(imageVector = Icons.Filled.Edit, contentDescription = "AddUser")},
                label = { Text("Invite someone New") },
                placeholder = { Text("example@outlook.com") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),

                trailingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add",
                        Modifier.clickable {


                            if (addedUsers.contains(usernameAdd) || usernameAdd.isEmpty()) {
                                return@clickable
                            }

                            userViewModel.getUserByUsername(usernameAdd)
                            addedUsers.add(usernameAdd)
                            Log.d("Users", "$addedUsers")
                            usernameAdd = ""
//                    Add new member, if successfull make a list with added users emails/usernames under this textfield - just save locally from input to display
//                    If not succesfull, popup with error message ...
//                    Ensure Users can only be added once

                        })
                },



                )
            if (addedUsers.isNotEmpty()) {
              LazyColumn (){
                  itemsIndexed(addedUsers) {index, item ->
                      OutlinedCard(modifier = Modifier
                          .fillMaxWidth()) {
                          Row(modifier = Modifier.padding(12.dp)) {
                              Text(text = item)
                              Button(onClick = {addedUsers.remove(item)}) {
                                  Text(text = "remove")
                              }
                          }
                      }
                  }
              }
            }
            Button(
                onClick = {
                    groupViewModel.createNewGroup(newGroupName, currentUserId,addedUsers)
                    addGroupPopUp = false
                },
            ) {
                Text(text = "Add Group")
            }
        }
    }
}