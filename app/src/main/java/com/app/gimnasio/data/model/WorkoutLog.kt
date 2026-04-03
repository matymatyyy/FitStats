package com.app.gimnasio.data.model

data class WorkoutLog(
    val id: Long = 0,
    val routineId: Long? = null,
    val routineName: String,
    val date: Long, // epoch millis at start of day
    val durationSeconds: Int = 0,
    val exercisesSummary: String = "", // comma-separated exercise names
    val totalSets: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)
