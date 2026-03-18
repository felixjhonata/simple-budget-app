package com.felixjhonata.simplebudgetapp.data.room.dao

import androidx.room.Dao
import androidx.room.Query
import com.felixjhonata.simplebudgetapp.data.room.entity.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM `Transaction`")
    fun getAll(): Flow<List<Transaction>>
}