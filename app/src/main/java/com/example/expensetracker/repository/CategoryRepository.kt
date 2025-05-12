package com.example.expensetracker.repository

import androidx.lifecycle.LiveData
import com.example.expensetracker.data.Category
import com.example.expensetracker.data.CategoryDao

class CategoryRepository(private val dao: CategoryDao) {
    val allCategories: LiveData<List<Category>> = dao.getAllCategories()

    suspend fun insert(category: Category) {
        dao.insert(category)
    }
}
