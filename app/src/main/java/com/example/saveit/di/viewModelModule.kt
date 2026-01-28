
import com.example.saveit.presentation.ui.add_expense.AddExpenseViewModel
import com.example.saveit.presentation.ui.analytics.AnalyticsViewModel
import com.example.saveit.presentation.ui.analytics.detail.AnalyticsDetailViewModel
import com.example.saveit.presentation.ui.budget.BudgetViewModel
import com.example.saveit.presentation.ui.budgetDetail.BudgetDetailViewModel
import com.example.saveit.presentation.ui.category.CategoryExpensesViewModel
import com.example.saveit.presentation.ui.search.SearchViewModel
import com.example.saveit.presentation.ui.transactionDetail.TransactionDetailViewModel
import com.example.saveit.presentation.ui.setting.SettingsViewModel
import com.example.saveit.presentation.ui.transaction_list.TransactionListViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { HomeViewModel(get(), get()) }
    viewModel { AddExpenseViewModel(get(), get()) }
    viewModel { TransactionListViewModel(get(), get()) }
    viewModel { BudgetViewModel(get(), get()) }
    viewModel { AnalyticsViewModel(get(), get()) }
    viewModel { params -> TransactionDetailViewModel(params.get(), get(), get()) }
    single { SettingsViewModel(get()) }
    viewModel { params -> BudgetDetailViewModel(params.get(), get(), get()) }
    viewModel { params -> CategoryExpensesViewModel(params.get(), get(), get()) }
    viewModel { params -> AnalyticsDetailViewModel(params.get(), get(), get()) }
    viewModel { SearchViewModel(get(), get()) }
}