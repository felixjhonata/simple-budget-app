package com.felixjhonata.simplebudgetapp.shared.data.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "total_balance")
data class TotalBalance(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val totalBalance: Double
)