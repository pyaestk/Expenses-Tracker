package com.example.expensetracker.presentation.ui.home



import com.example.expensetracker.domain.repository.model.TransactionModel

data class HomeUiState(
    val totalBalance: String = "$0.00",
    val income: String = "$0.00",
    val expense: String = "$0.00",
    val recentTransactions: List<TransactionModel> = emptyList()
)