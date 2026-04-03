package com.app.gimnasio.data.model

data class UserProfile(
    val id: Long = 1, // singleton, always id=1
    val name: String = "",
    val age: Int? = null,
    val gender: String? = null, // "Masculino", "Femenino", "Otro"
    val photoPath: String? = null
)
