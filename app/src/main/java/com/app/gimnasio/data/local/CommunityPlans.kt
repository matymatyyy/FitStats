package com.app.gimnasio.data.local

data class CommunityPlanDay(
    val dayOfWeek: Int, // 1=Lunes .. 7=Domingo
    val routine: CommunityRoutine
)

data class CommunityPlan(
    val name: String,
    val description: String,
    val author: String,
    val level: String,
    val daysPerWeek: Int,
    val days: List<CommunityPlanDay>
)

object CommunityPlans {

    fun getAll(): List<CommunityPlan> = listOf(
        pplSixDays(),
        upperLowerFourDays(),
        fullBodyThreeDays()
    )

    private fun pplSixDays(): CommunityPlan {
        val push = CommunityRoutines.pushDayRoutine()
        val pull = CommunityRoutines.pullDayRoutine()
        val legs = CommunityRoutines.legDayRoutine()
        return CommunityPlan(
            name = "Push Pull Legs (6 días)",
            description = "Clásica PPL de 6 días. Dos rondas de empuje, tracción y pierna con domingo libre.",
            author = "Comunidad FitStats",
            level = "Intermedio/Avanzado",
            daysPerWeek = 6,
            days = listOf(
                CommunityPlanDay(1, push),
                CommunityPlanDay(2, pull),
                CommunityPlanDay(3, legs),
                CommunityPlanDay(4, push),
                CommunityPlanDay(5, pull),
                CommunityPlanDay(6, legs)
            )
        )
    }

    private fun upperLowerFourDays(): CommunityPlan {
        val upper = CommunityRoutines.upperBodyRoutine()
        val lower = CommunityRoutines.lowerBodyRoutine()
        return CommunityPlan(
            name = "Upper / Lower (4 días)",
            description = "Tren superior e inferior alternados, 4 días por semana. Volumen controlado y buena recuperación.",
            author = "Comunidad FitStats",
            level = "Intermedio",
            daysPerWeek = 4,
            days = listOf(
                CommunityPlanDay(1, upper),
                CommunityPlanDay(2, lower),
                CommunityPlanDay(4, upper),
                CommunityPlanDay(5, lower)
            )
        )
    }

    private fun fullBodyThreeDays(): CommunityPlan {
        val fb = CommunityRoutines.fullBodyRoutine()
        return CommunityPlan(
            name = "Full Body (3 días)",
            description = "Cuerpo completo 3 veces por semana con un día de descanso entremedio. Ideal para principiantes.",
            author = "Comunidad FitStats",
            level = "Principiante",
            daysPerWeek = 3,
            days = listOf(
                CommunityPlanDay(1, fb),
                CommunityPlanDay(3, fb),
                CommunityPlanDay(5, fb)
            )
        )
    }

    fun dayLabel(dayOfWeek: Int): String = when (dayOfWeek) {
        1 -> "Lunes"
        2 -> "Martes"
        3 -> "Miércoles"
        4 -> "Jueves"
        5 -> "Viernes"
        6 -> "Sábado"
        7 -> "Domingo"
        else -> ""
    }
}
