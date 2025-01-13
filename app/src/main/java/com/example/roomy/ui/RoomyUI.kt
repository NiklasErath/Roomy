package com.example.roomy.ui

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

enum class Screens {
    Login, Register, Groups, Home, Balance, Profile
}

@Composable
fun RoomyApp(
    modifier: Modifier = Modifier,

    ) {

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()


    Scaffold(
        bottomBar = {
            val currentDestination = navBackStackEntry?.destination?.route

            val displayBottomBar=when(currentDestination){
                Screens.Login.name, Screens.Register.name, Screens.Groups.name -> false
                else -> true
            }
            if(displayBottomBar){
            BottomNavigationBar(navController,currentDestination) }},
//        Global Padding applied here
        modifier = Modifier.fillMaxSize().padding(20.dp),
    ) { innerPadding ->
        NavHost(navController, startDestination = Screens.Login.name,
            modifier = Modifier.padding(innerPadding),
//            enterTransition = {
//                fadeIn(
//                    animationSpec = tween(
//                        300, easing = LinearEasing
//                    )
//                ) + slideIntoContainer(
//                    animationSpec = tween(300, easing = EaseIn),
//                    towards = AnimatedContentTransitionScope.SlideDirection.Start
//                )
//            },
//            exitTransition = {
//                fadeOut(
//                    animationSpec = tween(
//                        300, easing = LinearEasing
//                    )
//                ) + slideOutOfContainer(
//                    animationSpec = tween(300, easing = EaseOut),
//                    towards = AnimatedContentTransitionScope.SlideDirection.End
//                )
//            }
        )
        {

            composable(
                Screens.Login.name,
            ) {
                Box(modifier = modifier.padding(innerPadding)) {
                    Login(
                        modifier,
//                        navController
                    )
                }
            }

            composable(
                Screens.Register.name,
            ) {
                Box(modifier = Modifier.padding(innerPadding)) {
                    Groups(
                        modifier,
                        navController
                    )
                }
            }

            composable(
                Screens.Groups.name,
            ) {
                Box(modifier = Modifier.padding(innerPadding)) {
                    Groups(
                        modifier,
                        navController
                    )
                }
            }

            composable(
                Screens.Home.name,
            ) {
                Box(modifier = Modifier.padding(innerPadding)) {
                    Home(
                        modifier,
                        navController
                    )
                }
            }

            composable(
                Screens.Balance.name,
            ) {
                Box(modifier = Modifier.padding(innerPadding)) {
                    Balance(
                        modifier,
                        navController
                    )
                }
            }

            composable(
                Screens.Profile.name,
            ) {
                Box(modifier = Modifier.padding(innerPadding)) {
                    Profile(
                        modifier,
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
            onClick = { navController.navigate(Screens.Home.name)},
            icon = { Icon(imageVector = Icons.Filled.CheckCircle, contentDescription = "Home") },
            label = { Text("Lists") }
        )
        NavigationBarItem(
            selected = (currentDestination == Screens.Balance.name),
            onClick = { navController.navigate(Screens.Balance.name)},
            icon = { Icon(imageVector = Icons.Filled.Refresh, contentDescription = "Overview") },
            label = { Text("Balance") }
        )
        NavigationBarItem(
            selected = (currentDestination == Screens.Profile.name),
            onClick = { navController.navigate(Screens.Profile.name)},
            icon = { Icon(imageVector = Icons.Filled.AccountCircle, contentDescription = "Overview") },
            label = { Text("Profile") }
        )
    }
}