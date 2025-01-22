package com.example.roomy.ui.Composables

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.example.roomy.ui.Screens


@Composable
fun BottomNavigationBar(
    navController: NavController,
    currentDestination: String?
) {

    NavigationBar(containerColor = MaterialTheme.colorScheme.background){
        NavigationBarItem(
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                unselectedIconColor = Color.White,
                indicatorColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = Color.White,
                unselectedTextColor = Color.White
            ),


            selected = (currentDestination == Screens.Groups.name),
            onClick = { navController.navigate(Screens.Groups.name) },
            icon = { Icon(imageVector = Icons.Filled.CheckCircle, contentDescription = "Home") },
            label = { Text("Lists") }
        )
        NavigationBarItem(
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                unselectedIconColor = Color.White,
                indicatorColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = Color.White,
                unselectedTextColor = Color.White
            ),
            selected = (currentDestination == Screens.Balance.name),
            onClick = { navController.navigate(Screens.Balance.name) },
            icon = { Icon(imageVector = Icons.Filled.Refresh, contentDescription = "Overview") },
            label = { Text("Balance") }
        )
        NavigationBarItem(
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                unselectedIconColor = Color.White,
                indicatorColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = Color.White,
                unselectedTextColor = Color.White
            ),
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


