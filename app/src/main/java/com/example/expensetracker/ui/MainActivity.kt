package com.example.expensetracker.ui

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.example.expensetracker.R
import com.example.expensetracker.viewmodel.ExpenseViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import java.util.*
import androidx.core.content.edit

class MainActivity : AppCompatActivity() {

    private lateinit var localTotalText: TextView
    private lateinit var convertedTotalText: TextView
    private val viewModel: ExpenseViewModel by viewModels()
    private var budgetAmount: Float = 100f // default


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // ✅ Request POST_NOTIFICATIONS permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1)
            }
        }

        // Initialize views
        localTotalText = findViewById(R.id.textLocalTotal)
        convertedTotalText = findViewById(R.id.textConvertedTotal)

        val addButton = findViewById<Button>(R.id.buttonAdd)
        val listButton = findViewById<Button>(R.id.buttonList)
        val scheduleButton = findViewById<Button>(R.id.buttonScheduleReminder)
        val budgetView = findViewById<CircularBudgetView>(R.id.circularBudgetView)
        val buttonSetBudget = findViewById<Button>(R.id.buttonSetBudget)
        val budgetPrefs = getSharedPreferences("budget_prefs", MODE_PRIVATE)
        budgetAmount = budgetPrefs.getFloat("budget", 100f)


        // Button listeners
        buttonSetBudget.setOnClickListener {
            val input = EditText(this).apply{
                inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
                hint = "Enter monthly budget (£)"
            }
            AlertDialog.Builder(this)
                .setTitle("Set Monthly Budget")
                .setView(input)
                .setPositiveButton("Save") { _, _ ->
                    val value = input.text.toString().toFloatOrNull()
                    if (value != null && value > 0) {
                        budgetAmount = value
                        budgetPrefs.edit { putFloat("budget", value) }
                        updateBudgetUI()
                    } else {
                        Toast.makeText(this, "Invalid budget", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        addButton.setOnClickListener {
            startActivity(Intent(this, AddExpenseActivity::class.java))
        }

        listButton.setOnClickListener {
            startActivity(Intent(this, ExpenseListActivity::class.java))
        }

        scheduleButton.setOnClickListener {
            showTimePicker()
        }

        // Observe total expenses
        viewModel.total.observe(this) { total ->
            val amount = total ?: 0f
            localTotalText.text = "Local Total: £%.2f".format(amount)
            fetchConvertedCurrency(amount)
            updateBudgetUI()
        }

        // Reschedule saved reminder
        val prefs = getSharedPreferences("reminder_prefs", MODE_PRIVATE)
        if (prefs.contains("hour") && prefs.contains("minute")) {
            val hour = prefs.getInt("hour", 9)
            val minute = prefs.getInt("minute", 0)
            scheduleReminderAt(hour, minute)
        }
    }


    private fun showTimePicker() {
        val now = Calendar.getInstance()
        TimePickerDialog(
            this,
            { _: TimePicker, hour: Int, minute: Int ->
                scheduleReminderAt(hour, minute)
            },
            now.get(Calendar.HOUR_OF_DAY),
            now.get(Calendar.MINUTE),
            true
        ).show()
    }

    private fun scheduleReminderAt(hour: Int, minute: Int) {
        val prefs = getSharedPreferences("reminder_prefs", MODE_PRIVATE)
        prefs.edit().apply {
            putInt("hour", hour)
            putInt("minute", minute)
            apply()
        }

        val intent = Intent(this, com.example.expensetracker.receiver.ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (before(Calendar.getInstance())) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                startActivity(intent)
                Toast.makeText(
                    this,
                    "Please allow exact alarms for reminders to work.",
                    Toast.LENGTH_LONG
                ).show()
                return
            }
        }

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )

        Toast.makeText(
            this,
            "Reminder set for $hour:${"%02d".format(minute)} daily",
            Toast.LENGTH_SHORT
        ).show()
        Log.d("Reminder", "Alarm set for ${calendar.time}")
    }

    private fun updateBudgetUI() {
        val spent = viewModel.total.value ?: 0f
        val percentUsed = ((spent / budgetAmount) * 100).coerceAtMost(100f).toInt()

        val budgetView = findViewById<CircularBudgetView>(R.id.circularBudgetView)
        budgetView.percent = percentUsed
        budgetView.text = "£%.2f / £%.2f".format(spent, budgetAmount)

        if (spent > budgetAmount) {
            sendBudgetExceededNotification()
        }
    }

    private fun sendBudgetExceededNotification() {
        val channelId = "budget_warning_channel"
        val notificationId = 2001

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("Budget Exceeded")
            .setContentText("You've spent over your monthly budget.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Budget Alerts",
                NotificationManager.IMPORTANCE_HIGH
            )
            manager.createNotificationChannel(channel)
        }

        manager.notify(notificationId, builder.build())
    }



    private fun fetchConvertedCurrency(amount: Float) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = URL("https://v6.exchangerate-api.com/v6/3cbb7abb77bb35413703dd95/latest/GBP").readText()
                Log.d("Currency", "Raw response: $response")

                val json = JSONObject(response)
                if (!json.has("conversion_rates")) throw Exception("No 'conversion_rates' field found")

                val rate = json.getJSONObject("conversion_rates").getDouble("EUR")
                val converted = amount * rate

                withContext(Dispatchers.Main) {
                    convertedTotalText.text = "Converted Total: €%.2f".format(converted)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    convertedTotalText.text = "Currency fetch failed"
                }
                Log.e("Currency", "Error: ${e.message}")
            }
        }
    }
}
