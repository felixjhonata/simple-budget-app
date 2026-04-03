package com.felixjhonata.simplebudgetapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import com.felixjhonata.simplebudgetapp.data.room.entity.TotalBalance
import com.felixjhonata.simplebudgetapp.model.TransactionItemUiModel
import com.felixjhonata.simplebudgetapp.model.TransactionType
import com.felixjhonata.simplebudgetapp.repository.TransactionRepository
import com.felixjhonata.simplebudgetapp.util.convertEpochSecondToLocalDateTime
import com.felixjhonata.simplebudgetapp.util.toLocalizedString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
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
    private val transactionRepository: TransactionRepository
): ViewModel() {
    private val _totalBalance = MutableStateFlow("0")
    val totalBalance = _totalBalance.asStateFlow()

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
        viewModelScope.launch(Dispatchers.IO) {
            try {
                transactionRepository.getTotalBalance().collect { item ->
                    _totalBalance.update {
                        item.totalBalance.toLocalizedString()
                    }
                }
            } catch (e: Exception) {
                Log.e("ERROR", e.localizedMessage ?: "")
                transactionRepository.insertTotalBalance(TotalBalance(1, 0.0))
                if (shouldRetry) getTotalBalance(false)
            }
        }
    }

    fun shouldSeparate(before: TransactionItemUiModel, after: TransactionItemUiModel): Boolean {
        val beforeTypeCasted = before as? TransactionItemUiModel.TransactionItem
        val afterTypeCasted = after as? TransactionItemUiModel.TransactionItem

        if (beforeTypeCasted == null || afterTypeCasted == null) return false

        val beforeDate = Instant.ofEpochSecond(beforeTypeCasted.epochTime)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()

        val afterDate = Instant.ofEpochSecond(afterTypeCasted.epochTime)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()

        return beforeDate.year != afterDate.year
                || beforeDate.monthValue != afterDate.monthValue
                || beforeDate.dayOfMonth != afterDate.dayOfMonth
    }

    fun formatDate(epochSecond: Long): String =
        epochSecond.convertEpochSecondToLocalDateTime().format(formatter)
}