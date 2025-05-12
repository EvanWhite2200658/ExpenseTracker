package com.example.expensetracker.repository

import com.example.expensetracker.data.Expense
import com.example.expensetracker.data.ExpenseDao

class ExpenseRepository(private val dao: ExpenseDao) {
    val allExpenses = dao.getAllExpenses()
    val total = dao.getTotal()

    suspend fun insert(expense: Expense){
        dao.insert(expense)
    }
    suspend fun delete(expense: Expense) {
        dao.delete(expense)
    }

}