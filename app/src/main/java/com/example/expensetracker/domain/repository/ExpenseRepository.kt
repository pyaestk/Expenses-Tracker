package com.example.expensetracker.data.repository

import TransactionEntity
import com.example.expensetracker.data.local.dao.CategoryTuple
import com.example.expensetracker.data.local.entity.BudgetEntity
import kotlinx.coroutines.flow.Flow

interface ExpenseRepository {
    // Transactions
    fun getAllTransactions(): Flow<List<TransactionEntity>>
    fun getRecentTransactions(): Flow<List<TransactionEntity>>
    fun getTotalIncome(): Flow<Double?>
    fun getTotalExpense(): Flow<Double?>
    fun getSpendingByCategory(): Flow<List<CategoryTuple>>
    suspend fun addTransaction(transaction: TransactionEntity)

    // Budgets
    fun getBudgetsForMonth(month: Int, year: Int): Flow<List<BudgetEntity>>
    suspend fun setBudget(budget: BudgetEntity)

    fun getSpendingByCategoryByDate(startDate: Long, endDate: Long): Flow<List<CategoryTuple>>
    fun getTotalExpenseByDate(startDate: Long, endDate: Long): Flow<Double?>

    fun getTransaction(id: Int): Flow<TransactionEntity?>
    suspend fun deleteTransaction(transaction: TransactionEntity)

    fun getBudget(id: Int): Flow<BudgetEntity?>
    suspend fun deleteBudget(budget: BudgetEntity)

    fun getTransactionsByCategory(category: String): Flow<List<TransactionEntity>>

    fun getTransactionsByCategoryAndDate(category: String, startDate: Long, endDate: Long): Flow<List<TransactionEntity>>
}