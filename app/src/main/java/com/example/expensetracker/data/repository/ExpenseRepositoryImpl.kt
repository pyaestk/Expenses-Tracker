package com.example.expensetracker.data.repository

import BudgetDao
import TransactionEntity
import com.example.expensetracker.data.local.dao.TransactionDao
import com.example.expensetracker.data.local.dao.CategoryTuple
import com.example.expensetracker.data.local.entity.BudgetEntity
import kotlinx.coroutines.flow.Flow

class ExpenseRepositoryImpl(
    private val transactionDao: TransactionDao,
    private val budgetDao: BudgetDao
) : ExpenseRepository {

    override fun getAllTransactions(): Flow<List<TransactionEntity>> =
        transactionDao.getAllTransactions()

    override fun getRecentTransactions(): Flow<List<TransactionEntity>> =
        transactionDao.getRecentTransactions()

    override fun getTotalIncome(): Flow<Double?> =
        transactionDao.getTotalByType("INCOME")

    override fun getTotalExpense(): Flow<Double?> =
        transactionDao.getTotalByType("EXPENSE")

    override fun getSpendingByCategory(): Flow<List<CategoryTuple>> =
        transactionDao.getSpendingByCategory()

    override suspend fun addTransaction(transaction: TransactionEntity) =
        transactionDao.insertTransaction(transaction)

    override fun getBudgetsForMonth(month: Int, year: Int): Flow<List<BudgetEntity>> =
        budgetDao.getBudgetsForMonth(month, year)

    override suspend fun setBudget(budget: BudgetEntity) =
        budgetDao.insertBudget(budget)


    override fun getSpendingByCategoryByDate(startDate: Long, endDate: Long) =
        transactionDao.getSpendingByCategoryByDate(startDate, endDate)

    override fun getTotalExpenseByDate(startDate: Long, endDate: Long) =
        transactionDao.getTotalExpenseByDate(startDate, endDate)

    override fun getTransaction(id: Int): Flow<TransactionEntity?> =
        transactionDao.getTransactionById(id)

    override suspend fun deleteTransaction(transaction: TransactionEntity) =
        transactionDao.deleteTransaction(transaction)

    override fun getBudget(id: Int): Flow<BudgetEntity?> = budgetDao.getBudgetById(id)

    override suspend fun deleteBudget(budget: BudgetEntity) = budgetDao.deleteBudget(budget)

    override fun getTransactionsByCategory(category: String): Flow<List<TransactionEntity>> =
        transactionDao.getTransactionsByCategory(category)

    override fun getTransactionsByCategoryAndDate(category: String, startDate: Long, endDate: Long): Flow<List<TransactionEntity>> {
        return transactionDao.getTransactionsByCategoryAndDate(category, startDate, endDate)
    }
}