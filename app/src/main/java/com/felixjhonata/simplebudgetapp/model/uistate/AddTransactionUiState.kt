package com.felixjhonata.simplebudgetapp.model.uistate

import com.felixjhonata.simplebudgetapp.model.AddTransactionDialog
import com.felixjhonata.simplebudgetapp.model.TransactionType
import com.felixjhonata.simplebudgetapp.util.convertEpochMillisToLocalDateTime
import java.time.format.DateTimeFormatter

data class AddTransactionUiState(
    val date: String,
    val dateInMillis: Long,
    val numDisplay: String = "",
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
