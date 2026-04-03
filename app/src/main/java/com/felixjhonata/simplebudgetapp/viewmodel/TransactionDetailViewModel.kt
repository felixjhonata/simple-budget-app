package com.felixjhonata.simplebudgetapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.felixjhonata.simplebudgetapp.data.room.entity.Transaction
import com.felixjhonata.simplebudgetapp.model.uistate.TransactionDetailUiState
import com.felixjhonata.simplebudgetapp.repository.TransactionRepository
import com.felixjhonata.simplebudgetapp.util.convertEpochSecondToLocalDateTime
import com.felixjhonata.simplebudgetapp.util.toLocalizedString
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.format.DateTimeFormatter
import java.util.Locale

@HiltViewModel(assistedFactory = TransactionDetailViewModel.Factory::class)
class TransactionDetailViewModel @AssistedInject constructor(
    @Assisted private val id: Int,
    private val transactionRepository: TransactionRepository
): ViewModel() {
    private val _uiState = MutableStateFlow(TransactionDetailUiState())
    val uiState = _uiState.asStateFlow()

    private var transaction: Transaction? = null

    private val formatter by lazy {
        DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm", Locale.getDefault())
    }

    @AssistedFactory
    interface Factory {
        fun create(id: Int): TransactionDetailViewModel
    }

    private fun formatEpoch(epoch: Long): String =
        epoch.convertEpochSecondToLocalDateTime().format(formatter)

    init {
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