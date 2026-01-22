package com.example.expensetracker.presentation.ui.analytics.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.repository.ExpenseRepository
import com.example.expensetracker.data.repository.UserPreferencesRepository
import com.example.expensetracker.domain.repository.model.CategoryConstants
import com.example.expensetracker.domain.repository.model.TransactionModel
import com.example.expensetracker.ui.theme.GreenIncome
import com.example.expensetracker.ui.theme.RedExpense
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AnalyticsDetailViewModel(
    savedStateHandle: SavedStateHandle,
    repository: ExpenseRepository,
    userPreferences: UserPreferencesRepository
) : ViewModel() {

    val categoryName: String = checkNotNull(savedStateHandle["categoryName"])
    private val startDate: Long = checkNotNull(savedStateHandle["startDate"]).toString().toLong()
    private val endDate: Long = checkNotNull(savedStateHandle["endDate"]).toString().toLong()

    // NEW: Get the month string passed from Navigation
    val monthDisplay: String = checkNotNull(savedStateHandle["monthName"])

    val transactions: StateFlow<List<TransactionModel>> = combine(
        repository.getTransactionsByCategoryAndDate(categoryName, startDate, endDate),
        userPreferences.currencySymbol
    ) { entities, symbol ->
        entities.map { entity ->
            val config = CategoryConstants.getCategoryConfig(entity.category)
            val isExpense = entity.type == "EXPENSE"

            val format = NumberFormat.getNumberInstance(Locale.US)
            format.minimumFractionDigits = 2
            val formattedAmount = "$symbol${format.format(entity.amount)}"
            val finalAmount = if (isExpense) "- $formattedAmount" else "+ $formattedAmount"

            TransactionModel(
                id = entity.id,
                title = entity.title,
                date = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(entity.date)),
                amount = finalAmount,
                color = if (isExpense) RedExpense else GreenIncome,
                icon = config.icon,
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}