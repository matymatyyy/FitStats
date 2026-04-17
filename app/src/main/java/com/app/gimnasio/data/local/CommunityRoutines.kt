package com.app.gimnasio.data.local

import com.app.gimnasio.data.model.Exercise
import com.app.gimnasio.data.model.ExercisePhase

data class CommunityRoutine(
    val name: String,
    val description: String,
    val author: String,
    val level: String,
    val focus: String,
    val exercises: List<Exercise>
)

object CommunityRoutines {

    fun getAll(): List<CommunityRoutine> = listOf(
        pushDay(),
        pullDay(),
        legDay(),
        fullBodyPrincipiante(),
        upperBody(),
        lowerBody()
    )

    fun pushDayRoutine() = pushDay()
    fun pullDayRoutine() = pullDay()
    fun legDayRoutine() = legDay()
    fun fullBodyRoutine() = fullBodyPrincipiante()
    fun upperBodyRoutine() = upperBody()
    fun lowerBodyRoutine() = lowerBody()

    private fun strength(
        name: String,
        sets: Int,
        reps: Int,
        rest: Int,
        weight: Double? = null
    ) = Exercise(
        name = name,
        phase = ExercisePhase.STRENGTH,
        sets = sets,
        strengthReps = reps,
        restSeconds = rest,
        weightKg = weight
    )

    private fun warmup(name: String, durationSec: Int) = Exercise(
        name = name,
        phase = ExercisePhase.WARMUP,
        durationSeconds = durationSec
    )

    private fun pushDay() = CommunityRoutine(
        name = "Push Day (PPL)",
        description = "Empuje — pecho, hombros y tríceps. Clásica de la rutina PPL de 6 días.",
        author = "Comunidad FitStats",
        level = "Intermedio",
        focus = "Pecho · Hombros · Tríceps",
        exercises = listOf(
            warmup("Movilidad de hombros", 120),
            strength("Press de banca", sets = 4, reps = 8, rest = 120),
            strength("Press inclinado con mancuernas", sets = 3, reps = 10, rest = 90),
            strength("Press militar", sets = 4, reps = 8, rest = 120),
            strength("Elevaciones laterales", sets = 3, reps = 15, rest = 60),
            strength("Fondos en paralelas", sets = 3, reps = 10, rest = 90),
            strength("Extensión de tríceps en polea", sets = 3, reps = 12, rest = 60)
        )
    )

    private fun pullDay() = CommunityRoutine(
        name = "Pull Day (PPL)",
        description = "Tracción — espalda y bíceps. Segundo día de la PPL.",
        author = "Comunidad FitStats",
        level = "Intermedio",
        focus = "Espalda · Bíceps",
        exercises = listOf(
            warmup("Remo con banda elástica", 90),
            strength("Dominadas", sets = 4, reps = 8, rest = 120),
            strength("Remo con barra", sets = 4, reps = 8, rest = 120),
            strength("Jalón al pecho", sets = 3, reps = 10, rest = 90),
            strength("Remo sentado en polea", sets = 3, reps = 12, rest = 90),
            strength("Curl con barra", sets = 3, reps = 10, rest = 75),
            strength("Curl martillo", sets = 3, reps = 12, rest = 60)
        )
    )

    private fun legDay() = CommunityRoutine(
        name = "Leg Day (PPL)",
        description = "Día de pierna completo — cuádriceps, femorales, glúteos y gemelos.",
        author = "Comunidad FitStats",
        level = "Intermedio",
        focus = "Cuádriceps · Femorales · Glúteos",
        exercises = listOf(
            warmup("Bici estática suave", 300),
            strength("Sentadilla trasera", sets = 4, reps = 8, rest = 150),
            strength("Peso muerto rumano", sets = 4, reps = 8, rest = 120),
            strength("Prensa de piernas", sets = 3, reps = 12, rest = 90),
            strength("Curl femoral tumbado", sets = 3, reps = 12, rest = 75),
            strength("Extensión de cuádriceps", sets = 3, reps = 15, rest = 60),
            strength("Elevación de gemelos de pie", sets = 4, reps = 15, rest = 45)
        )
    )

    private fun fullBodyPrincipiante() = CommunityRoutine(
        name = "Full Body Principiante",
        description = "Rutina de cuerpo completo 3 días por semana. Ideal si estás empezando.",
        author = "Comunidad FitStats",
        level = "Principiante",
        focus = "Cuerpo completo",
        exercises = listOf(
            warmup("Movilidad general", 180),
            strength("Sentadilla con barra", sets = 3, reps = 10, rest = 90),
            strength("Press de banca", sets = 3, reps = 10, rest = 90),
            strength("Remo con mancuerna", sets = 3, reps = 10, rest = 75),
            strength("Press militar con mancuernas", sets = 3, reps = 10, rest = 75),
            strength("Peso muerto convencional", sets = 3, reps = 8, rest = 120),
            strength("Plancha abdominal", sets = 3, reps = 30, rest = 45)
        )
    )

    private fun upperBody() = CommunityRoutine(
        name = "Upper Body",
        description = "Día de tren superior combinando empuje y tracción. Parte de la Upper/Lower.",
        author = "Comunidad FitStats",
        level = "Intermedio",
        focus = "Pecho · Espalda · Hombros · Brazos",
        exercises = listOf(
            warmup("Movilidad de hombros y escapulas", 120),
            strength("Press de banca inclinado", sets = 4, reps = 8, rest = 120),
            strength("Remo con barra", sets = 4, reps = 8, rest = 120),
            strength("Press militar", sets = 3, reps = 10, rest = 90),
            strength("Jalón al pecho", sets = 3, reps = 10, rest = 90),
            strength("Curl alterno con mancuernas", sets = 3, reps = 12, rest = 60),
            strength("Extensión de tríceps sobre la cabeza", sets = 3, reps = 12, rest = 60)
        )
    )

    private fun lowerBody() = CommunityRoutine(
        name = "Lower Body",
        description = "Día de tren inferior. Acompaña al Upper Body en la rutina Upper/Lower.",
        author = "Comunidad FitStats",
        level = "Intermedio",
        focus = "Cuádriceps · Femorales · Glúteos · Gemelos",
        exercises = listOf(
            warmup("Bici suave y movilidad de cadera", 240),
            strength("Sentadilla con barra", sets = 4, reps = 8, rest = 150),
            strength("Peso muerto rumano", sets = 4, reps = 8, rest = 120),
            strength("Zancadas con mancuernas", sets = 3, reps = 10, rest = 90),
            strength("Hip thrust", sets = 3, reps = 12, rest = 90),
            strength("Curl femoral sentado", sets = 3, reps = 12, rest = 60),
            strength("Elevación de gemelos sentado", sets = 4, reps = 15, rest = 45)
        )
    )
}
