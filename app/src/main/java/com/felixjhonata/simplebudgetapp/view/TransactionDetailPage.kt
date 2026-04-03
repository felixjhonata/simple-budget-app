package com.felixjhonata.simplebudgetapp.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.felixjhonata.simplebudgetapp.viewmodel.TransactionDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppBar(
    modifier: Modifier = Modifier,
    onBackPressed: () -> Unit
) {
    TopAppBar(
        title = { Text("Detail Transaksi") },
        modifier = modifier,
        navigationIcon = {
            IconButton(onClick = onBackPressed) {
                Icon(
                    Icons.AutoMirrored.Default.ArrowBack,
                    "back_button"
                )
            }
        }
    )
}

@Composable
private fun ActionButton(
    buttonLabel: String,
    buttonIcon: ImageVector,
    buttonIconTag: String,
    modifier: Modifier = Modifier,
    colors: ButtonColors = ButtonDefaults.filledTonalButtonColors(),
    onClick: () -> Unit
) {
    FilledTonalButton(
        modifier = modifier,
        colors = colors,
        onClick = onClick
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                buttonIcon,
                buttonIconTag
            )
            Text(buttonLabel)
        }
    }
}

@Composable
private fun BottomBar(modifier: Modifier = Modifier) {
    BottomAppBar {
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ActionButton(
                "Hapus",
                Icons.Default.Delete,
                "delete_icon",
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                ),
            ) { }

            ActionButton(
                "Ubah",
                Icons.Default.Edit,
                "edit_icon",
                modifier = Modifier.weight(1f)
            ) {  }
        }
    }
}

@Composable
fun TransactionDetailPage(
    viewModel: TransactionDetailViewModel,
    navBackStack: NavBackStack<NavKey>,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = modifier,
        topBar = {
            AppBar {
                navBackStack.removeLastOrNull()
            }
        },
        bottomBar = {
            BottomBar(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(uiState.date)
                Text(uiState.type)
            }

            Text(
                uiState.amount,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .padding(
                        horizontal = 24.dp
                    ),
                style = TextStyle(
                    fontSize = 24.sp
                ),
                textAlign = TextAlign.End
            )
        }
    }
}