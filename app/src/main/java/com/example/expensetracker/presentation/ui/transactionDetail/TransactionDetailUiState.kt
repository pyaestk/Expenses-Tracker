package com.example.expensetracker.presentation.ui.transactionDetail

import CategoryModel
import TransactionEntity
import androidx.compose.ui.graphics.Color

data class TransactionDetailUiState(
    val isLoading: Boolean = false,
    val transaction: TransactionEntity? = null,
    val formattedAmount: String = "",
    val formattedDate: String = "",
    val formattedTime: String = "",
    val color: Color = Color.Black,
    val categoryIcon: CategoryModel? = null,
    val isExpense: Boolean = true
)