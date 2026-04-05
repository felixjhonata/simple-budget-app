package com.felixjhonata.simplebudgetapp.model

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object Home: NavKey

@Serializable
data object AddTransaction: NavKey

@Serializable
data class TransactionDetail(val id: Int): NavKey

@Serializable
data class EditTransaction(val id: Int): NavKey