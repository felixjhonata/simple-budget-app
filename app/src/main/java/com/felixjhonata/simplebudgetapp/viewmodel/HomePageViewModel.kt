package com.felixjhonata.simplebudgetapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.felixjhonata.simplebudgetapp.data.room.entity.Transaction
import com.felixjhonata.simplebudgetapp.model.TransactionItemUiModel
import com.felixjhonata.simplebudgetapp.model.TransactionType
import com.felixjhonata.simplebudgetapp.repository.HomePageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class HomePageViewModel @Inject constructor(
    private val homePageRepository: HomePageRepository
): ViewModel() {
    private val _transactionItems = MutableStateFlow<List<TransactionItemUiModel>>(emptyList())
    val transactionItems = _transactionItems.asStateFlow()

    init {
        getTransactionItems()
    }

    private val formatter by lazy {
        DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.getDefault())
    }

    fun formatDate(epochSecond: Long): String {
        val instant = Instant.ofEpochSecond(epochSecond)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()

        return instant.format(formatter)
    }

    private fun mapTransactionsToUiModel(transactions: List<Transaction>): List<TransactionItemUiModel> {
        return transactions.map {
            TransactionItemUiModel.TransactionItem(
                type = TransactionType.valueOf(it.type),
                currency = it.currency,
                amount = it.amount
            )
        }
    }

    private fun getTransactionItems() {
        viewModelScope.launch(Dispatchers.IO) {
            homePageRepository.getTransactions().collect { transactions ->
                _transactionItems.update {
                    mapTransactionsToUiModel(transactions)
                }
            }
        }
    }
}