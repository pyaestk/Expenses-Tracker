package com.example.expensetracker.presentation.ui.transaction_list

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.repository.ExpenseRepository
import com.example.expensetracker.data.repository.UserPreferencesRepository
import com.example.expensetracker.domain.repository.model.CategoryConstants
import com.example.expensetracker.domain.repository.model.TransactionModel
import com.example.expensetracker.ui.theme.*
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.text.SimpleDateFormat
import java.util.*

class TransactionListViewModel(
    repository: ExpenseRepository,
    userPreferences: UserPreferencesRepository
) : ViewModel() {
    val transactions = combine(
    repository.getAllTransactions(),
    userPreferences.currencySymbol
    ) { entities, symbol ->
        entities.map { entity ->
                // Map Entity to UI Model (Reusable logic)
                val config = CategoryConstants.getCategoryConfig(entity.category)
                val isExpense = entity.type == "EXPENSE"
                val amountColor = if (isExpense) Color.Red else GreenIncome
                val amountStr = (if(isExpense) "- " else "+ ") + "$symbol${String.format("%.2f", entity.amount)}"

                TransactionModel(
                    id = entity.id,
                    title = entity.title,
                    date = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(entity.date)),
                    amount = amountStr,
                    color = amountColor,
                    icon = config.icon,
                ) to entity.date // Keep raw date for grouping
            }
                .groupBy { (_, date) ->
                    // Grouping Logic
                    val transactionDate = Date(date)
                    val today = Date()
                    val format = SimpleDateFormat("yyyyMMdd", Locale.getDefault())

                    when {
                        format.format(transactionDate) == format.format(today) -> "Today"
                        format.format(transactionDate) == format.format(Date(today.time - 86400000)) -> "Yesterday"
                        else -> SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(transactionDate)
                    }
                }
                .mapValues { entry -> entry.value.map { it.first } } // Remove raw date from final list
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())
}