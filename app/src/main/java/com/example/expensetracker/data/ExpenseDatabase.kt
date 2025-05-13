package com.example.expensetracker.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// Room database definition includes expense and category entities
// provides DAO instances and manages singleton access
@Database(entities = [Expense::class, Category::class], version = 2)
abstract class ExpenseDatabase : RoomDatabase() {

    //abstract methods to access the DAO
    abstract fun expenseDao(): ExpenseDao
    abstract fun categoryDao(): CategoryDao

    companion object {
        //makes sure only one database instance is created
        @Volatile private var INSTANCE: ExpenseDatabase? = null

        //provides access to singleton instance of the database
        fun getDatabase(context: Context): ExpenseDatabase {
            return INSTANCE ?: synchronized(this) {
                //builds database instance if not already created
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ExpenseDatabase::class.java,
                    "expense_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
