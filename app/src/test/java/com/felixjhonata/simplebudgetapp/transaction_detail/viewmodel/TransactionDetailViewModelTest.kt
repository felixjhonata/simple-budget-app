package com.felixjhonata.simplebudgetapp.transaction_detail.viewmodel

import com.felixjhonata.simplebudgetapp.MainDispatcherRule
import com.felixjhonata.simplebudgetapp.shared.data.room.entity.Transaction
import com.felixjhonata.simplebudgetapp.shared.model.TransactionType
import com.felixjhonata.simplebudgetapp.shared.repository.TransactionRepository
import com.felixjhonata.simplebudgetapp.shared.util.convertEpochSecondToLocalDateTime
import com.felixjhonata.simplebudgetapp.shared.util.toLocalizedString
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.just
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalCoroutinesApi::class)
class TransactionDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val transactionRepository: TransactionRepository = mockk()

    private lateinit var viewModel: TransactionDetailViewModel

    @Before
    fun setUp() {
        viewModel = TransactionDetailViewModel(
            transactionRepository,
            mainDispatcherRule.testDispatcher,
            mainDispatcherRule.testDispatcher
        )
    }

    @After
    fun tearDown() {
        confirmVerified(transactionRepository)
        unmockkAll()
    }

    @Test
    fun load_existingId_updatesUiState() = runTest {
        // given
        val id = 1
        val epochSecond = 1775001600L // 1 April 2026 00:00 UTC
        val transaction = Transaction(
            id = id,
            type = TransactionType.INCOME.name,
            amount = 1000.0,
            currency = "IDR",
            date = epochSecond
        )
        coEvery { transactionRepository.getTransaction(id) } returns transaction

        // when
        viewModel.load(id)

        // then
        val expectedDate = epochSecond.convertEpochSecondToLocalDateTime().format(
            DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm", Locale.getDefault())
        )
        val expectedAmount = "IDR ${1000.0.toLocalizedString()}"

        assertEquals(expectedDate, viewModel.uiState.value.date)
        assertEquals(TransactionType.INCOME.name, viewModel.uiState.value.type)
        assertEquals(expectedAmount, viewModel.uiState.value.amount)

        coVerify { transactionRepository.getTransaction(id) }
    }

    @Test
    fun load_nonExistingId_doesNotUpdateUiState() = runTest {
        // given
        val id = 1
        coEvery { transactionRepository.getTransaction(id) } returns null

        // when
        viewModel.load(id)

        // then
        assertEquals("", viewModel.uiState.value.date)
        assertEquals("", viewModel.uiState.value.type)
        assertEquals("", viewModel.uiState.value.amount)

        coVerify { transactionRepository.getTransaction(id) }
    }

    @Test
    fun deleteTransaction_transactionLoaded_callsRepositoryAndDeleteCallback() = runTest {
        // given
        val id = 1
        val transaction = Transaction(
            id = id,
            type = TransactionType.INCOME.name,
            amount = 1000.0,
            currency = "IDR",
            date = 1775001600L
        )
        coEvery { transactionRepository.getTransaction(id) } returns transaction
        coEvery { transactionRepository.deleteTransaction(transaction) } just Runs
        
        var onCompleteCalled = false
        val onComplete = { onCompleteCalled = true }

        viewModel.load(id)

        // when
        viewModel.deleteTransaction(onComplete)

        // then
        assertTrue(onCompleteCalled)
        coVerify { 
            transactionRepository.getTransaction(id)
            transactionRepository.deleteTransaction(transaction) 
        }
    }

    @Test
    fun deleteTransaction_transactionNotLoaded_callsOnlyDeleteCallback() = runTest {
        // given
        var onCompleteCalled = false
        val onComplete = { onCompleteCalled = true }

        // when
        viewModel.deleteTransaction(onComplete)

        // then
        assertTrue(onCompleteCalled)
        coVerify(exactly = 0) { transactionRepository.deleteTransaction(any()) }
    }

    @Test
    fun toggleDeleteDialog_changesShowDeleteDialogState() {
        // initial state
        assertFalse(viewModel.uiState.value.showDeleteDialog)

        // first toggle
        viewModel.toggleDeleteDialog()
        assertTrue(viewModel.uiState.value.showDeleteDialog)

        // second toggle
        viewModel.toggleDeleteDialog()
        assertFalse(viewModel.uiState.value.showDeleteDialog)
    }
}
