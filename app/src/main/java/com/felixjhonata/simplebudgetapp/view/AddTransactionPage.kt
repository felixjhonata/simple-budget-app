package com.felixjhonata.simplebudgetapp.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.felixjhonata.simplebudgetapp.R
import com.felixjhonata.simplebudgetapp.model.TransactionType
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
private fun InputField(
    numDisplay: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "IDR",
            style = TextStyle(
                fontSize = 24.sp
            )
        )

        Spacer(Modifier.width(4.dp))

        Text(
            numDisplay,
            style = TextStyle(
                fontSize = 48.sp,
                textAlign = TextAlign.End
            ),
            overflow = TextOverflow.StartEllipsis
        )
    }
}

@Composable
private fun KeyboardButton(
    text: String,
    modifier: Modifier = Modifier,
    setCurrentInput: (String) -> Unit
) {
    IconButton(
        modifier = modifier,
        onClick = {
            setCurrentInput(text)
        }
    ) {
        Text(
            text,
            style = TextStyle(
                fontSize = 24.sp
            )
        )
    }
}

@Composable
private fun Keyboard(
    currentMode: TransactionType,
    setCurrentInput: (String) -> Unit,
    onBackSpace: () -> Unit,
    onToggleMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Fixed(3)
    ) {
        items(9) { index ->
            KeyboardButton(
                (index + 1).toString(),
                modifier = Modifier.aspectRatio(1f),
                setCurrentInput = setCurrentInput
            )
        }

        items(3) { index ->
            when (index) {
                0 -> {
                    IconButton(
                        modifier = Modifier.aspectRatio(1f),
                        onClick = onToggleMode
                    ) {
                        Text(currentMode.toString())
                    }
                }
                1 -> {
                    KeyboardButton(
                        "0",
                        modifier = Modifier.aspectRatio(1f),
                        setCurrentInput = setCurrentInput
                    )
                }
                2 -> {
                    IconButton(
                        modifier = Modifier.aspectRatio(1f),
                        onClick = onBackSpace
                    ) {
                        Icon(
                            painterResource(R.drawable.ic_backspace),
                            "backspace_button"
                        )
                    }
                }
            }
        }
    }
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
                    viewModel.addTransaction()
                    navBackStack.removeLastOrNull()
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