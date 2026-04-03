package com.app.gimnasio.data.repository

import com.app.gimnasio.data.local.GimnasioDatabase
import com.app.gimnasio.data.model.Exercise
import com.app.gimnasio.data.model.Routine

class RoutineRepository(private val db: GimnasioDatabase) {

    fun getAllRoutines(): List<Routine> = db.getAllRoutines()

    fun getRoutineById(id: Long): Routine? = db.getRoutineById(id)

    fun createRoutine(name: String, description: String, exercises: List<Exercise>): Long =
        db.insertRoutine(name, description, exercises)

    fun deleteRoutine(routineId: Long) = db.deleteRoutine(routineId)

    fun updateRoutine(routineId: Long, name: String, description: String, exercises: List<Exercise>) =
        db.updateRoutine(routineId, name, description, exercises)
}
