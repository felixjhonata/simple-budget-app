package com.felixjhonata.simplebudgetapp.shared.room.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "transactions",
    indices = [
        Index(
            value = ["date", "id"],
            orders = [Index.Order.DESC, Index.Order.DESC]
        )
    ]
)
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val type: String,

    val amount: Double,

    val currency: String,

    val date: Long
)