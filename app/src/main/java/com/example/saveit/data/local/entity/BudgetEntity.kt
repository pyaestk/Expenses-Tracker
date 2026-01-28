package com.example.saveit.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "budgets")
data class BudgetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val category: String,
    val amountLimit: Double,
    val month: Int,
    val year: Int
)