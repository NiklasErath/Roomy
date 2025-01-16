package com.example.roomy.ui

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.roomy.ui.States.GroupMembersUiState
import com.example.roomy.ui.ViewModels.GroupViewModel
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.example.roomy.ui.ViewModels.UserViewModel


@SuppressLint("SuspiciousIndentation")
@Composable
fun GroupMembers(
    navController: NavController,
    groupViewModel: GroupViewModel,
    userViewModel: UserViewModel

) {
    val currentGroup by groupViewModel.currentGroup.collectAsState()

    val currentGroupIdInt: Int = currentGroup.id?.let { it } ?: 0

    val groupMemberInformation by groupViewModel.groupMembers.collectAsState(
        initial = GroupMembersUiState(
            emptyList()
        )
    )

    val context = LocalContext.current

    val currentUser by userViewModel.loggedInUser.collectAsState()

    var usernameAdd by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {

        currentGroup.id?.let { groupViewModel.getGroupMembers(it) }

    }
    Column {
        LazyColumn {
            itemsIndexed(groupMemberInformation.memberInformation) { index: Int, memberInformation ->
                OutlinedCard(
                    modifier = Modifier
                        .padding(12.dp)
                ) {
                    Row() {
                        Text(text = "${memberInformation.username}")
                        Button(onClick = {
                            groupViewModel.kickUser(
                                "${memberInformation.id}",
                                currentGroupIdInt
                            )
                        }) {
                            Text(text = "Kick Member")
                        }
                    }

                }
            }
        }
        Column(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = usernameAdd,
                onValueChange = { newValue -> usernameAdd = newValue },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.AddCircle,
                        contentDescription = "username"
                    )
                }
            )
            Button(
                onClick = {
                    if (usernameAdd.isBlank()) {
                        Toast.makeText(context, "Please enter a username", Toast.LENGTH_LONG).show()
                    } else {
                        groupViewModel.addMemberByNameToGroup(usernameAdd, currentGroupIdInt)
                        usernameAdd = ""
                    }
                },
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text(text = "Add")
            }
            if (currentUser.userId == currentGroup.creatorId) {
                Button(onClick = {
                    groupViewModel.deleteGroup(currentGroupIdInt)
                }) {
                    Text(text = "Delete group")
                }
            } else {
                Button(onClick = {
                    groupViewModel.kickUser(currentUser.userId, currentGroupIdInt)

                }) {
                    Text(text = "Leave group")
                }
            }
        }

    }
}

