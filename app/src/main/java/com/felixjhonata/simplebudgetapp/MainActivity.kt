package com.felixjhonata.simplebudgetapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.felixjhonata.simplebudgetapp.ui.theme.SimpleBudgetAppTheme
import com.felixjhonata.simplebudgetapp.view.HomePage
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SimpleBudgetAppTheme {
                HomePage()
            }
        }
    }
}