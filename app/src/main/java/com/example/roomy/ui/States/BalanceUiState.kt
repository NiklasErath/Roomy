package com.example.roomy.ui.States

import com.example.roomy.db.data.Balance

data class BalanceUiState (
    val userBalance: List<Balance> = emptyList(),
)