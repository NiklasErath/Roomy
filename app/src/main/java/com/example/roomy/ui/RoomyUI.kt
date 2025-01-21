package com.example.roomy.ui

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.roomy.ui.Composables.UserProfileCirclesStacked
import com.example.roomy.ui.ViewModels.GroupViewModel
import com.example.roomy.ui.Factory.GroupViewModelFactory
import com.example.roomy.ui.Factory.ItemViewModelFactory
import com.example.roomy.ui.ViewModels.UserViewModel
import com.example.roomy.ui.Factory.UserViewModelFactory
import com.example.roomy.ui.States.GroupMembersUiState
import com.example.roomy.ui.ViewModels.ItemViewModel
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import com.example.roomy.db.BalanceRepository
import com.example.roomy.db.PaymentsRepository
import com.example.roomy.db.Supabase.UserSessionManager
import com.example.roomy.ui.Composables.Snackbar
import com.example.roomy.ui.Factory.BalanceViewModelFactory
import com.example.roomy.ui.Factory.StateViewModelFactory
import com.example.roomy.ui.States.newGroupState
import com.example.roomy.ui.ViewModels.BalanceViewModel
import com.example.roomy.ui.ViewModels.StateViewModel
import kotlinx.coroutines.launch


enum class Screens(val route: String) {
    Login("Login"),
    Register("Register"),
    Home("Home"),
    Groups("Groups"),
    Balance("Balance"),
    Profile("Profile"),
    GroupMembers("GroupMembers"),
    RecipeSuggestion("RecipeSuggestion"),
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
    val paymentsRepository = PaymentsRepository()

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


//    Initialize StateViewModel which hold our global UI States and pass it to other ViewModels for updates
    val stateViewModel: StateViewModel = viewModel(
        factory = StateViewModelFactory()
    )


    val userViewModel: UserViewModel = viewModel(
        factory = UserViewModelFactory(userRepository, stateViewModel)
    )




//    Check currentUser session

    val startDestination: String

    if (UserSessionManager.getSessionToken(context) != null){
        userViewModel.logInAndFetchInformationWithSessionToken(context)
        startDestination = Screens.Home.name

    }else{
        startDestination = Screens.Login.name
    }


    val itemViewModel: ItemViewModel = viewModel(
        factory = ItemViewModelFactory(itemRepository, stateViewModel)
    )


    val groupViewModel: GroupViewModel = viewModel(
        factory = GroupViewModelFactory(
            groupRepository,
            userRepository,
            itemViewModel,
            balanceRepository,
            paymentsRepository,
            stateViewModel
            )
    )









    val balanceViewModel: BalanceViewModel = viewModel(
        factory = BalanceViewModelFactory(balanceRepository, paymentsRepository, groupRepository, stateViewModel)
    )

    val currentGroupInformation by groupViewModel.currentGroupInformation.collectAsState()

    // Step 2: Collect all groups state
    val allGroupsState by stateViewModel.allGroupsState.collectAsState(
        initial = emptyList()
    )

    // Step 3: Find the corresponding GroupState based on the currentGroup.id
    val currentGroup by remember(currentGroupInformation.id, allGroupsState) {
        derivedStateOf {
            // Find the corresponding GroupState based on the currentGroup.id
            allGroupsState.find { it.groupId == currentGroupInformation.id }
                ?: newGroupState(
                    groupId = -1,
                    groupName = "Unknown",
                    creatorId = "Unknown",
                    groupMembers = emptyList(),
                    shoppingListItems = emptyList(),
                    inventoryItems = emptyList(),
                    itemCount = 0
                )  // Fallback default group state
        }
    }

//    Snackbar
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    var snackbarData : String


    val userErrorMessage by userViewModel.userError.collectAsState()
    val itemErrorMessage by itemViewModel.itemError.collectAsState()
    val groupErrorMessage by groupViewModel.error.collectAsState()
    val balanceErrorMessage by balanceViewModel.balanceError.collectAsState()



    if (displayBottomBarAndHeader) {

        Scaffold(

            snackbarHost = {
                Column(
                    modifier = Modifier.fillMaxSize().padding(vertical=100.dp),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    SnackbarHost(
                        hostState = snackbarHostState,
                        snackbar = { snackbarData -> Snackbar(snackbarData) },
//                            modifier = Modifier
//                                .padding(top = 16.dp) // Add some padding from the top edge
                    )
                }
            },

            bottomBar = {

                BottomNavigationBar(navController, currentDestination)

            },

            topBar = {

                Header(navController, currentDestination, currentGroup)


            },


            modifier = Modifier.fillMaxSize(),

            ) { innerPadding ->


            LaunchedEffect(userErrorMessage, itemErrorMessage, groupErrorMessage, balanceErrorMessage) {
                when {
                    userErrorMessage.message.isNotEmpty() -> {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(
                                message = userErrorMessage.message // Pass message directly
                            )
                            userViewModel.clearUserError()
                        }
                    }
                    itemErrorMessage.message.isNotEmpty() -> {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(
                                message = itemErrorMessage.message // Pass message directly
                            )
                            itemViewModel.clearItemError()
                        }
                    }
                    groupErrorMessage.message.isNotEmpty() -> {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(
                                message = groupErrorMessage.message // Pass message directly
                            )
                            groupViewModel.clearGroupError()
                        }
                    }

                    balanceErrorMessage.message.isNotEmpty() -> {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(
                                message = balanceErrorMessage.message // Pass message directly
                            )
                            balanceViewModel.clearBalanceError()
                        }
                    }
                }
            }


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
                balanceViewModel,
                currentGroup,
                allGroupsState,
                startDestination


            )

        }


    } else {

        Scaffold(

            snackbarHost = {
                Column(
                    modifier = Modifier.fillMaxSize().padding(vertical=100.dp),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    SnackbarHost(
                        hostState = snackbarHostState,
                        snackbar = { snackbarData -> Snackbar(snackbarData) },
//                            modifier = Modifier
//                                .padding(top = 16.dp) // Add some padding from the top edge
                    )
                }
            },

            modifier = Modifier.fillMaxSize(),

            ) { innerPadding ->

            LaunchedEffect(userErrorMessage, itemErrorMessage, groupErrorMessage, balanceErrorMessage) {
                when {
                    userErrorMessage.message.isNotEmpty() -> {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(
                                message = userErrorMessage.message // Pass message directly
                            )
                            userViewModel.clearUserError()
                        }
                    }
                    itemErrorMessage.message.isNotEmpty() -> {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(
                                message = itemErrorMessage.message // Pass message directly
                            )
                            itemViewModel.clearItemError()
                        }
                    }
                    groupErrorMessage.message.isNotEmpty() -> {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(
                                message = groupErrorMessage.message // Pass message directly
                            )
                            groupViewModel.clearGroupError()
                        }
                    }
                    balanceErrorMessage.message.isNotEmpty() -> {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(
                                message = balanceErrorMessage.message // Pass message directly
                            )
                            balanceViewModel.clearBalanceError()
                        }
                    }
                }
            }


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
                balanceViewModel,
                currentGroup,
                allGroupsState,
                startDestination


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
    balanceViewModel: BalanceViewModel,
    currentGroup: newGroupState,
    allGroupsState: List<newGroupState>,
    startDestination: String
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
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
            val previousBackSTackEntry = navController.previousBackStackEntry
            val previousScreen = previousBackSTackEntry?.destination?.route ?: "No previous entry"

            Box {
                Home(

                    navController,
                    groupViewModel,
                    userViewModel,
                    itemViewModel,
                    networkConnection,
                    allGroupsState,
                    previousScreen
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
                    previousScreen,
                    currentGroup
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
                    userViewModel,
                    currentGroup
                )
            }
        }

        composable(
            Screens.Profile.route,
        ) {
            Box {
                Profile(
                    userViewModel,
                    navController,
                    currentGroup
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
                    userViewModel,
                    currentGroup
                )
            }
        }

        composable(
            Screens.RecipeSuggestion.route
        ) {
            Box {
                RecipeSuggestion(
                    navController,
                    itemViewModel,
                    currentGroup
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
    group: newGroupState) {
//    val currentGroup by groupViewModel.currentGroup.collectAsState()
//    val groupMemberInformation by groupViewModel.groupMembers.collectAsState(
//        initial = GroupMembersUiState(emptyList())
//    )

    Column(
        modifier = Modifier
            .height(100.dp)
            .fillMaxWidth()
            .padding(top = 40.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (currentDestination == "Groups") {
                    IconButton(
                        onClick = { navController.navigate(Screens.Home.name) },
                        modifier = Modifier.align(Alignment.CenterVertically)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go Back"
                        )
                    }

                    Text(
                        text = group.groupName,
                        fontSize = 20.sp,
                        modifier = Modifier
                            .weight(1f)
                            .align(Alignment.CenterVertically),
                        textAlign = TextAlign.Center
                    )

                    Row(
                        modifier = Modifier
                            .wrapContentSize()
                            .clickable { navController.navigate(Screens.GroupMembers.route) },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End
                    ) {
                        UserProfileCirclesStacked(GroupMembersUiState(group.groupMembers))
                    }
                }
                else if (currentDestination == "GroupMembers" || currentDestination == "RecipeSuggestion") {
                    IconButton(
                        onClick = { navController.navigateUp() },
                        modifier = Modifier.align(Alignment.CenterVertically)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go Back"
                        )


                    }
                    Text(
                        text = group.groupName,
                        fontSize = 20.sp,
                        modifier = Modifier
                            .weight(1f)
                            .align(Alignment.CenterVertically),
                        textAlign = TextAlign.Center
                    )

                    Row(
                        modifier = Modifier
                            .wrapContentSize()
                            .clickable { navController.navigate(Screens.GroupMembers.route) },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End
                    ) {
                        UserProfileCirclesStacked(GroupMembersUiState(group.groupMembers))
                    }
                } else {
                    Text(
                        text = group.groupName,
                        fontSize = 20.sp,
                        modifier = Modifier
                            .weight(1f)
                            .align(Alignment.CenterVertically),
                        textAlign = TextAlign.Start
                    )

                    Row(
                        modifier = Modifier
                            .wrapContentSize()
                            .clickable { navController.navigate(Screens.GroupMembers.route) },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End
                    ) {
                        UserProfileCirclesStacked(GroupMembersUiState(group.groupMembers))
                    }
                }
            }
        }
    }
}


