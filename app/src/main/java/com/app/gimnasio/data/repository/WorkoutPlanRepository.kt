package com.app.gimnasio.data.repository

import com.app.gimnasio.data.local.GimnasioDatabase
import com.app.gimnasio.data.model.WorkoutPlanDay

class WorkoutPlanRepository(private val db: GimnasioDatabase) {

    fun getWorkoutPlan(): List<WorkoutPlanDay> = db.getWorkoutPlan()

    fun saveWorkoutPlan(days: List<WorkoutPlanDay>) = db.saveWorkoutPlan(days)

    fun clearWorkoutPlan() = db.clearWorkoutPlan()
}
