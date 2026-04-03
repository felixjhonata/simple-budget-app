package com.felixjhonata.simplebudgetapp.model

sealed interface TransactionItemUiModel {
    data class Date(
        val epochTime: Long
    ): TransactionItemUiModel

    data class TransactionItem(
        val id: Int,
        val type: TransactionType,
        val currency: String,
        val amount: Double,
        val epochTime: Long
    ): TransactionItemUiModel
}