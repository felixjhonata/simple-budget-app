package com.felixjhonata.simplebudgetapp.shared.repository

import androidx.room.withTransaction
import com.felixjhonata.simplebudgetapp.MainDispatcherRule
import com.felixjhonata.simplebudgetapp.shared.data.room.dao.TotalBalanceDao
import com.felixjhonata.simplebudgetapp.shared.data.room.dao.TransactionDao
import com.felixjhonata.simplebudgetapp.shared.data.room.database.SimpleBudgetAppDatabase
import com.felixjhonata.simplebudgetapp.shared.data.room.entity.TotalBalance
import com.felixjhonata.simplebudgetapp.shared.data.room.entity.Transaction
import com.felixjhonata.simplebudgetapp.shared.model.TransactionType
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class TransactionRepositoryTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val db: SimpleBudgetAppDatabase = mockk()
    private val transactionDao: TransactionDao = mockk()
    private val totalBalanceDao: TotalBalanceDao = mockk()

    private lateinit var repository: TransactionRepository

    @Before
    fun setUp() {
        repository = TransactionRepository(db, transactionDao, totalBalanceDao)
        mockkStatic("androidx.room.RoomDatabaseKt")

        coEvery { db.withTransaction<Any>(any()) } coAnswers {
            secondArg<suspend () -> Any?>().invoke() ?: Unit
        }
    }

    @Test
    fun getTotalBalance_delegatesToTotalBalanceDao() = runTest {
        val expectedFlow = flowOf(TotalBalance(1, 100.0))
        every { totalBalanceDao.getTotalBalance() } returns expectedFlow

        val result = repository.getTotalBalance()

        assertEquals(expectedFlow, result)
        coVerify { totalBalanceDao.getTotalBalance() }
    }

    @Test
    fun insertTotalBalance_delegatesToTotalBalanceDao() = runTest {
        val totalBalance = TotalBalance(1, 100.0)
        coEvery { totalBalanceDao.insertTotalBalance(totalBalance) } just Runs

        repository.insertTotalBalance(totalBalance)

        coVerify { totalBalanceDao.insertTotalBalance(totalBalance) }
    }

    @Test
    fun getTransaction_delegatesToTransactionDao() = runTest {
        val transaction = Transaction(1, TransactionType.INCOME.name, 100.0, "USD", 0L)
        coEvery { transactionDao.getTransaction(1) } returns transaction

        val result = repository.getTransaction(1)

        assertEquals(transaction, result)
        coVerify { transactionDao.getTransaction(1) }
    }

    @Test
    fun getTransactions_delegatesToTransactionDao() = runTest {
        val expectedPagingSource = mockk<androidx.paging.PagingSource<Int, Transaction>>()
        every { transactionDao.getTransactions() } returns expectedPagingSource

        val result = repository.getTransactions()

        assertEquals(expectedPagingSource, result)
        coVerify { transactionDao.getTransactions() }
    }

    @Test
    fun insertTransaction_incomeType_updatesBalanceCorrectly() = runTest {
        val transaction = Transaction(1, TransactionType.INCOME.name, 100.0, "USD", 0L)
        val initialBalance = TotalBalance(1, 500.0)
        
        coEvery { transactionDao.addTransaction(transaction) } just Runs
        every { totalBalanceDao.getTotalBalance() } returns flowOf(initialBalance)
        coEvery { totalBalanceDao.updateTotalBalance(any()) } just Runs

        repository.insertTransaction(transaction)

        coVerify { transactionDao.addTransaction(transaction) }
        coVerify { totalBalanceDao.updateTotalBalance(match { it.totalBalance == 600.0 }) }
    }

    @Test
    fun insertTransaction_expenseType_updatesBalanceCorrectly() = runTest {
        val transaction = Transaction(1, TransactionType.EXPENSE.name, 100.0, "USD", 0L)
        val initialBalance = TotalBalance(1, 500.0)

        coEvery { transactionDao.addTransaction(transaction) } just Runs
        every { totalBalanceDao.getTotalBalance() } returns flowOf(initialBalance)
        coEvery { totalBalanceDao.updateTotalBalance(any()) } just Runs

        repository.insertTransaction(transaction)

        coVerify { transactionDao.addTransaction(transaction) }
        coVerify { totalBalanceDao.updateTotalBalance(match { it.totalBalance == 400.0 }) }
    }

    @Test
    fun updateTransaction_incomeToIncome_updatesBalanceCorrectly() = runTest {
        val oldTransaction = Transaction(1, TransactionType.INCOME.name, 100.0, "USD", 0L)
        val newTransaction = Transaction(1, TransactionType.INCOME.name, 150.0, "USD", 0L)
        val initialBalance = TotalBalance(1, 500.0)

        coEvery { transactionDao.updateTransaction(newTransaction) } just Runs
        every { totalBalanceDao.getTotalBalance() } returns flowOf(initialBalance)
        coEvery { totalBalanceDao.updateTotalBalance(any()) } just Runs

        repository.updateTransaction(oldTransaction, newTransaction)

        // 500 - 100 (old) + 150 (new) = 550
        coVerify { totalBalanceDao.updateTotalBalance(match { it.totalBalance == 550.0 }) }
    }

    @Test
    fun updateTransaction_incomeToExpense_updatesBalanceCorrectly() = runTest {
        val oldTransaction = Transaction(1, TransactionType.INCOME.name, 100.0, "USD", 0L)
        val newTransaction = Transaction(1, TransactionType.EXPENSE.name, 50.0, "USD", 0L)
        val initialBalance = TotalBalance(1, 500.0)

        coEvery { transactionDao.updateTransaction(newTransaction) } just Runs
        every { totalBalanceDao.getTotalBalance() } returns flowOf(initialBalance)
        coEvery { totalBalanceDao.updateTotalBalance(any()) } just Runs

        repository.updateTransaction(oldTransaction, newTransaction)

        // 500 - 100 (old income) - 50 (new expense) = 350
        coVerify { totalBalanceDao.updateTotalBalance(match { it.totalBalance == 350.0 }) }
    }

    @Test
    fun updateTransaction_expenseToIncome_updatesBalanceCorrectly() = runTest {
        val oldTransaction = Transaction(1, TransactionType.EXPENSE.name, 50.0, "USD", 0L)
        val newTransaction = Transaction(1, TransactionType.INCOME.name, 100.0, "USD", 0L)
        val initialBalance = TotalBalance(1, 500.0)

        coEvery { transactionDao.updateTransaction(newTransaction) } just Runs
        every { totalBalanceDao.getTotalBalance() } returns flowOf(initialBalance)
        coEvery { totalBalanceDao.updateTotalBalance(any()) } just Runs

        repository.updateTransaction(oldTransaction, newTransaction)

        // 500 + 50 (old expense reverted) + 100 (new income) = 650
        coVerify { totalBalanceDao.updateTotalBalance(match { it.totalBalance == 650.0 }) }
    }

    @Test
    fun updateTransaction_expenseToExpense_updatesBalanceCorrectly() = runTest {
        val oldTransaction = Transaction(1, TransactionType.EXPENSE.name, 100.0, "USD", 0L)
        val newTransaction = Transaction(1, TransactionType.EXPENSE.name, 50.0, "USD", 0L)
        val initialBalance = TotalBalance(1, 500.0)

        coEvery { transactionDao.updateTransaction(newTransaction) } just Runs
        every { totalBalanceDao.getTotalBalance() } returns flowOf(initialBalance)
        coEvery { totalBalanceDao.updateTotalBalance(any()) } just Runs

        repository.updateTransaction(oldTransaction, newTransaction)

        // 500 + 100 (old expense reverted) - 50 (new expense) = 550
        coVerify { totalBalanceDao.updateTotalBalance(match { it.totalBalance == 550.0 }) }
    }

    @Test
    fun deleteTransaction_incomeType_updatesBalanceCorrectly() = runTest {
        val transaction = Transaction(1, TransactionType.INCOME.name, 100.0, "USD", 0L)
        val initialBalance = TotalBalance(1, 500.0)

        coEvery { transactionDao.deleteTransaction(transaction) } just Runs
        every { totalBalanceDao.getTotalBalance() } returns flowOf(initialBalance)
        coEvery { totalBalanceDao.updateTotalBalance(any()) } just Runs

        repository.deleteTransaction(transaction)

        // 500 - 100 = 400
        coVerify { totalBalanceDao.updateTotalBalance(match { it.totalBalance == 400.0 }) }
    }

    @Test
    fun deleteTransaction_expenseType_updatesBalanceCorrectly() = runTest {
        val transaction = Transaction(1, TransactionType.EXPENSE.name, 100.0, "USD", 0L)
        val initialBalance = TotalBalance(1, 500.0)

        coEvery { transactionDao.deleteTransaction(transaction) } just Runs
        every { totalBalanceDao.getTotalBalance() } returns flowOf(initialBalance)
        coEvery { totalBalanceDao.updateTotalBalance(any()) } just Runs

        repository.deleteTransaction(transaction)

        // 500 + 100 = 600
        coVerify { totalBalanceDao.updateTotalBalance(match { it.totalBalance == 600.0 }) }
    }

    @Test
    fun fetchDBBackupData_delegatesToTransactionDao() = runTest {
        val transactions = listOf(Transaction(1, TransactionType.INCOME.name, 100.0, "USD", 0L))
        coEvery { transactionDao.getTransactionList() } returns transactions

        val result = repository.fetchDBBackupData()

        assertEquals(transactions, result.transactions)
        coVerify { transactionDao.getTransactionList() }
    }
}
