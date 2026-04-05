package com.felixjhonata.simplebudgetapp.view

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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.felixjhonata.simplebudgetapp.view.components.InputField
import com.felixjhonata.simplebudgetapp.view.components.Keyboard
import com.felixjhonata.simplebudgetapp.viewmodel.AddTransactionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddTransactionAppBar(modifier: Modifier = Modifier, onDone: () -> Unit, onBack: () -> Unit) {
    TopAppBar(
        modifier = modifier,
        title = {
            Text("Tambah Transaksi")
        },
        navigationIcon = {
            IconButton(
                onClick = onBack
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    "back_navigation_icon"
                )
            }
        },
        actions = {
            IconButton(
                onClick = onDone
            ) {
                Icon(
                    Icons.Default.Check,
                    "done_button"
                )
            }
        }
    )
}

@Composable
fun AddTransactionPage(
    navBackStack: NavBackStack<NavKey>,
    modifier: Modifier = Modifier,
    viewModel: AddTransactionViewModel = hiltViewModel()
) {
    val numDisplay by viewModel.numDisplay.collectAsState()
    val currentMode by viewModel.currentMode.collectAsState()

    Scaffold(
        modifier = modifier,
        topBar = {
            AddTransactionAppBar(
                onDone = {
                    viewModel.addTransaction {
                        navBackStack.removeLastOrNull()
                    }
                }
            ) {
                navBackStack.removeLastOrNull()
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Center
        ) {
            InputField(
                numDisplay = numDisplay,
                modifier = Modifier
                    .padding(
                        horizontal = 24.dp
                    )
                    .fillMaxWidth()
            )

            Spacer(Modifier.height(48.dp))

            Keyboard(
                modifier = Modifier.padding(
                    horizontal = 24.dp
                ),
                currentMode = currentMode,
                setCurrentInput = viewModel::setCurrentInput,
                onBackSpace = viewModel::onBackSpace,
                onToggleMode = viewModel::toggleMode
            )
        }
    }
}