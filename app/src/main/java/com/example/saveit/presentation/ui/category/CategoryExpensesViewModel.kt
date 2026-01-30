package com.example.saveit.presentation.ui.category


import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.saveit.data.repository.ExpenseRepository
import com.example.saveit.data.repository.UserPreferencesRepository
import com.example.saveit.domain.repository.model.CategoryConstants
import com.example.saveit.domain.repository.model.TransactionModel
import com.example.saveit.ui.theme.GreenIncome
import com.example.saveit.ui.theme.RedExpense
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
// ... imports

class CategoryExpensesViewModel(
    savedStateHandle: SavedStateHandle,
    repository: ExpenseRepository,
    userPreferences: UserPreferencesRepository
) : ViewModel() {

    val categoryName: String = checkNotNull(savedStateHandle["categoryName"])
    // 1. Get the Type (EXPENSE or INCOME)
    val transactionType: String = checkNotNull(savedStateHandle["transactionType"])

    // 2. Use the new Repository function
    val transactions: StateFlow<List<TransactionModel>> = combine(
        repository.getTransactionsByCategoryAndType(categoryName, transactionType),
        userPreferences.currencySymbol
    ) { entities, symbol ->
        entities.map { entity ->
            val config = CategoryConstants.getCategoryConfig(entity.category)
            val isExpense = entity.type == "EXPENSE"

            // Format Amount
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
                icon = config.icon
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}