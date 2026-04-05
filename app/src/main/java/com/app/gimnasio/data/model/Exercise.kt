package com.app.gimnasio.data.model

enum class ExercisePhase { WARMUP, STRENGTH }

data class Exercise(
    val id: Long = 0,
    val name: String,
    val phase: ExercisePhase,
    // Warmup: puede ser por reps O por tiempo
    val durationSeconds: Int? = null,
    val reps: Int? = null,
    // Strength: series, reps, peso, descanso
    val sets: Int? = null,
    val strengthReps: Int? = null,
    val restSeconds: Int? = null,
    val weightKg: Double? = null,
    // Circuito: ejercicios encadenados por rondas
    val isCircuit: Boolean = false,
    val circuitExercises: List<String> = emptyList(),
    val circuitRounds: Int? = null,
    // Series personalizadas (null = mismos valores para todas)
    val weightPerSet: List<Double>? = null,
    val repsPerSet: List<Int>? = null
)
