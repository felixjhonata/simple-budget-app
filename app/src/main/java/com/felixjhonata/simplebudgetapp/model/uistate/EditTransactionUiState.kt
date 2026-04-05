package com.felixjhonata.simplebudgetapp.model.uistate

import com.felixjhonata.simplebudgetapp.model.EditTransactionDialog
import com.felixjhonata.simplebudgetapp.model.TransactionType

data class EditTransactionUiState(
    val numDisplay: String = "0",
    val type: TransactionType = TransactionType.EXPENSE,
    val date: String = "",
    val dateInMillis: Long = 0,
    val dialog: EditTransactionDialog = EditTransactionDialog.None
)
