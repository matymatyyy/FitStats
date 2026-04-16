package com.app.gimnasio.data.model

data class WorkoutSetLog(
    val id: Long = 0,
    val workoutLogId: Long,
    val exerciseName: String,
    val setNumber: Int,
    val reps: Int?,
    val weightKg: Double?,
    val durationSeconds: Int?,
    val phase: String, // WARMUP or STRENGTH
    val isCircuit: Boolean = false,
    val date: Long // same as workout_logs.date for easy querying
)
