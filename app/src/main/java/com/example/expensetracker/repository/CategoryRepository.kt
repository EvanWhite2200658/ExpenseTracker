package com.example.expensetracker.repository

import androidx.lifecycle.LiveData
import com.example.expensetracker.data.Category
import com.example.expensetracker.data.CategoryDao

class CategoryRepository(private val dao: CategoryDao) {
    //livedata list of all categories fetched from DAO
    val allCategories: LiveData<List<Category>> = dao.getAllCategories()

    //inserts a new category into the database (called from viewmodel)
    suspend fun insert(category: Category) {
        dao.insert(category)
    }
}
