package com.felixjhonata.simplebudgetapp.add_transaction.viewmodel

import com.felixjhonata.simplebudgetapp.MainDispatcherRule
import com.felixjhonata.simplebudgetapp.add_transaction.model.AddTransactionDialog
import com.felixjhonata.simplebudgetapp.shared.data.room.entity.Transaction
import com.felixjhonata.simplebudgetapp.shared.model.TransactionType
import com.felixjhonata.simplebudgetapp.shared.repository.TransactionRepository
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

class AddTransactionViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: AddTransactionViewModel
    private val mockTransactionRepository = mockk<TransactionRepository>()

    @Before
    fun setup() {
        viewModel = AddTransactionViewModel(
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
    fun numberInputted_inputIsOneAndTwo_updatesNumDisplay() {
        viewModel.setCurrentInput("1")
        assertEquals("1", viewModel.uiState.value.numDisplay)

        viewModel.setCurrentInput("2")
        assertEquals("12", viewModel.uiState.value.numDisplay)
    }

    @Test
    fun numberInputted_inputIsNotANumber_doNothing() {
        viewModel.setCurrentInput("something")

        assertEquals("0", viewModel.uiState.value.numDisplay)
    }

    @Test
    fun backspacePressed_numDisplayIsNinetyNine_updatesNumDisplay() {
        viewModel.setCurrentInput("99")
        assertEquals("99", viewModel.uiState.value.numDisplay)

        viewModel.onBackSpace()
        assertEquals("9", viewModel.uiState.value.numDisplay)
    }

    @Test
    fun addTransaction_success_callsRepositoryAndOnComplete() = runTest {
        val amount = "100000"
        val dateInMillis = 1775001600000 // 1 April 2026
        val expectedTransaction = Transaction(
            type = TransactionType.EXPENSE.toString(),
            currency = "IDR",
            amount = amount.toDouble(),
            date = TimeUnit.MILLISECONDS.toSeconds(dateInMillis)
        )
        var onCompleteCalled = false

        coEvery { mockTransactionRepository.insertTransaction(any()) } returns Unit

        viewModel.setCurrentInput(amount)
        viewModel.setDateInMillis(dateInMillis)

        viewModel.addTransaction {
            onCompleteCalled = true
        }

        coVerify { mockTransactionRepository.insertTransaction(expectedTransaction) }
        assertEquals(true, onCompleteCalled)
    }

    @Test
    fun datePickerDialogConfirmButtonClicked_OneAprilTwoThousandTwentySixSelected_updatesDate() {
        val millis = 1775001600000 // 1 April 2026 00:00:00 UTC in millis

        viewModel.setDateInMillis(millis)
        assertEquals("01 April 2026", viewModel.uiState.value.date)
        assertEquals(millis, viewModel.uiState.value.dateInMillis)
    }

    @Test
    fun datePickerDialogConfirmButtonClicked_NoDateIsSelected_doNothing() {
        val initialDate = viewModel.uiState.value.date
        val initialDateInMillis = viewModel.uiState.value.dateInMillis

        viewModel.setDateInMillis(null)
        assertEquals(initialDate, viewModel.uiState.value.date)
        assertEquals(initialDateInMillis, viewModel.uiState.value.dateInMillis)
    }

    @Test
    fun currentTypeExpense_transactionTypeToggled_transactionTypeIsIncome() {
        assertEquals(TransactionType.EXPENSE, viewModel.uiState.value.currentMode)

        viewModel.toggleMode()
        assertEquals(TransactionType.INCOME, viewModel.uiState.value.currentMode)
    }

    @Test
    fun currentTypeIncome_transactionTypeToggled_transactionTypeIsExpense() {
        viewModel.toggleMode()
        assertEquals(TransactionType.INCOME, viewModel.uiState.value.currentMode)

        viewModel.toggleMode()
        assertEquals(TransactionType.EXPENSE, viewModel.uiState.value.currentMode)
    }

    @Test
    fun dialogIsHidden_editDatePressed_showDialog() {
        assertEquals(AddTransactionDialog.None, viewModel.uiState.value.dialog)

        viewModel.showDialog(AddTransactionDialog.DatePicker)
        assertEquals(AddTransactionDialog.DatePicker, viewModel.uiState.value.dialog)
    }

    @Test
    fun datePickerDialogIsShown_dialogDismissed_hideDialog() {
        viewModel.showDialog(AddTransactionDialog.DatePicker)
        assertEquals(AddTransactionDialog.DatePicker, viewModel.uiState.value.dialog)

        viewModel.hideDialog()
        assertEquals(AddTransactionDialog.None, viewModel.uiState.value.dialog)
    }

}