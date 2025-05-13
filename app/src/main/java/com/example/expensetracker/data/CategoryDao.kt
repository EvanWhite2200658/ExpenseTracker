package com.example.expensetracker.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

//DAO manages categories in the database
@Dao
interface CategoryDao {

    //returns list of all categories
    @Query("SELECT * FROM categories")
    fun getAllCategories(): LiveData<List<Category>>

    //inserts a new category ignoring duplicates
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(category: Category)
}
