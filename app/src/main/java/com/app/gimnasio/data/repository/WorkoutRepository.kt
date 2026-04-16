package com.app.gimnasio.data.repository

import com.app.gimnasio.data.local.GimnasioDatabase
import com.app.gimnasio.data.model.WorkoutLog
import com.app.gimnasio.data.model.WorkoutSetLog

class WorkoutRepository(private val db: GimnasioDatabase) {

    fun insertWorkoutLog(log: WorkoutLog): Long = db.insertWorkoutLog(log)

    fun insertSetLogs(logs: List<WorkoutSetLog>, workoutLogId: Long) =
        db.insertWorkoutSetLogs(logs, workoutLogId)

    fun getWorkoutLogsByDateRange(startDate: Long, endDate: Long): List<WorkoutLog> =
        db.getWorkoutLogsByDateRange(startDate, endDate)

    fun getWorkoutLogsForDate(date: Long): List<WorkoutLog> =
        db.getWorkoutLogsForDate(date)

    fun getSetLogsByDateRange(startDate: Long, endDate: Long): List<WorkoutSetLog> =
        db.getSetLogsByDateRange(startDate, endDate)

    fun getSetLogsForWorkout(workoutLogId: Long): List<WorkoutSetLog> =
        db.getSetLogsForWorkout(workoutLogId)

    fun getLoggedExerciseNames(): List<String> =
        db.getLoggedExerciseNames()

    fun deleteWorkoutLog(id: Long) = db.deleteWorkoutLog(id)
}
