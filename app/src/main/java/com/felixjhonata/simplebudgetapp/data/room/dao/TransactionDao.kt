package com.felixjhonata.simplebudgetapp.data.room.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.felixjhonata.simplebudgetapp.data.room.entity.Transaction

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY date DESC, id DESC")
    fun getAll(): PagingSource<Int, Transaction>

    @Insert
    suspend fun addTransaction(transaction: Transaction)
}