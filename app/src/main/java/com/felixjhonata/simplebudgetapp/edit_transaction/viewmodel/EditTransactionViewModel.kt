package com.felixjhonata.simplebudgetapp.edit_transaction.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.felixjhonata.simplebudgetapp.edit_transaction.model.EditTransactionDialog
import com.felixjhonata.simplebudgetapp.edit_transaction.model.uistate.EditTransactionUiState
import com.felixjhonata.simplebudgetapp.shared.data.room.entity.Transaction
import com.felixjhonata.simplebudgetapp.shared.model.TransactionType
import com.felixjhonata.simplebudgetapp.shared.module.IoDispatcher
import com.felixjhonata.simplebudgetapp.shared.module.MainDispatcher
import com.felixjhonata.simplebudgetapp.shared.repository.TransactionRepository
import com.felixjhonata.simplebudgetapp.shared.util.convertEpochMillisToLocalDateTime
import com.felixjhonata.simplebudgetapp.shared.util.convertEpochSecondToLocalDateTime
import com.felixjhonata.simplebudgetapp.shared.util.toLocalizedString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.math.floor

@HiltViewModel
class EditTransactionViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @param:MainDispatcher private val mainDispatcher: CoroutineDispatcher
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

    fun load(id: Int, setSelectedDate: (Long) -> Unit) {
        viewModelScope.launch(ioDispatcher) {
            oldTransaction = transactionRepository.getTransaction(id)

            oldTransaction?.let { transaction ->
                inputtedNumber = transaction.amount

                val localDateTime = transaction.date.convertEpochSecondToLocalDateTime()
                val dateInMillis = TimeUnit.SECONDS.toMillis(transaction.date)

                _uiState.update {
                    it.copy(
                        type = TransactionType.valueOf(transaction.type),
                        date = localDateTime.format(formatter),
                        dateInMillis = dateInMillis
                    )
                }

                withContext(mainDispatcher) { setSelectedDate(dateInMillis) }
            }
        }
    }

    fun updateTransaction(onComplete: () -> Unit) {
        viewModelScope.launch(ioDispatcher) {
            with(uiState.value) {
                oldTransaction?.let { oldTransaction ->
                    val newTransaction = oldTransaction.copy(
                        type = type.toString(),
                        amount = inputtedNumber,
                        date = TimeUnit.MILLISECONDS.toSeconds(dateInMillis)
                    )

                    transactionRepository.updateTransaction(oldTransaction, newTransaction)
                }
            }

            withContext(mainDispatcher) { onComplete.invoke() }
        }
    }

    fun setInputtedNumber(num: String) {
        num.toIntOrNull()?.let {
            inputtedNumber = inputtedNumber * 10 + it
        }
    }

    fun setDate(epochMillis: Long) {
        _uiState.update {
            val localDateTime = epochMillis.convertEpochMillisToLocalDateTime()
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