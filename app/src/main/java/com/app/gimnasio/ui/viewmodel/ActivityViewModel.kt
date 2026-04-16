package com.app.gimnasio.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.app.gimnasio.GimnasioApplication
import com.app.gimnasio.data.model.WorkoutLog
import com.app.gimnasio.data.model.WorkoutSetLog
import com.app.gimnasio.data.repository.WorkoutRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

data class ExerciseStats(
    val name: String,
    val totalSets: Int,
    val totalReps: Int,
    val maxWeight: Double,
    val totalVolume: Double // reps * weight
)

data class PeriodSummary(
    val workouts: Int,
    val totalSeconds: Int,
    val totalSets: Int,
    val totalReps: Int,
    val totalVolume: Double,
    val avgDurationSeconds: Int,
    val exerciseBreakdown: List<ExerciseStats>,
    val volumePerDay: List<Pair<Long, Double>>, // date to volume
    val exerciseProgress: Map<String, List<ExerciseProgressPoint>> // exercise -> points over time
)

data class ExerciseProgressPoint(
    val date: Long,
    val maxWeight: Double,
    val totalVolume: Double,
    val totalReps: Int
)

class ActivityViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: WorkoutRepository

    private val _currentMonth = MutableStateFlow(Calendar.getInstance())
    val currentMonth: StateFlow<Calendar> = _currentMonth.asStateFlow()

    private val _monthWorkouts = MutableStateFlow<Map<Long, List<WorkoutLog>>>(emptyMap())
    val monthWorkouts: StateFlow<Map<Long, List<WorkoutLog>>> = _monthWorkouts.asStateFlow()

    private val _selectedDate = MutableStateFlow<Long?>(null)
    val selectedDate: StateFlow<Long?> = _selectedDate.asStateFlow()

    private val _selectedDateWorkouts = MutableStateFlow<List<WorkoutLog>>(emptyList())
    val selectedDateWorkouts: StateFlow<List<WorkoutLog>> = _selectedDateWorkouts.asStateFlow()

    // Set logs for the selected date (detail view)
    private val _selectedDateSetLogs = MutableStateFlow<List<WorkoutSetLog>>(emptyList())
    val selectedDateSetLogs: StateFlow<List<WorkoutSetLog>> = _selectedDateSetLogs.asStateFlow()

    private val _weeklyCount = MutableStateFlow(0)
    val weeklyCount: StateFlow<Int> = _weeklyCount.asStateFlow()

    private val _weeklyDates = MutableStateFlow<Set<Long>>(emptySet())
    val weeklyDates: StateFlow<Set<Long>> = _weeklyDates.asStateFlow()

    private val _weeklyTotalSeconds = MutableStateFlow(0)
    val weeklyTotalSeconds: StateFlow<Int> = _weeklyTotalSeconds.asStateFlow()

    private val _weeklyTotalSets = MutableStateFlow(0)
    val weeklyTotalSets: StateFlow<Int> = _weeklyTotalSets.asStateFlow()

    // Period-based stats (7/14/30/90 days)
    private val _periodDays = MutableStateFlow(7)
    val periodDays: StateFlow<Int> = _periodDays.asStateFlow()

    private val _periodWorkouts = MutableStateFlow(0)
    val periodWorkouts: StateFlow<Int> = _periodWorkouts.asStateFlow()

    private val _periodTotalSeconds = MutableStateFlow(0)
    val periodTotalSeconds: StateFlow<Int> = _periodTotalSeconds.asStateFlow()

    private val _periodTotalSets = MutableStateFlow(0)
    val periodTotalSets: StateFlow<Int> = _periodTotalSets.asStateFlow()

    // Daily calories for chart: list of (dayLabel, calories)
    private val _dailyCalories = MutableStateFlow<List<Pair<String, Int>>>(emptyList())
    val dailyCalories: StateFlow<List<Pair<String, Int>>> = _dailyCalories.asStateFlow()

    // Period summary with detailed stats
    private val _periodSummary = MutableStateFlow<PeriodSummary?>(null)
    val periodSummary: StateFlow<PeriodSummary?> = _periodSummary.asStateFlow()

    // Exercise names that have logged data
    private val _loggedExercises = MutableStateFlow<List<String>>(emptyList())
    val loggedExercises: StateFlow<List<String>> = _loggedExercises.asStateFlow()

    // Widget suggestion banner
    private val prefs = application.getSharedPreferences("fitstats_prefs", Context.MODE_PRIVATE)
    private val _showWidgetBanner = MutableStateFlow(!prefs.getBoolean("widget_banner_dismissed", false))
    val showWidgetBanner: StateFlow<Boolean> = _showWidgetBanner.asStateFlow()

    fun dismissWidgetBanner() {
        _showWidgetBanner.value = false
        prefs.edit().putBoolean("widget_banner_dismissed", true).apply()
    }

    init {
        val db = (application as GimnasioApplication).database
        repository = WorkoutRepository(db)
        loadMonthData()
        loadWeeklyData()
        loadPeriodData()
        loadLoggedExercises()
    }

    private fun loadLoggedExercises() {
        viewModelScope.launch {
            _loggedExercises.value = withContext(Dispatchers.IO) {
                repository.getLoggedExerciseNames()
            }
        }
    }

    fun loadMonthData() {
        viewModelScope.launch {
            val cal = _currentMonth.value.clone() as Calendar
            cal.set(Calendar.DAY_OF_MONTH, 1)
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            val startOfMonth = cal.timeInMillis

            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
            val endOfMonth = cal.timeInMillis

            val logs = withContext(Dispatchers.IO) {
                repository.getWorkoutLogsByDateRange(startOfMonth, endOfMonth)
            }

            _monthWorkouts.value = logs.groupBy { it.date }
        }
    }

    fun loadWeeklyData() {
        viewModelScope.launch {
            val cal = Calendar.getInstance()
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            val startOfWeek = cal.timeInMillis

            cal.add(Calendar.DAY_OF_WEEK, 6)
            val endOfWeek = cal.timeInMillis

            val logs = withContext(Dispatchers.IO) {
                repository.getWorkoutLogsByDateRange(startOfWeek, endOfWeek)
            }

            val dates = logs.map { it.date }.toSet()
            _weeklyDates.value = dates
            _weeklyCount.value = dates.size
            _weeklyTotalSeconds.value = logs.sumOf { it.durationSeconds }
            _weeklyTotalSets.value = logs.sumOf { it.totalSets }
        }
    }

    fun setPeriod(days: Int) {
        _periodDays.value = days
        loadPeriodData()
    }

    fun loadPeriodData() {
        viewModelScope.launch {
            val days = _periodDays.value
            val cal = Calendar.getInstance()
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            val today = cal.timeInMillis

            cal.add(Calendar.DAY_OF_YEAR, -(days - 1))
            val start = cal.timeInMillis

            val (logs, setLogs) = withContext(Dispatchers.IO) {
                val l = repository.getWorkoutLogsByDateRange(start, today)
                val s = repository.getSetLogsByDateRange(start, today)
                l to s
            }

            val dates = logs.map { it.date }.toSet()
            _periodWorkouts.value = dates.size
            _periodTotalSeconds.value = logs.sumOf { it.durationSeconds }
            _periodTotalSets.value = logs.sumOf { it.totalSets }

            // Build daily calories list
            val logsByDate = logs.groupBy { it.date }
            val dayNames = arrayOf("D", "L", "M", "X", "J", "V", "S")
            val dailyList = mutableListOf<Pair<String, Int>>()
            val iterCal = Calendar.getInstance()
            iterCal.timeInMillis = start
            for (i in 0 until days) {
                iterCal.set(Calendar.HOUR_OF_DAY, 0)
                iterCal.set(Calendar.MINUTE, 0)
                iterCal.set(Calendar.SECOND, 0)
                iterCal.set(Calendar.MILLISECOND, 0)
                val dateMs = iterCal.timeInMillis
                val dayLogs = logsByDate[dateMs] ?: emptyList()
                val seconds = dayLogs.sumOf { it.durationSeconds }
                val cals = (seconds / 3600.0 * 300).toInt()
                val dow = iterCal.get(Calendar.DAY_OF_WEEK)
                val label = dayNames[dow - 1]
                dailyList.add(label to cals)
                iterCal.add(Calendar.DAY_OF_YEAR, 1)
            }
            _dailyCalories.value = dailyList

            // Build detailed period summary
            val totalReps = setLogs.mapNotNull { it.reps }.sum()
            val totalVolume = setLogs.sumOf { (it.reps ?: 0) * (it.weightKg ?: 0.0) }

            val exerciseBreakdown = setLogs.groupBy { it.exerciseName }.map { (name, sets) ->
                ExerciseStats(
                    name = name,
                    totalSets = sets.size,
                    totalReps = sets.mapNotNull { it.reps }.sum(),
                    maxWeight = sets.mapNotNull { it.weightKg }.maxOrNull() ?: 0.0,
                    totalVolume = sets.sumOf { (it.reps ?: 0) * (it.weightKg ?: 0.0) }
                )
            }.sortedByDescending { it.totalVolume }

            val volumePerDay = setLogs.groupBy { it.date }.map { (date, sets) ->
                date to sets.sumOf { (it.reps ?: 0) * (it.weightKg ?: 0.0) }
            }.sortedBy { it.first }

            // Exercise progression: for each exercise, group by date
            val exerciseProgress = setLogs.groupBy { it.exerciseName }.mapValues { (_, sets) ->
                sets.groupBy { it.date }.map { (date, daySets) ->
                    ExerciseProgressPoint(
                        date = date,
                        maxWeight = daySets.mapNotNull { it.weightKg }.maxOrNull() ?: 0.0,
                        totalVolume = daySets.sumOf { (it.reps ?: 0) * (it.weightKg ?: 0.0) },
                        totalReps = daySets.mapNotNull { it.reps }.sum()
                    )
                }.sortedBy { it.date }
            }

            _periodSummary.value = PeriodSummary(
                workouts = dates.size,
                totalSeconds = logs.sumOf { it.durationSeconds },
                totalSets = setLogs.size,
                totalReps = totalReps,
                totalVolume = totalVolume,
                avgDurationSeconds = if (dates.isNotEmpty()) logs.sumOf { it.durationSeconds } / dates.size else 0,
                exerciseBreakdown = exerciseBreakdown,
                volumePerDay = volumePerDay,
                exerciseProgress = exerciseProgress
            )
        }
    }

    fun selectDate(date: Long) {
        _selectedDate.value = date
        viewModelScope.launch {
            val (workouts, sets) = withContext(Dispatchers.IO) {
                val w = repository.getWorkoutLogsForDate(date)
                val s = repository.getSetLogsByDateRange(date, date)
                w to s
            }
            _selectedDateWorkouts.value = workouts
            _selectedDateSetLogs.value = sets
        }
    }

    fun clearSelection() {
        _selectedDate.value = null
        _selectedDateWorkouts.value = emptyList()
        _selectedDateSetLogs.value = emptyList()
    }

    fun previousMonth() {
        val cal = _currentMonth.value.clone() as Calendar
        cal.add(Calendar.MONTH, -1)
        _currentMonth.value = cal
        loadMonthData()
    }

    fun nextMonth() {
        val cal = _currentMonth.value.clone() as Calendar
        cal.add(Calendar.MONTH, 1)
        _currentMonth.value = cal
        loadMonthData()
    }

    fun markManualWorkout(date: Long, name: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.insertWorkoutLog(
                    WorkoutLog(
                        routineName = name,
                        date = date,
                        durationSeconds = 0,
                        exercisesSummary = name
                    )
                )
            }
            loadMonthData()
            loadWeeklyData()
            if (_selectedDate.value == date) {
                selectDate(date)
            }
        }
    }

    fun deleteWorkout(id: Long) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.deleteWorkoutLog(id)
            }
            loadMonthData()
            loadWeeklyData()
            loadPeriodData()
            _selectedDate.value?.let { selectDate(it) }
        }
    }

    fun formatDuration(totalSeconds: Int): String {
        if (totalSeconds == 0) return "Manual"
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        return if (hours > 0) "${hours}h ${minutes}min" else "${minutes}min"
    }

    fun formatVolume(kg: Double): String {
        return when {
            kg >= 1000 -> "${String.format("%.1f", kg / 1000)}t"
            else -> "${kg.toInt()} kg"
        }
    }
}
