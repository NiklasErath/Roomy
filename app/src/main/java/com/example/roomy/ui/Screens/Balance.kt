package com.example.roomy.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.roomy.db.BalanceRepository
import com.example.roomy.ui.States.GroupMembersUiState
import com.example.roomy.ui.ViewModels.BalanceViewModel
import com.example.roomy.ui.ViewModels.GroupViewModel
import com.example.roomy.ui.ViewModels.UserViewModel

@Composable
fun Balance(
    navController: NavController,
    balanceRepository: BalanceRepository,
    balanceViewModel: BalanceViewModel,
    groupViewModel: GroupViewModel,
    userViewModel: UserViewModel
) {

    val currentGroup by groupViewModel.currentGroup.collectAsState()
    val currentGroupIdInt: Int = currentGroup.id?.let { it } ?: 0


    val groupMemberInformation by groupViewModel.groupMembers.collectAsState(
        initial = GroupMembersUiState(
            emptyList()
        )
    )

    val currentUser by userViewModel.loggedInUser.collectAsState()

    LaunchedEffect(Unit) {

        currentGroup.id?.let { groupViewModel.getGroupMembers(it) }
        balanceViewModel.getUserBalance(currentGroupIdInt, currentUser.userId)

    }


    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {

        Text(text = "BALANCE LOADING .... ",
            modifier = Modifier.fillMaxWidth(), fontWeight = FontWeight.Bold)
        Text(text = "Coming soon ... ")
        LazyColumn {
            itemsIndexed(balanceViewModel.balance.value.userBalanceLent) { index: Int, Balance ->
                OutlinedCard(modifier = Modifier.padding(12.dp)) {
                    Text(text = "${Balance.userLent} lent ${Balance.userOwes}")
                }
            }
            itemsIndexed(balanceViewModel.balance.value.userBalanceOwes) { index: Int, Balance ->
                OutlinedCard(modifier = Modifier.padding(12.dp)) {
                    Text(text = "${Balance.userOwes} owes ${Balance.userLent}")
                }
            }
        }
    }


}