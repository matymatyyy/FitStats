package com.app.gimnasio.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.app.gimnasio.GimnasioApplication
import com.app.gimnasio.data.model.Exercise
import com.app.gimnasio.data.model.ExercisePhase
import com.app.gimnasio.data.model.Routine
import com.app.gimnasio.data.model.WorkoutLog
import com.app.gimnasio.data.model.WorkoutNote
import com.app.gimnasio.data.model.WorkoutSetLog
import com.app.gimnasio.data.repository.RoutineRepository
import com.app.gimnasio.data.repository.WorkoutRepository
import com.app.gimnasio.widget.WidgetUpdater
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
    val totalSets: Int,
    // Circuit info
    val isCircuitStep: Boolean = false,
    val circuitExerciseName: String? = null,
    val circuitRound: Int = 0, // 1-based
    val circuitTotalRounds: Int = 0,
    // Per-set customization
    val weightForThisSet: Double? = null,
    val repsForThisSet: Int? = null
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

    private val _hasActiveWorkout = MutableStateFlow(false)
    val hasActiveWorkout: StateFlow<Boolean> = _hasActiveWorkout.asStateFlow()

    private val _currentExerciseNotes = MutableStateFlow<List<WorkoutNote>>(emptyList())
    val currentExerciseNotes: StateFlow<List<WorkoutNote>> = _currentExerciseNotes.asStateFlow()

    private var timerJob: Job? = null
    private var restTimerJob: Job? = null
    private var startTimeMillis: Long = 0L

    private val prefs = application.getSharedPreferences("active_workout", Context.MODE_PRIVATE)

    init {
        val db = (application as GimnasioApplication).database
        routineRepository = RoutineRepository(db)
        workoutRepository = WorkoutRepository(db)
        _hasActiveWorkout.value = hasSavedWorkout()
    }

    private var currentRoutineId: Long? = null

    fun startWorkout(routineId: Long) {
        if (currentRoutineId == routineId && _routine.value != null) return
        currentRoutineId = routineId

        viewModelScope.launch {
            val r = withContext(Dispatchers.IO) {
                routineRepository.getRoutineById(routineId)
            } ?: return@launch

            val steps = buildSteps(r.exercises)

            // Check if we have a saved session for this routine
            val savedRoutineId = prefs.getLong("routine_id", -1L)
            val savedStepIndex = prefs.getInt("step_index", 0)
            val savedStartTime = prefs.getLong("start_time", 0L)
            val isResume = savedRoutineId == routineId
                    && savedStartTime > 0L
                    && savedStepIndex < steps.size

            _routine.value = r
            _isFinished.value = false
            _workoutResult.value = null
            _isResting.value = false

            _steps.value = steps
            _totalSteps.value = steps.size

            if (isResume) {
                // Restore from saved state
                _currentStepIndex.value = savedStepIndex
                _currentStep.value = steps[savedStepIndex]
                startTimeMillis = savedStartTime
                // Calculate elapsed from the real start time
                _elapsedSeconds.value = ((System.currentTimeMillis() - savedStartTime) / 1000).toInt()
            } else {
                // Fresh start
                _currentStepIndex.value = 0
                _currentStep.value = steps.firstOrNull()
                _elapsedSeconds.value = 0
                startTimeMillis = System.currentTimeMillis()
                saveState()
            }

            startTimer()
        }
    }

    private fun saveState() {
        prefs.edit()
            .putLong("routine_id", currentRoutineId ?: -1L)
            .putInt("step_index", _currentStepIndex.value)
            .putLong("start_time", startTimeMillis)
            .apply()
        _hasActiveWorkout.value = true
    }

    private fun clearSavedState() {
        prefs.edit().clear().apply()
        _hasActiveWorkout.value = false
    }

    /** Check if there's a workout in progress that can be resumed */
    fun hasSavedWorkout(): Boolean {
        val savedRoutineId = prefs.getLong("routine_id", -1L)
        val savedStartTime = prefs.getLong("start_time", 0L)
        return savedRoutineId != -1L && savedStartTime > 0L
    }

    fun getSavedRoutineId(): Long = prefs.getLong("routine_id", -1L)

    private fun buildSteps(exercises: List<Exercise>): List<WorkoutStep> {
        val steps = mutableListOf<WorkoutStep>()
        for (exercise in exercises) {
            if (exercise.isCircuit) {
                val rounds = exercise.circuitRounds ?: 1
                val circuitExNames = exercise.circuitExercises
                // Each round goes through all exercises in the circuit
                for (round in 1..rounds) {
                    for (exName in circuitExNames) {
                        steps.add(
                            WorkoutStep(
                                exercise = exercise,
                                currentSet = round,
                                totalSets = rounds,
                                isCircuitStep = true,
                                circuitExerciseName = exName,
                                circuitRound = round,
                                circuitTotalRounds = rounds
                            )
                        )
                    }
                }
            } else {
                when (exercise.phase) {
                    ExercisePhase.WARMUP -> {
                        steps.add(WorkoutStep(exercise, currentSet = 1, totalSets = 1))
                    }
                    ExercisePhase.STRENGTH -> {
                        val sets = exercise.sets ?: 1
                        for (s in 1..sets) {
                            val weightForSet = exercise.weightPerSet?.getOrNull(s - 1)
                                ?: exercise.weightKg
                            val repsForSet = exercise.repsPerSet?.getOrNull(s - 1)
                                ?: exercise.strengthReps
                            steps.add(
                                WorkoutStep(
                                    exercise = exercise,
                                    currentSet = s,
                                    totalSets = sets,
                                    weightForThisSet = weightForSet,
                                    repsForThisSet = repsForSet
                                )
                            )
                        }
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
                _elapsedSeconds.value = ((System.currentTimeMillis() - startTimeMillis) / 1000).toInt()
                delay(1000)
            }
        }
    }

    fun completeCurrentStep() {
        val wasResting = _isResting.value
        if (wasResting) {
            // Cancel current rest before advancing
            restTimerJob?.cancel()
            _isResting.value = false
            _restSecondsRemaining.value = 0
        }

        val steps = _steps.value
        val nextIndex = _currentStepIndex.value + 1

        if (nextIndex >= steps.size) {
            finishWorkout()
            return
        }

        val currentStep = steps[_currentStepIndex.value]
        val nextStep = steps[nextIndex]

        // Don't trigger rest if we were already resting (user is skipping ahead)
        if (!wasResting) {
            val shouldRest = if (currentStep.isCircuitStep) {
                currentStep.exercise.phase == ExercisePhase.STRENGTH &&
                        currentStep.exercise.restSeconds != null &&
                        currentStep.exercise.restSeconds > 0 &&
                        nextStep.isCircuitStep &&
                        nextStep.circuitRound != currentStep.circuitRound
            } else {
                currentStep.exercise.phase == ExercisePhase.STRENGTH &&
                        currentStep.exercise.restSeconds != null &&
                        currentStep.exercise.restSeconds > 0 &&
                        nextIndex < steps.size
            }

            if (shouldRest) {
                startRestTimer(currentStep.exercise.restSeconds!!)
            }
        }

        _currentStepIndex.value = nextIndex
        _currentStep.value = nextStep
        saveState()
    }

    fun skipToNextExercise() {
        restTimerJob?.cancel()
        _isResting.value = false
        _restSecondsRemaining.value = 0

        val steps = _steps.value
        val currentIdx = _currentStepIndex.value
        val currentExercise = steps[currentIdx].exercise

        // Find the first step that belongs to a different exercise
        var nextIdx = currentIdx + 1
        while (nextIdx < steps.size && steps[nextIdx].exercise === currentExercise) {
            nextIdx++
        }

        if (nextIdx >= steps.size) {
            finishWorkout()
            return
        }

        _currentStepIndex.value = nextIdx
        _currentStep.value = steps[nextIdx]
        saveState()
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
        clearSavedState()

        val r = _routine.value ?: return
        val elapsed = _elapsedSeconds.value
        val exerciseNames = r.exercises.flatMap { ex ->
            if (ex.isCircuit) ex.circuitExercises else listOf(ex.name)
        }.distinct()
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
        val completedSteps = _steps.value
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val today = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis

                val logId = workoutRepository.insertWorkoutLog(
                    WorkoutLog(
                        routineId = r.id,
                        routineName = r.name,
                        date = today,
                        durationSeconds = elapsed,
                        exercisesSummary = exerciseNames.joinToString(", "),
                        totalSets = totalSets
                    )
                )

                // Save detailed per-set data
                val setLogs = completedSteps.map { step ->
                    val exName = if (step.isCircuitStep) {
                        step.circuitExerciseName ?: step.exercise.name
                    } else {
                        step.exercise.name
                    }
                    WorkoutSetLog(
                        workoutLogId = logId,
                        exerciseName = exName,
                        setNumber = step.currentSet,
                        reps = step.repsForThisSet
                            ?: step.exercise.strengthReps
                            ?: step.exercise.reps,
                        weightKg = step.weightForThisSet ?: step.exercise.weightKg,
                        durationSeconds = step.exercise.durationSeconds,
                        phase = step.exercise.phase.name,
                        isCircuit = step.isCircuitStep,
                        date = today
                    )
                }
                workoutRepository.insertSetLogs(setLogs, logId)
            }
            WidgetUpdater.updateAll(getApplication())
        }
    }

    fun reset() {
        timerJob?.cancel()
        restTimerJob?.cancel()
        clearSavedState()
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

    private fun currentExerciseName(): String? {
        val step = _currentStep.value ?: return null
        return if (step.isCircuitStep) step.circuitExerciseName else step.exercise.name
    }

    fun loadNotesForCurrentExercise() {
        val exName = currentExerciseName() ?: return
        val routineId = _routine.value?.id
        viewModelScope.launch {
            val notes = withContext(Dispatchers.IO) {
                workoutRepository.getNotesForExercise(routineId, exName)
            }
            _currentExerciseNotes.value = notes
        }
    }

    fun addNoteToCurrentExercise(text: String) {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return
        val exName = currentExerciseName() ?: return
        val r = _routine.value ?: return
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                workoutRepository.insertNote(
                    WorkoutNote(
                        routineId = r.id,
                        routineName = r.name,
                        exerciseName = exName,
                        note = trimmed
                    )
                )
            }
            loadNotesForCurrentExercise()
        }
    }

    fun deleteNote(id: Long) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                workoutRepository.deleteNote(id)
            }
            loadNotesForCurrentExercise()
        }
    }
}
