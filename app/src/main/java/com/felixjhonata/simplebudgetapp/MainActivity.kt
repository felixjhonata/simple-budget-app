package com.felixjhonata.simplebudgetapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.felixjhonata.simplebudgetapp.model.AddTransaction
import com.felixjhonata.simplebudgetapp.model.Home
import com.felixjhonata.simplebudgetapp.ui.theme.SimpleBudgetAppTheme
import com.felixjhonata.simplebudgetapp.view.AddTransactionPage
import com.felixjhonata.simplebudgetapp.view.HomePage
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
                    backStack = navBackStack,
                    onBack = { navBackStack.removeLastOrNull() },
                    entryProvider = entryProvider {
                        entry<Home> {
                            HomePage(navBackStack)
                        }
                        entry<AddTransaction> {
                            AddTransactionPage(navBackStack)
                        }
                    }
                )
            }
        }
    }
}