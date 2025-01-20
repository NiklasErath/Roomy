package com.example.roomy.ui.ViewModels

import android.util.Log
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.roomy.db.BalanceRepository
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.ViewModel
import com.example.roomy.db.GroupRepository
import com.example.roomy.db.PaymentsRepository
import com.example.roomy.db.Supabase.supabase
import com.example.roomy.db.data.Balance
import com.example.roomy.db.data.Groups
import com.example.roomy.ui.States.BalanceUiState
import com.example.roomy.ui.States.GroupState
import com.example.roomy.ui.States.GroupsUiState
import com.example.roomy.ui.States.PaymentsUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class BalanceViewModel(
    private val balanceRepository: BalanceRepository,
    private val paymentsRepository: PaymentsRepository,
    private val groupRepository: GroupRepository
) : ViewModel() {

    sealed class BalanceError {
        data class Error(val message: String)
    }

    private val _balance = MutableStateFlow(BalanceUiState(emptyList()))
    private val _balanceError = MutableStateFlow(BalanceError.Error(""))
    private val _groupMembers = MutableStateFlow(GroupState(emptyList()))


    private val _payments = MutableStateFlow(PaymentsUiState(emptyList()))

    val balance = _balance.asStateFlow()
    val payments = _payments.asStateFlow()
    val groupMembers = _groupMembers.asStateFlow()

    suspend fun getBalanceByGroupId(groupId: Int) {
        viewModelScope.launch {
            val balance = balanceRepository.getBalanceByGroupId(groupId)
            if (balance == null) {
                _balanceError.update { oldState ->
                    oldState.copy("Something went wrong while getting the Balance")
                }
                Log.d("BALANCEIIII", "ERROR: Empty balance lists")
            } else {
                Log.d("BALANCE", "User Owes: $balance")
                _balance.update { oldState ->
                    oldState.copy(
                        balance
                    )
                }
            }

        }
    }

    suspend fun updateBalance(
        userId: String,
        groupMemberId: String,
        amountOwe: Int,
        amountLent: Int
    ) {
        viewModelScope.launch {
            val balance =
                balanceRepository.updateBalance(userId, groupMemberId, amountOwe, amountLent)
            if (!balance) {
                _balanceError.update { oldState ->
                    oldState.copy("Update failed, please try again")
                }
            }
        }
    }

    suspend fun addNewBalance(groupId: Int, userId: String, groupMemberId: String, amount: Int) {
        viewModelScope.launch {
            val newBalance = balanceRepository.addBalance(groupId, userId, groupMemberId, amount)
            if (!newBalance) {
                _balanceError.update { oldState ->
                    oldState.copy("add balance failed")
                }
            }

        }
    }

    suspend fun deleteBalanceByGroupId(groupId: Int) {
        viewModelScope.launch {
            val delete = balanceRepository.deleteBalanceByGroupId(groupId)
            if (!delete) {
                _balanceError.update { oldState ->
                    oldState.copy("deleting balances of the group failed")
                }
            }
        }
    }

    suspend fun deleteUserBalanceByByGroupId(groupId: Int, userId: String) {
        viewModelScope.launch {
            val delete = balanceRepository.deleteUserBalanceByGroupId(groupId, userId)
            if (!delete) {
                _balanceError.update { oldState ->
                    oldState.copy("deleting group balances failed")
                }
            }
        }
    }

    suspend fun deleteBalanceByUserId(groupId: Int, userId: String) {
        viewModelScope.launch {
            val delete = balanceRepository.deleteBalanceByUserId(groupId, userId)
            if (!delete) {
                _balanceError.update { oldState ->
                    oldState.copy("deleting user balances failed")
                }
            }
        }
    }

    suspend fun getPaymentsByGroupId(groupId: Int) {
        viewModelScope.launch {
            Log.d("PAY", "get payments")
            val payments = paymentsRepository.getPaymentsByGroupId(groupId)
            if (payments == null) {
                _balanceError.update { oldState ->
                    oldState.copy("getting payments failed")
                }
                Log.d("PAY", "get payments failed")

            } else {
                _payments.update { oldState ->
                    oldState.copy(payments)
                }
                Log.d("PAY", "get payments success")


            }
        }
    }

    fun addPayment(userId: String, groupId: Int, amount: Int, items: String) {
        viewModelScope.launch {
            val payment = paymentsRepository.addPayment(userId, groupId, amount, items)
            if (payment == null) {
                _balanceError.update { oldState ->
                    oldState.copy("add payment failed")

                }
            } else {
                _payments.update { oldState ->
                    val updatedPayments = oldState.payments.toMutableList()
                    updatedPayments.add(payment)
                    oldState.copy(payments = updatedPayments)
                }
                val dividedAmount = amount / _groupMembers.value.groups.size
                Log.d("AMOUNT", "$dividedAmount")
                groupMembers.value.groups.map { groups ->
                    if (groups.userId != userId) {
                        balanceRepository.addBalance(groupId, userId, groups.userId, dividedAmount)
                    }
                }
            }
        }
    }

    suspend fun deletePayment(paymentId: Int) {
        viewModelScope.launch {
            val deleteSuccess = paymentsRepository.deletePayment(paymentId)
            if (!deleteSuccess) {
                _balanceError.update { oldState ->
                    oldState.copy("deleting payment failed, please try again")
                }
            }
        }
    }

    fun getGroupMembers(groupId: Int) {
        viewModelScope.launch {
            val users = groupRepository.getGroupMembers(groupId)
            if (users == null) {
                _balanceError.update { oldState ->
                    oldState.copy("Could not fetch group Members")
                }
            } else {
                _groupMembers.update { oldState ->
                    oldState.copy(users)
                }
                Log.d("MEMBERS", "users")
            }
        }
    }

}