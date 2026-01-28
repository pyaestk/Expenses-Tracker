package com.example.saveit.presentation.ui.home



import com.example.saveit.domain.repository.model.TransactionModel

data class HomeUiState(
    val totalBalance: String = "$0.00",
    val income: String = "$0.00",
    val expense: String = "$0.00",
    val recentTransactions: List<TransactionModel> = emptyList()
)