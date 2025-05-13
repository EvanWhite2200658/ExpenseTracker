package com.example.expensetracker.data

import androidx.lifecycle.LiveData
import androidx.room.*

//DAO for the expense entity
// defines sql operations Room uses to interact with the expenses table
@Dao
interface ExpenseDao {

    //inserts new expense to the database
    @Insert
    suspend fun insert(expense: Expense)

    //lists all expenses by timestamp descending
    @Query("SELECT * FROM expenses ORDER BY timestamp DESC")
    fun getAllExpenses(): LiveData<List<Expense>>

    //returns total amount of all expenses
    @Query("SELECT SUM(amount) FROM expenses")
    fun getTotal(): LiveData<Float?>

    //deletes an expense from the database
    @Delete
    suspend fun delete(expense: Expense)

}
