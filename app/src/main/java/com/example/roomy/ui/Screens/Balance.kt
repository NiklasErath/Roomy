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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.roomy.db.BalanceRepository
import com.example.roomy.ui.States.GroupMembersUiState
import com.example.roomy.ui.ViewModels.BalanceViewModel
import com.example.roomy.ui.ViewModels.GroupViewModel
import com.example.roomy.ui.ViewModels.UserViewModel
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun Balance(
    navController: NavController,
    balanceRepository: BalanceRepository,
    balanceViewModel: BalanceViewModel,
    groupViewModel: GroupViewModel,
    userViewModel: UserViewModel,
) {

    val currentGroup by groupViewModel.currentGroup.collectAsState()
    val currentGroupIdInt: Int = currentGroup.id?.let { it } ?: 0

    var newPayment by remember { mutableStateOf("") }
    var itemsBought by remember { mutableStateOf("") }

    val paymentAmount = newPayment.toIntOrNull() ?: 0

    val groupMemberInformation by groupViewModel.groupMembers.collectAsState(
        initial = GroupMembersUiState(
            emptyList()
        )
    )

    val payments by balanceViewModel.payments.collectAsState()

    val currentUser by userViewModel.loggedInUser.collectAsState()

    LaunchedEffect(Unit) {

        currentGroup.id?.let { groupViewModel.getGroupMembers(it) }
        balanceViewModel.getBalanceByGroupId(currentGroupIdInt)
        balanceViewModel.getPaymentsByGroupId(currentGroupIdInt)
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        Column {

            Text(
                text = "BALANCE LOADING .... ",
                modifier = Modifier.fillMaxWidth(), fontWeight = FontWeight.Bold
            )
            Text(text = "Coming soon ... ")
            LazyColumn {
                itemsIndexed(balanceViewModel.balance.value.userBalance) { index: Int, Balance ->
                    val owedBy =
                        groupMemberInformation.memberInformation.find { it.id == Balance.owedBy }?.username
                            ?: "Unknown User"
                    val owedTo =
                        groupMemberInformation.memberInformation.find { it.id == Balance.owedTo }?.username
                            ?: "Unknown User"
                    if (Balance.amount != 0) {
                        OutlinedCard(modifier = Modifier.padding(12.dp)) {
                            Text(text = "$owedBy lent $owedTo ${Balance.amount} $")
                        }
                    }
                }
                itemsIndexed(balanceViewModel.balance.value.userBalance) { index: Int, Balance ->
                    val userNameLent =
                        groupMemberInformation.memberInformation.find { it.id == Balance.owedBy }?.username
                            ?: "Unknown User"
                    val userNameOwes =
                        groupMemberInformation.memberInformation.find { it.id == Balance.owedTo }?.username
                            ?: "Unknown User"
                    if (Balance.amount != 0) {
                        OutlinedCard(modifier = Modifier.padding(12.dp)) {
                            Text(text = "$userNameOwes owes $userNameLent ${Balance.amount}")
                        }
                    }
                }
            }

            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {

                Text(
                    text = "Payments",
                    modifier = Modifier.fillMaxWidth(), fontWeight = FontWeight.Bold
                )
                LazyColumn {
                    itemsIndexed(payments.payments) { index: Int, Payment ->
                        val userName =
                            groupMemberInformation.memberInformation.find { it.id == Payment.paidBy }?.username
                                ?: "Unknown User"
                        OutlinedCard(modifier = Modifier.padding(12.dp)) {
                            Text(text = "$userName bought ${Payment.items} for ${Payment.amount} $")

                        }
                    }

                }
            }
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Text(text = "Add new Payment")
                OutlinedTextField(
                    value = newPayment,
                    onValueChange = { newValue -> newPayment = newValue },
                    placeholder = { Text(text = "Amount") },
                )

                OutlinedTextField(
                    value = itemsBought,
                    onValueChange = { newValue -> itemsBought = newValue },
                    placeholder = { Text(text = "Items you bought") },
                )
                Button(onClick = {
                    if (paymentAmount != 0) {
                        balanceViewModel.addPayment(
                            currentUser.userId,
                            currentGroupIdInt,
                            paymentAmount,
                            itemsBought
                        )
                        newPayment = ""
                        itemsBought = ""
                    } else {
                        newPayment = ""
                        itemsBought = ""
                    }
                }) {
                    Text(text = "Add new Payment")
                }
            }
        }
    }
}


