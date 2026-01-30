package com.example.saveit.data.local.dao

import TransactionEntity
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    //List of transactions ordered by date
    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    //Recent activity (limit 5)
    @Query("SELECT * FROM transactions ORDER BY date DESC LIMIT 5")
    fun getRecentTransactions(): Flow<List<TransactionEntity>>

    // Calculate Total Income/Expense
    @Query("SELECT SUM(amount) FROM transactions WHERE type = :type")
    fun getTotalByType(type: String): Flow<Double?>

    // Returns a custom data class (defined below) with category and total sum
    @Query("SELECT category, SUM(amount) as total FROM transactions WHERE type = 'EXPENSE' GROUP BY category")
    fun getSpendingByCategory(): Flow<List<CategoryTuple>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)

    @Query("""
    SELECT category, SUM(amount) as total 
    FROM transactions 
    WHERE type = 'EXPENSE' AND date >= :startDate AND date <= :endDate 
    GROUP BY category
""")
    fun getSpendingByCategoryByDate(startDate: Long, endDate: Long): Flow<List<CategoryTuple>>

    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'EXPENSE' AND date >= :startDate AND date <= :endDate")
    fun getTotalExpenseByDate(startDate: Long, endDate: Long): Flow<Double?>

    @Query("SELECT * FROM transactions WHERE id = :id")
    fun getTransactionById(id: Int): Flow<TransactionEntity?>

    @Query("SELECT * FROM transactions WHERE category = :category ORDER BY date DESC")
    fun getTransactionsByCategory(category: String): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE category = :category AND date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getTransactionsByCategoryAndDate(category: String, startDate: Long, endDate: Long): Flow<List<TransactionEntity>>

    // Add this new query
    @Query("SELECT * FROM transactions WHERE category = :category AND type = :type ORDER BY date DESC")
    fun getTransactionsByCategoryAndType(category: String, type: String): Flow<List<TransactionEntity>>
}

// Helper class for the Analytics Query
data class CategoryTuple(
    val category: String,
    val total: Double
)
