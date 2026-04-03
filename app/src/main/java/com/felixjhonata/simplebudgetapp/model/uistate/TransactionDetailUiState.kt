package com.felixjhonata.simplebudgetapp.model.uistate

data class TransactionDetailUiState(
    val date: String = "",
    val type: String = "",
    val amount: String = "",
    val showDeleteDialog: Boolean = false
)
