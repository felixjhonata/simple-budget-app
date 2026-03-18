package com.felixjhonata.simplebudgetapp.model

sealed interface TransactionItemUiModel {
    data class Date(
        val epochTime: Long
    ): TransactionItemUiModel

    data class TransactionItem(
        val type: TransactionType,
        val currency: String,
        val amount: Double
    ): TransactionItemUiModel
}