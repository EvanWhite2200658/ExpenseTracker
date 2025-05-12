package com.example.expensetracker.ui

import android.os.Bundle
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.expensetracker.R
import com.example.expensetracker.data.Category
import com.example.expensetracker.data.Expense
import com.example.expensetracker.viewmodel.CategoryViewModel
import com.example.expensetracker.viewmodel.ExpenseViewModel

class AddExpenseActivity : AppCompatActivity() {
    private lateinit var amountEditText: EditText
    private lateinit var categorySpinner: Spinner
    private lateinit var noteEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var spinnerCategory: Spinner
    private lateinit var editNewCategory: EditText
    private lateinit var buttonAddCategory: Button
    private lateinit var categoryViewModel: CategoryViewModel


    private val viewModel: ExpenseViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_expense)

        // UI references
        val editAmount = findViewById<EditText>(R.id.editTextAmount)
        val spinnerCategory = findViewById<Spinner>(R.id.spinnerCategory)
        val editNote = findViewById<EditText>(R.id.editTextNote)
        val buttonSave = findViewById<Button>(R.id.buttonSave)
        val editNewCategory = findViewById<EditText>(R.id.editNewCategory)
        val buttonAddCategory = findViewById<Button>(R.id.buttonAddCategory)

        // ViewModels
        val expenseViewModel = ViewModelProvider(this)[ExpenseViewModel::class.java]
        val categoryViewModel = ViewModelProvider(this)[CategoryViewModel::class.java]

        // Spinner adapter for dynamic category list
        val spinnerAdapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item,
            mutableListOf()
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = spinnerAdapter

        // Observe category list from database
        categoryViewModel.allCategories.observe(this) { categories ->
            spinnerAdapter.clear()
            spinnerAdapter.addAll(categories.map { it.name })
            spinnerAdapter.notifyDataSetChanged()
        }

        // Add new category button
        buttonAddCategory.setOnClickListener {
            val newCategory = editNewCategory.text.toString().trim()
            if (newCategory.isNotEmpty()) {
                categoryViewModel.insert(Category(name = newCategory))
                editNewCategory.text.clear()
                Toast.makeText(this, "Category added", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Enter a category name", Toast.LENGTH_SHORT).show()
            }
        }

        // Save expense
        buttonSave.setOnClickListener {
            val amountText = editAmount.text.toString().trim()
            val note = editNote.text.toString().trim()
            val category = spinnerCategory.selectedItem?.toString() ?: ""

            if (amountText.isNotEmpty() && category.isNotEmpty()) {
                val amount = amountText.toFloatOrNull()
                if (amount != null) {
                    val expense = Expense(
                        note = note.ifEmpty { "Expense" },
                        amount = amount,
                        timestamp = System.currentTimeMillis(),
                        category = category
                    )
                    expenseViewModel.insert(expense)
                    Toast.makeText(this, "Expense saved", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Enter a valid amount", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Amount and category are required", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun setupSpinner(){
        val categories = listOf("Food", "Transport", "Entertainment", "Shopping", "Other")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = adapter
    }

}