package com.example.roomy.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.roomy.db.GroupRepository
import com.example.roomy.db.ItemRepository
import com.example.roomy.db.NetworkConnection
import com.example.roomy.db.UserRepository
import com.example.roomy.ui.ViewModels.GroupViewModel
import com.example.roomy.ui.Factory.GroupViewModelFactory
import com.example.roomy.ui.Factory.ItemViewModelFactory
import com.example.roomy.ui.ViewModels.UserViewModel
import com.example.roomy.ui.Factory.UserViewModelFactory
import com.example.roomy.ui.ViewModels.ItemViewModel


enum class Screens(val route: String) {
    Login("Login"),
    Register("Register"),
    Home("Home"),
    Groups("Groups"),
    Balance("Balance"),
    Profile("Profile"),
    GroupMembers("GroupMembers")
}

@Composable
fun RoomyApp(
    modifier: Modifier = Modifier,

    ) {

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val userRepository = UserRepository()
    val groupRepository = GroupRepository()
    val itemRepository = ItemRepository()

    val networkConnection = NetworkConnection()


    val currentDestination = navBackStackEntry?.destination?.route
    val displayBottomBarAndHeader = when (currentDestination) {
        Screens.Login.name, Screens.Register.name, Screens.Home.name -> false
        else -> true
    }

    val groupViewModel: GroupViewModel = viewModel(
        factory = GroupViewModelFactory(groupRepository, userRepository)
    )

    val userViewModel: UserViewModel = viewModel(
        factory = UserViewModelFactory(userRepository)
    )

    val itemViewModel: ItemViewModel = viewModel(
        factory = ItemViewModelFactory(itemRepository)
    )


    if (displayBottomBarAndHeader) {

        Scaffold(

            bottomBar = {

                BottomNavigationBar(navController, currentDestination)

            },

            topBar = {

                Header(navController, currentDestination, groupViewModel)


            },


            modifier = Modifier.fillMaxSize(),

            ) { innerPadding ->
            AppNavHost(
                navController,
                Modifier.padding(innerPadding).padding(horizontal = 0.dp),
                userViewModel,
                groupViewModel,
                itemViewModel,
                networkConnection


            )

        }


    } else {

        Scaffold(

            modifier = Modifier.fillMaxSize(),

            ) { innerPadding ->
            AppNavHost(
                navController,
                Modifier.padding(innerPadding).padding(horizontal = 20.dp),
                userViewModel,
                groupViewModel,
                itemViewModel,
                networkConnection


            )

        }


    }
}


@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    userViewModel: UserViewModel,
    groupViewModel: GroupViewModel,
    itemViewModel: ItemViewModel,
    networkConnection: NetworkConnection
) {
    NavHost(
        navController = navController,
        startDestination = Screens.Login.route,
        modifier = modifier
//                .padding(horizontal = if (currentDestination != Screens.Login.name) 0.dp else 20.dp),
//            .padding(horizontal = 20.dp),
    ) {


        composable(
            Screens.Login.route,
        ) {
            Box {
                Login(
                    userViewModel,
                    navController,

                    )
            }
        }

        composable(
            Screens.Register.route,
        ) {
            Box {
                Register(
                    userViewModel,
                    navController
                )
            }
        }

        composable(
            Screens.Home.route,
        ) {
            Box {
                Home(

                    navController,
                    groupViewModel,
                    userViewModel,
                    networkConnection
                )
            }
        }

        composable(
            Screens.Groups.route,
        ) {

            val previousBackSTackEntry = navController.previousBackStackEntry
            val previousScreen = previousBackSTackEntry?.destination?.route ?: "No previous entry"

            Box {
                Group(
                    groupViewModel,
                    itemViewModel,
                    navController,
                    previousScreen
                )
            }
        }

        composable(
            Screens.Balance.route,
        ) {
            Box {
                Balance(
                    navController
                )
            }
        }

        composable(
            Screens.Profile.route,
        ) {
            Box {
                Profile(
                    userViewModel,
                    navController
                )
            }
        }
        composable(
            Screens.GroupMembers.route
        ) {
            Box {
                GroupMembers(
                    navController,
                    groupViewModel
                )
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
            selected = (currentDestination == Screens.Groups.route),
            onClick = { navController.navigate(Screens.Groups.route) },
            icon = { Icon(imageVector = Icons.Filled.CheckCircle, contentDescription = "Home") },
            label = { Text("Lists") }
        )
        NavigationBarItem(
            selected = (currentDestination == Screens.Balance.route),
            onClick = { navController.navigate(Screens.Balance.route) },
            icon = { Icon(imageVector = Icons.Filled.Refresh, contentDescription = "Overview") },
            label = { Text("Balance") }
        )
        NavigationBarItem(
            selected = (currentDestination == Screens.Profile.route),
            onClick = { navController.navigate(Screens.Profile.route) },
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
    currentDestination: String?,
    groupViewModel: GroupViewModel
) {

    val currentGroup by groupViewModel.currentGroup.collectAsState()


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
                IconButton(onClick = { navController.navigate(Screens.Home.name) }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Group",
                    )
                }
                Text(
                    text = currentGroup.name,
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