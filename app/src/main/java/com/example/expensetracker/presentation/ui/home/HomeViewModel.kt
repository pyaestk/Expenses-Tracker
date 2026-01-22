import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.repository.ExpenseRepository
import com.example.expensetracker.data.repository.UserPreferencesRepository
import com.example.expensetracker.domain.repository.model.CategoryConstants // 1. Import this
import com.example.expensetracker.domain.repository.model.TransactionModel
import com.example.expensetracker.presentation.ui.home.HomeUiState
import com.example.expensetracker.ui.theme.*
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeViewModel(
    private val repository: ExpenseRepository,
    private val userPreferences: UserPreferencesRepository
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = combine(
        repository.getTotalIncome(),
        repository.getTotalExpense(),
        repository.getRecentTransactions(),
        userPreferences.currencySymbol
    ) { incomeTotal, expenseTotal, transactions, symbol ->

        val income = incomeTotal ?: 0.0
        val expense = expenseTotal ?: 0.0
        val balance = income - expense

        HomeUiState(
            totalBalance = formatCurrency(balance, symbol),
            income = formatCurrency(income, symbol),
            expense = formatCurrency(expense, symbol),
            recentTransactions = transactions.map { it.toUiModel(symbol) }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeUiState()
    )

    // Helper: Format Double with Dynamic Symbol and correct negative sign placement
    private fun formatCurrency(amount: Double, symbol: String): String {
        val format = NumberFormat.getNumberInstance(Locale.US)
        format.minimumFractionDigits = 2
        format.maximumFractionDigits = 2

        val isNegative = amount < 0
        val absoluteAmount = Math.abs(amount)

        // FIX 2: Check for negative and place symbol AFTER the minus sign
        // Example: -$500.00 instead of $-500.00
        return if (isNegative) {
            "-$symbol${format.format(absoluteAmount)}"
        } else {
            "$symbol${format.format(absoluteAmount)}"
        }
    }

    // Helper: Map Entity to UI Model
    private fun TransactionEntity.toUiModel(symbol: String): TransactionModel {
        val isExpense = type == "EXPENSE"

        // FIX 1: Use CategoryConstants instead of hardcoded 'when' block
        // This ensures "Edu", "Fun", etc. get their correct icons defined in Constants
        val config = CategoryConstants.getCategoryConfig(category)

        val dateFormat = SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault())
        val dateString = dateFormat.format(Date(date))

        // Format amount string for the list item (Explicit + or -)
        val format = NumberFormat.getNumberInstance(Locale.US)
        format.minimumFractionDigits = 2
        format.maximumFractionDigits = 2
        val absAmount = Math.abs(amount)

        val formattedAmount = "$symbol${format.format(absAmount)}"

        return TransactionModel(
            id = id,
            title = title,
            date = dateString,
            amount = if (isExpense) "- $formattedAmount" else "+ $formattedAmount",
            color = if (isExpense) RedExpense else GreenIncome,
            icon = config.icon, // Uses the icon from Constants (e.g. School icon for Edu)
        )
    }
}