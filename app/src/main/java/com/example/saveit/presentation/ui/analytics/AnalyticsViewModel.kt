package com.example.saveit.presentation.ui.analytics

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.saveit.data.repository.ExpenseRepository
import com.example.saveit.data.repository.UserPreferencesRepository
import com.example.saveit.domain.repository.model.CategoryConstants
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class AnalyticsUiState(
    val totalSpending: Double = 0.0,
    val categories: List<CategoryAnalyticsModel> = emptyList(),
    val dateDisplay: String = "",
    val currencySymbol: String = "",
    val startDate: Long = 0L,
    val endDate: Long = 0L
)

data class CategoryAnalyticsModel(
    val name: String,
    val amount: Double,
    val percentage: Float,
    val color: Color,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

class AnalyticsViewModel(
    private val repository: ExpenseRepository,
    private val userPreferences: UserPreferencesRepository
) : ViewModel() {

    // Manage Date Selection
    private val _selectedDate = MutableStateFlow(Calendar.getInstance())

    val uiState: StateFlow<AnalyticsUiState> = _selectedDate.flatMapLatest { calendar ->
        // Calculate Start and End of the selected month
        val start = calendar.clone() as Calendar
        start.set(Calendar.DAY_OF_MONTH, 1)
        start.set(Calendar.HOUR_OF_DAY, 0)
        start.set(Calendar.MINUTE, 0)
        start.set(Calendar.SECOND, 0)

        val end = calendar.clone() as Calendar
        end.set(Calendar.DAY_OF_MONTH, end.getActualMaximum(Calendar.DAY_OF_MONTH))
        end.set(Calendar.HOUR_OF_DAY, 23)
        end.set(Calendar.MINUTE, 59)
        end.set(Calendar.SECOND, 59)

        // Combine Total Expense and Category Breakdown for that month
        combine(
            repository.getTotalExpenseByDate(start.timeInMillis, end.timeInMillis),
            repository.getSpendingByCategoryByDate(start.timeInMillis, end.timeInMillis),
            userPreferences.currencySymbol
        ) { total, list, symbol ->
            val totalSafe = total ?: 0.0

            val categoryModels = list.map { tuple ->
                val config = CategoryConstants.getCategoryConfig(tuple.category)
                val percentage = if (totalSafe > 0) (tuple.total / totalSafe).toFloat() else 0f

                CategoryAnalyticsModel(
                    name = tuple.category,
                    amount = tuple.total,
                    percentage = percentage,
                    color = config.color,
                    icon = config.icon,

                )
            }.sortedByDescending { it.percentage }

            AnalyticsUiState(
                totalSpending = totalSafe,
                categories = categoryModels,
                dateDisplay = SimpleDateFormat("MMMM yyyy", Locale.US).format(calendar.time)
                    .uppercase(),
                currencySymbol = symbol,
                startDate = start.timeInMillis,
                endDate = end.timeInMillis
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AnalyticsUiState())

    fun changeMonth(increment: Int) {
        val newCal = _selectedDate.value.clone() as Calendar
        newCal.add(Calendar.MONTH, increment)
        _selectedDate.value = newCal
    }
}