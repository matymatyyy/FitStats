package com.app.gimnasio.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.app.gimnasio.GimnasioApplication
import com.app.gimnasio.data.model.Routine
import com.app.gimnasio.data.model.WorkoutPlanDay
import com.app.gimnasio.data.repository.RoutineRepository
import com.app.gimnasio.data.repository.WorkoutPlanRepository
import com.app.gimnasio.data.repository.WorkoutRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

data class NextWorkoutInfo(
    val routineName: String,
    val routineId: Long,
    val dayLabel: String, // "Hoy", "Mañana", "Lunes", etc.
    val description: String = ""
)

class PlanViewModel(application: Application) : AndroidViewModel(application) {

    private val planRepository: WorkoutPlanRepository
    private val workoutRepository: WorkoutRepository
    private val routineRepository: RoutineRepository

    private val _planDays = MutableStateFlow<List<WorkoutPlanDay>>(emptyList())
    val planDays: StateFlow<List<WorkoutPlanDay>> = _planDays.asStateFlow()

    private val _nextWorkout = MutableStateFlow<NextWorkoutInfo?>(null)
    val nextWorkout: StateFlow<NextWorkoutInfo?> = _nextWorkout.asStateFlow()

    private val _routines = MutableStateFlow<List<Routine>>(emptyList())
    val routines: StateFlow<List<Routine>> = _routines.asStateFlow()

    private val _hasPlan = MutableStateFlow(false)
    val hasPlan: StateFlow<Boolean> = _hasPlan.asStateFlow()

    init {
        val db = (application as GimnasioApplication).database
        planRepository = WorkoutPlanRepository(db)
        workoutRepository = WorkoutRepository(db)
        routineRepository = RoutineRepository(db)
        loadPlan()
        loadRoutines()
    }

    fun loadRoutines() {
        viewModelScope.launch {
            _routines.value = withContext(Dispatchers.IO) {
                routineRepository.getAllRoutines()
            }
        }
    }

    fun loadPlan() {
        viewModelScope.launch {
            val days = withContext(Dispatchers.IO) {
                planRepository.getWorkoutPlan()
            }
            _planDays.value = days
            _hasPlan.value = days.isNotEmpty()
            if (days.isNotEmpty()) {
                calculateNextWorkout(days)
            } else {
                _nextWorkout.value = null
            }
        }
    }

    fun savePlan(days: List<WorkoutPlanDay>) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                planRepository.saveWorkoutPlan(days)
            }
            loadPlan()
        }
    }

    fun clearPlan() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                planRepository.clearWorkoutPlan()
            }
            _planDays.value = emptyList()
            _hasPlan.value = false
            _nextWorkout.value = null
        }
    }

    private suspend fun calculateNextWorkout(planDays: List<WorkoutPlanDay>) {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val todayDate = cal.timeInMillis

        // Calendar: Sunday=1, Monday=2... Saturday=7
        // Our plan: Lunes=1, Martes=2... Domingo=7
        val calDow = cal.get(Calendar.DAY_OF_WEEK)
        val todayDow = if (calDow == Calendar.SUNDAY) 7 else calDow - 1

        // Check if today has a workout log already
        val todayLogs = withContext(Dispatchers.IO) {
            workoutRepository.getWorkoutLogsForDate(todayDate)
        }
        val trainedToday = todayLogs.isNotEmpty()

        // Find today's plan entry
        val todayPlan = planDays.find { it.dayOfWeek == todayDow }

        if (todayPlan != null && !trainedToday) {
            // Today is a training day and hasn't trained yet
            val routine = withContext(Dispatchers.IO) {
                routineRepository.getRoutineById(todayPlan.routineId)
            }
            _nextWorkout.value = NextWorkoutInfo(
                routineName = todayPlan.routineName,
                routineId = todayPlan.routineId,
                dayLabel = "Hoy",
                description = routine?.description ?: ""
            )
            return
        }

        // Find next training day
        for (offset in 1..7) {
            val checkDow = ((todayDow - 1 + offset) % 7) + 1
            val nextPlan = planDays.find { it.dayOfWeek == checkDow }
            if (nextPlan != null) {
                val routine = withContext(Dispatchers.IO) {
                    routineRepository.getRoutineById(nextPlan.routineId)
                }
                val dayLabel = if (offset == 1) "Mañana" else dayName(checkDow)
                _nextWorkout.value = NextWorkoutInfo(
                    routineName = nextPlan.routineName,
                    routineId = nextPlan.routineId,
                    dayLabel = dayLabel,
                    description = routine?.description ?: ""
                )
                return
            }
        }

        _nextWorkout.value = null
    }

    private fun dayName(dow: Int): String = when (dow) {
        1 -> "Lunes"
        2 -> "Martes"
        3 -> "Miércoles"
        4 -> "Jueves"
        5 -> "Viernes"
        6 -> "Sábado"
        7 -> "Domingo"
        else -> ""
    }
}
