package com.example.roomy.ui.Factory

import androidx.compose.runtime.key
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.roomy.db.BalanceRepository
import com.example.roomy.db.GroupRepository
import com.example.roomy.db.PaymentsRepository
import com.example.roomy.db.UserRepository
import com.example.roomy.ui.ViewModels.GroupViewModel
import com.example.roomy.ui.ViewModels.ItemViewModel
import com.example.roomy.ui.ViewModels.StateViewModel


class GroupViewModelFactory(
    private val groupRepository: GroupRepository,
    private  val userRepository: UserRepository,
    private  val itemViewModel: ItemViewModel,
    private val balanceRepository: BalanceRepository,
    private val paymentsRepository: PaymentsRepository,
    private val stateViewModel: StateViewModel


) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GroupViewModel::class.java)) {
            return GroupViewModel(
                groupRepository = groupRepository,
                userRepository = userRepository,
                itemViewModel = itemViewModel,
                balanceRepository = balanceRepository,
                paymentsRepository = paymentsRepository,
                stateViewModel = stateViewModel
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}