package com.felixjhonata.simplebudgetapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.felixjhonata.simplebudgetapp.data.room.entity.Transaction
import com.felixjhonata.simplebudgetapp.model.EditTransactionDialog
import com.felixjhonata.simplebudgetapp.model.TransactionType
import com.felixjhonata.simplebudgetapp.model.uistate.EditTransactionUiState
import com.felixjhonata.simplebudgetapp.repository.TransactionRepository
import com.felixjhonata.simplebudgetapp.util.convertEpochSecondToLocalDateTime
import com.felixjhonata.simplebudgetapp.util.toLocalizedString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.math.floor

@HiltViewModel
class EditTransactionViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(EditTransactionUiState())
    val uiState = _uiState.asStateFlow()

    private var oldTransaction: Transaction? = null

    private var inputtedNumber = 0.0
        set(value) {
            field = value
            _uiState.value = _uiState.value.copy(
                numDisplay = value.toLocalizedString()
            )
        }

    private val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")

    fun load(id: Int, onComplete: suspend () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            oldTransaction = transactionRepository.getTransaction(id)

            oldTransaction?.let { transaction ->
                inputtedNumber = transaction.amount

                val localDateTime = transaction.date.convertEpochSecondToLocalDateTime()

                _uiState.update {
                    it.copy(
                        type = TransactionType.valueOf(transaction.type),
                        date = localDateTime.format(formatter),
                        dateInMillis = TimeUnit.SECONDS.toMillis(transaction.date)
                    )
                }

                onComplete.invoke()
            }
        }
    }

    fun updateTransaction(onComplete: suspend () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            with(uiState.value) {
                oldTransaction?.let { oldTransactionDenull ->
                    val newTransaction = oldTransactionDenull.copy(
                        type = type.toString(),
                        amount = inputtedNumber,
                        date = TimeUnit.MILLISECONDS.toSeconds(dateInMillis)
                    )

                    transactionRepository.updateTransaction(oldTransactionDenull, newTransaction)
                }
            }

            onComplete.invoke()
        }
    }

    fun setInputtedNumber(num: String) {
        num.toIntOrNull()?.let {
            inputtedNumber = inputtedNumber * 10 + it
        }
    }

    fun setDate(epochMillis: Long) {
        _uiState.update {
            val epochSecond = TimeUnit.MILLISECONDS.toSeconds(epochMillis)
            val localDateTime = epochSecond.convertEpochSecondToLocalDateTime()
            it.copy(
                date = localDateTime.format(formatter),
                dateInMillis = epochMillis
            )
        }
    }

    fun onBackSpace() {
        inputtedNumber = floor(inputtedNumber / 10)
    }

    fun toggleMode() {
        if (uiState.value.type == TransactionType.INCOME) {
            _uiState.update {
                it.copy(type = TransactionType.EXPENSE)
            }
        } else {
            _uiState.update {
                it.copy(type = TransactionType.INCOME)
            }
        }
    }

    fun hideDialog() {
        setDialog(EditTransactionDialog.None)
    }

    fun setDialog(dialog: EditTransactionDialog) {
        _uiState.update {
            it.copy(dialog = dialog)
        }
    }
}