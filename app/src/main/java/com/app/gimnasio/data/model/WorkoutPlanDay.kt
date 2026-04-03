package com.app.gimnasio.data.model

data class WorkoutPlanDay(
    val id: Long = 0,
    val dayOfWeek: Int, // 1=Lunes, 2=Martes, ..., 7=Domingo
    val routineId: Long,
    val routineName: String = ""
)
