package com.felixjhonata.simplebudgetapp.data.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val type: String,
    val amount: Double,
    val currency: String,
    val date: Long
)