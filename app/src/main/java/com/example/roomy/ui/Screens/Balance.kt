package com.example.roomy.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.roomy.db.BalanceRepository
import com.example.roomy.ui.States.GroupMembersUiState
import com.example.roomy.ui.States.newGroupState
import com.example.roomy.ui.ViewModels.BalanceViewModel
import com.example.roomy.ui.ViewModels.GroupViewModel
import com.example.roomy.ui.ViewModels.StateViewModel
import com.example.roomy.ui.ViewModels.UserViewModel
import kotlinx.coroutines.flow.map

@Composable
fun Balance(
    navController: NavController,
    balanceViewModel: BalanceViewModel,
    groupViewModel: GroupViewModel,
    userViewModel: UserViewModel,
    currentGroup: newGroupState,
) {

    val currentGroupInformation by groupViewModel.currentGroupInformation.collectAsState()

    val currentGroupIdInt: Int = currentGroupInformation.id?.let { it } ?: 0

    var newPayment by remember { mutableStateOf("") }
    var itemsBought by remember { mutableStateOf("") }

    val paymentAmount = newPayment.toIntOrNull() ?: 0

    val payments = currentGroup.payments

    val balanceUi = balanceViewModel.balance.collectAsState()

    val currentUser by userViewModel.loggedInUser.collectAsState()

    var addPayment by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        balanceViewModel.getBalanceByGroupId(currentGroupIdInt)
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        Column {

            Text(
                text = "BALANCE",
                modifier = Modifier.fillMaxWidth(), fontWeight = FontWeight.Bold
            )

            LazyColumn {
                itemsIndexed(balanceUi.value.userBalance) { index: Int, Balance ->
                    val owedBy =
                        currentGroup.groupMembers.find { it.id == Balance.owedBy }?.username
                            ?: "Unknown User"
                    val owedTo =
                        currentGroup.groupMembers.find { it.id == Balance.owedTo }?.username
                            ?: "Unknown User"
                    if (Balance.owedBy != currentUser.userId) {
                        OutlinedCard(modifier = Modifier.padding(12.dp)) {
                            Text(text = "$owedTo owes $owedBy ${Balance.amount} $")
                        }
                    } else {
                        OutlinedCard(modifier = Modifier.padding(12.dp)) {
                            Text(text = "$owedBy lent $owedTo ${Balance.amount} $")
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
                Box(
                    modifier = Modifier
                        .height(400.dp)
                        .fillMaxWidth()
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        itemsIndexed(payments.asReversed()) { index: Int, Payment ->
                            val userName =
                                currentGroup.groupMembers.find { it.id == Payment.paidBy }?.username
                                    ?: "Unknown User"
                            OutlinedCard(modifier = Modifier.padding(12.dp)) {
                                Text(text = "$userName bought ${Payment.items} for ${Payment.amount} $")
                            }
                        }
                    }
                }

            }
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                if (addPayment) {
                    Text(text = "Add new Payment")
                    OutlinedTextField(
                        value = newPayment,
                        onValueChange = { newValue ->
                            val filteredValue = newValue.filter { it.isDigit() || it == ',' }
                            newPayment = filteredValue
                        },
                        placeholder = { Text(text = "Amount") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(
                        value = itemsBought,
                        onValueChange = { newValue -> itemsBought = newValue },
                        placeholder = { Text(text = "Items you bought") },
                    )
                    Row() {
                        Button(onClick = {
                            val groupSize = currentGroup.groupMembers.size
                            if (paymentAmount != 0) {
                                balanceViewModel.addPayment(
                                    currentUser.userId,
                                    currentGroupIdInt,
                                    paymentAmount,
                                    itemsBought,
                                    groupSize,
                                    currentGroup.groupMembers
                                )
                                addPayment = false
                                newPayment = ""
                                itemsBought = ""
                            } else {
                                addPayment = false
                                newPayment = ""
                                itemsBought = ""
                            }
                        }) {
                            Text(text = "Add new Payment")
                        }
                        Button(onClick = {
                            addPayment = false
                            newPayment = ""
                            itemsBought = ""
                        }) { Text(text = "Cancel") }
                    }
                } else {
                    Button(onClick = { addPayment = true }) {
                        Text(text = "Add new Payment")
                    }
                }
            }
        }
    }
}


