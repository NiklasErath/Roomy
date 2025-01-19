package com.example.roomy.ui.Factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.roomy.db.BalanceRepository
import com.example.roomy.db.GroupRepository
import com.example.roomy.db.ItemRepository
import com.example.roomy.db.PaymentsRepository
import com.example.roomy.ui.ViewModels.BalanceViewModel
import com.example.roomy.ui.ViewModels.ItemViewModel

class BalanceViewModelFactory(private val balanceRepository: BalanceRepository, private val paymentsRepository: PaymentsRepository, private val groupRepository: GroupRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(BalanceViewModel::class.java)) {

            return BalanceViewModel(balanceRepository, paymentsRepository, groupRepository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }


}