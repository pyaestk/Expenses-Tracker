package com.example.saveit.presentation.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object AddExpense : Screen("add_expense")
    object Analytics : Screen("analytics")
    object Budget : Screen("budget")

    object Settings: Screen("settings")

    object BudgetDetail : Screen("budget_detail/{budgetId}") {
        fun createRoute(id: Int) = "budget_detail/$id"
    }

    object CategoryExpenses : Screen("category_expenses/{categoryName}") {
        fun createRoute(categoryName: String) = "category_expenses/$categoryName"
    }
    object AnalyticsDetail : Screen("analytics_detail/{categoryName}/{startDate}/{endDate}/{monthName}") {
        fun createRoute(category: String, start: Long, end: Long, month: String) =
            "analytics_detail/$category/$start/$end/$month"
    }

    object Search : Screen("search")
}