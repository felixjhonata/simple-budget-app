package com.felixjhonata.simplebudgetapp.repository

import com.felixjhonata.simplebudgetapp.data.room.dao.TransactionDao
import com.felixjhonata.simplebudgetapp.data.room.entity.Transaction
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddTransactionRepository @Inject constructor(
    private val transactionDao: TransactionDao
) {
    suspend fun addTransaction(transaction: Transaction) =
        transactionDao.addTransaction(transaction)
}