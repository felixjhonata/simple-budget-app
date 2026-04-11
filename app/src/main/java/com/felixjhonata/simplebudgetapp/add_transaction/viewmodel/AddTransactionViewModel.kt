package com.felixjhonata.simplebudgetapp.add_transaction.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.felixjhonata.simplebudgetapp.add_transaction.model.AddTransactionDialog
import com.felixjhonata.simplebudgetapp.add_transaction.model.uistate.AddTransactionUiState
import com.felixjhonata.simplebudgetapp.shared.model.TransactionType
import com.felixjhonata.simplebudgetapp.shared.repository.TransactionRepository
import com.felixjhonata.simplebudgetapp.shared.data.room.entity.Transaction
import com.felixjhonata.simplebudgetapp.shared.util.convertEpochMillisToLocalDateTime
import com.felixjhonata.simplebudgetapp.shared.util.toLocalizedString
import com.felixjhonata.simplebudgetapp.shared.module.IoDispatcher
import com.felixjhonata.simplebudgetapp.shared.module.MainDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.math.floor

@HiltViewModel
class AddTransactionViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    @param:MainDispatcher private val mainDispatcher: CoroutineDispatcher
): ViewModel() {
    private val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")

    private val _uiState = MutableStateFlow(
        AddTransactionUiState.create(
            Instant.now().toEpochMilli(),
            formatter
        )
    )
    val uiState = _uiState.asStateFlow()

    private var currentInput = 0.0
        set(value) {
            field = value
            _uiState.update {
                it.copy(
                    numDisplay = value.toLocalizedString()
                )
            }
        }

    fun setCurrentInput(num: String) {
        num.toIntOrNull()?.let {
            currentInput = currentInput * 10 + it
        }
    }

    fun onBackSpace() {
        currentInput = floor(currentInput / 10)
    }

    fun addTransaction(onComplete: () -> Unit) {
        viewModelScope.launch(ioDispatcher) {
            with(uiState.value) {
                transactionRepository.insertTransaction(
                    Transaction(
                        type = currentMode.toString(),
                        currency = "IDR",
                        amount = currentInput,
                        date = TimeUnit.MILLISECONDS.toSeconds(dateInMillis)
                    )
                )
            }

            withContext(mainDispatcher) { onComplete.invoke() }
        }
    }

    fun setDateInMillis(selectedDateInMillis: Long?) {
        selectedDateInMillis?.let { selectedDateInMillis ->
            val localDateTime = selectedDateInMillis.convertEpochMillisToLocalDateTime()
            _uiState.update {
                it.copy(
                    date = localDateTime.format(formatter),
                    dateInMillis = selectedDateInMillis
                )
            }
        }
    }

    fun toggleMode() {
        with(uiState.value) {
            if (currentMode == TransactionType.INCOME) {
                _uiState.update {
                    it.copy(
                        currentMode = TransactionType.EXPENSE
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        currentMode = TransactionType.INCOME
                    )
                }
            }
        }
    }

    fun hideDialog() {
        showDialog(AddTransactionDialog.None)
    }

    fun showDialog(dialog: AddTransactionDialog) {
        _uiState.update {
            it.copy(dialog = dialog)
        }
    }
}