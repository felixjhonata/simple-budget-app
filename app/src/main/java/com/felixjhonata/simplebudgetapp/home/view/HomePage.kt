package com.felixjhonata.simplebudgetapp.home.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.paging.compose.collectAsLazyPagingItems
import com.felixjhonata.simplebudgetapp.R
import com.felixjhonata.simplebudgetapp.home.model.AddTransaction
import com.felixjhonata.simplebudgetapp.home.model.TransactionDetail
import com.felixjhonata.simplebudgetapp.home.model.TransactionItemUiModel
import com.felixjhonata.simplebudgetapp.shared.util.toLocalizedString
import com.felixjhonata.simplebudgetapp.home.viewmodel.HomeViewModel

@Composable
fun HomePage(
    navBackStack: NavBackStack<NavKey>,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val totalBalance by viewModel.totalBalance.collectAsState()
    val transactionItems = viewModel.transactionItems.collectAsLazyPagingItems()

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navBackStack.add(AddTransaction)
                }
            ) {
                Icon(
                    Icons.Default.Add,
                    "add_icon"
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(innerPadding)
        ) {
            item {
                Spacer(Modifier.height(12.dp))
                AppLogoAndName(
                    Modifier.padding(horizontal = 24.dp)
                )
            }

            item {
                TotalBalanceCard(
                    totalBalance,
                    Modifier.padding(horizontal = 24.dp, vertical = 18.dp)
                )
            }

            item {
                Text(
                    stringResource(R.string.transaction),
                    modifier = Modifier.padding(horizontal = 24.dp),
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                )
            }

            if (transactionItems.itemCount > 0) {
                items(transactionItems.itemCount) { position ->
                    when(val item = transactionItems[position]) {
                        is TransactionItemUiModel.Date -> {
                            Box(
                                Modifier
                                    .padding(top = 18.dp)
                                    .background(MaterialTheme.colorScheme.surfaceContainer)
                                    .fillMaxWidth()
                            ) {
                                Text(
                                    viewModel.formatDate(item.epochTime),
                                    modifier = Modifier.padding(
                                        vertical = 4.dp,
                                        horizontal = 24.dp
                                    ),
                                    style = TextStyle(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                )
                            }
                        }

                        is TransactionItemUiModel.TransactionItem -> {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp)
                                    .clickable {
                                        navBackStack.add(TransactionDetail(item.id))
                                    }
                                    .padding(horizontal = 24.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    item.type.toString(),
                                    style = TextStyle(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                )

                                Text(
                                    "${item.currency} ${item.amount.toLocalizedString()}",
                                    style = TextStyle(
                                        fontSize = 14.sp,
                                        textAlign = TextAlign.End
                                    )
                                )
                            }
                        }

                        else -> Unit
                    }
                }

                item {
                    Spacer(Modifier.height(96.dp))
                }
            } else {
                item {
                    val density = LocalDensity.current
                    val screenHeight = with(density) {
                        LocalWindowInfo.current.containerSize.height.toDp()
                    }
                    val height = (screenHeight - 300.dp).coerceAtLeast(100.dp)

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(height),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(stringResource(R.string.no_transaction_yet))
                    }
                }
            }
        }
    }
}

@Composable
fun AppLogoAndName(modifier: Modifier = Modifier) {
    Row(
        modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            painterResource(R.drawable.ic_wallet),
            "app_logo",
            modifier = Modifier.size(32.dp)
        )

        Text(
            stringResource(R.string.app_name),
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        )
    }
}

@Composable
fun TotalBalanceCard(
    totalBalance: String,
    modifier: Modifier = Modifier
) {
    Card(modifier) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                stringResource(R.string.total_balance),
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            )

            HorizontalDivider(
                modifier = Modifier.padding(8.dp)
            )

            Text(
                "IDR $totalBalance",
                style = TextStyle(
                    fontSize = 24.sp
                )
            )
        }
    }
}