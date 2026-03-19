package com.felixjhonata.simplebudgetapp.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import com.felixjhonata.simplebudgetapp.R
import com.felixjhonata.simplebudgetapp.ui.theme.SimpleBudgetAppTheme
import com.felixjhonata.simplebudgetapp.util.toLocalizedString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddTransactionAppBar(modifier: Modifier = Modifier, onBack: () -> Unit) {
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
        }
    )
}

@Composable
private fun InputField(
    currentInput: Int,
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
            currentInput.toLocalizedString(),
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
    setText: (Int) -> Unit
) {
    IconButton(
        modifier = modifier,
        onClick = {
            setText(text.toInt())
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
    currentInput: Int,
    modifier: Modifier = Modifier,
    setCurrentInput: (Int) -> Unit,
) {
    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Fixed(3)
    ) {
        items(9) { index ->
            KeyboardButton(
                (index + 1).toString(),
                modifier = Modifier.aspectRatio(1f)
            ) { input ->
                setCurrentInput(currentInput * 10 + input)
            }
        }

        items(3) { index ->
            when (index) {
                0 -> {
                    IconButton(
                        modifier = Modifier.aspectRatio(1f),
                        onClick = {
                            setCurrentInput(currentInput / 10)
                        }
                    ) {
                        Icon(
                            painterResource(R.drawable.ic_backspace),
                            "backspace_button"
                        )
                    }
                }
                1 -> {
                    KeyboardButton(
                        "0",
                        modifier = Modifier.aspectRatio(1f)
                    ) { input ->
                        setCurrentInput(currentInput * 10 + input)
                    }
                }
                2 -> {
                    Box(
                        Modifier.aspectRatio(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        FilledIconButton(
                            onClick = {}
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowForward,
                                "done_button"
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddTransactionPage(
    navBackStack: NavBackStack<NavKey>,
    modifier: Modifier = Modifier
) {
    var currentInput by remember { mutableIntStateOf(0) }

    Scaffold(
        modifier = modifier,
        topBar = {
            AddTransactionAppBar {
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
                currentInput = currentInput,
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
                currentInput = currentInput
            ) { input ->
                currentInput = input
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewAddTransactionPage() {
    SimpleBudgetAppTheme {
        val navBackStack = rememberNavBackStack()
        AddTransactionPage(navBackStack)
    }
}