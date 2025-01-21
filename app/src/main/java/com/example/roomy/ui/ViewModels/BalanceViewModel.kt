package com.example.roomy.ui.ViewModels

import android.util.Log
import com.example.roomy.db.BalanceRepository
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.ViewModel
import com.example.roomy.db.GroupRepository
import com.example.roomy.db.PaymentsRepository
import com.example.roomy.db.data.Payments
import com.example.roomy.db.data.UserInformation
import com.example.roomy.ui.States.BalanceUiState
import com.example.roomy.ui.States.GroupState
import com.example.roomy.ui.States.PaymentsUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class BalanceViewModel(
    private val balanceRepository: BalanceRepository,
    private val paymentsRepository: PaymentsRepository,
    private val stateViewModel: StateViewModel
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
    val balanceError = _balanceError.asStateFlow()


    // this could be removed/refactored in the future to improve the performance of our app
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

    // add a new payment and update/add/delete the balance
    fun addPayment(userId: String, groupId: Int, amount: Int, items: String, groupSize: Int, groupMembers: List<UserInformation>) {
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
                stateViewModel.addNewPayment(payment, groupId)
                val dividedAmount = amount / groupSize
                Log.d("AMOUNT", "$dividedAmount")
                groupMembers.map { member ->
                    if (member.id != userId) {
                        val updatedBalance = balanceRepository.addBalance(
                            groupId,
                            userId,
                            member.id,
                            dividedAmount
                        )
                        if (updatedBalance != null) {
                            Log.d("Balance", "$balance")
                        }

                    }
                }
            }
        }
    }

    // for future features
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

    // clear the Error messages if necessary
    fun clearBalanceError() {
        _balanceError.update { oldState ->
            oldState.copy("")
        }
    }

}

