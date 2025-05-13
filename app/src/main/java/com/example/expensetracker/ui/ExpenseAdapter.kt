package com.example.expensetracker.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.expensetracker.R
import com.example.expensetracker.data.Expense
import java.text.SimpleDateFormat
import java.util.*

class ExpenseAdapter(private var expenses: List<Expense>) :
        RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {
            //holds reference to individual views for each expense item
    inner class ExpenseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val amount: TextView = view.findViewById(R.id.textAmount)
        val category: TextView = view.findViewById(R.id.textCategory)
        val note: TextView = view.findViewById(R.id.textNote)
        val date: TextView = view.findViewById(R.id.textDate)

    }

    //called when RecyclerView needs a new viewholder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_expense, parent, false)
        return ExpenseViewHolder(view)
    }

    //binds data to viewholder based on list position
    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = expenses[position]
        holder.amount.text = "£%.2f".format(expense.amount)
        holder.category.text = expense.category
        holder.note.text = expense.note
        holder.date.text = formatDate(expense.timestamp)
    }

    override fun getItemCount() = expenses.size

    //helper formats timestamp into human readable string
    private fun formatDate(millis: Long): String {
        val formatter = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
        return formatter.format(Date(millis))
    }

    //returns expense at certain list position (used for swipe-to-delete)
    fun getExpenseAt(position: Int): Expense {
        return expenses[position]
    }

    //updates adapder dataset and refreshes the list
    fun updateData(newExpenses: List<Expense>) {
        expenses = newExpenses
        notifyDataSetChanged()
    }


}