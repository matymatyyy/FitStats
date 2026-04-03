package com.app.gimnasio.data.model

enum class MuscleGroup(val displayName: String) {
    PECTORALES("Pectorales"),
    HOMBROS("Hombros"),
    TRICEPS("Tríceps"),
    ESPALDA("Espalda"),
    BICEPS("Bíceps"),
    TRAPECIO("Trapecio"),
    ANTEBRAZOS("Antebrazos"),
    CUADRICEPS("Cuádriceps"),
    ISQUIOTIBIALES("Isquiotibiales"),
    GLUTEOS("Glúteos"),
    ABDUCTORES("Abductores"),
    ADUCTORES("Aductores"),
    GEMELOS("Gemelos"),
    ABDOMINALES("Abdominales"),
    LUMBARES("Lumbares")
}

data class ExerciseInfo(
    val id: Long = 0,
    val name: String,
    val description: String,
    val muscleGroup: MuscleGroup,
    val imagePath: String? = null
)
