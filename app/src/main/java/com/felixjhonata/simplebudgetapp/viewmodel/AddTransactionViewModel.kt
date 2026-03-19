package com.felixjhonata.simplebudgetapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.felixjhonata.simplebudgetapp.data.room.entity.Transaction
import com.felixjhonata.simplebudgetapp.model.TransactionType
import com.felixjhonata.simplebudgetapp.repository.AddTransactionRepository
import com.felixjhonata.simplebudgetapp.util.toLocalizedString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject
import kotlin.math.floor

@HiltViewModel
class AddTransactionViewModel @Inject constructor(
    private val addTransactionRepository: AddTransactionRepository
): ViewModel() {
    private val _numDisplay = MutableStateFlow("0")
    val numDisplay = _numDisplay.asStateFlow()

    private val _currentMode = MutableStateFlow(TransactionType.INCOME)
    val currentMode = _currentMode.asStateFlow()

    private var currentInput = 0.0
        set(value) {
            field = value
            _numDisplay.update { value.toLocalizedString() }
        }

    fun setCurrentInput(num: String) {
        num.toIntOrNull()?.let {
            currentInput = currentInput * 10 + it
        }
    }

    fun onBackSpace() {
        currentInput = floor(currentInput / 10)
    }

    fun addTransaction() {
        viewModelScope.launch(Dispatchers.IO) {
            addTransactionRepository.addTransaction(
                Transaction(
                    type = currentMode.value.toString(),
                    currency = "IDR",
                    amount = currentInput,
                    date = Instant.now().epochSecond
                )
            )
        }
    }

    fun toggleMode() {
        if (currentMode.value == TransactionType.INCOME) {
            _currentMode.update { TransactionType.EXPENSE }
        } else {
            _currentMode.update { TransactionType.INCOME }
        }
    }
}