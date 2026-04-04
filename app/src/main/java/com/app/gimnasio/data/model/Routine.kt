package com.app.gimnasio.data.model

data class Routine(
    val id: Long = 0,
    val name: String,
    val description: String,
    val exercises: List<Exercise> = emptyList(),
    val createdAt: Long = 0,
    val imagePath: String? = null
)
