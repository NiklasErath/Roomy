package com.example.roomy.ui

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.roomy.db.GroupRepository
import com.example.roomy.db.Supabase.supabase
import com.example.roomy.db.UserRepository
import com.example.roomy.ui.ViewModels.GroupViewModel
import com.example.roomy.ui.ViewModels.GroupViewModelFactory
import com.example.roomy.ui.ViewModels.UserViewModel
import com.example.roomy.ui.ViewModels.UserViewModelFactory

enum class Screens {
    Login, Register, Groups, Home, Balance, Profile
}

@Composable
fun RoomyApp(
    modifier: Modifier = Modifier,

    ) {

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val userRepository = UserRepository()
    val groupRepository = GroupRepository()


    val currentDestination = navBackStackEntry?.destination?.route
    val displayBottomBarAndHeader = when (currentDestination) {
        Screens.Login.name, Screens.Register.name, Screens.Groups.name -> false
        else -> true
    }

    val groupViewModel: GroupViewModel = viewModel(
        factory = GroupViewModelFactory(groupRepository)
    )

    val userViewModel: UserViewModel = viewModel(
        factory = UserViewModelFactory(userRepository)
    )


    Scaffold(

        bottomBar = {
            if (displayBottomBarAndHeader) {
                BottomNavigationBar(navController, currentDestination)
            }
        },

        topBar = {
            if (displayBottomBarAndHeader) {
                Header(navController, currentDestination)
            }


        },


        modifier = Modifier.fillMaxSize(),

        ) { innerPadding ->
        NavHost(
            navController, startDestination = Screens.Login.name,
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = if (currentDestination == Screens.Home.name) 0.dp else 20.dp),

            )
        {

            composable(
                Screens.Login.name,
            ) {
                Box {
                    Login(
                        userViewModel,
                        navController,

                        )
                }
            }

            composable(
                Screens.Register.name,
            ) {
                Box {
                    Register(
                        userViewModel,
                        navController
                    )
                }
            }

            composable(
                Screens.Groups.name,
            ) {
                Box {
                    Groups(

                        navController,
                        groupViewModel,
                        userViewModel
                    )
                }
            }

            composable(
                Screens.Home.name,
            ) {
                Box {
                    Home(

                        navController
                    )
                }
            }

            composable(
                Screens.Balance.name,
            ) {
                Box {
                    Balance(
                        navController
                    )
                }
            }

            composable(
                Screens.Profile.name,
            ) {
                Box {
                    Profile(
                        navController
                    )
                }
            }
        }


    }

}


@Composable
fun BottomNavigationBar(
    navController: NavController,
    currentDestination: String?
) {

    NavigationBar {
        NavigationBarItem(
            selected = (currentDestination == Screens.Home.name),
            onClick = { navController.navigate(Screens.Home.name) },
            icon = { Icon(imageVector = Icons.Filled.CheckCircle, contentDescription = "Home") },
            label = { Text("Lists") }
        )
        NavigationBarItem(
            selected = (currentDestination == Screens.Balance.name),
            onClick = { navController.navigate(Screens.Balance.name) },
            icon = { Icon(imageVector = Icons.Filled.Refresh, contentDescription = "Overview") },
            label = { Text("Balance") }
        )
        NavigationBarItem(
            selected = (currentDestination == Screens.Profile.name),
            onClick = { navController.navigate(Screens.Profile.name) },
            icon = {
                Icon(
                    imageVector = Icons.Filled.AccountCircle,
                    contentDescription = "Overview"
                )
            },
            label = { Text("Profile") }
        )
    }
}

@Composable
fun Header(
    navController: NavController,
    currentDestination: String?
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 40.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { navController.navigate(Screens.Groups.name) }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Group",
                    )
                }
                Text(
                    text = currentDestination ?: "No Destination",
                    fontSize = 20.sp,
                )
                IconButton(onClick = {
//                Add Members screen
                }) {
                    Icon(
                        imageVector = Icons.Filled.AccountCircle,
                        contentDescription = "Profile",
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        }
    }
}