package com.app.gimnasio.data.repository

import com.app.gimnasio.data.local.GimnasioDatabase
import com.app.gimnasio.data.model.BodyMeasurements
import com.app.gimnasio.data.model.PRHistoryEntry
import com.app.gimnasio.data.model.PersonalRecords
import com.app.gimnasio.data.model.UserProfile

class ProfileRepository(private val db: GimnasioDatabase) {

    fun getUserProfile(): UserProfile? = db.getUserProfile()

    fun saveUserProfile(profile: UserProfile) = db.saveUserProfile(profile)

    fun getBodyMeasurements(): BodyMeasurements? = db.getBodyMeasurements()

    fun saveBodyMeasurements(measurements: BodyMeasurements) = db.saveBodyMeasurements(measurements)

    fun getPersonalRecords(): PersonalRecords? = db.getPersonalRecords()

    fun savePersonalRecords(pr: PersonalRecords) = db.savePersonalRecords(pr)

    fun getTotalWorkoutCount(): Int = db.getTotalWorkoutCount()

    fun getPRHistory(): List<PRHistoryEntry> = db.getPRHistory()
}
