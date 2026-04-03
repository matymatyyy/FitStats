package com.app.gimnasio.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.app.gimnasio.GimnasioApplication
import com.app.gimnasio.data.model.Exercise
import com.app.gimnasio.data.model.Routine
import com.app.gimnasio.data.repository.RoutineRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RoutinesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: RoutineRepository

    private val _routines = MutableStateFlow<List<Routine>>(emptyList())
    val routines: StateFlow<List<Routine>> = _routines.asStateFlow()

    private val _selectedRoutine = MutableStateFlow<Routine?>(null)
    val selectedRoutine: StateFlow<Routine?> = _selectedRoutine.asStateFlow()

    init {
        val db = (application as GimnasioApplication).database
        repository = RoutineRepository(db)
        loadRoutines()
    }

    fun loadRoutines() {
        viewModelScope.launch {
            _routines.value = withContext(Dispatchers.IO) {
                repository.getAllRoutines()
            }
        }
    }

    fun selectRoutine(routineId: Long) {
        viewModelScope.launch {
            _selectedRoutine.value = withContext(Dispatchers.IO) {
                repository.getRoutineById(routineId)
            }
        }
    }

    fun createRoutine(name: String, description: String, exercises: List<Exercise>) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.createRoutine(name, description, exercises)
            }
            loadRoutines()
        }
    }

    fun deleteRoutine(routineId: Long) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.deleteRoutine(routineId)
            }
            loadRoutines()
        }
    }

    fun updateRoutine(routineId: Long, name: String, description: String, exercises: List<Exercise>) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.updateRoutine(routineId, name, description, exercises)
            }
            loadRoutines()
        }
    }
}
