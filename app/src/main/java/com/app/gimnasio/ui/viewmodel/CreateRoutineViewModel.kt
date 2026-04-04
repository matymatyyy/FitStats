package com.app.gimnasio.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.app.gimnasio.data.model.Exercise
import com.app.gimnasio.data.model.ExercisePhase
import com.app.gimnasio.data.model.Routine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CreateRoutineViewModel : ViewModel() {

    val routineName = MutableStateFlow("")
    val routineDescription = MutableStateFlow("")
    val routineImagePath = MutableStateFlow<String?>(null)

    var editingRoutineId: Long? = null
        private set

    private val _warmupExercises = MutableStateFlow<List<Exercise>>(emptyList())
    val warmupExercises: StateFlow<List<Exercise>> = _warmupExercises.asStateFlow()

    private val _strengthExercises = MutableStateFlow<List<Exercise>>(emptyList())
    val strengthExercises: StateFlow<List<Exercise>> = _strengthExercises.asStateFlow()

    fun addWarmupExercise(exercise: Exercise) {
        _warmupExercises.value = _warmupExercises.value + exercise
    }

    fun addStrengthExercise(exercise: Exercise) {
        _strengthExercises.value = _strengthExercises.value + exercise
    }

    fun removeWarmupExercise(index: Int) {
        _warmupExercises.value = _warmupExercises.value.toMutableList().apply { removeAt(index) }
    }

    fun removeStrengthExercise(index: Int) {
        _strengthExercises.value = _strengthExercises.value.toMutableList().apply { removeAt(index) }
    }

    fun updateWarmupExercise(index: Int, exercise: Exercise) {
        _warmupExercises.value = _warmupExercises.value.toMutableList().apply { set(index, exercise) }
    }

    fun updateStrengthExercise(index: Int, exercise: Exercise) {
        _strengthExercises.value = _strengthExercises.value.toMutableList().apply { set(index, exercise) }
    }

    fun getAllExercises(): List<Exercise> =
        _warmupExercises.value + _strengthExercises.value

    fun isValid(): Boolean =
        routineName.value.isNotBlank() &&
                (_warmupExercises.value.isNotEmpty() || _strengthExercises.value.isNotEmpty())

    fun loadForEdit(routine: Routine) {
        editingRoutineId = routine.id
        routineName.value = routine.name
        routineDescription.value = routine.description
        routineImagePath.value = routine.imagePath
        _warmupExercises.value = routine.exercises.filter { it.phase == ExercisePhase.WARMUP }
        _strengthExercises.value = routine.exercises.filter { it.phase == ExercisePhase.STRENGTH }
    }

    fun reset() {
        editingRoutineId = null
        routineName.value = ""
        routineDescription.value = ""
        routineImagePath.value = null
        _warmupExercises.value = emptyList()
        _strengthExercises.value = emptyList()
    }
}
