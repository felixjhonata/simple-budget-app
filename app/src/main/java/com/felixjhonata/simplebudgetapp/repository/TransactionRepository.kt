package com.felixjhonata.simplebudgetapp.repository

import androidx.room.withTransaction
import com.felixjhonata.simplebudgetapp.data.room.dao.TotalBalanceDao
import com.felixjhonata.simplebudgetapp.data.room.dao.TransactionDao
import com.felixjhonata.simplebudgetapp.data.room.database.SimpleBudgetAppDatabase
import com.felixjhonata.simplebudgetapp.data.room.entity.TotalBalance
import com.felixjhonata.simplebudgetapp.data.room.entity.Transaction
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepository @Inject constructor(
    private val db: SimpleBudgetAppDatabase,
    private val transactionDao: TransactionDao,
    private val totalBalanceDao: TotalBalanceDao
) {
    // Total Balance

    fun getTotalBalance() = totalBalanceDao.getTotalBalance()

    suspend fun insertTotalBalance(totalBalance: TotalBalance) {
        return totalBalanceDao.insertTotalBalance(totalBalance)
    }

    // Transaction
    suspend fun getTransaction(id: Int) = transactionDao.getTransaction(id)

    fun getTransactions() = transactionDao.getTransactions()

    suspend fun insertTransaction(transaction: Transaction) {
        db.withTransaction {
            transactionDao.addTransaction(transaction)

            val currentBalance = totalBalanceDao.getTotalBalance().first().totalBalance
            val newBalance = if (transaction.type == "INCOME") {
                currentBalance + transaction.amount
            } else {
                currentBalance - transaction.amount
            }

            totalBalanceDao.updateTotalBalance(
                TotalBalance(1, newBalance)
            )
        }
    }

    suspend fun updateTransaction(oldTransaction: Transaction, newTransaction: Transaction) {
        db.withTransaction {
            transactionDao.updateTransaction(newTransaction)

            val currentBalance = totalBalanceDao.getTotalBalance().first().totalBalance
            val oldAmount = oldTransaction.amount
            val newAmount = newTransaction.amount

            var newBalance = currentBalance

            if (oldTransaction.type == "INCOME") {
                newBalance -= oldAmount
            } else {
                newBalance += oldAmount
            }

            if (newTransaction.type == "INCOME") {
                newBalance += newAmount
            } else {
                newBalance -= newAmount
            }

            totalBalanceDao.updateTotalBalance(
                TotalBalance(1, newBalance)
            )
        }
    }

    suspend fun deleteTransaction(transaction: Transaction) {
        db.withTransaction {
            transactionDao.deleteTransaction(transaction)

            val oldBalance = totalBalanceDao.getTotalBalance().first().totalBalance
            val newBalance = if (transaction.type == "INCOME") {
                oldBalance - transaction.amount
            } else {
                oldBalance + transaction.amount
            }

            totalBalanceDao.updateTotalBalance(
                TotalBalance(1, newBalance)
            )
        }
    }
}