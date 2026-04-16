package com.app.gimnasio.data.model

data class CustomMeasurement(
    val id: Long = 0,
    val name: String,
    val valueCm: Double,
    val updatedAt: Long = System.currentTimeMillis()
)

data class CustomPR(
    val id: Long = 0,
    val exerciseName: String,
    val weightKg: Double,
    val reps: Int,
    val updatedAt: Long = System.currentTimeMillis()
)

data class CustomPRHistoryPoint(
    val date: Long,
    val weightKg: Double,
    val reps: Int
)

data class CustomMeasurementHistoryPoint(
    val date: Long,
    val valueCm: Double
)
