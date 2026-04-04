package com.app.gimnasio.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.app.gimnasio.MainActivity
import com.app.gimnasio.R
import com.app.gimnasio.widget.WidgetDataHelper

class WorkoutReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val prefs = context.getSharedPreferences("fitstats_prefs", Context.MODE_PRIVATE)
        if (!prefs.getBoolean("daily_reminder_enabled", false)) return

        val nextWorkout = WidgetDataHelper.getNextWorkout(context)

        val title: String
        val body: String
        if (nextWorkout != null && nextWorkout.dayLabel == "Hoy") {
            title = "Hoy toca entrenar"
            body = nextWorkout.routineName
        } else if (nextWorkout != null) {
            title = "Recordatorio de entrenamiento"
            body = "${nextWorkout.dayLabel}: ${nextWorkout.routineName}"
        } else {
            title = "Dia de descanso"
            body = "No tenes entrenamiento programado hoy"
        }

        val channelId = "workout_reminder"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            channelId,
            "Recordatorio de entrenamiento",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Notificacion diaria para recordar entrenar"
        }
        notificationManager.createNotificationChannel(channel)

        val tapIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, tapIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(body)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1001, notification)
    }
}
