package com.app.gimnasio.widget

import android.content.Context
import androidx.glance.appwidget.updateAll
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object WidgetUpdater {
    fun updateAll(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            NextWorkoutWidget().updateAll(context)
            WeeklyStatsWidget().updateAll(context)
        }
    }
}
