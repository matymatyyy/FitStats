package com.app.gimnasio.data.repository

import com.app.gimnasio.data.local.GimnasioDatabase
import com.app.gimnasio.data.model.ExerciseInfo
import com.app.gimnasio.data.model.MuscleGroup

class ExerciseGalleryRepository(private val db: GimnasioDatabase) {

    fun getExercisesByMuscle(muscleGroup: MuscleGroup): List<ExerciseInfo> =
        db.getExercisesByMuscle(muscleGroup)

    fun addExercise(exercise: ExerciseInfo): Long =
        db.insertExerciseInfo(exercise)

    fun getExerciseById(id: Long): ExerciseInfo? = db.getExerciseInfoById(id)

    fun updateExercise(exercise: ExerciseInfo) = db.updateExerciseInfo(exercise)

    fun deleteExercise(id: Long) = db.deleteExerciseInfo(id)

    fun getExerciseCountsByMuscle(): Map<String, Int> = db.getExerciseCountsByMuscle()

    fun getAllExercises(): List<ExerciseInfo> = db.getAllExerciseInfos()
}
