package com.app.gimnasio.data.model

data class PersonalRecords(
    val id: Long = 1,
    val sentadillas: Double? = null,
    val pesoMuerto: Double? = null,
    val pressBanca: Double? = null,
    val pressMilitar: Double? = null,
    val dominadas: Double? = null,
    val updatedAt: Long? = null
)

data class PRHistoryEntry(
    val id: Long = 0,
    val date: Long,
    val sentadillas: Double? = null,
    val pesoMuerto: Double? = null,
    val pressBanca: Double? = null,
    val pressMilitar: Double? = null,
    val dominadas: Double? = null
)
