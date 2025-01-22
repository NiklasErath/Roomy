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
import kotlinx.coroutines.coroutineScope
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

    // all states related to the balance
    private val _balance = MutableStateFlow(BalanceUiState(emptyList()))
    private val _balanceError = MutableStateFlow(BalanceError.Error(""))

    val balance = _balance.asStateFlow()
    val balanceError = _balanceError.asStateFlow()

    // this could be removed/refactored in the future to improve the performance of our app
    suspend fun getBalanceByGroupId(groupId: Int) {
        viewModelScope.launch {
            val balance = balanceRepository.getBalanceByGroupId(groupId)
            if (balance == null) {
                _balanceError.update { oldState ->
                    oldState.copy("Something went wrong while getting the Balance")
                }
            } else {
                _balance.update { oldState ->
                    oldState.copy(
                        balance
                    )
                }
            }
        }
    }

    // add a new payment and update/add/delete the balance
    fun addPayment(
        userId: String,
        groupId: Int,
        amount: Int,
        items: String,
        groupSize: Int,
        groupMembers: List<UserInformation>
    ) {
        viewModelScope.launch {
            val payment = paymentsRepository.addPayment(userId, groupId, amount, items)
            if (payment == null) {
                _balanceError.update { oldState ->
                    oldState.copy("add payment failed")
                }
            } else {
                stateViewModel.addNewPayment(payment, groupId)
                val dividedAmount = amount / groupSize
                coroutineScope {
                    groupMembers.map { member ->
                        if (member.id != userId) {
                            val updatedBalance = balanceRepository.addBalance(
                                groupId,
                                userId,
                                member.id,
                                dividedAmount
                            )

                            if (updatedBalance != null) {
                                getBalanceByGroupId(groupId)
                            }
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

