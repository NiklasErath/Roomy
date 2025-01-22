package com.example.roomy.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController

import com.example.roomy.ui.Composables.UserProfileCircle
import com.example.roomy.ui.States.GroupState
import com.example.roomy.ui.ViewModels.GroupViewModel
import com.example.roomy.ui.ViewModels.UserViewModel

@Composable
fun Profile(
    userViewModel: UserViewModel,
    groupViewModel: GroupViewModel,
    navController: NavController,
    currentGroup: GroupState
) {

    val currentUser by userViewModel.loggedInUser.collectAsState()

    var username by remember { mutableStateOf("") }
    var theme by remember { mutableStateOf("") }
    var isChangeUsernameVisible by remember { mutableStateOf(false) }

    val context = LocalContext.current

    var deleteGroup by remember { mutableStateOf(false) }
    var leaveGroup by remember { mutableStateOf(false) }


    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Profile")
        Column(modifier = Modifier.padding(top = 20.dp)) {
            UserProfileCircle(currentUser.username, 100.dp, Color.Gray)
        }
        Column(
            Modifier
                .fillMaxSize()
                .padding(top = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                thickness = 1.dp,
                color = Color.Gray
            )
            LazyColumn(modifier = Modifier.padding(bottom = 16.dp)) {
                item {
                    OutlinedCard(modifier = Modifier.padding(top = 12.dp)) {
                        Column(
                            modifier = Modifier
                                .padding(12.dp)
                                .fillMaxWidth()
                        ) {
                            Text(text = "User Info")

                            HorizontalDivider(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                thickness = 1.dp,
                                color = Color.Gray
                            )

                            Box() {
                                Column {
                                    Text(text = "Name: ${currentUser.username}")
                                    Text(text = "ID: ${currentUser.userId}")
                                    Text(text = "Email: ${currentUser.email}")
                                }
                            }
                        }
                    }
                }
                item {
                    OutlinedCard(modifier = Modifier.padding(top = 12.dp)) {
                        Column(
                            modifier = Modifier
                                .padding(12.dp)
                                .fillMaxWidth()
                        ) {

                            Column() {
                                Text(text = "User settings")

                                HorizontalDivider(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    thickness = 1.dp,
                                    color = Color.Gray
                                )

                                if (isChangeUsernameVisible) {
                                    Dialog(
                                        onDismissRequest = {
                                            isChangeUsernameVisible = false
                                            username = ""
                                        }
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp)
                                                .background(
                                                    Color.White,
                                                    shape = RoundedCornerShape(8.dp)
                                                )
                                        ) {
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(16.dp),
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Text(
                                                    text = "Change Username",
                                                    modifier = Modifier.padding(bottom = 8.dp),
                                                    color = Color.Black
                                                )
                                                OutlinedTextField(
                                                    value = username,
                                                    onValueChange = { newValue ->
                                                        username = newValue
                                                    },
                                                    Modifier.fillMaxWidth(),
                                                    label = { Text("new username") },
                                                    leadingIcon = {
                                                        Icon(
                                                            imageVector = Icons.Filled.Person,
                                                            contentDescription = "username",
                                                            modifier = Modifier
                                                        )
                                                    }


                                                )
                                                if (username.length in 1..2) {
                                                    Text(
                                                        text = "The username must be at least 3 characters long",
                                                        color = Color.Red,
                                                    )
                                                }
                                                Row(
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(top = 8.dp)
                                                ) {
                                                    Button(onClick = {
                                                        if (username.length >= 3) {
                                                            userViewModel.updateUserName(
                                                                username,
                                                                currentUser.userId,
                                                            )
                                                            isChangeUsernameVisible = false
                                                            username = ""
                                                        }
                                                    }) {
                                                        Text(text = "Change username")
                                                    }
                                                    Button(onClick = {
                                                        isChangeUsernameVisible = false
                                                        username = ""
                                                    }) {
                                                        Text(text = "Cancel")
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                Button(
                                    onClick = { isChangeUsernameVisible = true },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(text = "Change username")
                                }

                                Button(
                                    onClick = {
                                        userViewModel.logout({ loggedOut ->
                                            if (!loggedOut) {
                                                navController.navigate(Screens.Login.route)
                                            }
                                        }, context)
                                    }, modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(text = "Logout")
                                }
                            }
                        }
                    }
                }
                item {


                    OutlinedCard(modifier = Modifier.padding(top = 12.dp)) {
                        Column(
                            modifier = Modifier
                                .padding(12.dp)
                                .fillMaxWidth()
                        ) {
                            Text(text = "Theme settings")

                            HorizontalDivider(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                thickness = 1.dp,
                                color = Color.Gray
                            )

                            Button(
                                onClick = {
                                    if (theme == "dark") {
                                        theme = "white"
                                    } else {
                                        theme = "dark"
                                    }
                                }, modifier = Modifier.fillMaxWidth()
                            ) {
                                if (theme == "dark") {
                                    Text(text = "Change to White mode")
                                } else {
                                    Text(text = "Change to Dark mode")
                                }
                            }
                        }
                    }
                }
                item {

                    OutlinedCard(modifier = Modifier.padding(top = 12.dp)) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(text = "Group settings")

                            HorizontalDivider(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                thickness = 1.dp,
                                color = Color.Gray
                            )

                            Button(
                                onClick = { navController.navigate(Screens.GroupMembers.route) },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(text = "Manage Members")
                            }
                            if (currentUser.userId == currentGroup.creatorId) {
                                Button(onClick = {
                                    deleteGroup = true
                                }, modifier = Modifier.fillMaxWidth()) {
                                    Text(text = "Delete group")
                                }
                            } else {
                                Button(onClick = {
                                    leaveGroup = true
                                }, modifier = Modifier.fillMaxWidth()) {
                                    Text(text = "Leave group")
                                }
                            }
                        }
                    }
                    if (leaveGroup || deleteGroup) {
                        Dialog(onDismissRequest = {
                            deleteGroup = false
                            leaveGroup = false
                        }) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                                    .background(Color.White, shape = RoundedCornerShape(8.dp))
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    if (leaveGroup) {
                                        Text(
                                            text = "You really want to leave ${currentGroup.groupName}?",
                                            modifier = Modifier.padding(bottom = 8.dp),
                                            color = Color.Black,
                                        )
                                        Row(
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                        ) {
                                        Button(onClick = {
                                            leaveGroup = false
                                            groupViewModel.kickUser(
                                                currentUser.userId,
                                                currentGroup.groupId
                                            )
                                            navController.navigate("Home")

                                        }) {
                                            Text(text = "Leave")
                                        }
                                        Button(onClick = {
                                            leaveGroup = false
                                        }) { Text(text = "Cancel") }
                                            }
                                    } else {
                                        Text(
                                            text = "You really want to delete ${currentGroup.groupName}?",
                                            modifier = Modifier.padding(bottom = 8.dp),
                                            color = Color.Black,
                                        )
                                        Row(
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                        ) {
                                            Button(onClick = {
                                                groupViewModel.deleteGroup(currentGroup.groupId)
                                                navController.navigate("Home")
                                                deleteGroup = false
                                            }) {
                                                Text(text = "Delete")
                                            }
                                            Button(onClick = {
                                                deleteGroup = false
                                            }) { Text(text = "Cancel") }
                                        }
                                    }
                                }

                            }
                        }
                    }
                }
            }
        }
    }
}

