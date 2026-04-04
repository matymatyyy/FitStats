package com.app.gimnasio.widget

import android.content.Context
import com.app.gimnasio.data.local.GimnasioDatabase
import java.util.Calendar

data class WidgetNextWorkout(
    val routineName: String,
    val dayLabel: String,
    val description: String
)

data class WidgetWeeklyStats(
    val workouts: Int,
    val totalMinutes: Int,
    val totalSets: Int,
    val calories: Int
)

object WidgetDataHelper {

    fun getNextWorkout(context: Context): WidgetNextWorkout? {
        val db = GimnasioDatabase.getInstance(context)
        val planDays = db.getWorkoutPlan()
        if (planDays.isEmpty()) return null

        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val todayDate = cal.timeInMillis

        val calDow = cal.get(Calendar.DAY_OF_WEEK)
        val todayDow = if (calDow == Calendar.SUNDAY) 7 else calDow - 1

        val todayLogs = db.getWorkoutLogsForDate(todayDate)
        val trainedToday = todayLogs.isNotEmpty()

        val todayPlan = planDays.find { it.dayOfWeek == todayDow }
        if (todayPlan != null && !trainedToday) {
            val routine = db.getRoutineById(todayPlan.routineId)
            return WidgetNextWorkout(
                routineName = todayPlan.routineName,
                dayLabel = "Hoy",
                description = routine?.description ?: ""
            )
        }

        for (offset in 1..7) {
            val checkDow = ((todayDow - 1 + offset) % 7) + 1
            val nextPlan = planDays.find { it.dayOfWeek == checkDow }
            if (nextPlan != null) {
                val routine = db.getRoutineById(nextPlan.routineId)
                val dayLabel = if (offset == 1) "Mañana" else dayName(checkDow)
                return WidgetNextWorkout(
                    routineName = nextPlan.routineName,
                    dayLabel = dayLabel,
                    description = routine?.description ?: ""
                )
            }
        }
        return null
    }

    fun getWeeklyStats(context: Context): WidgetWeeklyStats {
        val db = GimnasioDatabase.getInstance(context)
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        val startOfWeek = cal.timeInMillis
        cal.add(Calendar.DAY_OF_WEEK, 6)
        val endOfWeek = cal.timeInMillis

        val logs = db.getWorkoutLogsByDateRange(startOfWeek, endOfWeek)
        val dates = logs.map { it.date }.toSet()
        val totalSeconds = logs.sumOf { it.durationSeconds }
        val totalSets = logs.sumOf { it.totalSets }
        val calories = (totalSeconds / 3600.0 * 300).toInt()

        return WidgetWeeklyStats(
            workouts = dates.size,
            totalMinutes = totalSeconds / 60,
            totalSets = totalSets,
            calories = calories
        )
    }

    private fun dayName(dow: Int): String = when (dow) {
        1 -> "Lunes"
        2 -> "Martes"
        3 -> "Miércoles"
        4 -> "Jueves"
        5 -> "Viernes"
        6 -> "Sábado"
        7 -> "Domingo"
        else -> ""
    }
}
