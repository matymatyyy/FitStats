package com.app.gimnasio.data.model

data class BodyMeasurements(
    val id: Long = 1, // singleton, always id=1
    val cintura: Double? = null,
    val abdomen: Double? = null,
    val gluteos: Double? = null,
    val pecho: Double? = null,
    val hombros: Double? = null,
    val antebrazo: Double? = null,
    val biceps: Double? = null,
    val muslos: Double? = null,
    val pantorrillas: Double? = null,
    val cuello: Double? = null,
    val updatedAt: Long? = null
)

data class MeasurementsHistoryEntry(
    val id: Long = 0,
    val date: Long,
    val cintura: Double? = null,
    val abdomen: Double? = null,
    val gluteos: Double? = null,
    val pecho: Double? = null,
    val hombros: Double? = null,
    val antebrazo: Double? = null,
    val biceps: Double? = null,
    val muslos: Double? = null,
    val pantorrillas: Double? = null,
    val cuello: Double? = null
)
