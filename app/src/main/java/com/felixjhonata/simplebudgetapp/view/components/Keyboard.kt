package com.felixjhonata.simplebudgetapp.view.components

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import com.felixjhonata.simplebudgetapp.R
import com.felixjhonata.simplebudgetapp.model.TransactionType

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
fun Keyboard(
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