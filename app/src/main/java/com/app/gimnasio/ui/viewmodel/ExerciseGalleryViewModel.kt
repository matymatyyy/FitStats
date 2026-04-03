package com.app.gimnasio.ui.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.app.gimnasio.GimnasioApplication
import com.app.gimnasio.data.model.ExerciseInfo
import com.app.gimnasio.data.model.MuscleGroup
import com.app.gimnasio.data.repository.ExerciseGalleryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

class ExerciseGalleryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ExerciseGalleryRepository

    private val _exercises = MutableStateFlow<List<ExerciseInfo>>(emptyList())
    val exercises: StateFlow<List<ExerciseInfo>> = _exercises.asStateFlow()

    private val _selectedExercise = MutableStateFlow<ExerciseInfo?>(null)
    val selectedExercise: StateFlow<ExerciseInfo?> = _selectedExercise.asStateFlow()

    private val _exerciseCounts = MutableStateFlow<Map<String, Int>>(emptyMap())
    val exerciseCounts: StateFlow<Map<String, Int>> = _exerciseCounts.asStateFlow()

    private val _allExercises = MutableStateFlow<List<ExerciseInfo>>(emptyList())
    val allExercises: StateFlow<List<ExerciseInfo>> = _allExercises.asStateFlow()

    init {
        val db = (application as GimnasioApplication).database
        repository = ExerciseGalleryRepository(db)
        loadCounts()
    }

    fun loadCounts() {
        viewModelScope.launch {
            _exerciseCounts.value = withContext(Dispatchers.IO) {
                repository.getExerciseCountsByMuscle()
            }
        }
    }

    fun loadAllExercises() {
        viewModelScope.launch {
            _allExercises.value = withContext(Dispatchers.IO) {
                repository.getAllExercises()
            }
        }
    }

    fun loadExercises(muscleGroup: MuscleGroup) {
        viewModelScope.launch {
            _exercises.value = withContext(Dispatchers.IO) {
                repository.getExercisesByMuscle(muscleGroup)
            }
        }
    }

    fun addExercise(name: String, description: String, muscleGroup: MuscleGroup, imageUri: Uri?) {
        viewModelScope.launch {
            val imagePath = imageUri?.let { uri ->
                withContext(Dispatchers.IO) {
                    saveImageToInternal(getApplication(), uri)
                }
            }
            withContext(Dispatchers.IO) {
                repository.addExercise(
                    ExerciseInfo(
                        name = name,
                        description = description,
                        muscleGroup = muscleGroup,
                        imagePath = imagePath
                    )
                )
            }
            loadExercises(muscleGroup)
        }
    }

    fun loadExercise(id: Long) {
        viewModelScope.launch {
            _selectedExercise.value = withContext(Dispatchers.IO) {
                repository.getExerciseById(id)
            }
        }
    }

    fun updateExercise(exercise: ExerciseInfo, newImageUri: Uri?) {
        viewModelScope.launch {
            val newImagePath = if (newImageUri != null) {
                withContext(Dispatchers.IO) {
                    // Delete old image if replacing
                    exercise.imagePath?.let { File(it).delete() }
                    saveImageToInternal(getApplication(), newImageUri)
                }
            } else {
                exercise.imagePath
            }
            withContext(Dispatchers.IO) {
                repository.updateExercise(exercise.copy(imagePath = newImagePath))
            }
            _selectedExercise.value = exercise.copy(imagePath = newImagePath)
            loadExercises(exercise.muscleGroup)
        }
    }

    fun deleteExercise(exercise: ExerciseInfo) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                exercise.imagePath?.let { path ->
                    File(path).delete()
                }
                repository.deleteExercise(exercise.id)
            }
            loadExercises(exercise.muscleGroup)
        }
    }

    private fun saveImageToInternal(context: Context, uri: Uri): String? {
        return try {
            val dir = File(context.filesDir, "exercise_images")
            if (!dir.exists()) dir.mkdirs()
            val file = File(dir, "${UUID.randomUUID()}.jpg")
            context.contentResolver.openInputStream(uri)?.use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            file.absolutePath
        } catch (e: Exception) {
            null
        }
    }
}
