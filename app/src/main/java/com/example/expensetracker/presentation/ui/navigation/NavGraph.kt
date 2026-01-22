package com.example.expensetracker.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.expensetracker.presentation.ui.add_expense.AddExpenseScreen
import com.example.expensetracker.presentation.ui.analytics.AnalyticsScreen
import com.example.expensetracker.presentation.ui.analytics.AnalyticsViewModel
import com.example.expensetracker.presentation.ui.analytics.detail.AnalyticsDetailScreen
import com.example.expensetracker.presentation.ui.budget.BudgetPlannerScreen
import com.example.expensetracker.presentation.ui.budgetDetail.BudgetDetailScreen
import com.example.expensetracker.presentation.ui.category_detail.CategoryExpensesScreen
import com.example.expensetracker.presentation.ui.transactionDetail.TransactionDetailScreen
import com.example.expensetracker.presentation.ui.home.HomeScreen
import com.example.expensetracker.presentation.ui.navigation.Screen
import com.example.expensetracker.presentation.ui.search.SearchScreen
import com.example.expensetracker.presentation.ui.setting.SettingsScreen
import com.example.expensetracker.presentation.ui.transaction_list.TransactionListScreen
import org.koin.androidx.compose.koinViewModel

@Composable
fun ExpenseTrackerNavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onAddExpenseClick = { navController.navigate(Screen.AddExpense.route) },
                onSeeAllClick = { navController.navigate("transactions") },
                onTransactionClick = { transactionId ->
                    navController.navigate("transaction_detail/$transactionId")
                },
                onCategoryClick = { category ->
                    navController.navigate(
                        Screen.CategoryExpenses.createRoute(
                            category
                        )
                    )
                },
                onSearchClick = { navController.navigate(Screen.Search.route) }
            )
        }

        composable(
            route = "transaction_detail/{transactionId}",
            arguments = listOf(navArgument("transactionId") { type = NavType.IntType })
        ) {
            TransactionDetailScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.AddExpense.route) {
            AddExpenseScreen(onBackClick = { navController.popBackStack() })
        }

        composable("transactions") {
            TransactionListScreen(
                onBackClick = { navController.popBackStack() },
                onTransactionClick = { transactionId ->
                    navController.navigate("transaction_detail/$transactionId")
                },
                onSearchClick = { navController.navigate(Screen.Search.route) }
            )
        }

        composable(Screen.Budget.route) {
            BudgetPlannerScreen(
                onBudgetClick = { id -> navController.navigate(Screen.BudgetDetail.createRoute(id)) }
            )
        }

        composable(
            route = Screen.BudgetDetail.route,
            arguments = listOf(navArgument("budgetId") { type = NavType.IntType })
        ) {
            BudgetDetailScreen(onBackClick = { navController.popBackStack() })
        }

        composable(Screen.Analytics.route) {
            val viewModel: AnalyticsViewModel = koinViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            AnalyticsScreen(
                viewModel = viewModel,
                onCategoryClick = { category, start, end ->
                    navController.navigate(
                        Screen.AnalyticsDetail.createRoute(
                            category,
                            start,
                            end,
                            uiState.dateDisplay
                        )
                    )
                }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen()
        }

        composable(
            route = Screen.CategoryExpenses.route,
            arguments = listOf(navArgument("categoryName") { type = NavType.StringType })
        ) {
            CategoryExpensesScreen(
                onBackClick = { navController.popBackStack() },
                onTransactionClick = { id -> navController.navigate("transaction_detail/$id") }
            )
        }

        composable(
            route = Screen.AnalyticsDetail.route,
            arguments = listOf(
                navArgument("categoryName") { type = NavType.StringType },
                navArgument("startDate") { type = NavType.LongType },
                navArgument("endDate") { type = NavType.LongType },
                navArgument("monthName") { type = NavType.StringType }
            )
        ) {
            AnalyticsDetailScreen(
                onBackClick = { navController.popBackStack() },
                onTransactionClick = { id ->
                    navController.navigate("transaction_detail/$id")
                }
            )
        }

        composable(Screen.Search.route) {
            SearchScreen(
                onBackClick = { navController.popBackStack() },
                onTransactionClick = { id -> navController.navigate("transaction_detail/$id") }
            )
        }
    }
}