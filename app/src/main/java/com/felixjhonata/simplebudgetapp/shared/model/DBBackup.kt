package com.felixjhonata.simplebudgetapp.shared.model

import com.felixjhonata.simplebudgetapp.shared.data.room.entity.Transaction
import kotlinx.serialization.Serializable

@Serializable
data class DBBackup(
    val totalBalance: Double,
    val transactions: List<Transaction>
)
