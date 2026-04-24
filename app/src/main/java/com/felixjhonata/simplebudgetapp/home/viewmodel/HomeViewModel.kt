package com.felixjhonata.simplebudgetapp.home.viewmodel

import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import com.felixjhonata.simplebudgetapp.R
import com.felixjhonata.simplebudgetapp.home.model.TransactionItemUiModel
import com.felixjhonata.simplebudgetapp.home.model.uievent.HomeUiEvent
import com.felixjhonata.simplebudgetapp.shared.data.room.entity.TotalBalance
import com.felixjhonata.simplebudgetapp.shared.model.DBBackup
import com.felixjhonata.simplebudgetapp.shared.model.TransactionType
import com.felixjhonata.simplebudgetapp.shared.model.UiText
import com.felixjhonata.simplebudgetapp.shared.module.IoDispatcher
import com.felixjhonata.simplebudgetapp.shared.repository.TransactionRepository
import com.felixjhonata.simplebudgetapp.shared.util.convertEpochSecondToLocalDateTime
import com.felixjhonata.simplebudgetapp.shared.util.readFromUri
import com.felixjhonata.simplebudgetapp.shared.util.toLocalizedString
import com.felixjhonata.simplebudgetapp.shared.util.writeToUri
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val contentResolver: ContentResolver,
    private val transactionRepository: TransactionRepository,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
): ViewModel() {
    private val _totalBalance = MutableStateFlow("0")
    val totalBalance = _totalBalance.asStateFlow()

    private val _uiEvent = MutableSharedFlow<HomeUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    val transactionItems by lazy {
        Pager(
            config = PagingConfig(50),
        ) {
            transactionRepository.getTransactions()
        }.flow.map { pagingData ->
            pagingData.map {
                TransactionItemUiModel.TransactionItem(
                    id = it.id,
                    type = TransactionType.valueOf(it.type),
                    currency = it.currency,
                    amount = it.amount,
                    epochTime = it.date
                )
            }.insertSeparators { before, after ->
                when {
                    (before == null && after != null)
                        || (before != null && after != null && shouldSeparate(before, after)) -> {
                        TransactionItemUiModel.Date(after.epochTime)
                    }

                    else -> null
                }
            }
        }.cachedIn(viewModelScope)
    }

    private val formatter by lazy {
        DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.getDefault())
    }

    init {
        getTotalBalance()
    }

    fun getTotalBalance(shouldRetry: Boolean = true) {
        viewModelScope.launch(ioDispatcher) {
            try {
                transactionRepository.getTotalBalance().collect { item ->
                    _totalBalance.update {
                        item.totalBalance.toLocalizedString()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                transactionRepository.insertTotalBalance(TotalBalance(1, 0.0))
                if (shouldRetry) getTotalBalance(false)
            }
        }
    }

    private fun shouldSeparate(before: TransactionItemUiModel.TransactionItem, after: TransactionItemUiModel.TransactionItem): Boolean {
        val beforeDate = Instant.ofEpochSecond(before.epochTime)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()

        val afterDate = Instant.ofEpochSecond(after.epochTime)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()

        return beforeDate.year != afterDate.year
                || beforeDate.monthValue != afterDate.monthValue
                || beforeDate.dayOfMonth != afterDate.dayOfMonth
    }

    fun formatDate(epochSecond: Long): String =
        epochSecond.convertEpochSecondToLocalDateTime().format(formatter)

    fun saveJsonToUri(uri: Uri) {
        viewModelScope.launch(ioDispatcher) {
            val jsonData = transactionRepository.fetchDBBackupData()

            try {
                contentResolver.writeToUri(uri, jsonData)
                _uiEvent.emit(
                    HomeUiEvent.ShowSnackbar(
                        UiText.StringResource(R.string.successful_export)
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
                _uiEvent.emit(
                    HomeUiEvent.ShowSnackbar(
                        UiText.StringResource(R.string.failed_export)
                    )
                )
            }
        }
    }

    fun readJsonFromUri(uri: Uri) {
        viewModelScope.launch(ioDispatcher) {
            try {
                val dbBackup = contentResolver.readFromUri<DBBackup>(uri)
                dbBackup?.run {
                    transactions.forEach {
                        transactionRepository.insertTransaction(it)
                    }
                }
                _uiEvent.emit(
                    HomeUiEvent.ShowSnackbar(
                        UiText.StringResource(R.string.successful_import)
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
                _uiEvent.emit(
                    HomeUiEvent.ShowSnackbar(
                        UiText.StringResource(R.string.failed_import)
                    )
                )
            }
        }
    }
}