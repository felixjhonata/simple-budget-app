package com.felixjhonata.simplebudgetapp.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.felixjhonata.simplebudgetapp.data.room.entity.TotalBalance
import kotlinx.coroutines.flow.Flow

@Dao
interface TotalBalanceDao {
    @Query("SELECT * FROM total_balance WHERE id = 1 LIMIT 1")
    fun getTotalBalance(): Flow<TotalBalance>

    @Update
    suspend fun updateTotalBalance(totalBalance: TotalBalance)

    @Insert
    suspend fun insertTotalBalance(totalBalance: TotalBalance)
}