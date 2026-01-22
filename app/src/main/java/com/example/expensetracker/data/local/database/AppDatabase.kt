package com.example.expensetracker.data.local

import BudgetDao
import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.expensetracker.data.local.dao.TransactionDao
import com.example.expensetracker.data.local.entity.BudgetEntity

@Database(
    entities = [TransactionEntity::class, BudgetEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun budgetDao(): BudgetDao
}