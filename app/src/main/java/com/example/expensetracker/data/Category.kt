package com.example.expensetracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

//entity represents a row in the categories table of the database
@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String
)
