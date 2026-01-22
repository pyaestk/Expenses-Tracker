package com.example.expensetracker.presentation.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.repository.ExpenseRepository
import com.example.expensetracker.data.repository.UserPreferencesRepository
import com.example.expensetracker.domain.repository.model.CategoryConstants
import com.example.expensetracker.domain.repository.model.TransactionModel
import com.example.expensetracker.ui.theme.GreenIncome
import com.example.expensetracker.ui.theme.RedExpense
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

// Enums for Filter Options
enum class TransactionTypeFilter { ALL, EXPENSE, INCOME }
enum class SortOption { NEWEST, OLDEST, HIGHEST_AMOUNT, LOWEST_AMOUNT }
enum class DateFilter { ALL_TIME, THIS_MONTH, LAST_30_DAYS, LAST_7_DAYS }

data class SearchUiState(
    val query: String = "",
    val typeFilter: TransactionTypeFilter = TransactionTypeFilter.ALL,
    val dateFilter: DateFilter = DateFilter.ALL_TIME,
    val sortOption: SortOption = SortOption.NEWEST,
    val selectedCategory: String? = null,
    val results: List<TransactionModel> = emptyList(),
    val currencySymbol: String = "$"
)

// Helper Data Class to group filter inputs
private data class FilterCriteria(
    val query: String,
    val type: TransactionTypeFilter,
    val date: DateFilter,
    val sort: SortOption,
    val category: String?
)

class SearchViewModel(
    repository: ExpenseRepository,
    userPreferences: UserPreferencesRepository
) : ViewModel() {

    // Mutable States
    private val _query = MutableStateFlow("")
    private val _typeFilter = MutableStateFlow(TransactionTypeFilter.ALL)
    private val _dateFilter = MutableStateFlow(DateFilter.ALL_TIME)
    private val _sortOption = MutableStateFlow(SortOption.NEWEST)
    private val _selectedCategory = MutableStateFlow<String?>(null)

    // Step 1: Combine the 5 filter states into one "criteria" flow
    private val filterCriteriaFlow = combine(
        _query,
        _typeFilter,
        _dateFilter,
        _sortOption,
        _selectedCategory
    ) { query, type, date, sort, category ->
        FilterCriteria(query, type, date, sort, category)
    }

    // Step 2: Combine the criteria with the Data (Transactions & Currency)
    // Now we are only combining 3 flows (Transactions, Symbol, Filters), which is allowed.
    val uiState: StateFlow<SearchUiState> = combine(
        repository.getAllTransactions(),
        userPreferences.currencySymbol,
        filterCriteriaFlow
    ) { transactions, symbol, filters ->

        // Unpack the filters for easier usage
        val (query, type, dateRange, sort, category) = filters

        // 1. Filter Logic
        val filtered = transactions.filter { item ->
            // Text Search (Title or Note)
            val matchesQuery = item.title.contains(query, ignoreCase = true) ||
                    (item.note ?: "").contains(query, ignoreCase = true)

            // Type Filter
            val matchesType = when (type) {
                TransactionTypeFilter.ALL -> true
                TransactionTypeFilter.EXPENSE -> item.type == "EXPENSE"
                TransactionTypeFilter.INCOME -> item.type == "INCOME"
            }

            // Category Filter
            val matchesCategory = category == null || item.category == category

            // Date Filter
            val matchesDate = isDateInRanges(item.date, dateRange)

            matchesQuery && matchesType && matchesCategory && matchesDate
        }

        // 2. Sort Logic
        val sorted = when (sort) {
            SortOption.NEWEST -> filtered.sortedByDescending { it.date }
            SortOption.OLDEST -> filtered.sortedBy { it.date }
            SortOption.HIGHEST_AMOUNT -> filtered.sortedByDescending { it.amount }
            SortOption.LOWEST_AMOUNT -> filtered.sortedBy { it.amount }
        }

        // 3. Map to UI Model
        val uiModels = sorted.map { entity ->
            val config = CategoryConstants.getCategoryConfig(entity.category)
            val isExpense = entity.type == "EXPENSE"
            val format = NumberFormat.getNumberInstance(Locale.US)
            format.minimumFractionDigits = 2
            val formattedAmount = "$symbol${format.format(entity.amount)}"

            TransactionModel(
                id = entity.id,
                title = entity.title,
                date = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(entity.date)),
                amount = if (isExpense) "- $formattedAmount" else "+ $formattedAmount",
                color = if (isExpense) RedExpense else GreenIncome,
                icon = config.icon,
            )
        }

        SearchUiState(
            query = query,
            typeFilter = type,
            dateFilter = dateRange,
            sortOption = sort,
            selectedCategory = category,
            results = uiModels,
            currencySymbol = symbol
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SearchUiState())

    // Update Methods
    fun onQueryChange(newQuery: String) = _query.update { newQuery }
    fun onTypeChange(newType: TransactionTypeFilter) = _typeFilter.update { newType }
    fun onDateChange(newDate: DateFilter) = _dateFilter.update { newDate }
    fun onSortChange(newSort: SortOption) = _sortOption.update { newSort }
    fun onCategoryChange(newCategory: String?) = _selectedCategory.update { newCategory }

    // Helper for Date Filtering
    private fun isDateInRanges(dateMillis: Long, filter: DateFilter): Boolean {
        val transactionDate = Calendar.getInstance().apply { timeInMillis = dateMillis }
        val now = Calendar.getInstance()

        return when (filter) {
            DateFilter.ALL_TIME -> true
            DateFilter.THIS_MONTH -> {
                transactionDate.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                        transactionDate.get(Calendar.MONTH) == now.get(Calendar.MONTH)
            }
            DateFilter.LAST_30_DAYS -> {
                val diff = now.timeInMillis - dateMillis
                diff <= 30L * 24 * 60 * 60 * 1000
            }
            DateFilter.LAST_7_DAYS -> {
                val diff = now.timeInMillis - dateMillis
                diff <= 7L * 24 * 60 * 60 * 1000
            }
        }
    }
}