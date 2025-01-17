package com.example.roomy.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

import com.example.roomy.ui.Composables.UserProfileCircle
import com.example.roomy.ui.ViewModels.UserViewModel

@Composable
fun Profile(
    userViewModel: UserViewModel,
    navController: NavController
) {

    val currentUser by userViewModel.loggedInUser.collectAsState()

    var username by remember { mutableStateOf("") }
    var theme by remember { mutableStateOf("") }

    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Profile/Settings Page")
        Column(modifier = Modifier.padding(top = 20.dp)) {
            UserProfileCircle(currentUser.username, 100.dp, Color.Gray)
        }
        Column(
            Modifier
                .fillMaxSize()
                .padding(top = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Name: ${currentUser.username}")
            Text(text = "ID: ${currentUser.userId}")
            Text(text = "Email: ${currentUser.email}")


            Column(modifier = Modifier.padding(top = 20.dp)) {
                OutlinedTextField(
                    value = username,
                    onValueChange = { newValue -> username = newValue },
                    Modifier.fillMaxWidth(),
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = "username",
                            modifier = Modifier
                        )
                    }


                )
                Button(onClick = { userViewModel.updateUserName(username, currentUser.userId) }) {
                    Text(text = "Change username")
                }
                Button(onClick = {
                    if (theme == "dark") {
                        theme = "white"
                    } else {
                        theme = "dark"
                    }
                }) {
                    if (theme == "dark") {
                        Text(text = "Change to White mode")
                    } else {
                        Text(text = "Change to Dark mode")
                    }
                }

                Button(onClick = { navController.navigate(Screens.GroupMembers.route) }) {
                    Text(text = "Manage Members")
                }

                Button(onClick = {
                    userViewModel.logout() { loggedOut ->
                        if (!loggedOut) {
                            navController.navigate(Screens.Login.route)
                        }
                    }
                }) {
                    Text(text = "Logout")
                }
            }
        }
    }


}