package com.felixjhonata.simplebudgetapp.repository

import com.felixjhonata.simplebudgetapp.data.room.dao.TotalBalanceDao
import com.felixjhonata.simplebudgetapp.data.room.dao.TransactionDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeRepository @Inject constructor(
    private val transactionDao: TransactionDao,
    private val totalBalanceDao: TotalBalanceDao
) {
    fun getTransactions() = transactionDao.getAll()
}