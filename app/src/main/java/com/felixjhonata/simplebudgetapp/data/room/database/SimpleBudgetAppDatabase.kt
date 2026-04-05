package com.felixjhonata.simplebudgetapp.data.room.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.felixjhonata.simplebudgetapp.data.room.dao.TotalBalanceDao
import com.felixjhonata.simplebudgetapp.data.room.dao.TransactionDao
import com.felixjhonata.simplebudgetapp.data.room.entity.TotalBalance
import com.felixjhonata.simplebudgetapp.data.room.entity.Transaction

@Database(
    entities = [Transaction::class, TotalBalance::class],
    version = 1
)
abstract class SimpleBudgetAppDatabase: RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun totalBalanceDao(): TotalBalanceDao
}