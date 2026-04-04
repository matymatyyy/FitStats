package com.app.gimnasio.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            val prefs = context.getSharedPreferences("fitstats_prefs", Context.MODE_PRIVATE)
            if (prefs.getBoolean("daily_reminder_enabled", false)) {
                val hour = prefs.getInt("reminder_hour", 8)
                val minute = prefs.getInt("reminder_minute", 0)
                ReminderScheduler.schedule(context, hour, minute)
            }
        }
    }
}
