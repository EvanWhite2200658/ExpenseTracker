package com.example.expensetracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

//entity class represents a row in the expenses table
@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val amount: Float,
    val category: String,
    val note: String,
    val timestamp: Long
)


