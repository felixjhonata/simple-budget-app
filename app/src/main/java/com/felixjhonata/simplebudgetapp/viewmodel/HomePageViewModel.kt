package com.felixjhonata.simplebudgetapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import com.felixjhonata.simplebudgetapp.model.TransactionItemUiModel
import com.felixjhonata.simplebudgetapp.model.TransactionType
import com.felixjhonata.simplebudgetapp.repository.HomePageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class HomePageViewModel @Inject constructor(
    private val homePageRepository: HomePageRepository
): ViewModel() {
    val transactionItems by lazy {
        Pager(
            config = PagingConfig(50),
        ) {
            homePageRepository.getTransactions()
        }.flow.map { pagingData ->
            pagingData.map {
                TransactionItemUiModel.TransactionItem(
                    type = TransactionType.valueOf(it.type),
                    currency = it.currency,
                    amount = it.amount,
                    epochTime = it.date
                )
            }.insertSeparators { before, after ->
                when {
                    (before == null && after != null)
                            || (before != null && after != null && shouldSeperate(before, after)) -> {
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

    fun shouldSeperate(before: TransactionItemUiModel, after: TransactionItemUiModel): Boolean {
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

    fun formatDate(epochSecond: Long): String {
        val instant = Instant.ofEpochSecond(epochSecond)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()

        return instant.format(formatter)
    }
}