package com.felixjhonata.simplebudgetapp.shared.data.room.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.felixjhonata.simplebudgetapp.shared.data.room.entity.Transaction

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions WHERE id = :id LIMIT 1")
    suspend fun getTransaction(id: Int): Transaction?

    @Query("SELECT * FROM transactions ORDER BY date DESC, id DESC")
    fun getTransactions(): PagingSource<Int, Transaction>

    @Insert
    suspend fun addTransaction(transaction: Transaction)

    @Update
    suspend fun updateTransaction(transaction: Transaction)

    @Delete
    suspend fun deleteTransaction(transaction: Transaction)
}