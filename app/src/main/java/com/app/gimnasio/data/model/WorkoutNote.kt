package com.app.gimnasio.data.model

data class WorkoutNote(
    val id: Long = 0,
    val routineId: Long?,
    val routineName: String,
    val exerciseName: String,
    val note: String,
    val createdAt: Long = System.currentTimeMillis()
)
