package com.example.roomy.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
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
import com.example.roomy.ui.Composables.UserProfileCircle
import com.example.roomy.ui.Composables.UserProfileCirclesStacked
import com.example.roomy.ui.ViewModels.GroupViewModel
import com.example.roomy.ui.Factory.GroupViewModelFactory
import com.example.roomy.ui.Factory.ItemViewModelFactory
import com.example.roomy.ui.ViewModels.UserViewModel
import com.example.roomy.ui.Factory.UserViewModelFactory
import com.example.roomy.ui.States.GroupMembersUiState
import com.example.roomy.ui.ViewModels.ItemViewModel
import androidx.compose.ui.platform.LocalContext
import com.example.roomy.db.BalanceRepository
import com.example.roomy.ui.Factory.BalanceViewModelFactory
import com.example.roomy.ui.ViewModels.BalanceViewModel


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
    val balanceRepository = BalanceRepository()

    val networkConnection = NetworkConnection()
    val context = LocalContext.current


    val currentDestination = navBackStackEntry?.destination?.route
    val displayBottomBarAndHeader = when (currentDestination) {
        Screens.Login.name, Screens.Register.name, Screens.Home.name -> false
        else -> true
    }

    if (!networkConnection.isNetworkAvailable(context)) {
        Toast.makeText(context, "No internet connection. Please try again.", Toast.LENGTH_LONG)
            .show()
    }


    val userViewModel: UserViewModel = viewModel(
        factory = UserViewModelFactory(userRepository)
    )

    val itemViewModel: ItemViewModel = viewModel(
        factory = ItemViewModelFactory(itemRepository)
    )


    val groupViewModel: GroupViewModel = viewModel(
        factory = GroupViewModelFactory(groupRepository, userRepository, itemViewModel )
    )

    val balanceViewModel: BalanceViewModel = viewModel(
        factory = BalanceViewModelFactory(balanceRepository)
    )

    val userErrorMessage by userViewModel.userError.collectAsState()
    val itemErrorMessage by itemViewModel.itemError.collectAsState()
    val groupErrorMessage by groupViewModel.error.collectAsState()

    if (userErrorMessage.message.isNotEmpty()) {
        Toast.makeText(context, userErrorMessage.message, Toast.LENGTH_LONG)
            .show()
            userViewModel.clearUserError()
    }

    if (itemErrorMessage.message.isNotEmpty()) {
        Toast.makeText(context, itemErrorMessage.message, Toast.LENGTH_LONG)
            .show()
        itemViewModel.clearItemError()
    }

    if (groupErrorMessage.message.isNotEmpty()) {
        Toast.makeText(context, groupErrorMessage.message, Toast.LENGTH_LONG)
            .show()
        groupViewModel.clearGroupError()
    }


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
                Modifier
                    .padding(innerPadding)
                    .padding(horizontal = 0.dp),
                userViewModel,
                groupViewModel,
                itemViewModel,
                networkConnection,
                balanceRepository,
                balanceViewModel


                )

        }


    } else {

        Scaffold(

            modifier = Modifier.fillMaxSize(),

            ) { innerPadding ->
            AppNavHost(
                navController,
                Modifier
                    .padding(innerPadding)
                    .padding(horizontal = 20.dp),
                userViewModel,
                groupViewModel,
                itemViewModel,
                networkConnection,
                balanceRepository,
                balanceViewModel


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
    networkConnection: NetworkConnection,
    balanceRepository: BalanceRepository,
    balanceViewModel: BalanceViewModel
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
                    itemViewModel,

                    networkConnection,
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
                    navController,
                    balanceRepository,
                    balanceViewModel,
                    groupViewModel,
                    userViewModel
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
                    groupViewModel,
                    userViewModel
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

    val groupMemberInformation by groupViewModel.groupMembers.collectAsState(
        initial = GroupMembersUiState(
            emptyList()
        )
    )



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
                Column(
                    Modifier.weight(1f),
                    horizontalAlignment = Alignment.Start
                ) {
                    IconButton(
                        onClick = { navController.navigate(Screens.Home.name) },

                        ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Group",
                        )
                    }

                }


                Column(
                    Modifier.weight(2f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = currentGroup.name,
                        fontSize = 20.sp,
                    )
                }

                Row (Modifier.weight(1f).clickable { navController.navigate(Screens.GroupMembers.route) }, verticalAlignment = Alignment.CenterVertically){

                    UserProfileCirclesStacked(groupMemberInformation)


                }



                }

        }
    }
}