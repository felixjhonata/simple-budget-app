package com.felixjhonata.simplebudgetapp.home.viewmodel

import android.content.ContentResolver
import android.net.Uri
import androidx.paging.testing.asPagingSourceFactory
import androidx.paging.testing.asSnapshot
import com.felixjhonata.simplebudgetapp.MainDispatcherRule
import com.felixjhonata.simplebudgetapp.R
import com.felixjhonata.simplebudgetapp.home.model.uievent.HomeUiEvent
import com.felixjhonata.simplebudgetapp.shared.data.room.entity.TotalBalance
import com.felixjhonata.simplebudgetapp.shared.data.room.entity.Transaction
import com.felixjhonata.simplebudgetapp.shared.model.DBBackup
import com.felixjhonata.simplebudgetapp.shared.model.TransactionType
import com.felixjhonata.simplebudgetapp.shared.model.UiText
import com.felixjhonata.simplebudgetapp.shared.repository.TransactionRepository
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.OutputStream

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val contentResolver: ContentResolver = mockk()
    private val transactionRepository: TransactionRepository = mockk()

    private lateinit var viewModel: HomeViewModel

    @Before
    fun setUp() {
        every {
            transactionRepository.getTotalBalance()
        } throws NullPointerException() andThen flowOf(TotalBalance(1, 1000.0))
        
        coEvery {
            transactionRepository.insertTotalBalance(any())
        } returns Unit

        viewModel = HomeViewModel(contentResolver, transactionRepository, mainDispatcherRule.testDispatcher)
        coVerifyOrder {
            transactionRepository.getTotalBalance()
            transactionRepository.insertTotalBalance(any())
            transactionRepository.getTotalBalance()
        }
    }

    @After
    fun tearDown() {
        confirmVerified(contentResolver, transactionRepository)
        unmockkAll()
    }

    @Test
    fun getTotalBalance_throwsExceptionAndFailedToInsert_onlyRetryOnce() = runTest {
        every {
            transactionRepository.getTotalBalance()
        } throws NullPointerException()

        viewModel.getTotalBalance(false)

        advanceUntilIdle()

        // 3 because 2 from setup + 1 from call
        coVerify(exactly = 3) { transactionRepository.getTotalBalance() }
        // 2 because 1 from setup + 1 from call
        coVerify(exactly = 2) { transactionRepository.insertTotalBalance(any()) }
    }
    
    @Test
    fun fetchTransactionItems_pagingSourceIsValid_returnsValidPagingData() = runTest {
        val transactionList = createTransactionList()
        val transactionPagingSourceFactory = transactionList.asPagingSourceFactory()

        every { transactionRepository.getTransactions() } answers {
            transactionPagingSourceFactory.invoke()
        }

        val transactionSnapshot = viewModel.transactionItems.asSnapshot()

        coVerify { transactionRepository.getTransactions() }
        assertTrue(transactionSnapshot.isNotEmpty())
    }

    @Test
    fun fetchTransactionItems_pagingSourceIsEmpty_returnsValidPagingData() = runTest {
        val transactionPagingSourceFactory = emptyList<Transaction>().asPagingSourceFactory()

        every { transactionRepository.getTransactions() } answers {
            transactionPagingSourceFactory.invoke()
        }

        val transactionSnapshot = viewModel.transactionItems.asSnapshot()

        coVerify { transactionRepository.getTransactions() }
        assertTrue(transactionSnapshot.isEmpty())
    }

    @Test
    fun formatDate_OneAprilTwoThousandTwentySix_returnsFormattedString() {
        val epochSecond = 1775001600L // 1 April 2026
        val formattedDate = viewModel.formatDate(epochSecond)
        assertEquals("1 April 2026", formattedDate)
    }

    @Test
    fun saveJsonToUri_writeSuccessful_emitsSuccessSnackbar() = runTest {
        // given
        val dbBackupData = DBBackup(createTransactionList())
        val mockUri = mockk<Uri>()
        val mockOutputStream = mockk<OutputStream>()

        coEvery { transactionRepository.fetchDBBackupData() } returns dbBackupData
        coEvery { contentResolver.openOutputStream(mockUri) } returns mockOutputStream
        coEvery { mockOutputStream.write(any<ByteArray>()) } just Runs
        coEvery { mockOutputStream.close() } just Runs

        // when
        val values = mutableListOf<HomeUiEvent>()
        backgroundScope.launch(mainDispatcherRule.testDispatcher) {
            viewModel.uiEvent.toList(values)
        }

        viewModel.saveJsonToUri(mockUri)

        // then
        val snackbarUiEvent = (values[0] as? HomeUiEvent.ShowSnackbar)
        val stringRes = (snackbarUiEvent?.message as? UiText.StringResource)?.id
        assertEquals(R.string.successful_export, stringRes)

        coVerify { transactionRepository.fetchDBBackupData() }
        coVerify { contentResolver.openOutputStream(mockUri) }
        coVerify { mockOutputStream.write(any<ByteArray>()) }
        coVerify { mockOutputStream.close() }

        confirmVerified(mockUri, mockOutputStream)
    }

    @Test
    fun saveJsonToUri_writeFails_emitsErrorSnackbar() = runTest {
        // given
        val dbBackupData = DBBackup(createTransactionList())
        val mockUri = mockk<Uri>()
        val exception = Exception("Write failed")

        coEvery { transactionRepository.fetchDBBackupData() } returns dbBackupData
        coEvery { contentResolver.openOutputStream(mockUri) } throws exception

        // when
        val values = mutableListOf<HomeUiEvent>()
        backgroundScope.launch(mainDispatcherRule.testDispatcher) {
            viewModel.uiEvent.toList(values)
        }

        viewModel.saveJsonToUri(mockUri)

        // then
        val snackbarUiEvent = (values[0] as? HomeUiEvent.ShowSnackbar)
        val stringRes = (snackbarUiEvent?.message as? UiText.StringResource)?.id
        assertEquals(R.string.failed_export, stringRes)

        coVerify { transactionRepository.fetchDBBackupData() }
        coVerify { contentResolver.openOutputStream(mockUri) }

        confirmVerified(mockUri)
    }

    @Test
    fun readJsonFromUri_readSuccess_emitsSuccessSnackbar() = runTest {
        // given
        val transactions = createTransactionList()
        val dbBackupData = DBBackup(transactions)
        val jsonString = Json.encodeToString(dbBackupData)
        val mockUri = mockk<Uri>()
        val mockInputStream = ByteArrayInputStream(jsonString.toByteArray())

        coEvery { contentResolver.openInputStream(mockUri) } returns mockInputStream
        coEvery { transactionRepository.insertTransaction(any()) } just Runs

        // when
        val values = mutableListOf<HomeUiEvent>()
        backgroundScope.launch(mainDispatcherRule.testDispatcher) {
            viewModel.uiEvent.toList(values)
        }

        viewModel.readJsonFromUri(mockUri)

        // then
        val snackbarUiEvent = (values[0] as? HomeUiEvent.ShowSnackbar)
        val stringRes = (snackbarUiEvent?.message as? UiText.StringResource)?.id
        assertEquals(R.string.successful_import, stringRes)

        coVerify { contentResolver.openInputStream(mockUri) }
        coVerify(exactly = transactions.size) { transactionRepository.insertTransaction(any()) }

        confirmVerified(mockUri)
    }

    @Test
    fun readJsonFromUri_readNullBackup_emitsSuccessSnackbar() = runTest {
        // given
        val mockUri = mockk<Uri>()

        coEvery { contentResolver.openInputStream(mockUri) } returns null

        // when
        val values = mutableListOf<HomeUiEvent>()
        backgroundScope.launch(mainDispatcherRule.testDispatcher) {
            viewModel.uiEvent.toList(values)
        }

        viewModel.readJsonFromUri(mockUri)

        // then
        val snackbarUiEvent = (values[0] as? HomeUiEvent.ShowSnackbar)
        val stringRes = (snackbarUiEvent?.message as? UiText.StringResource)?.id
        assertEquals(R.string.successful_import, stringRes)

        coVerify { contentResolver.openInputStream(mockUri) }
        coVerify(exactly = 0) { transactionRepository.insertTransaction(any()) }

        confirmVerified(mockUri)
    }

    @Test
    fun readJsonFromUri_readFails_emitsErrorSnackbar() = runTest {
        // given
        val mockUri = mockk<Uri>()
        val exception = Exception("Read failed")

        coEvery { contentResolver.openInputStream(mockUri) } throws exception

        // when
        val values = mutableListOf<HomeUiEvent>()
        backgroundScope.launch(mainDispatcherRule.testDispatcher) {
            viewModel.uiEvent.toList(values)
        }

        viewModel.readJsonFromUri(mockUri)

        // then
        val snackbarUiEvent = (values[0] as? HomeUiEvent.ShowSnackbar)
        val stringRes = (snackbarUiEvent?.message as? UiText.StringResource)?.id
        assertEquals(R.string.failed_import, stringRes)

        coVerify { contentResolver.openInputStream(mockUri) }

        confirmVerified(mockUri)
    }

    private fun createTransactionList(): List<Transaction> {
        val day1 = 1776038400L // April 13, 2026
        val day2 = 1775952000L // April 12, 2026
        val day3 = 1773360000L // March 13, 2026
        val day4 = 1744502400L // April 13, 2025

        return listOf(
            Transaction(
                id = 1,
                type = TransactionType.INCOME.name,
                amount = 100.0,
                currency = "USD",
                date = day1 // 1 transaction on this day
            ),
            Transaction(
                id = 2,
                type = TransactionType.INCOME.name,
                amount = 200.0,
                currency = "USD",
                date = day2 // 1st of 2 transactions on this day
            ),
            Transaction(
                id = 3,
                type = TransactionType.EXPENSE.name,
                amount = 50.0,
                currency = "USD",
                date = day2 // 2nd of 2 transactions on this day
            ),
            Transaction(
                id = 4,
                type = TransactionType.INCOME.name,
                amount = 300.0,
                currency = "USD",
                date = day3 // Same date (13), different month (March vs April)
            ),
            Transaction(
                id = 5,
                type = TransactionType.INCOME.name,
                amount = 400.0,
                currency = "USD",
                date = day4 // Same date and month (April 13), different year (2025 vs 2026)
            )
        )
    }
}
