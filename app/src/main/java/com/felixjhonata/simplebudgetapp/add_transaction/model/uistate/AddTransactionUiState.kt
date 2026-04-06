package com.felixjhonata.simplebudgetapp.add_transaction.model.uistate

import com.felixjhonata.simplebudgetapp.add_transaction.model.AddTransactionDialog
import com.felixjhonata.simplebudgetapp.shared.model.TransactionType
import com.felixjhonata.simplebudgetapp.shared.util.convertEpochMillisToLocalDateTime
import java.time.format.DateTimeFormatter

data class AddTransactionUiState(
    val date: String,
    val dateInMillis: Long,
    val numDisplay: String = "0",
    val currentMode: TransactionType = TransactionType.EXPENSE,
    val dialog: AddTransactionDialog = AddTransactionDialog.None
) {
    companion object {
        fun create(currentMillis: Long, formatter: DateTimeFormatter): AddTransactionUiState {
            val localDateTime = currentMillis.convertEpochMillisToLocalDateTime()
            return AddTransactionUiState(localDateTime.format(formatter), currentMillis)
        }
    }
}