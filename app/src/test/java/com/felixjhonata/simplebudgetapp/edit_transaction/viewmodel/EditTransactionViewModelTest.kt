package com.felixjhonata.simplebudgetapp.edit_transaction.viewmodel

import com.felixjhonata.simplebudgetapp.MainDispatcherRule
import com.felixjhonata.simplebudgetapp.edit_transaction.model.EditTransactionDialog
import com.felixjhonata.simplebudgetapp.shared.data.room.entity.Transaction
import com.felixjhonata.simplebudgetapp.shared.model.TransactionType
import com.felixjhonata.simplebudgetapp.shared.repository.TransactionRepository
import com.felixjhonata.simplebudgetapp.shared.util.toLocalizedString
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.TimeUnit

class EditTransactionViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: EditTransactionViewModel
    private val mockTransactionRepository = mockk<TransactionRepository>()

    @Before
    fun setup() {
        viewModel = EditTransactionViewModel(
            mockTransactionRepository,
            mainDispatcherRule.testDispatcher,
            mainDispatcherRule.testDispatcher
        )
    }

    @After
    fun tearDown() {
        confirmVerified(mockTransactionRepository)
    }

    @Test
    fun load_existingTransaction_updatesUiState() = runTest {
        val transactionId = 1
        val amount = 50000.0
        val dateSeconds = 1712880000L // 12 April 2024
        val transaction = Transaction(
            id = transactionId,
            type = TransactionType.EXPENSE.toString(),
            amount = amount,
            currency = "IDR",
            date = dateSeconds
        )
        val expectedDateMillis = TimeUnit.SECONDS.toMillis(dateSeconds)
        var selectedDateFromCallback: Long? = null

        coEvery { mockTransactionRepository.getTransaction(transactionId) } returns transaction

        viewModel.load(transactionId) { selectedDateFromCallback = it }

        // Wait for coroutine to finish
        coVerify { mockTransactionRepository.getTransaction(transactionId) }
        
        assertEquals(amount.toLocalizedString(), viewModel.uiState.value.numDisplay)
        assertEquals(TransactionType.EXPENSE, viewModel.uiState.value.type)
        assertEquals(expectedDateMillis, viewModel.uiState.value.dateInMillis)
        assertEquals("12 April 2024", viewModel.uiState.value.date)
        assertEquals(expectedDateMillis, selectedDateFromCallback)
    }

    @Test
    fun updateTransaction_success_callsRepositoryAndUpdateTransaction() = runTest {
        val transactionId = 1
        val oldTransaction = Transaction(
            id = transactionId,
            type = TransactionType.EXPENSE.toString(),
            amount = 50000.0,
            currency = "IDR",
            date = 1712880000L
        )
        
        coEvery { mockTransactionRepository.getTransaction(transactionId) } returns oldTransaction
        coEvery { mockTransactionRepository.updateTransaction(any(), any()) } returns Unit

        viewModel.load(transactionId) { }

        // Change some values
        viewModel.setInputtedNumber("5") // 50000 * 10 + 5 = 500005
        viewModel.toggleMode() // From EXPENSE to INCOME
        val newDateMillis = 1712966400000L // 13 April 2024
        viewModel.setDate(newDateMillis)

        var onCompleteCalled = false
        viewModel.updateTransaction { onCompleteCalled = true }

        val expectedNewTransaction = oldTransaction.copy(
            type = TransactionType.INCOME.toString(),
            amount = 500005.0,
            date = TimeUnit.MILLISECONDS.toSeconds(newDateMillis)
        )

        coVerify {
            mockTransactionRepository.getTransaction(transactionId)
            mockTransactionRepository.updateTransaction(oldTransaction, expectedNewTransaction)
        }
        assertEquals(true, onCompleteCalled)
    }

    @Test
    fun setInputtedNumber_validInput_updatesNumDisplay() {
        viewModel.setInputtedNumber("1")
        assertEquals("1", viewModel.uiState.value.numDisplay)

        viewModel.setInputtedNumber("2")
        assertEquals("12", viewModel.uiState.value.numDisplay)
    }

    @Test
    fun setInputtedNumber_invalidInput_doesNotUpdateNumDisplay() {
        viewModel.setInputtedNumber("a")
        assertEquals("0", viewModel.uiState.value.numDisplay)
    }

    @Test
    fun onBackSpace_updatesNumDisplay() {
        viewModel.setInputtedNumber("1")
        viewModel.setInputtedNumber("2")
        assertEquals("12", viewModel.uiState.value.numDisplay)

        viewModel.onBackSpace()
        assertEquals("1", viewModel.uiState.value.numDisplay)

        viewModel.onBackSpace()
        assertEquals("0", viewModel.uiState.value.numDisplay)
    }

    @Test
    fun toggleMode_updatesType() {
        assertEquals(TransactionType.EXPENSE, viewModel.uiState.value.type)

        viewModel.toggleMode()
        assertEquals(TransactionType.INCOME, viewModel.uiState.value.type)

        viewModel.toggleMode()
        assertEquals(TransactionType.EXPENSE, viewModel.uiState.value.type)
    }

    @Test
    fun setDate_updatesUiState() {
        val dateMillis = 1712880000000L // 12 April 2024
        viewModel.setDate(dateMillis)

        assertEquals("12 April 2024", viewModel.uiState.value.date)
        assertEquals(dateMillis, viewModel.uiState.value.dateInMillis)
    }

    @Test
    fun setDialog_updatesUiState() {
        viewModel.setDialog(EditTransactionDialog.DatePicker)
        assertEquals(EditTransactionDialog.DatePicker, viewModel.uiState.value.dialog)

        viewModel.hideDialog()
        assertEquals(EditTransactionDialog.None, viewModel.uiState.value.dialog)
    }

    @Test
    fun load_transactionNotFound_doesNotUpdateUiState() = runTest {
        val transactionId = 99
        coEvery { mockTransactionRepository.getTransaction(transactionId) } returns null
        var onCompleteCalled = false

        viewModel.load(transactionId) { onCompleteCalled = true }

        coVerify { mockTransactionRepository.getTransaction(transactionId) }
        assertEquals(false, onCompleteCalled)
        assertEquals("0", viewModel.uiState.value.numDisplay)
    }

    @Test
    fun updateTransaction_oldTransactionIsNull_doesNotCallRepositoryButCallsOnComplete() = runTest {
        var onCompleteCalled = false

        viewModel.updateTransaction { onCompleteCalled = true }

        coVerify(exactly = 0) { mockTransactionRepository.updateTransaction(any(), any()) }
        assertEquals(true, onCompleteCalled)
    }
}
