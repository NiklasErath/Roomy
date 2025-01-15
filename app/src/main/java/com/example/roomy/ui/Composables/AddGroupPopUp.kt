package com.example.roomy.ui.Composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.roomy.R
import com.example.roomy.ui.ViewModels.GroupViewModel


@Composable
fun AddGroupPopUp(
    onDismissRequest: ()-> Unit,
    groupViewModel: GroupViewModel,
    currentUserId: String,
) {

    var newGroupName by remember { mutableStateOf("Home") }
    var newMemberEmail by remember { mutableStateOf("") }

    Dialog(onDismissRequest = { onDismissRequest() }) {

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
                    Modifier.clickable { onDismissRequest() })

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
                value = newMemberEmail,
                onValueChange = { newValue -> newMemberEmail = newValue },
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

//                    Add new member, if successfull make a list with added users emails/usernames under this textfield - just save locally from input to display
//                    If not succesfull, popup with error message ...
//                    Ensure Users can only be added once

                        })
                },


                )

            Button(
                onClick = { onDismissRequest()
                    //groupViewModel.createNewGroup(newGroupName, currentUserId, a)
                    onDismissRequest()                },
            ) {
                Text(text = "Add Group")
            }

/*
            if (addGroupPopUp) {
                AddGroupPopUp(
                    onDismissRequest = {addGroupPopUp = false},
                    groupViewModel = groupViewModel,
                    currentUserId = currentUserId
                )

            }

 */
        }
    }
}