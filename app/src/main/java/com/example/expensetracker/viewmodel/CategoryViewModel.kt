package com.example.expensetracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.Category
import com.example.expensetracker.data.ExpenseDatabase
import com.example.expensetracker.repository.CategoryRepository
import kotlinx.coroutines.launch

//viewmodel manages category data
//bridge between UI and CategoryRepository
class CategoryViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = ExpenseDatabase.getDatabase(application).categoryDao()
    private val repository = CategoryRepository(dao)

    //exposes LiveData list of categories to the UI
    val allCategories: LiveData<List<Category>> = repository.allCategories

    //asynchronously inserts a new category into the database
    fun insert(category: Category) = viewModelScope.launch {
        repository.insert(category)
    }
}
