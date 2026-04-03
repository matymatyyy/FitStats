package com.app.gimnasio.data.repository

import com.app.gimnasio.data.local.GimnasioDatabase
import com.app.gimnasio.data.model.WorkoutLog

class WorkoutRepository(private val db: GimnasioDatabase) {

    fun insertWorkoutLog(log: WorkoutLog): Long = db.insertWorkoutLog(log)

    fun getWorkoutLogsByDateRange(startDate: Long, endDate: Long): List<WorkoutLog> =
        db.getWorkoutLogsByDateRange(startDate, endDate)

    fun getWorkoutLogsForDate(date: Long): List<WorkoutLog> =
        db.getWorkoutLogsForDate(date)

    fun deleteWorkoutLog(id: Long) = db.deleteWorkoutLog(id)
}
