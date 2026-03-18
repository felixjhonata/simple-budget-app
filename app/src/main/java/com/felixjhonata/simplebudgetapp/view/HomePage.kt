package com.felixjhonata.simplebudgetapp.view

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.felixjhonata.simplebudgetapp.R
import com.felixjhonata.simplebudgetapp.model.TransactionItemUiModel
import com.felixjhonata.simplebudgetapp.ui.theme.SimpleBudgetAppTheme
import com.felixjhonata.simplebudgetapp.util.toLocalizedString
import com.felixjhonata.simplebudgetapp.viewmodel.HomePageViewModel

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
            "Simple Budget App",
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        )
    }
}

@Composable
fun TotalBalanceCard(modifier: Modifier = Modifier) {
    Card(modifier) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Total Saldo",
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            )

            HorizontalDivider(
                modifier = Modifier.padding(8.dp)
            )

            Text(
                "IDR 12,000,000",
                style = TextStyle(
                    fontSize = 24.sp
                )
            )
        }
    }
}

@Composable
fun HomePage(
    modifier: Modifier = Modifier,
    viewModel: HomePageViewModel = hiltViewModel()
) {
    val transactionItems = viewModel.getTransactionItems()

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {}
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
                    Modifier.padding(horizontal = 24.dp, vertical = 18.dp)
                )
            }

            item {
                Text(
                    "Transaksi",
                    modifier = Modifier.padding(horizontal = 24.dp),
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                )
            }

            items(transactionItems) { item ->
                when(item) {
                    is TransactionItemUiModel.Date -> {
                        Box(
                            Modifier
                                .padding(top = 18.dp)
                                .background(Color.LightGray)
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
                                .padding(
                                    top = 8.dp,
                                    start = 24.dp,
                                    end = 24.dp
                                )
                                .fillMaxWidth(),
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
                }
            }
        }
    }
}

@Preview
@Composable
private fun PreviewHomePage() {
    SimpleBudgetAppTheme {
        HomePage()
    }
}