package com.felixjhonata.simplebudgetapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.felixjhonata.simplebudgetapp.data.room.entity.Transaction
import com.felixjhonata.simplebudgetapp.model.AddTransactionDialog
import com.felixjhonata.simplebudgetapp.model.TransactionType
import com.felixjhonata.simplebudgetapp.model.uistate.AddTransactionUiState
import com.felixjhonata.simplebudgetapp.repository.TransactionRepository
import com.felixjhonata.simplebudgetapp.util.convertEpochMillisToLocalDateTime
import com.felixjhonata.simplebudgetapp.util.toLocalizedString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
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
    private val transactionRepository: TransactionRepository
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
        viewModelScope.launch(Dispatchers.IO) {
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

            withContext(Dispatchers.Main) { onComplete.invoke() }
        }
    }

    fun setDateInMillis(selectedDateInMillis: Long?) {
        selectedDateInMillis?.let { selectedDateInMillisDenull ->
            val localDateTime = selectedDateInMillisDenull.convertEpochMillisToLocalDateTime()
            _uiState.update {
                it.copy(
                    date = localDateTime.format(formatter),
                    dateInMillis = selectedDateInMillisDenull
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