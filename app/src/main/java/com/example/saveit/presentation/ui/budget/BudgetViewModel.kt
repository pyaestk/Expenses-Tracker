package com.example.saveit.presentation.ui.budget

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.saveit.data.local.entity.BudgetEntity
import com.example.saveit.data.repository.ExpenseRepository
import com.example.saveit.data.repository.UserPreferencesRepository
import com.example.saveit.domain.repository.model.CategoryConstants
import com.example.saveit.ui.theme.*
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

data class BudgetUiState(
    val budgetList: List<BudgetUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val currencySymbol: String = "$"
)

data class BudgetUiModel(
    val id: Int,
    val categoryName: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val iconColor: Color,
    val iconBgColor: Color,
    val totalLimit: Double,
    val spentAmount: Double,
    val percentage: Float,
    val leftAmount: Double,
    val color: Color
)

class BudgetViewModel(
    private val repository: ExpenseRepository,
    userPreferences: UserPreferencesRepository
) : ViewModel() {

    private val calendar = Calendar.getInstance()
    private val currentMonth = calendar.get(Calendar.MONTH) + 1
    private val currentYear = calendar.get(Calendar.YEAR)

    val uiState: StateFlow<BudgetUiState> = combine(
        repository.getSpendingByCategory(),
        repository.getBudgetsForMonth(currentMonth, currentYear),
        userPreferences.currencySymbol
    ) { spendingList, budgetList, symbol ->

        val spendingMap = spendingList.associate { it.category to it.total }

        val uiList = budgetList.map { budget ->
            val spent = spendingMap[budget.category] ?: 0.0
            val limit = budget.amountLimit
            val percentage = (spent / limit).toFloat()
            val left = limit - spent

            val config = CategoryConstants.getCategoryConfig(budget.category)

            val statusColor = when {
                percentage >= 1f -> RedExpense
                percentage >= 0.8f -> OrangeEntertainment
                else -> BluePrimary
            }

            BudgetUiModel(
                id = budget.id,
                categoryName = budget.category,
                icon = config.icon,
                iconColor = config.color,
                iconBgColor = config.backgroundColor,
                totalLimit = limit,
                spentAmount = spent,
                percentage = percentage,
                leftAmount = left,
                color = statusColor
            )
        }.sortedByDescending { it.percentage }

        BudgetUiState(
            budgetList = uiList,
            currencySymbol = symbol
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = BudgetUiState(isLoading = true)
    )

    fun saveBudget(category: String, limit: String) {
        val amount = limit.toDoubleOrNull() ?: return
        if (amount <= 0) return

        viewModelScope.launch {
            repository.setBudget(
                BudgetEntity(
                    category = category,
                    amountLimit = amount,
                    month = currentMonth,
                    year = currentYear
                )
            )
        }
    }
}