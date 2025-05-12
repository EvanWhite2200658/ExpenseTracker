package com.example.expensetracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.Category
import com.example.expensetracker.data.ExpenseDatabase
import com.example.expensetracker.repository.CategoryRepository
import kotlinx.coroutines.launch

class CategoryViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = ExpenseDatabase.getDatabase(application).categoryDao()
    private val repository = CategoryRepository(dao)
    val allCategories: LiveData<List<Category>> = repository.allCategories

    fun insert(category: Category) = viewModelScope.launch {
        repository.insert(category)
    }
}
