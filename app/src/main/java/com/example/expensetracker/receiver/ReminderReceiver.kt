package com.example.expensetracker.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.expensetracker.R
import com.example.expensetracker.ui.MainActivity


//BroadcastReceiver that handles daily reminders
class ReminderReceiver : BroadcastReceiver() {

    //called when the alarm or broadcast is received
    override fun onReceive(context: Context, intent: Intent?) {
        Log.d("Reminder", "Receiver triggered!")

        val channelId = "expense_reminder_channel"
        val notificationId = 1001

        //intent to open MainActivity when the notification is clicked
        val openIntent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            openIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        //builds notification content
        val builder = NotificationCompat.Builder(context, channelId)
            //default android icon used because notification is suppressed otherwise
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Daily Expense Reminder")
            .setContentText("Don't forget to log your expenses today.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent) //opens app when tapped
            .setAutoCancel(true) //deletes notification when tapped

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Expense Tracker Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(notificationId, builder.build())
    }
}
