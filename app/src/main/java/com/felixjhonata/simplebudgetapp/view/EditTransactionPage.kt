package com.felixjhonata.simplebudgetapp.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.felixjhonata.simplebudgetapp.model.EditTransactionDialog
import com.felixjhonata.simplebudgetapp.view.components.DateField
import com.felixjhonata.simplebudgetapp.view.components.InputField
import com.felixjhonata.simplebudgetapp.view.components.Keyboard
import com.felixjhonata.simplebudgetapp.viewmodel.EditTransactionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    onBack: () -> Unit,
    onDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        modifier = modifier,
        title = { Text("Ubah Transaksi") },
        navigationIcon = {
            IconButton(onBack) {
                Icon(
                    Icons.AutoMirrored.Default.ArrowBack,
                    "back_button"
                )
            }
        },
        actions = {
            IconButton(onDone) {
                Icon(
                    Icons.Default.Check,
                    "done_button"
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTransactionPage(
    id: Int,
    navBackStack: NavBackStack<NavKey>,
    modifier: Modifier = Modifier,
    viewModel: EditTransactionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val datePickerState = rememberDatePickerState()

    LaunchedEffect(id) {
        viewModel.load(id) { dateInMillis ->
            datePickerState.selectedDateMillis = dateInMillis
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopBar(
                onBack = { navBackStack.removeLastOrNull() },
                onDone = {
                    viewModel.updateTransaction {
                        navBackStack.removeLastOrNull()
                    }
                }
            )
        }
    ) { innerPadding ->
        when (uiState.dialog) {
            EditTransactionDialog.DatePicker -> {
                DatePickerDialog(
                    onDismissRequest = viewModel::hideDialog,
                    confirmButton = {
                        Button(onClick = {
                            datePickerState.selectedDateMillis?.let {
                                viewModel.setDate(it)
                            }
                            viewModel.hideDialog()
                        }) {
                            Text("Ok")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = viewModel::hideDialog) {
                            Text("Batal")
                        }
                    }
                ) {
                    DatePicker(datePickerState)
                }
            }

            EditTransactionDialog.None -> Unit
        }

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Center
        ) {
            DateField(
                uiState.date,
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(horizontal = 24.dp)
                    .clickable {
                        viewModel.setDialog(EditTransactionDialog.DatePicker)
                    }
            )

            Spacer(Modifier.height(12.dp))

            InputField(
                numDisplay = uiState.numDisplay,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            )

            Spacer(Modifier.height(48.dp))

            Keyboard(
                modifier = Modifier.padding(
                    horizontal = 24.dp
                ),
                currentMode = uiState.type,
                setCurrentInput = viewModel::setInputtedNumber,
                onBackSpace = viewModel::onBackSpace,
                onToggleMode = viewModel::toggleMode
            )
        }
    }
}