package com.example.expensetracker.repository

import com.example.expensetracker.data.Expense
import com.example.expensetracker.data.ExpenseDao

//abstracts the data layer and handles access to the ExpenseDAO
class ExpenseRepository(private val dao: ExpenseDao) {

    //livedata of all expenses and total amount retrieved from the DAO
    val allExpenses = dao.getAllExpenses()
    val total = dao.getTotal()

    //inserts new expense to the database (called from viewmodel)
    suspend fun insert(expense: Expense){
        dao.insert(expense)
    }

    //deletes an expense from the database (called from the viewmodel)
    suspend fun delete(expense: Expense) {
        dao.delete(expense)
    }

}