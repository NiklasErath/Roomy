package com.example.roomy.ui.ViewModels

import android.util.Log
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.roomy.db.BalanceRepository
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.ViewModel
import com.example.roomy.db.Supabase.supabase
import com.example.roomy.db.data.Balance
import com.example.roomy.ui.States.BalanceUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class BalanceViewModel(private val balanceRepository: BalanceRepository) : ViewModel() {

    sealed class BalanceError {
        data class Error(val message: String)
    }

    private val _balance = MutableStateFlow(BalanceUiState(emptyList(), emptyList()))
    private val _balanceError = MutableStateFlow(BalanceError.Error(""))

    val balance = _balance.asStateFlow()

    suspend fun getUserBalance(groupId: Int, userId: String) {
        viewModelScope.launch {
            Log.d("BALANCEIIII", "Fetching balance for groupId: $groupId, userId: $userId")
            val userLent = balanceRepository.getUserLentByGroupId(groupId, userId)
            val userOwes = balanceRepository.getUserOwesByGroupId(groupId, userId)

            if (userLent.isNullOrEmpty() || userOwes.isNullOrEmpty()) {
                _balanceError.update { oldState ->
                    oldState.copy("Something went wrong while getting the Balance")
                }
                Log.d("BALANCEIIII", "ERROR: Empty balance lists")
            } else {
                Log.d("BALANCE", "User Owes: $userOwes")
                _balance.update { oldState ->
                    oldState.copy(
                        userBalanceLent = userLent,
                        userBalanceOwes = userOwes
                    )
                }
            }
        }
    }

    suspend fun updateBalance(userId: String, groupMemberId: String, amountOwe:Int, amountLent:Int){
        viewModelScope.launch{
            val balance = balanceRepository.updateBalance(userId, groupMemberId, amountOwe, amountLent)
            if(!balance){
                _balanceError.update { oldState ->
                    oldState.copy("Update failed, please try again")
                }
            }
        }
    }

    suspend fun addNewBalance(groupId: Int, userId: String, groupMemberId: String){
        viewModelScope.launch{
            val newBalance = balanceRepository.addBalance(groupId, userId, groupMemberId)
            if (!newBalance){
                _balanceError.update { oldState ->
                    oldState.copy("add balance failed")
                }
            }

        }
    }

    suspend fun deleteBalanceByGroupId(groupId: Int){
        viewModelScope.launch{
            val delete = balanceRepository.deleteBalanceByGroupId(groupId)
            if (!delete){
                _balanceError.update { oldState ->
                    oldState.copy("deleting balances of the group failed")
                }
            }
        }
    }

    suspend fun deleteUserBalanceByByGroupId(groupId: Int, userId: String){
        viewModelScope.launch {
            val delete = balanceRepository.deleteUserBalanceByGroupId(groupId, userId)
            if (!delete){
                _balanceError.update { oldState ->
                    oldState.copy("deleting group balances failed")
                }
            }
        }
    }

    suspend fun deleteBalanceByUserId(groupId: Int, userId: String){
        viewModelScope.launch {
            val delete = balanceRepository.deleteBalanceByUserId(groupId, userId)
            if (!delete){
                _balanceError.update { oldState ->
                    oldState.copy("deleting user balances failed")
                }
            }
        }
    }

}