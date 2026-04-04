package com.app.gimnasio.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.app.gimnasio.GimnasioApplication
import com.app.gimnasio.data.model.WorkoutLog
import com.app.gimnasio.data.repository.WorkoutRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

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

    private val _weeklyCount = MutableStateFlow(0)
    val weeklyCount: StateFlow<Int> = _weeklyCount.asStateFlow()

    private val _weeklyDates = MutableStateFlow<Set<Long>>(emptySet())
    val weeklyDates: StateFlow<Set<Long>> = _weeklyDates.asStateFlow()

    private val _weeklyTotalSeconds = MutableStateFlow(0)
    val weeklyTotalSeconds: StateFlow<Int> = _weeklyTotalSeconds.asStateFlow()

    private val _weeklyTotalSets = MutableStateFlow(0)
    val weeklyTotalSets: StateFlow<Int> = _weeklyTotalSets.asStateFlow()

    // Period-based stats (7/14/30 days)
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

            val logs = withContext(Dispatchers.IO) {
                repository.getWorkoutLogsByDateRange(start, today)
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
        }
    }

    fun selectDate(date: Long) {
        _selectedDate.value = date
        viewModelScope.launch {
            _selectedDateWorkouts.value = withContext(Dispatchers.IO) {
                repository.getWorkoutLogsForDate(date)
            }
        }
    }

    fun clearSelection() {
        _selectedDate.value = null
        _selectedDateWorkouts.value = emptyList()
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
            // Refresh selected date if same
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
            _selectedDate.value?.let { selectDate(it) }
        }
    }

    fun formatDuration(totalSeconds: Int): String {
        if (totalSeconds == 0) return "Manual"
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        return if (hours > 0) "${hours}h ${minutes}min" else "${minutes}min"
    }
}
