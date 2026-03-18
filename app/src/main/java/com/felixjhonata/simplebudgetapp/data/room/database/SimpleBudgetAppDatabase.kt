package com.felixjhonata.simplebudgetapp.data.room.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.felixjhonata.simplebudgetapp.data.room.dao.TransactionDao
import com.felixjhonata.simplebudgetapp.data.room.entity.Transaction

@Database(
    entities = [Transaction::class],
    version = 1,
    exportSchema = false
)
abstract class SimpleBudgetAppDatabase: RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
}