package com.felixjhonata.simplebudgetapp.repository

import com.felixjhonata.simplebudgetapp.data.room.dao.TransactionDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomePageRepository @Inject constructor(
    private val transactionDao: TransactionDao
) {
    fun getTransactions() = transactionDao.getAll()
}