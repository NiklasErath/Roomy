package com.example.roomy.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.roomy.db.GroupRepository
import com.example.roomy.db.UserRepository
import com.example.roomy.ui.Screens
import com.example.roomy.ui.ViewModels.GroupViewModel
import com.example.roomy.ui.ViewModels.GroupViewModelFactory
import com.example.roomy.ui.ViewModels.UserViewModel
import com.example.roomy.ui.ViewModels.UserViewModelFactory

@Composable
fun Groups(
    navController: NavController,
    groupRepository: GroupRepository,
    userRepository: UserRepository
){

    val viewModel: GroupViewModel = viewModel(
        factory = GroupViewModelFactory(groupRepository)
    )

    val userViewModel: UserViewModel = viewModel(
        factory = UserViewModelFactory(userRepository)
    )

    val currentUserId = userViewModel.currentUserSession.collectAsState().value.userId

    LaunchedEffect(currentUserId) {
        viewModel.getGroupsByUserId(currentUserId)
    }

    Column (
        Modifier.fillMaxSize()
    ){
        Text(text="Groups Page")
        Button(onClick = {navController.navigate(Screens.Home.name)}) {
            Text(text="Select and enter Group")
        }
    }


}