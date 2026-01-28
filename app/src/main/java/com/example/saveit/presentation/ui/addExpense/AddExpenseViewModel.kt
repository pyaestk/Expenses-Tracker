package com.example.saveit.presentation.ui.add_expense

import TransactionEntity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.saveit.data.repository.ExpenseRepository
import com.example.saveit.data.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AddExpenseViewModel(
    private val repository: ExpenseRepository,
    private val userPreferences: UserPreferencesRepository
) : ViewModel() {

    val currencySymbol: StateFlow<String> = userPreferences.currencySymbol
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = "$"
        )

    fun saveTransaction(
        amount: String,
        category: String,
        note: String,
        date: Long,
        type: String = "EXPENSE" // Default to Expense for this screen
    ) {
        val amountDouble = amount.toDoubleOrNull() ?: 0.0


        if (amountDouble <= 0) return

        val transaction = TransactionEntity(
            title = note.ifBlank { category },
            amount = amountDouble,
            type = type,
            category = category,
            date = date,
            note = note
        )

        viewModelScope.launch {
            repository.addTransaction(transaction)
        }
    }
}