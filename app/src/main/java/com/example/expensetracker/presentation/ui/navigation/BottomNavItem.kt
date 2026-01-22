package com.example.expensetracker.presentation.ui.navigation


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object Home : BottomNavItem("home", Icons.Default.Home, "Home")
    object Analytics : BottomNavItem("analytics", Icons.Default.Analytics, "Stats")
    object Budget : BottomNavItem("budget", Icons.Default.PieChart, "Budget")
    object Settings : BottomNavItem("settings", Icons.Default.Settings, "Settings")
}