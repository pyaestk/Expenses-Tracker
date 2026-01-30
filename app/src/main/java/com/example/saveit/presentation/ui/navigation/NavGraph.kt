package com.example.saveit.ui.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.expensetracker.presentation.ui.category_list.CategoryListScreen
import com.example.saveit.presentation.ui.add_expense.AddExpenseScreen
import com.example.saveit.presentation.ui.analytics.AnalyticsScreen
import com.example.saveit.presentation.ui.analytics.AnalyticsViewModel
import com.example.saveit.presentation.ui.analytics.detail.AnalyticsDetailScreen
import com.example.saveit.presentation.ui.budget.BudgetPlannerScreen
import com.example.saveit.presentation.ui.budgetDetail.BudgetDetailScreen
import com.example.saveit.presentation.ui.category_detail.CategoryExpensesScreen
import com.example.saveit.presentation.ui.transactionDetail.TransactionDetailScreen
import com.example.saveit.presentation.ui.home.HomeScreen
import com.example.saveit.presentation.ui.navigation.Screen
import com.example.saveit.presentation.ui.search.SearchScreen
import com.example.saveit.presentation.ui.setting.SettingsScreen
import com.example.saveit.presentation.ui.transaction_list.TransactionListScreen
import org.koin.androidx.compose.koinViewModel

@Composable
fun ExpenseTrackerNavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route,
            enterTransition = { fadeIn() },
            exitTransition = { fadeOut() },
            popEnterTransition = { fadeIn() },
            popExitTransition = { fadeOut() }
        ) {
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
                onSearchClick = { navController.navigate(Screen.Search.route) },
                onManageCategoryClick = { navController.navigate(Screen.CategoryList.route) }
            )
        }

        composable(
            route = "transaction_detail/{transactionId}",
            arguments = listOf(navArgument("transactionId") { type = NavType.IntType }),
            enterTransition = { fadeIn() },
            exitTransition = { fadeOut() },
            popEnterTransition = { fadeIn() },
            popExitTransition = { fadeOut() }
        ) {
            TransactionDetailScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.AddExpense.route,
            enterTransition = { fadeIn() },
            exitTransition = { fadeOut() },
            popEnterTransition = { fadeIn() },
            popExitTransition = { fadeOut() }
        ) {
            AddExpenseScreen(onBackClick = { navController.popBackStack() })
        }

        composable("transactions",
            enterTransition = { fadeIn() },
            exitTransition = { fadeOut() },
            popEnterTransition = { fadeIn() },
            popExitTransition = { fadeOut() }
        ) {
            TransactionListScreen(
                onBackClick = { navController.popBackStack() },
                onTransactionClick = { transactionId ->
                    navController.navigate("transaction_detail/$transactionId")
                },
                onSearchClick = { navController.navigate(Screen.Search.route) }
            )
        }

        composable(Screen.Budget.route,
            enterTransition = { fadeIn() },
            exitTransition = { fadeOut() },
            popEnterTransition = { fadeIn() },
            popExitTransition = { fadeOut() }
        ) {
            BudgetPlannerScreen(
                onBudgetClick = { id -> navController.navigate(Screen.BudgetDetail.createRoute(id)) }
            )
        }

        composable(
            route = Screen.BudgetDetail.route,
            arguments = listOf(navArgument("budgetId") { type = NavType.IntType }),
            enterTransition = { fadeIn() },
            exitTransition = { fadeOut() },
            popEnterTransition = { fadeIn() },
            popExitTransition = { fadeOut() }
        ) {
            BudgetDetailScreen(onBackClick = { navController.popBackStack() })
        }

        composable(Screen.Analytics.route,
            enterTransition = { fadeIn() },
            exitTransition = { fadeOut() },
            popEnterTransition = { fadeIn() },
            popExitTransition = { fadeOut() }) {
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

        composable(Screen.Settings.route,
            enterTransition = { fadeIn() },
            exitTransition = { fadeOut() },
            popEnterTransition = { fadeIn() },
            popExitTransition = { fadeOut() }) {
            SettingsScreen()
        }

        composable(
            route = Screen.CategoryExpenses.route,
            arguments = listOf(navArgument("categoryName") { type = NavType.StringType }),
            enterTransition = { fadeIn() },
            exitTransition = { fadeOut() },
            popEnterTransition = { fadeIn() },
            popExitTransition = { fadeOut() }
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
            ),
            enterTransition = { fadeIn() },
            exitTransition = { fadeOut() },
            popEnterTransition = { fadeIn() },
            popExitTransition = { fadeOut() }
        ) {
            AnalyticsDetailScreen(
                onBackClick = { navController.popBackStack() },
                onTransactionClick = { id ->
                    navController.navigate("transaction_detail/$id")
                }
            )
        }

        composable(Screen.Search.route,
            enterTransition = { fadeIn() },
            exitTransition = { fadeOut() },
            popEnterTransition = { fadeIn() },
            popExitTransition = { fadeOut() }) {
            SearchScreen(
                onBackClick = { navController.popBackStack() },
                onTransactionClick = { id -> navController.navigate("transaction_detail/$id") }
            )
        }

        composable(Screen.CategoryList.route) {
            CategoryListScreen(
                onBackClick = { navController.popBackStack() },
                onCategoryClick = { category ->
                    navController.navigate(Screen.CategoryExpenses.createRoute(category))
                }
            )
        }
    }
}