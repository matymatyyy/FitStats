package com.app.gimnasio.data.local

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.app.gimnasio.data.model.Exercise
import com.app.gimnasio.data.model.ExerciseInfo
import com.app.gimnasio.data.model.ExercisePhase
import com.app.gimnasio.data.model.MuscleGroup
import com.app.gimnasio.data.model.Routine
import com.app.gimnasio.data.model.BodyMeasurements
import com.app.gimnasio.data.model.PersonalRecords
import com.app.gimnasio.data.model.UserProfile
import com.app.gimnasio.data.model.WorkoutLog
import com.app.gimnasio.data.model.WorkoutPlanDay

class GimnasioDatabase(context: Context) : SQLiteOpenHelper(
    context, DATABASE_NAME, null, DATABASE_VERSION
) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE routines (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                description TEXT NOT NULL DEFAULT '',
                created_at INTEGER NOT NULL DEFAULT 0,
                image_path TEXT
            )
        """)
        db.execSQL("""
            CREATE TABLE routine_exercises (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                routine_id INTEGER NOT NULL,
                name TEXT NOT NULL,
                order_index INTEGER NOT NULL,
                phase TEXT NOT NULL,
                duration_seconds INTEGER,
                reps INTEGER,
                sets INTEGER,
                strength_reps INTEGER,
                rest_seconds INTEGER,
                weight_kg REAL,
                FOREIGN KEY (routine_id) REFERENCES routines(id) ON DELETE CASCADE
            )
        """)
        db.execSQL("CREATE INDEX idx_exercises_routine ON routine_exercises(routine_id)")

        db.execSQL("""
            CREATE TABLE exercise_gallery (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                description TEXT NOT NULL DEFAULT '',
                muscle_group TEXT NOT NULL,
                image_path TEXT
            )
        """)
        db.execSQL("CREATE INDEX idx_gallery_muscle ON exercise_gallery(muscle_group)")

        db.execSQL("""
            CREATE TABLE workout_logs (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                routine_id INTEGER,
                routine_name TEXT NOT NULL,
                date INTEGER NOT NULL,
                duration_seconds INTEGER NOT NULL DEFAULT 0,
                exercises_summary TEXT NOT NULL DEFAULT '',
                total_sets INTEGER NOT NULL DEFAULT 0,
                created_at INTEGER NOT NULL DEFAULT 0
            )
        """)
        db.execSQL("CREATE INDEX idx_workout_date ON workout_logs(date)")

        db.execSQL("""
            CREATE TABLE user_profile (
                id INTEGER PRIMARY KEY,
                name TEXT NOT NULL DEFAULT '',
                age INTEGER,
                gender TEXT,
                photo_path TEXT
            )
        """)

        db.execSQL("""
            CREATE TABLE body_measurements (
                id INTEGER PRIMARY KEY,
                cintura REAL,
                abdomen REAL,
                gluteos REAL,
                pecho REAL,
                hombros REAL,
                antebrazo REAL,
                biceps REAL,
                muslos REAL,
                pantorrillas REAL,
                cuello REAL
            )
        """)

        db.execSQL("""
            CREATE TABLE workout_plan_days (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                day_of_week INTEGER NOT NULL,
                routine_id INTEGER NOT NULL,
                FOREIGN KEY (routine_id) REFERENCES routines(id) ON DELETE CASCADE
            )
        """)
        db.execSQL("CREATE UNIQUE INDEX idx_plan_day ON workout_plan_days(day_of_week)")

        db.execSQL("""
            CREATE TABLE personal_records (
                id INTEGER PRIMARY KEY,
                sentadillas REAL,
                peso_muerto REAL,
                press_banca REAL,
                press_militar REAL,
                dominadas REAL
            )
        """)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS exercise_gallery (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    description TEXT NOT NULL DEFAULT '',
                    muscle_group TEXT NOT NULL,
                    image_path TEXT
                )
            """)
            db.execSQL("CREATE INDEX IF NOT EXISTS idx_gallery_muscle ON exercise_gallery(muscle_group)")
        }
        if (oldVersion < 3) {
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS workout_logs (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    routine_id INTEGER,
                    routine_name TEXT NOT NULL,
                    date INTEGER NOT NULL,
                    duration_seconds INTEGER NOT NULL DEFAULT 0,
                    exercises_summary TEXT NOT NULL DEFAULT '',
                    total_sets INTEGER NOT NULL DEFAULT 0,
                    created_at INTEGER NOT NULL DEFAULT 0
                )
            """)
            db.execSQL("CREATE INDEX IF NOT EXISTS idx_workout_date ON workout_logs(date)")
        }
        if (oldVersion < 4) {
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS user_profile (
                    id INTEGER PRIMARY KEY,
                    name TEXT NOT NULL DEFAULT '',
                    age INTEGER,
                    gender TEXT,
                    photo_path TEXT
                )
            """)
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS body_measurements (
                    id INTEGER PRIMARY KEY,
                    cintura REAL,
                    abdomen REAL,
                    gluteos REAL,
                    pecho REAL,
                    hombros REAL,
                    antebrazo REAL,
                    biceps REAL,
                    muslos REAL,
                    pantorrillas REAL,
                    cuello REAL
                )
            """)
        }
        if (oldVersion < 5) {
            try {
                db.execSQL("ALTER TABLE workout_logs ADD COLUMN total_sets INTEGER NOT NULL DEFAULT 0")
            } catch (_: Exception) { }
        }
        if (oldVersion < 6) {
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS workout_plan_days (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    day_of_week INTEGER NOT NULL,
                    routine_id INTEGER NOT NULL,
                    FOREIGN KEY (routine_id) REFERENCES routines(id) ON DELETE CASCADE
                )
            """)
            db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS idx_plan_day ON workout_plan_days(day_of_week)")
        }
        if (oldVersion < 7) {
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS personal_records (
                    id INTEGER PRIMARY KEY,
                    sentadillas REAL,
                    peso_muerto REAL,
                    press_banca REAL,
                    press_militar REAL,
                    dominadas REAL
                )
            """)
        }
        if (oldVersion < 8) {
            try {
                db.execSQL("ALTER TABLE routines ADD COLUMN image_path TEXT")
            } catch (_: Exception) { }
        }
    }

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    // --- Operaciones ---

    fun insertRoutine(name: String, description: String, exercises: List<Exercise>, imagePath: String? = null): Long {
        val db = writableDatabase
        val routineId = db.insert("routines", null, ContentValues().apply {
            put("name", name)
            put("description", description)
            put("created_at", System.currentTimeMillis())
            put("image_path", imagePath)
        })

        exercises.forEachIndexed { index, exercise ->
            db.insert("routine_exercises", null, ContentValues().apply {
                put("routine_id", routineId)
                put("name", exercise.name)
                put("order_index", index)
                put("phase", exercise.phase.name)
                put("duration_seconds", exercise.durationSeconds)
                put("reps", exercise.reps)
                put("sets", exercise.sets)
                put("strength_reps", exercise.strengthReps)
                put("rest_seconds", exercise.restSeconds)
                put("weight_kg", exercise.weightKg)
            })
        }

        return routineId
    }

    fun getAllRoutines(): List<Routine> {
        val db = readableDatabase
        val routines = mutableListOf<Routine>()

        val cursor = db.rawQuery(
            "SELECT id, name, description, created_at, image_path FROM routines ORDER BY created_at DESC",
            null
        )
        cursor.use {
            while (it.moveToNext()) {
                val id = it.getLong(0)
                routines.add(
                    Routine(
                        id = id,
                        name = it.getString(1),
                        description = it.getString(2),
                        createdAt = it.getLong(3),
                        exercises = getExercisesForRoutine(id),
                        imagePath = if (it.isNull(4)) null else it.getString(4)
                    )
                )
            }
        }
        return routines
    }

    fun getRoutineById(id: Long): Routine? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT id, name, description, created_at, image_path FROM routines WHERE id = ?",
            arrayOf(id.toString())
        )
        cursor.use {
            if (it.moveToFirst()) {
                return Routine(
                    id = it.getLong(0),
                    name = it.getString(1),
                    description = it.getString(2),
                    createdAt = it.getLong(3),
                    exercises = getExercisesForRoutine(id),
                    imagePath = if (it.isNull(4)) null else it.getString(4)
                )
            }
        }
        return null
    }

    fun deleteRoutine(routineId: Long) {
        val db = writableDatabase
        db.delete("routines", "id = ?", arrayOf(routineId.toString()))
    }

    fun updateRoutine(routineId: Long, name: String, description: String, exercises: List<Exercise>, imagePath: String? = null) {
        val db = writableDatabase
        db.beginTransaction()
        try {
            db.update("routines", ContentValues().apply {
                put("name", name)
                put("description", description)
                put("image_path", imagePath)
            }, "id = ?", arrayOf(routineId.toString()))

            db.delete("routine_exercises", "routine_id = ?", arrayOf(routineId.toString()))

            exercises.forEachIndexed { index, exercise ->
                db.insert("routine_exercises", null, ContentValues().apply {
                    put("routine_id", routineId)
                    put("name", exercise.name)
                    put("order_index", index)
                    put("phase", exercise.phase.name)
                    put("duration_seconds", exercise.durationSeconds)
                    put("reps", exercise.reps)
                    put("sets", exercise.sets)
                    put("strength_reps", exercise.strengthReps)
                    put("rest_seconds", exercise.restSeconds)
                    put("weight_kg", exercise.weightKg)
                })
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    private fun getExercisesForRoutine(routineId: Long): List<Exercise> {
        val db = readableDatabase
        val exercises = mutableListOf<Exercise>()
        val cursor = db.rawQuery(
            """SELECT id, name, order_index, phase, duration_seconds, reps,
                      sets, strength_reps, rest_seconds, weight_kg
               FROM routine_exercises
               WHERE routine_id = ?
               ORDER BY order_index""",
            arrayOf(routineId.toString())
        )
        cursor.use {
            while (it.moveToNext()) {
                exercises.add(
                    Exercise(
                        id = it.getLong(0),
                        name = it.getString(1),
                        phase = ExercisePhase.valueOf(it.getString(3)),
                        durationSeconds = if (it.isNull(4)) null else it.getInt(4),
                        reps = if (it.isNull(5)) null else it.getInt(5),
                        sets = if (it.isNull(6)) null else it.getInt(6),
                        strengthReps = if (it.isNull(7)) null else it.getInt(7),
                        restSeconds = if (it.isNull(8)) null else it.getInt(8),
                        weightKg = if (it.isNull(9)) null else it.getDouble(9)
                    )
                )
            }
        }
        return exercises
    }

    // --- Galería de ejercicios ---

    fun insertExerciseInfo(exercise: ExerciseInfo): Long {
        val db = writableDatabase
        return db.insert("exercise_gallery", null, ContentValues().apply {
            put("name", exercise.name)
            put("description", exercise.description)
            put("muscle_group", exercise.muscleGroup.name)
            put("image_path", exercise.imagePath)
        })
    }

    fun getExercisesByMuscle(muscleGroup: MuscleGroup): List<ExerciseInfo> {
        val db = readableDatabase
        val list = mutableListOf<ExerciseInfo>()
        val cursor = db.rawQuery(
            "SELECT id, name, description, muscle_group, image_path FROM exercise_gallery WHERE muscle_group = ? ORDER BY name",
            arrayOf(muscleGroup.name)
        )
        cursor.use {
            while (it.moveToNext()) {
                list.add(
                    ExerciseInfo(
                        id = it.getLong(0),
                        name = it.getString(1),
                        description = it.getString(2),
                        muscleGroup = MuscleGroup.valueOf(it.getString(3)),
                        imagePath = if (it.isNull(4)) null else it.getString(4)
                    )
                )
            }
        }
        return list
    }

    fun getExerciseInfoById(id: Long): ExerciseInfo? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT id, name, description, muscle_group, image_path FROM exercise_gallery WHERE id = ?",
            arrayOf(id.toString())
        )
        cursor.use {
            if (it.moveToFirst()) {
                return ExerciseInfo(
                    id = it.getLong(0),
                    name = it.getString(1),
                    description = it.getString(2),
                    muscleGroup = MuscleGroup.valueOf(it.getString(3)),
                    imagePath = if (it.isNull(4)) null else it.getString(4)
                )
            }
        }
        return null
    }

    fun getExerciseCountsByMuscle(): Map<String, Int> {
        val db = readableDatabase
        val map = mutableMapOf<String, Int>()
        val cursor = db.rawQuery(
            "SELECT muscle_group, COUNT(*) FROM exercise_gallery GROUP BY muscle_group",
            null
        )
        cursor.use {
            while (it.moveToNext()) {
                map[it.getString(0)] = it.getInt(1)
            }
        }
        return map
    }

    fun updateExerciseInfo(exercise: ExerciseInfo) {
        val db = writableDatabase
        db.update("exercise_gallery", ContentValues().apply {
            put("name", exercise.name)
            put("description", exercise.description)
            put("image_path", exercise.imagePath)
        }, "id = ?", arrayOf(exercise.id.toString()))
    }

    fun deleteExerciseInfo(id: Long) {
        val db = writableDatabase
        db.delete("exercise_gallery", "id = ?", arrayOf(id.toString()))
    }

    fun getAllExerciseInfos(): List<ExerciseInfo> {
        val db = readableDatabase
        val list = mutableListOf<ExerciseInfo>()
        val cursor = db.rawQuery(
            "SELECT id, name, description, muscle_group, image_path FROM exercise_gallery ORDER BY name",
            null
        )
        cursor.use {
            while (it.moveToNext()) {
                list.add(
                    ExerciseInfo(
                        id = it.getLong(0),
                        name = it.getString(1),
                        description = it.getString(2),
                        muscleGroup = MuscleGroup.valueOf(it.getString(3)),
                        imagePath = if (it.isNull(4)) null else it.getString(4)
                    )
                )
            }
        }
        return list
    }

    // --- Workout logs ---

    fun insertWorkoutLog(log: WorkoutLog): Long {
        val db = writableDatabase
        return db.insert("workout_logs", null, ContentValues().apply {
            put("routine_id", log.routineId)
            put("routine_name", log.routineName)
            put("date", log.date)
            put("duration_seconds", log.durationSeconds)
            put("exercises_summary", log.exercisesSummary)
            put("total_sets", log.totalSets)
            put("created_at", System.currentTimeMillis())
        })
    }

    fun getWorkoutLogsByDateRange(startDate: Long, endDate: Long): List<WorkoutLog> {
        val db = readableDatabase
        val list = mutableListOf<WorkoutLog>()
        val cursor = db.rawQuery(
            "SELECT id, routine_id, routine_name, date, duration_seconds, exercises_summary, total_sets, created_at FROM workout_logs WHERE date >= ? AND date <= ? ORDER BY date DESC",
            arrayOf(startDate.toString(), endDate.toString())
        )
        cursor.use {
            while (it.moveToNext()) {
                list.add(
                    WorkoutLog(
                        id = it.getLong(0),
                        routineId = if (it.isNull(1)) null else it.getLong(1),
                        routineName = it.getString(2),
                        date = it.getLong(3),
                        durationSeconds = it.getInt(4),
                        exercisesSummary = it.getString(5),
                        totalSets = it.getInt(6),
                        createdAt = it.getLong(7)
                    )
                )
            }
        }
        return list
    }

    fun getWorkoutLogsForDate(date: Long): List<WorkoutLog> {
        return getWorkoutLogsByDateRange(date, date)
    }

    fun deleteWorkoutLog(id: Long) {
        val db = writableDatabase
        db.delete("workout_logs", "id = ?", arrayOf(id.toString()))
    }

    fun getTotalWorkoutCount(): Int {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM workout_logs", null)
        cursor.use {
            if (it.moveToFirst()) return it.getInt(0)
        }
        return 0
    }

    // --- User Profile ---

    fun getUserProfile(): UserProfile? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT id, name, age, gender, photo_path FROM user_profile WHERE id = 1",
            null
        )
        cursor.use {
            if (it.moveToFirst()) {
                return UserProfile(
                    id = it.getLong(0),
                    name = it.getString(1),
                    age = if (it.isNull(2)) null else it.getInt(2),
                    gender = if (it.isNull(3)) null else it.getString(3),
                    photoPath = if (it.isNull(4)) null else it.getString(4)
                )
            }
        }
        return null
    }

    fun saveUserProfile(profile: UserProfile) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("id", 1)
            put("name", profile.name)
            put("age", profile.age)
            put("gender", profile.gender)
            put("photo_path", profile.photoPath)
        }
        val updated = db.update("user_profile", values, "id = 1", null)
        if (updated == 0) {
            db.insert("user_profile", null, values)
        }
    }

    // --- Body Measurements ---

    fun getBodyMeasurements(): BodyMeasurements? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT id, cintura, abdomen, gluteos, pecho, hombros, antebrazo, biceps, muslos, pantorrillas, cuello FROM body_measurements WHERE id = 1",
            null
        )
        cursor.use {
            if (it.moveToFirst()) {
                return BodyMeasurements(
                    id = it.getLong(0),
                    cintura = if (it.isNull(1)) null else it.getDouble(1),
                    abdomen = if (it.isNull(2)) null else it.getDouble(2),
                    gluteos = if (it.isNull(3)) null else it.getDouble(3),
                    pecho = if (it.isNull(4)) null else it.getDouble(4),
                    hombros = if (it.isNull(5)) null else it.getDouble(5),
                    antebrazo = if (it.isNull(6)) null else it.getDouble(6),
                    biceps = if (it.isNull(7)) null else it.getDouble(7),
                    muslos = if (it.isNull(8)) null else it.getDouble(8),
                    pantorrillas = if (it.isNull(9)) null else it.getDouble(9),
                    cuello = if (it.isNull(10)) null else it.getDouble(10)
                )
            }
        }
        return null
    }

    fun saveBodyMeasurements(m: BodyMeasurements) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("id", 1)
            put("cintura", m.cintura)
            put("abdomen", m.abdomen)
            put("gluteos", m.gluteos)
            put("pecho", m.pecho)
            put("hombros", m.hombros)
            put("antebrazo", m.antebrazo)
            put("biceps", m.biceps)
            put("muslos", m.muslos)
            put("pantorrillas", m.pantorrillas)
            put("cuello", m.cuello)
        }
        val updated = db.update("body_measurements", values, "id = 1", null)
        if (updated == 0) {
            db.insert("body_measurements", null, values)
        }
    }

    // --- Personal Records ---

    fun getPersonalRecords(): PersonalRecords? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT id, sentadillas, peso_muerto, press_banca, press_militar, dominadas FROM personal_records WHERE id = 1",
            null
        )
        cursor.use {
            if (it.moveToFirst()) {
                return PersonalRecords(
                    id = it.getLong(0),
                    sentadillas = if (it.isNull(1)) null else it.getDouble(1),
                    pesoMuerto = if (it.isNull(2)) null else it.getDouble(2),
                    pressBanca = if (it.isNull(3)) null else it.getDouble(3),
                    pressMilitar = if (it.isNull(4)) null else it.getDouble(4),
                    dominadas = if (it.isNull(5)) null else it.getDouble(5)
                )
            }
        }
        return null
    }

    fun savePersonalRecords(pr: PersonalRecords) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("id", 1)
            put("sentadillas", pr.sentadillas)
            put("peso_muerto", pr.pesoMuerto)
            put("press_banca", pr.pressBanca)
            put("press_militar", pr.pressMilitar)
            put("dominadas", pr.dominadas)
        }
        val updated = db.update("personal_records", values, "id = 1", null)
        if (updated == 0) {
            db.insert("personal_records", null, values)
        }
    }

    // --- Workout Plan ---

    fun saveWorkoutPlan(days: List<WorkoutPlanDay>) {
        val db = writableDatabase
        db.beginTransaction()
        try {
            db.delete("workout_plan_days", null, null)
            days.forEach { day ->
                db.insert("workout_plan_days", null, ContentValues().apply {
                    put("day_of_week", day.dayOfWeek)
                    put("routine_id", day.routineId)
                })
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    fun getWorkoutPlan(): List<WorkoutPlanDay> {
        val db = readableDatabase
        val list = mutableListOf<WorkoutPlanDay>()
        val cursor = db.rawQuery(
            """SELECT wpd.id, wpd.day_of_week, wpd.routine_id, r.name
               FROM workout_plan_days wpd
               INNER JOIN routines r ON r.id = wpd.routine_id
               ORDER BY wpd.day_of_week""",
            null
        )
        cursor.use {
            while (it.moveToNext()) {
                list.add(
                    WorkoutPlanDay(
                        id = it.getLong(0),
                        dayOfWeek = it.getInt(1),
                        routineId = it.getLong(2),
                        routineName = it.getString(3)
                    )
                )
            }
        }
        return list
    }

    fun clearWorkoutPlan() {
        val db = writableDatabase
        db.delete("workout_plan_days", null, null)
    }

    companion object {
        const val DATABASE_NAME = "gimnasio.db"
        const val DATABASE_VERSION = 8

        @Volatile
        private var INSTANCE: GimnasioDatabase? = null

        fun getInstance(context: Context): GimnasioDatabase {
            return INSTANCE ?: synchronized(this) {
                GimnasioDatabase(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}
