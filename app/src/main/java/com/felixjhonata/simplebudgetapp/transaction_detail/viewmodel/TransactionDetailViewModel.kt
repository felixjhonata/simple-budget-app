package com.felixjhonata.simplebudgetapp.transaction_detail.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.felixjhonata.simplebudgetapp.shared.repository.TransactionRepository
import com.felixjhonata.simplebudgetapp.shared.data.room.entity.Transaction
import com.felixjhonata.simplebudgetapp.shared.util.convertEpochSecondToLocalDateTime
import com.felixjhonata.simplebudgetapp.shared.util.toLocalizedString
import com.felixjhonata.simplebudgetapp.transaction_detail.model.uistate.TransactionDetailUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class TransactionDetailViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository
): ViewModel() {
    private val _uiState = MutableStateFlow(TransactionDetailUiState())
    val uiState = _uiState.asStateFlow()

    private var transaction: Transaction? = null

    private val formatter by lazy {
        DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm", Locale.getDefault())
    }

    private fun formatEpoch(epoch: Long): String =
        epoch.convertEpochSecondToLocalDateTime().format(formatter)

    fun load(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            transaction = transactionRepository.getTransaction(id)

            transaction?.let {
                _uiState.value = TransactionDetailUiState(
                    date = formatEpoch(it.date),
                    type = it.type,
                    amount = "IDR ${it.amount.toLocalizedString()}"
                )
            }
        }
    }

    fun deleteTransaction(onComplete: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            transaction?.let {
                transactionRepository.deleteTransaction(it)
            }
            withContext(Dispatchers.Main) { onComplete.invoke() }
        }
    }

    fun toggleDeleteDialog() {
        _uiState.update {
            it.copy(showDeleteDialog = !it.showDeleteDialog)
        }
    }
}