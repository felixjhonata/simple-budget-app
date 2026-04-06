package com.felixjhonata.simplebudgetapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.felixjhonata.simplebudgetapp.home.model.AddTransaction
import com.felixjhonata.simplebudgetapp.home.model.EditTransaction
import com.felixjhonata.simplebudgetapp.home.model.Home
import com.felixjhonata.simplebudgetapp.home.model.TransactionDetail
import com.felixjhonata.simplebudgetapp.ui.theme.SimpleBudgetAppTheme
import com.felixjhonata.simplebudgetapp.add_transaction.view.AddTransactionPage
import com.felixjhonata.simplebudgetapp.edit_transaction.view.EditTransactionPage
import com.felixjhonata.simplebudgetapp.home.view.HomePage
import com.felixjhonata.simplebudgetapp.transaction_detail.view.TransactionDetailPage
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navBackStack = rememberNavBackStack(Home)

            SimpleBudgetAppTheme {
                NavDisplay(
                    entryDecorators = listOf(
                        rememberSaveableStateHolderNavEntryDecorator(),
                        rememberViewModelStoreNavEntryDecorator()
                    ),
                    backStack = navBackStack,
                    onBack = { navBackStack.removeLastOrNull() },
                    transitionSpec = {
                        slideInHorizontally { it } togetherWith slideOutHorizontally { -it }
                    },
                    popTransitionSpec = {
                        slideInHorizontally { -it } togetherWith slideOutHorizontally { it }
                    },
                    predictivePopTransitionSpec = {
                        slideInHorizontally { -it } togetherWith slideOutHorizontally { it }
                    },
                    entryProvider = entryProvider {
                        entry<Home> {
                            HomePage(navBackStack)
                        }
                        entry<AddTransaction> {
                            AddTransactionPage(navBackStack)
                        }
                        entry<TransactionDetail> { entry ->
                            TransactionDetailPage(
                                entry.id,
                                navBackStack
                            )
                        }
                        entry<EditTransaction> { entry ->
                            EditTransactionPage(
                                entry.id,
                                navBackStack
                            )
                        }
                    }
                )
            }
        }
    }
}