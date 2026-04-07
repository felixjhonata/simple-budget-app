package com.felixjhonata.simplebudgetapp.transaction_detail.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.felixjhonata.simplebudgetapp.R
import com.felixjhonata.simplebudgetapp.home.model.EditTransaction
import com.felixjhonata.simplebudgetapp.transaction_detail.viewmodel.TransactionDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppBar(
    modifier: Modifier = Modifier,
    onBackPressed: () -> Unit
) {
    TopAppBar(
        title = { Text(stringResource(R.string.transaction_detail)) },
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
private fun BottomBar(
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BottomAppBar {
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ActionButton(
                stringResource(R.string.delete),
                Icons.Default.Delete,
                "delete_icon",
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                ),
                onClick = onDelete
            )

            ActionButton(
                stringResource(R.string.edit),
                Icons.Default.Edit,
                "edit_icon",
                modifier = Modifier.weight(1f),
                onClick = onEdit
            )
        }
    }
}

@Composable
fun TransactionDetailPage(
    id: Int,
    navBackStack: NavBackStack<NavKey>,
    modifier: Modifier = Modifier,
    viewModel: TransactionDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(id) {
        viewModel.load(id)
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            AppBar {
                navBackStack.removeLastOrNull()
            }
        },
        bottomBar = {
            BottomBar(
                viewModel::toggleDeleteDialog,
                { navBackStack.add(EditTransaction(id)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            )
        }
    ) { innerPadding ->
        if (uiState.showDeleteDialog) {
            AlertDialog(
                viewModel::toggleDeleteDialog,
                title = { Text(stringResource(R.string.delete_transaction)) },
                text = {
                    Text(stringResource(R.string.delete_transaction_confirmation))
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.toggleDeleteDialog()
                            viewModel.deleteTransaction {
                                navBackStack.removeLastOrNull()
                            }
                        }
                    ) {
                        Text(stringResource(R.string.yes))
                    }
                },
                dismissButton = {
                    TextButton(onClick = viewModel::toggleDeleteDialog) {
                        Text(stringResource(android.R.string.cancel))
                    }
                }
            )
        }

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