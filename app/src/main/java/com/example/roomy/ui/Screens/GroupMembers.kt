package com.example.roomy.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.roomy.R
import com.example.roomy.ui.Composables.UserProfileCircle
import com.example.roomy.ui.States.GroupState
import com.example.roomy.ui.ViewModels.GroupViewModel
import com.example.roomy.ui.ViewModels.UserViewModel


@SuppressLint("SuspiciousIndentation")
@Composable
fun GroupMembers(
    navController: NavController,
    groupViewModel: GroupViewModel,
    userViewModel: UserViewModel,
    currentGroup: GroupState

) {
    val context = LocalContext.current
    val currentUser by userViewModel.loggedInUser.collectAsState()
    var usernameAdd by remember { mutableStateOf("") }

    val circleColors = listOf(
        MaterialTheme.colorScheme.secondary,
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.surfaceContainer,
        )

    Column(Modifier.padding(top=20.dp), horizontalAlignment = Alignment.Start) {

        Column(modifier = Modifier.padding(horizontal = 20.dp).fillMaxWidth()) {

            CustomOutlinedTextField(
                value = usernameAdd,
                onValueChange = { newValue -> usernameAdd = newValue },
                label = { Text(text = "Add a new Member to the Group") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add",
                        Modifier.clickable {
                            if (usernameAdd.isBlank()) {
                                groupViewModel.setGroupError("Please enter a username")
                            } else {
                                groupViewModel.addMemberByNameToGroup(
                                    usernameAdd,
                                    currentGroup.groupId
                                )
                                usernameAdd = ""
                            }

                        }
                    )
                },
            )

            if (currentUser.userId == currentGroup.creatorId) {
                Button(onClick = {
                    groupViewModel.deleteGroup(currentGroup.groupId)
                    navController.navigate("Home")
                }) {
                    Text(text = "Delete group")
                }
            } else {
                Button(onClick = {
                    groupViewModel.kickUser(currentUser.userId, currentGroup.groupId)
                    navController.navigate("Home")

                }) {
                    Text(text = "Leave group")
                }
            }
        }





        LazyColumn(
            modifier = Modifier.padding(top=20.dp)
        ){
            itemsIndexed(currentGroup.groupMembers) { index: Int, memberInformation ->


                val userCircleColor = circleColors[index % circleColors.size]


                Row(
                    Modifier
                        .fillMaxWidth()
                        .background(colorResource(id = R.color.dimBackground))
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row {
                        UserProfileCircle(username = memberInformation.username, circleColor = userCircleColor, circleSize = 50.dp )
                        Spacer(Modifier.width(20.dp))
                        Column {

                            Text(text = memberInformation.username, fontSize = integerResource(id = R.integer.heading3).sp )
                            Text(text = memberInformation.email,)
                        }

                    }
                    if (memberInformation.id != currentUser.userId) {
                        Button(
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
                            modifier = Modifier
                                .defaultMinSize(minWidth = 1.dp, minHeight = 1.dp),
                            colors = ButtonDefaults.buttonColors(
                                MaterialTheme.colorScheme.error


                            ),
                            onClick = {
                                groupViewModel.kickUser(
                                    memberInformation.id,
                                    currentGroup.groupId
                                )
                            }) {
                            Text(text = "Kick Member", color = Color.White)
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
            }
        }

    }



}


