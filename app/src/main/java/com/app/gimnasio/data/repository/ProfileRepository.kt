package com.app.gimnasio.data.repository

import com.app.gimnasio.data.local.GimnasioDatabase
import com.app.gimnasio.data.model.BodyMeasurements
import com.app.gimnasio.data.model.CustomMeasurement
import com.app.gimnasio.data.model.CustomMeasurementHistoryPoint
import com.app.gimnasio.data.model.CustomPR
import com.app.gimnasio.data.model.CustomPRHistoryPoint
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

    fun getCustomMeasurements(): List<CustomMeasurement> = db.getCustomMeasurements()
    fun upsertCustomMeasurement(m: CustomMeasurement) = db.upsertCustomMeasurement(m)
    fun deleteCustomMeasurement(name: String) = db.deleteCustomMeasurement(name)
    fun getCustomMeasurementHistory(name: String): List<CustomMeasurementHistoryPoint> =
        db.getCustomMeasurementHistory(name)

    fun getCustomPRs(): List<CustomPR> = db.getCustomPRs()
    fun upsertCustomPR(pr: CustomPR) = db.upsertCustomPR(pr)
    fun deleteCustomPR(exerciseName: String) = db.deleteCustomPR(exerciseName)
    fun getCustomPRHistory(exerciseName: String): List<CustomPRHistoryPoint> =
        db.getCustomPRHistory(exerciseName)

    fun getMeasurementHistoryByColumn(column: String): List<Pair<Long, Double>> =
        db.getMeasurementHistoryByColumn(column)

    fun getPRHistoryByColumn(column: String): List<Pair<Long, Double>> =
        db.getPRHistoryByColumn(column)
}
