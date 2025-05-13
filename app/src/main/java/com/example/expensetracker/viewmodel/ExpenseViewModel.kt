package com.example.expensetracker.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.expensetracker.data.Expense
import com.example.expensetracker.data.ExpenseDatabase
import com.example.expensetracker.repository.ExpenseRepository
import kotlinx.coroutines.launch

//viewmodel manages expense data
class ExpenseViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ExpenseRepository

    //livedata holds all expenses and total amount
    val allExpenses: LiveData<List<Expense>>
    val total: LiveData<Float?>

    init {
        val dao = ExpenseDatabase.getDatabase(application).expenseDao()
        repository = ExpenseRepository(dao)

        //initialize livedata from the repo
        allExpenses = repository.allExpenses
        total = repository.total
    }

    //async insert a new expense
    fun insert(expense: Expense) = viewModelScope.launch {
        repository.insert(expense)
    }

    //async delete an expense
    fun delete(expense: Expense) = viewModelScope.launch {
        repository.delete(expense)
    }

}