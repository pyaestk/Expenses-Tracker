package com.example.saveit.domain.repository.model

import CategoryModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import com.example.saveit.ui.theme.*

object CategoryConstants {

    // Expense Categories
    val expenseCategories = listOf(
        CategoryModel("Food", Icons.Default.Fastfood, BluePrimary, BlueSecondary),
        CategoryModel("Travel", Icons.Default.DirectionsCar, Color(0xFF2196F3), Color(0xFFE3F2FD)),
        CategoryModel("Shop", Icons.Default.ShoppingBag, Color(0xFF9C27B0), Color(0xFFF3E5F5)),
        CategoryModel("Bills", Icons.Default.Bolt, Color(0xFF009688), Color(0xFFE0F2F1)),
        CategoryModel("Health", Icons.Default.MedicalServices, Color(0xFFF44336), Color(0xFFFFEBEE)),
        CategoryModel("Fun", Icons.Default.Movie, OrangeEntertainment, Color(0xFFFFF3E0)),
        CategoryModel("Edu", Icons.Default.School, Color.DarkGray, Color(0xFFEEEEEE)),
        CategoryModel("Other", Icons.Default.MoreHoriz, Color(0xFF607D8B), Color(0xFFECEFF1))
    )

    // Income Categories
    val incomeCategories = listOf(
        CategoryModel("Salary", Icons.Default.AttachMoney, GreenIncome, Color(0xFFE8F5E9)),
        CategoryModel("Freelance", Icons.Default.Computer, GreenIncome, Color(0xFFE8F5E9)),
        CategoryModel("Gift", Icons.Default.CardGiftcard, GreenIncome, Color(0xFFE8F5E9)),
        CategoryModel("Invest", Icons.Default.TrendingUp, GreenIncome, Color(0xFFE8F5E9)),
        CategoryModel("Other", Icons.Default.MoreHoriz, Color(0xFF607D8B), Color(0xFFECEFF1))
    )

    // Helper to get all for mapping
    fun getCategoryConfig(name: String): CategoryModel {
        return (expenseCategories + incomeCategories).find { it.name == name }
            ?: CategoryModel("Other", Icons.Default.Apps, Color.Gray, Color.LightGray)
    }
}