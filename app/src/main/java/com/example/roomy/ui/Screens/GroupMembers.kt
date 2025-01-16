package com.example.roomy.ui

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


@Composable
fun GroupMembers(
    navController: NavController,
    groupViewModel: GroupViewModel

) {
    val currentGroup by groupViewModel.currentGroup.collectAsState()

    val currentGroupIdInt: Int = currentGroup.id?.let { it } ?: 0

    val errorMessage by groupViewModel.error.collectAsState()

    val groupMemberInformation by groupViewModel.groupMembers.collectAsState(
        initial = GroupMembersUiState(
            emptyList()
        )
    )

    var usernameAdd by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {

        currentGroup.id?.let { groupViewModel.getGroupMembers(it) }

    }
 // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    Column {
        LazyColumn {
            itemsIndexed(groupMemberInformation.memberInformation) { index: Int, memberInformation ->
                OutlinedCard(modifier = Modifier
                    .padding(12.dp)) {
                    Row() {
                        Text(text = "${memberInformation.username}")
                        Button(onClick = {groupViewModel.kickUser("${memberInformation.id}", currentGroupIdInt)}) {
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
            if (errorMessage.message.isNotEmpty()){
            Text("User not found")
            }
            Button(
                onClick = {
                    if (usernameAdd.isBlank()){
                        //groupViewModel.showErrorMessage("Please enter a username.")
                    }else {
                    groupViewModel.addMemberByNameToGroup(usernameAdd, currentGroupIdInt)
                        usernameAdd = ""
                        }
                },
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text(text = "Add")
            }
        }
        // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    }

}