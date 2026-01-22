package com.example.expensetracker.domain.repository.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class TransactionModel(
    val id: Int,
    val title: String,
    val date: String,
    val amount: String,
    val color: Color,
    val icon: ImageVector,
)