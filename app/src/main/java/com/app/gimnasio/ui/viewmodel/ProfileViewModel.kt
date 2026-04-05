package com.app.gimnasio.ui.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.app.gimnasio.GimnasioApplication
import com.app.gimnasio.data.model.BodyMeasurements
import com.app.gimnasio.data.model.PRHistoryEntry
import com.app.gimnasio.data.model.PersonalRecords
import com.app.gimnasio.data.model.UserProfile
import com.app.gimnasio.data.repository.ProfileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ProfileRepository

    private val _profile = MutableStateFlow(UserProfile())
    val profile: StateFlow<UserProfile> = _profile.asStateFlow()

    private val _measurements = MutableStateFlow(BodyMeasurements())
    val measurements: StateFlow<BodyMeasurements> = _measurements.asStateFlow()

    private val _personalRecords = MutableStateFlow(PersonalRecords())
    val personalRecords: StateFlow<PersonalRecords> = _personalRecords.asStateFlow()

    private val _totalWorkouts = MutableStateFlow(0)
    val totalWorkouts: StateFlow<Int> = _totalWorkouts.asStateFlow()

    private val _prHistory = MutableStateFlow<List<PRHistoryEntry>>(emptyList())
    val prHistory: StateFlow<List<PRHistoryEntry>> = _prHistory.asStateFlow()

    private val _isFirstTime = MutableStateFlow<Boolean?>(null)
    val isFirstTime: StateFlow<Boolean?> = _isFirstTime.asStateFlow()

    private val prefs = application.getSharedPreferences("fitstats_prefs", Context.MODE_PRIVATE)

    private val _showInfoCard = MutableStateFlow(false)
    val showInfoCard: StateFlow<Boolean> = _showInfoCard.asStateFlow()

    init {
        val db = (application as GimnasioApplication).database
        repository = ProfileRepository(db)
        _showInfoCard.value = !prefs.getBoolean("info_card_dismissed", false)
        loadProfile()
    }

    fun dismissInfoCard() {
        _showInfoCard.value = false
        prefs.edit().putBoolean("info_card_dismissed", true).apply()
    }

    fun loadProfile() {
        viewModelScope.launch {
            val p = withContext(Dispatchers.IO) { repository.getUserProfile() }
            if (p == null) {
                _isFirstTime.value = true
                _profile.value = UserProfile()
            } else {
                _isFirstTime.value = false
                _profile.value = p
            }

            val m = withContext(Dispatchers.IO) { repository.getBodyMeasurements() }
            if (m != null) _measurements.value = m

            val pr = withContext(Dispatchers.IO) { repository.getPersonalRecords() }
            if (pr != null) _personalRecords.value = pr

            _totalWorkouts.value = withContext(Dispatchers.IO) { repository.getTotalWorkoutCount() }
            _prHistory.value = withContext(Dispatchers.IO) { repository.getPRHistory() }
        }
    }

    fun saveName(name: String) {
        val updated = _profile.value.copy(name = name)
        _profile.value = updated
        viewModelScope.launch {
            withContext(Dispatchers.IO) { repository.saveUserProfile(updated) }
            _isFirstTime.value = false
        }
    }

    fun saveProfile(name: String, age: Int?, gender: String?) {
        val updated = _profile.value.copy(name = name, age = age, gender = gender)
        _profile.value = updated
        viewModelScope.launch {
            withContext(Dispatchers.IO) { repository.saveUserProfile(updated) }
        }
    }

    fun savePhoto(uri: Uri) {
        viewModelScope.launch {
            val path = withContext(Dispatchers.IO) {
                saveImageToInternal(uri)
            } ?: return@launch
            val updated = _profile.value.copy(photoPath = path)
            _profile.value = updated
            withContext(Dispatchers.IO) { repository.saveUserProfile(updated) }
        }
    }

    private fun saveImageToInternal(uri: Uri): String? {
        val context = getApplication<Application>()
        val dir = File(context.filesDir, "profile_photos")
        if (!dir.exists()) dir.mkdirs()
        val file = File(dir, "profile_${UUID.randomUUID()}.jpg")
        context.contentResolver.openInputStream(uri)?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        } ?: return null
        return file.absolutePath
    }

    fun saveMeasurements(m: BodyMeasurements) {
        _measurements.value = m
        viewModelScope.launch {
            withContext(Dispatchers.IO) { repository.saveBodyMeasurements(m) }
        }
    }

    fun savePersonalRecords(pr: PersonalRecords) {
        _personalRecords.value = pr
        viewModelScope.launch {
            withContext(Dispatchers.IO) { repository.savePersonalRecords(pr) }
            _prHistory.value = withContext(Dispatchers.IO) { repository.getPRHistory() }
        }
    }

    fun refreshWorkoutCount() {
        viewModelScope.launch {
            _totalWorkouts.value = withContext(Dispatchers.IO) { repository.getTotalWorkoutCount() }
        }
    }
}
