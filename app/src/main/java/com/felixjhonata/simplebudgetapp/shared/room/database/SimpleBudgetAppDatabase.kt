package com.felixjhonata.simplebudgetapp.shared.room.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.felixjhonata.simplebudgetapp.shared.room.dao.TotalBalanceDao
import com.felixjhonata.simplebudgetapp.shared.room.dao.TransactionDao
import com.felixjhonata.simplebudgetapp.shared.room.entity.TotalBalance
import com.felixjhonata.simplebudgetapp.shared.room.entity.Transaction

@Database(
    entities = [Transaction::class, TotalBalance::class],
    version = 1
)
abstract class SimpleBudgetAppDatabase: RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun totalBalanceDao(): TotalBalanceDao
}