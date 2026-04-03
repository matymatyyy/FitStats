package com.app.gimnasio.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RestTimerViewModel : ViewModel() {

    private val _secondsRemaining = MutableStateFlow(0)
    val secondsRemaining: StateFlow<Int> = _secondsRemaining.asStateFlow()

    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow()

    private val _selectedSeconds = MutableStateFlow(90)
    val selectedSeconds: StateFlow<Int> = _selectedSeconds.asStateFlow()

    private var timerJob: Job? = null

    fun setDuration(seconds: Int) {
        if (!_isRunning.value) {
            _selectedSeconds.value = seconds
            _secondsRemaining.value = seconds
        }
    }

    fun start() {
        if (_isRunning.value) return
        if (_secondsRemaining.value == 0) {
            _secondsRemaining.value = _selectedSeconds.value
        }
        _isRunning.value = true
        timerJob = viewModelScope.launch {
            while (_secondsRemaining.value > 0) {
                delay(1000L)
                _secondsRemaining.value -= 1
            }
            _isRunning.value = false
        }
    }

    fun pause() {
        timerJob?.cancel()
        _isRunning.value = false
    }

    fun reset() {
        timerJob?.cancel()
        _isRunning.value = false
        _secondsRemaining.value = _selectedSeconds.value
    }

    fun formatTime(totalSeconds: Int): String {
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return "%02d:%02d".format(minutes, seconds)
    }
}
