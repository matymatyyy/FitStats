package com.app.gimnasio.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.app.gimnasio.GimnasioApplication
import com.app.gimnasio.data.model.Exercise
import com.app.gimnasio.data.model.ExercisePhase
import com.app.gimnasio.data.model.Routine
import com.app.gimnasio.data.model.WorkoutLog
import com.app.gimnasio.data.repository.RoutineRepository
import com.app.gimnasio.data.repository.WorkoutRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

data class WorkoutStep(
    val exercise: Exercise,
    val currentSet: Int, // 1-based
    val totalSets: Int
)

data class WorkoutResult(
    val routineId: Long?,
    val routineName: String,
    val durationSeconds: Int,
    val exerciseNames: List<String>,
    val totalExercises: Int,
    val totalSets: Int
)

class ActiveWorkoutViewModel(application: Application) : AndroidViewModel(application) {

    private val routineRepository: RoutineRepository
    private val workoutRepository: WorkoutRepository

    private val _routine = MutableStateFlow<Routine?>(null)
    val routine: StateFlow<Routine?> = _routine.asStateFlow()

    private val _steps = MutableStateFlow<List<WorkoutStep>>(emptyList())

    private val _currentStepIndex = MutableStateFlow(0)
    val currentStepIndex: StateFlow<Int> = _currentStepIndex.asStateFlow()

    private val _currentStep = MutableStateFlow<WorkoutStep?>(null)
    val currentStep: StateFlow<WorkoutStep?> = _currentStep.asStateFlow()

    private val _totalSteps = MutableStateFlow(0)
    val totalSteps: StateFlow<Int> = _totalSteps.asStateFlow()

    private val _elapsedSeconds = MutableStateFlow(0)
    val elapsedSeconds: StateFlow<Int> = _elapsedSeconds.asStateFlow()

    private val _isResting = MutableStateFlow(false)
    val isResting: StateFlow<Boolean> = _isResting.asStateFlow()

    private val _restSecondsRemaining = MutableStateFlow(0)
    val restSecondsRemaining: StateFlow<Int> = _restSecondsRemaining.asStateFlow()

    private val _isFinished = MutableStateFlow(false)
    val isFinished: StateFlow<Boolean> = _isFinished.asStateFlow()

    private val _workoutResult = MutableStateFlow<WorkoutResult?>(null)
    val workoutResult: StateFlow<WorkoutResult?> = _workoutResult.asStateFlow()

    private var timerJob: Job? = null
    private var restTimerJob: Job? = null
    private var startTimeMillis: Long = 0L

    init {
        val db = (application as GimnasioApplication).database
        routineRepository = RoutineRepository(db)
        workoutRepository = WorkoutRepository(db)
    }

    private var currentRoutineId: Long? = null

    fun startWorkout(routineId: Long) {
        if (currentRoutineId == routineId && _routine.value != null) return
        currentRoutineId = routineId

        viewModelScope.launch {
            val r = withContext(Dispatchers.IO) {
                routineRepository.getRoutineById(routineId)
            } ?: return@launch

            _routine.value = r
            _isFinished.value = false
            _workoutResult.value = null
            _currentStepIndex.value = 0
            _elapsedSeconds.value = 0
            _isResting.value = false

            val steps = buildSteps(r.exercises)
            _steps.value = steps
            _totalSteps.value = steps.size
            _currentStep.value = steps.firstOrNull()

            startTimeMillis = System.currentTimeMillis()
            startTimer()
        }
    }

    private fun buildSteps(exercises: List<Exercise>): List<WorkoutStep> {
        val steps = mutableListOf<WorkoutStep>()
        for (exercise in exercises) {
            when (exercise.phase) {
                ExercisePhase.WARMUP -> {
                    steps.add(WorkoutStep(exercise, currentSet = 1, totalSets = 1))
                }
                ExercisePhase.STRENGTH -> {
                    val sets = exercise.sets ?: 1
                    for (s in 1..sets) {
                        steps.add(WorkoutStep(exercise, currentSet = s, totalSets = sets))
                    }
                }
            }
        }
        return steps
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                _elapsedSeconds.value++
            }
        }
    }

    fun completeCurrentStep() {
        val steps = _steps.value
        val nextIndex = _currentStepIndex.value + 1

        if (nextIndex >= steps.size) {
            finishWorkout()
            return
        }

        val currentStep = steps[_currentStepIndex.value]
        val nextStep = steps[nextIndex]

        // If we just finished a strength set and there's a rest time, start rest
        if (currentStep.exercise.phase == ExercisePhase.STRENGTH &&
            currentStep.exercise.restSeconds != null &&
            currentStep.exercise.restSeconds > 0 &&
            // Only rest between sets of same exercise or before next exercise
            nextIndex < steps.size
        ) {
            startRestTimer(currentStep.exercise.restSeconds)
        }

        _currentStepIndex.value = nextIndex
        _currentStep.value = nextStep
    }

    fun skipRest() {
        restTimerJob?.cancel()
        _isResting.value = false
        _restSecondsRemaining.value = 0
    }

    private fun startRestTimer(seconds: Int) {
        restTimerJob?.cancel()
        _isResting.value = true
        _restSecondsRemaining.value = seconds
        restTimerJob = viewModelScope.launch {
            for (i in seconds downTo 1) {
                _restSecondsRemaining.value = i
                delay(1000)
            }
            _isResting.value = false
            _restSecondsRemaining.value = 0
        }
    }

    private fun finishWorkout() {
        timerJob?.cancel()
        restTimerJob?.cancel()
        _isResting.value = false

        val r = _routine.value ?: return
        val elapsed = _elapsedSeconds.value
        val exerciseNames = r.exercises.map { it.name }.distinct()
        val totalSets = _steps.value.size

        val result = WorkoutResult(
            routineId = r.id,
            routineName = r.name,
            durationSeconds = elapsed,
            exerciseNames = exerciseNames,
            totalExercises = exerciseNames.size,
            totalSets = totalSets
        )
        _workoutResult.value = result
        _isFinished.value = true

        // Save to database
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val today = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis

                workoutRepository.insertWorkoutLog(
                    WorkoutLog(
                        routineId = r.id,
                        routineName = r.name,
                        date = today,
                        durationSeconds = elapsed,
                        exercisesSummary = exerciseNames.joinToString(", "),
                        totalSets = totalSets
                    )
                )
            }
        }
    }

    fun reset() {
        timerJob?.cancel()
        restTimerJob?.cancel()
        currentRoutineId = null
        _routine.value = null
        _steps.value = emptyList()
        _currentStepIndex.value = 0
        _currentStep.value = null
        _totalSteps.value = 0
        _elapsedSeconds.value = 0
        _isResting.value = false
        _restSecondsRemaining.value = 0
        _isFinished.value = false
        _workoutResult.value = null
    }

    fun formatTime(totalSeconds: Int): String {
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return "%02d:%02d".format(minutes, seconds)
    }
}
