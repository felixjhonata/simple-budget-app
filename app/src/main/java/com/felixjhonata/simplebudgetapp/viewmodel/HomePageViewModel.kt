package com.felixjhonata.simplebudgetapp.viewmodel

import androidx.lifecycle.ViewModel
import com.felixjhonata.simplebudgetapp.model.TransactionItemUiModel
import com.felixjhonata.simplebudgetapp.model.TransactionType
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@HiltViewModel
class HomePageViewModel: ViewModel() {
    private val formatter by lazy {
        DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.getDefault())
    }

    fun formatDate(epochSecond: Long): String {
        val instant = Instant.ofEpochSecond(epochSecond)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()

        return instant.format(formatter)
    }

    fun getTransactionItems(): List<TransactionItemUiModel> {
        return listOf(
            TransactionItemUiModel.Date(
                1773964800
            ),
            TransactionItemUiModel.TransactionItem(
                type = TransactionType.INCOME,
                currency = "IDR",
                amount = 200_000.0
            ),
            TransactionItemUiModel.TransactionItem(
                type = TransactionType.EXPENSE,
                currency = "IDR",
                amount = 32_000.0
            ),

            TransactionItemUiModel.Date(
                1773878400
            ),
            TransactionItemUiModel.TransactionItem(
                type = TransactionType.INCOME,
                currency = "IDR",
                amount = 200_000.0
            ),
            TransactionItemUiModel.TransactionItem(
                type = TransactionType.EXPENSE,
                currency = "IDR",
                amount = 32_000.0
            ),

            TransactionItemUiModel.Date(
                1773792000
            ),
            TransactionItemUiModel.TransactionItem(
                type = TransactionType.INCOME,
                currency = "IDR",
                amount = 200_000.0
            ),
            TransactionItemUiModel.TransactionItem(
                type = TransactionType.EXPENSE,
                currency = "IDR",
                amount = 32_000.0
            ),

            TransactionItemUiModel.Date(
                1773705600
            ),
            TransactionItemUiModel.TransactionItem(
                type = TransactionType.INCOME,
                currency = "IDR",
                amount = 200_000.0
            ),
            TransactionItemUiModel.TransactionItem(
                type = TransactionType.EXPENSE,
                currency = "IDR",
                amount = 32_000.0
            )
        )
    }
}