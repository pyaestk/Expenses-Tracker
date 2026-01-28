package com.example.saveit.presentation.ui.transactionDetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.saveit.data.repository.ExpenseRepository
import com.example.saveit.data.repository.UserPreferencesRepository // Import
import com.example.saveit.domain.repository.model.CategoryConstants
import com.example.saveit.ui.theme.RedExpense
import com.example.saveit.ui.theme.GreenIncome
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class TransactionDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: ExpenseRepository,
    private val userPreferences: UserPreferencesRepository
) : ViewModel() {

    private val transactionId: Int = checkNotNull(savedStateHandle["transactionId"])

    // 2. Use 'combine' to listen to both the Transaction and the Currency Symbol
    val uiState: StateFlow<TransactionDetailUiState> = combine(
        repository.getTransaction(transactionId),
        userPreferences.currencySymbol
    ) { entity, symbol ->

        if (entity == null) {
            TransactionDetailUiState(isLoading = false)
        } else {
            val isExpense = entity.type == "EXPENSE"
            val categoryConfig = CategoryConstants.getCategoryConfig(entity.category)

            // 3. Format Amount using the dynamic symbol
            val numberFormat = NumberFormat.getNumberInstance(Locale.US)
            numberFormat.minimumFractionDigits = 2
            numberFormat.maximumFractionDigits = 2
            val formattedAmount = "$symbol${numberFormat.format(entity.amount)}"

            TransactionDetailUiState(
                isLoading = false,
                transaction = entity,
                formattedAmount = formattedAmount, // Pass the custom string
                formattedDate = SimpleDateFormat("EEEE, dd MMM yyyy", Locale.getDefault()).format(Date(entity.date)),
                formattedTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(entity.date)),
                color = if (isExpense) RedExpense else GreenIncome,
                categoryIcon = categoryConfig,
                isExpense = isExpense
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = TransactionDetailUiState(isLoading = true)
    )

    fun deleteTransaction() {
        val currentTransaction = uiState.value.transaction
        if (currentTransaction != null) {
            viewModelScope.launch {
                repository.deleteTransaction(currentTransaction)
            }
        }
    }
}