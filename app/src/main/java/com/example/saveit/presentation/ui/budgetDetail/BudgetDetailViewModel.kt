package com.example.saveit.presentation.ui.budgetDetail


import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.saveit.data.local.entity.BudgetEntity
import com.example.saveit.data.repository.ExpenseRepository
import com.example.saveit.data.repository.UserPreferencesRepository
import com.example.saveit.domain.repository.model.CategoryConstants
import com.example.saveit.ui.theme.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar

data class BudgetDetailUiState(
    val isLoading: Boolean = false,
    val budget: BudgetEntity? = null,
    val categoryIcon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    val iconColor: Color = Color.Gray,
    val iconBgColor: Color = Color.LightGray,
    val spentAmount: Double = 0.0,
    val totalLimit: Double = 0.0,
    val percentage: Float = 0f,
    val currencySymbol: String = "$",
    val statusColor: Color = BluePrimary
)

class BudgetDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: ExpenseRepository,
    userPreferences: UserPreferencesRepository
) : ViewModel() {

    private val budgetId: Int = checkNotNull(savedStateHandle["budgetId"])

    // Current Month Context
    private val calendar = Calendar.getInstance()
    private val currentMonth = calendar.get(Calendar.MONTH) + 1
    private val currentYear = calendar.get(Calendar.YEAR)

    val uiState: StateFlow<BudgetDetailUiState> = combine(
        repository.getBudget(budgetId),
        repository.getSpendingByCategory(), // We need all spending to filter for this category
        userPreferences.currencySymbol
    ) { budget, spendingList, symbol ->

        if (budget == null) {
            BudgetDetailUiState(isLoading = false)
        } else {
            // Find spending for this category
            val spent = spendingList.find { it.category == budget.category }?.total ?: 0.0
            val limit = budget.amountLimit
            val percentage = (spent / limit).toFloat()

            val config = CategoryConstants.getCategoryConfig(budget.category)

            val statusColor = when {
                percentage >= 1f -> RedExpense
                percentage >= 0.8f -> OrangeEntertainment
                else -> BluePrimary
            }

            BudgetDetailUiState(
                isLoading = false,
                budget = budget,
                categoryIcon = config.icon,
                iconColor = config.color,
                iconBgColor = config.backgroundColor,
                spentAmount = spent,
                totalLimit = limit,
                percentage = percentage,
                currencySymbol = symbol,
                statusColor = statusColor
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = BudgetDetailUiState(isLoading = true)
    )

    fun deleteBudget() {
        val budget = uiState.value.budget ?: return
        viewModelScope.launch {
            repository.deleteBudget(budget)
        }
    }

    fun updateBudget(amount: String) {
        val budget = uiState.value.budget ?: return
        val newLimit = amount.toDoubleOrNull() ?: return

        viewModelScope.launch {
            repository.setBudget(budget.copy(amountLimit = newLimit))
        }
    }
}