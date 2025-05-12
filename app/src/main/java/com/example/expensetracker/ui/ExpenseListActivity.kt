package com.example.expensetracker.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.expensetracker.R
import com.example.expensetracker.viewmodel.CategoryViewModel
import com.example.expensetracker.data.Expense
import com.example.expensetracker.viewmodel.ExpenseViewModel
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.*

class ExpenseListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ExpenseAdapter
    private lateinit var viewModel: ExpenseViewModel
    private lateinit var categoryViewModel: CategoryViewModel
    private lateinit var textTotal: TextView
    private lateinit var textDateRange: TextView

    private var selectedCategory: String? = null
    private var startDate: Long? = null
    private var endDate: Long? = null
    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expense_list)

        recyclerView = findViewById(R.id.expenseRecyclerView)
        textTotal = findViewById(R.id.textTotal)
        adapter = ExpenseAdapter(emptyList())
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        viewModel = ViewModelProvider(this)[ExpenseViewModel::class.java]
        categoryViewModel = ViewModelProvider(this)[CategoryViewModel::class.java]

        val spinnerCategory = findViewById<Spinner>(R.id.spinnerFilterCategory)
        val buttonStartDate = findViewById<Button>(R.id.buttonStartDate)
        val buttonEndDate = findViewById<Button>(R.id.buttonEndDate)
        textDateRange = findViewById(R.id.textDateRange)
        val buttonClearFilters = findViewById<Button>(R.id.buttonClearFilters)

        val spinnerAdapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mutableListOf("All"))
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = spinnerAdapter

        categoryViewModel.allCategories.observe(this) { categories ->
            val categoryNames = categories.map { it.name }
            spinnerAdapter.clear()
            spinnerAdapter.add("All")
            spinnerAdapter.addAll(categoryNames)
            spinnerAdapter.notifyDataSetChanged()
        }

        spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedCategory = if (position == 0) null else parent.getItemAtPosition(position).toString()
                filterExpenses()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        buttonStartDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(this, { _, year, month, day ->
                val picked = Calendar.getInstance()
                picked.set(year, month, day, 0, 0, 0)
                startDate = picked.timeInMillis
                filterExpenses()
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        buttonEndDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(this, { _, year, month, day ->
                val picked = Calendar.getInstance()
                picked.set(year, month, day, 23, 59, 59)
                endDate = picked.timeInMillis
                filterExpenses()
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        buttonClearFilters.setOnClickListener {
            selectedCategory = null
            startDate = null
            endDate = null
            textDateRange.text = "No date range selected"
            spinnerCategory.setSelection(0)
            filterExpenses()
        }

        viewModel.allExpenses.observe(this) {
            filterExpenses()
        }
    }

    private fun filterExpenses() {
        val expenses = viewModel.allExpenses.value ?: return

        val filtered = expenses.filter { expense ->
            val matchesCategory = selectedCategory == null || expense.category == selectedCategory
            val matchesStart = startDate == null || expense.timestamp >= startDate!!
            val matchesEnd = endDate == null || expense.timestamp <= endDate!!
            matchesCategory && matchesStart && matchesEnd
        }

        adapter.updateData(filtered)

        val total = filtered.sumOf { it.amount.toDouble() }.toFloat()
        textTotal.text = "Total: £%.2f".format(total)

        textDateRange.text = when {
            startDate != null && endDate != null ->
                "From ${dateFormat.format(Date(startDate!!))} to ${dateFormat.format(Date(endDate!!))}"
            startDate != null ->
                "From ${dateFormat.format(Date(startDate!!))}"
            endDate != null ->
                "Up to ${dateFormat.format(Date(endDate!!))}"
            else -> "No date range selected"
        }
    }


}
