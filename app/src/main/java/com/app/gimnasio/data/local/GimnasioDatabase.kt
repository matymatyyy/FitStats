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
import com.app.gimnasio.data.model.CustomMeasurement
import com.app.gimnasio.data.model.CustomMeasurementHistoryPoint
import com.app.gimnasio.data.model.CustomPR
import com.app.gimnasio.data.model.CustomPRHistoryPoint
import com.app.gimnasio.data.model.MeasurementsHistoryEntry
import com.app.gimnasio.data.model.PRHistoryEntry
import com.app.gimnasio.data.model.PersonalRecords
import com.app.gimnasio.data.model.UserProfile
import com.app.gimnasio.data.model.WorkoutLog
import com.app.gimnasio.data.model.WorkoutPlanDay
import com.app.gimnasio.data.model.WorkoutSetLog

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
                is_circuit INTEGER NOT NULL DEFAULT 0,
                circuit_exercises TEXT,
                circuit_rounds INTEGER,
                weight_per_set TEXT,
                reps_per_set TEXT,
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
            CREATE TABLE workout_set_logs (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                workout_log_id INTEGER NOT NULL,
                exercise_name TEXT NOT NULL,
                set_number INTEGER NOT NULL,
                reps INTEGER,
                weight_kg REAL,
                duration_seconds INTEGER,
                phase TEXT NOT NULL,
                is_circuit INTEGER NOT NULL DEFAULT 0,
                date INTEGER NOT NULL,
                FOREIGN KEY (workout_log_id) REFERENCES workout_logs(id) ON DELETE CASCADE
            )
        """)
        db.execSQL("CREATE INDEX idx_set_logs_workout ON workout_set_logs(workout_log_id)")
        db.execSQL("CREATE INDEX idx_set_logs_exercise ON workout_set_logs(exercise_name, date)")

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
                cuello REAL,
                updated_at INTEGER
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
                dominadas REAL,
                updated_at INTEGER
            )
        """)

        db.execSQL("""
            CREATE TABLE body_measurements_history (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                date INTEGER NOT NULL,
                cintura REAL, abdomen REAL, gluteos REAL, pecho REAL, hombros REAL,
                antebrazo REAL, biceps REAL, muslos REAL, pantorrillas REAL, cuello REAL
            )
        """)

        db.execSQL("""
            CREATE TABLE personal_records_history (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                date INTEGER NOT NULL,
                sentadillas REAL, peso_muerto REAL, press_banca REAL,
                press_militar REAL, dominadas REAL
            )
        """)

        db.execSQL("""
            CREATE TABLE custom_measurements (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL UNIQUE,
                value_cm REAL NOT NULL,
                updated_at INTEGER NOT NULL
            )
        """)
        db.execSQL("""
            CREATE TABLE custom_measurements_history (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                value_cm REAL NOT NULL,
                date INTEGER NOT NULL
            )
        """)
        db.execSQL("""
            CREATE TABLE custom_prs (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                exercise_name TEXT NOT NULL UNIQUE,
                weight_kg REAL NOT NULL,
                reps INTEGER NOT NULL,
                updated_at INTEGER NOT NULL
            )
        """)
        db.execSQL("""
            CREATE TABLE custom_prs_history (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                exercise_name TEXT NOT NULL,
                weight_kg REAL NOT NULL,
                reps INTEGER NOT NULL,
                date INTEGER NOT NULL
            )
        """)

        // Seed exercise gallery
        seedExerciseGallery(db)
    }

    private fun seedExerciseGallery(db: SQLiteDatabase) {
        // Basic seed without images (called from onCreate/onUpgrade).
        // Images are copied later by seedIfNeeded() from Application.
        val cursor = db.rawQuery("SELECT COUNT(*) FROM exercise_gallery", null)
        val count = cursor.use { if (it.moveToFirst()) it.getInt(0) else 0 }
        if (count > 0) return

        ExerciseSeedData.getAll().forEach { ex ->
            db.insert("exercise_gallery", null, ContentValues().apply {
                put("name", ex.name)
                put("description", ex.description)
                put("muscle_group", ex.muscleGroup.name)
            })
        }
    }

    fun seedIfNeeded(context: Context) {
        val db = writableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM exercise_gallery", null)
        val count = cursor.use { if (it.moveToFirst()) it.getInt(0) else 0 }
        if (count == 0) {
            seedExerciseGalleryWithContext(db, context)
            return
        }
        // If exercises exist but images not yet copied, copy them
        val noImgCursor = db.rawQuery(
            "SELECT COUNT(*) FROM exercise_gallery WHERE image_path IS NULL", null
        )
        val noImgCount = noImgCursor.use { if (it.moveToFirst()) it.getInt(0) else 0 }
        if (noImgCount > 0) {
            copyAssetImages(db, context)
        }
    }

    private fun copyAssetImages(db: SQLiteDatabase, context: Context) {
        val imgDir = java.io.File(context.filesDir, "exercise_images")
        if (!imgDir.exists()) imgDir.mkdirs()

        val seedMap = ExerciseSeedData.getAll()
            .filter { it.assetImage != null }
            .associateBy({ it.name }, { it.assetImage!! })

        seedMap.forEach { (name, assetName) ->
            try {
                val destFile = java.io.File(imgDir, "${assetName}.jpg")
                if (!destFile.exists()) {
                    context.assets.open("exercise_images/${assetName}.jpg").use { input ->
                        destFile.outputStream().use { output -> input.copyTo(output) }
                    }
                }
                db.update("exercise_gallery", ContentValues().apply {
                    put("image_path", destFile.absolutePath)
                }, "name = ? AND image_path IS NULL", arrayOf(name))
            } catch (_: Exception) { }
        }
    }

    private fun seedExerciseGalleryWithContext(db: SQLiteDatabase, context: Context) {
        val imgDir = java.io.File(context.filesDir, "exercise_images")
        if (!imgDir.exists()) imgDir.mkdirs()

        ExerciseSeedData.getAll().forEach { ex ->
            var imagePath: String? = null
            if (ex.assetImage != null) {
                try {
                    val assetFile = "exercise_images/${ex.assetImage}.jpg"
                    val destFile = java.io.File(imgDir, "${ex.assetImage}.jpg")
                    if (!destFile.exists()) {
                        context.assets.open(assetFile).use { input ->
                            destFile.outputStream().use { output -> input.copyTo(output) }
                        }
                    }
                    imagePath = destFile.absolutePath
                } catch (_: Exception) { }
            }

            db.insert("exercise_gallery", null, ContentValues().apply {
                put("name", ex.name)
                put("description", ex.description)
                put("muscle_group", ex.muscleGroup.name)
                put("image_path", imagePath)
            })
        }
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
        if (oldVersion < 9) {
            try { db.execSQL("ALTER TABLE routine_exercises ADD COLUMN is_circuit INTEGER NOT NULL DEFAULT 0") } catch (_: Exception) { }
            try { db.execSQL("ALTER TABLE routine_exercises ADD COLUMN circuit_exercises TEXT") } catch (_: Exception) { }
            try { db.execSQL("ALTER TABLE routine_exercises ADD COLUMN circuit_rounds INTEGER") } catch (_: Exception) { }
            try { db.execSQL("ALTER TABLE routine_exercises ADD COLUMN weight_per_set TEXT") } catch (_: Exception) { }
            seedExerciseGallery(db)
        }
        if (oldVersion < 10) {
            try { db.execSQL("ALTER TABLE routine_exercises ADD COLUMN reps_per_set TEXT") } catch (_: Exception) { }
        }
        if (oldVersion < 11) {
            try { db.execSQL("ALTER TABLE body_measurements ADD COLUMN updated_at INTEGER") } catch (_: Exception) { }
            try { db.execSQL("ALTER TABLE personal_records ADD COLUMN updated_at INTEGER") } catch (_: Exception) { }
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS body_measurements_history (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    date INTEGER NOT NULL,
                    cintura REAL, abdomen REAL, gluteos REAL, pecho REAL, hombros REAL,
                    antebrazo REAL, biceps REAL, muslos REAL, pantorrillas REAL, cuello REAL
                )
            """)
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS personal_records_history (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    date INTEGER NOT NULL,
                    sentadillas REAL, peso_muerto REAL, press_banca REAL,
                    press_militar REAL, dominadas REAL
                )
            """)
        }
        if (oldVersion < 12) {
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS workout_set_logs (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    workout_log_id INTEGER NOT NULL,
                    exercise_name TEXT NOT NULL,
                    set_number INTEGER NOT NULL,
                    reps INTEGER,
                    weight_kg REAL,
                    duration_seconds INTEGER,
                    phase TEXT NOT NULL,
                    is_circuit INTEGER NOT NULL DEFAULT 0,
                    date INTEGER NOT NULL,
                    FOREIGN KEY (workout_log_id) REFERENCES workout_logs(id) ON DELETE CASCADE
                )
            """)
            db.execSQL("CREATE INDEX IF NOT EXISTS idx_set_logs_workout ON workout_set_logs(workout_log_id)")
            db.execSQL("CREATE INDEX IF NOT EXISTS idx_set_logs_exercise ON workout_set_logs(exercise_name, date)")
        }
        if (oldVersion < 13) {
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS custom_measurements (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL UNIQUE,
                    value_cm REAL NOT NULL,
                    updated_at INTEGER NOT NULL
                )
            """)
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS custom_measurements_history (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    value_cm REAL NOT NULL,
                    date INTEGER NOT NULL
                )
            """)
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS custom_prs (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    exercise_name TEXT NOT NULL UNIQUE,
                    weight_kg REAL NOT NULL,
                    reps INTEGER NOT NULL,
                    updated_at INTEGER NOT NULL
                )
            """)
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS custom_prs_history (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    exercise_name TEXT NOT NULL,
                    weight_kg REAL NOT NULL,
                    reps INTEGER NOT NULL,
                    date INTEGER NOT NULL
                )
            """)
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
            db.insert("routine_exercises", null, exerciseToContentValues(routineId, index, exercise))
        }

        return routineId
    }

    private fun exerciseToContentValues(routineId: Long, index: Int, exercise: Exercise): ContentValues {
        return ContentValues().apply {
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
            put("is_circuit", if (exercise.isCircuit) 1 else 0)
            put("circuit_exercises", if (exercise.circuitExercises.isNotEmpty()) exercise.circuitExercises.joinToString("|") else null)
            put("circuit_rounds", exercise.circuitRounds)
            put("weight_per_set", exercise.weightPerSet?.joinToString(","))
            put("reps_per_set", exercise.repsPerSet?.joinToString(","))
        }
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
                db.insert("routine_exercises", null, exerciseToContentValues(routineId, index, exercise))
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
                      sets, strength_reps, rest_seconds, weight_kg,
                      is_circuit, circuit_exercises, circuit_rounds, weight_per_set,
                      reps_per_set
               FROM routine_exercises
               WHERE routine_id = ?
               ORDER BY order_index""",
            arrayOf(routineId.toString())
        )
        cursor.use {
            while (it.moveToNext()) {
                val circuitExStr = if (it.isNull(11)) null else it.getString(11)
                val weightPerSetStr = if (it.isNull(13)) null else it.getString(13)
                val repsPerSetStr = if (it.isNull(14)) null else it.getString(14)
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
                        weightKg = if (it.isNull(9)) null else it.getDouble(9),
                        isCircuit = it.getInt(10) == 1,
                        circuitExercises = circuitExStr?.split("|")?.filter { s -> s.isNotBlank() } ?: emptyList(),
                        circuitRounds = if (it.isNull(12)) null else it.getInt(12),
                        weightPerSet = weightPerSetStr?.split(",")?.mapNotNull { s -> s.toDoubleOrNull() },
                        repsPerSet = repsPerSetStr?.split(",")?.mapNotNull { s -> s.toIntOrNull() }
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
        db.delete("workout_set_logs", "workout_log_id = ?", arrayOf(id.toString()))
        db.delete("workout_logs", "id = ?", arrayOf(id.toString()))
    }

    fun insertWorkoutSetLogs(logs: List<WorkoutSetLog>, workoutLogId: Long) {
        val db = writableDatabase
        db.beginTransaction()
        try {
            logs.forEach { log ->
                db.insert("workout_set_logs", null, ContentValues().apply {
                    put("workout_log_id", workoutLogId)
                    put("exercise_name", log.exerciseName)
                    put("set_number", log.setNumber)
                    put("reps", log.reps)
                    put("weight_kg", log.weightKg)
                    put("duration_seconds", log.durationSeconds)
                    put("phase", log.phase)
                    put("is_circuit", if (log.isCircuit) 1 else 0)
                    put("date", log.date)
                })
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    fun getSetLogsByDateRange(startDate: Long, endDate: Long): List<WorkoutSetLog> {
        val db = readableDatabase
        val list = mutableListOf<WorkoutSetLog>()
        val cursor = db.rawQuery(
            """SELECT id, workout_log_id, exercise_name, set_number, reps, weight_kg,
                      duration_seconds, phase, is_circuit, date
               FROM workout_set_logs WHERE date >= ? AND date <= ? ORDER BY date ASC, id ASC""",
            arrayOf(startDate.toString(), endDate.toString())
        )
        cursor.use {
            while (it.moveToNext()) {
                list.add(WorkoutSetLog(
                    id = it.getLong(0),
                    workoutLogId = it.getLong(1),
                    exerciseName = it.getString(2),
                    setNumber = it.getInt(3),
                    reps = if (it.isNull(4)) null else it.getInt(4),
                    weightKg = if (it.isNull(5)) null else it.getDouble(5),
                    durationSeconds = if (it.isNull(6)) null else it.getInt(6),
                    phase = it.getString(7),
                    isCircuit = it.getInt(8) == 1,
                    date = it.getLong(9)
                ))
            }
        }
        return list
    }

    fun getSetLogsForWorkout(workoutLogId: Long): List<WorkoutSetLog> {
        val db = readableDatabase
        val list = mutableListOf<WorkoutSetLog>()
        val cursor = db.rawQuery(
            """SELECT id, workout_log_id, exercise_name, set_number, reps, weight_kg,
                      duration_seconds, phase, is_circuit, date
               FROM workout_set_logs WHERE workout_log_id = ? ORDER BY id ASC""",
            arrayOf(workoutLogId.toString())
        )
        cursor.use {
            while (it.moveToNext()) {
                list.add(WorkoutSetLog(
                    id = it.getLong(0),
                    workoutLogId = it.getLong(1),
                    exerciseName = it.getString(2),
                    setNumber = it.getInt(3),
                    reps = if (it.isNull(4)) null else it.getInt(4),
                    weightKg = if (it.isNull(5)) null else it.getDouble(5),
                    durationSeconds = if (it.isNull(6)) null else it.getInt(6),
                    phase = it.getString(7),
                    isCircuit = it.getInt(8) == 1,
                    date = it.getLong(9)
                ))
            }
        }
        return list
    }

    /** Get distinct exercise names that have set logs */
    fun getLoggedExerciseNames(): List<String> {
        val db = readableDatabase
        val list = mutableListOf<String>()
        val cursor = db.rawQuery(
            "SELECT DISTINCT exercise_name FROM workout_set_logs ORDER BY exercise_name",
            null
        )
        cursor.use {
            while (it.moveToNext()) {
                list.add(it.getString(0))
            }
        }
        return list
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
            "SELECT id, cintura, abdomen, gluteos, pecho, hombros, antebrazo, biceps, muslos, pantorrillas, cuello, updated_at FROM body_measurements WHERE id = 1",
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
                    cuello = if (it.isNull(10)) null else it.getDouble(10),
                    updatedAt = if (it.isNull(11)) null else it.getLong(11)
                )
            }
        }
        return null
    }

    fun saveBodyMeasurements(m: BodyMeasurements) {
        val db = writableDatabase
        val now = System.currentTimeMillis()
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
            put("updated_at", now)
        }
        val updated = db.update("body_measurements", values, "id = 1", null)
        if (updated == 0) {
            db.insert("body_measurements", null, values)
        }
        // Save to history
        db.insert("body_measurements_history", null, ContentValues().apply {
            put("date", now)
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
        })
    }

    // --- Personal Records ---

    fun getPersonalRecords(): PersonalRecords? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT id, sentadillas, peso_muerto, press_banca, press_militar, dominadas, updated_at FROM personal_records WHERE id = 1",
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
                    dominadas = if (it.isNull(5)) null else it.getDouble(5),
                    updatedAt = if (it.isNull(6)) null else it.getLong(6)
                )
            }
        }
        return null
    }

    fun savePersonalRecords(pr: PersonalRecords) {
        val db = writableDatabase
        val now = System.currentTimeMillis()
        val values = ContentValues().apply {
            put("id", 1)
            put("sentadillas", pr.sentadillas)
            put("peso_muerto", pr.pesoMuerto)
            put("press_banca", pr.pressBanca)
            put("press_militar", pr.pressMilitar)
            put("dominadas", pr.dominadas)
            put("updated_at", now)
        }
        val updated = db.update("personal_records", values, "id = 1", null)
        if (updated == 0) {
            db.insert("personal_records", null, values)
        }
        // Save to history
        db.insert("personal_records_history", null, ContentValues().apply {
            put("date", now)
            put("sentadillas", pr.sentadillas)
            put("peso_muerto", pr.pesoMuerto)
            put("press_banca", pr.pressBanca)
            put("press_militar", pr.pressMilitar)
            put("dominadas", pr.dominadas)
        })
    }

    fun getPRHistory(): List<PRHistoryEntry> {
        val db = readableDatabase
        val list = mutableListOf<PRHistoryEntry>()
        val cursor = db.rawQuery(
            "SELECT id, date, sentadillas, peso_muerto, press_banca, press_militar, dominadas FROM personal_records_history ORDER BY date ASC",
            null
        )
        cursor.use {
            while (it.moveToNext()) {
                list.add(PRHistoryEntry(
                    id = it.getLong(0),
                    date = it.getLong(1),
                    sentadillas = if (it.isNull(2)) null else it.getDouble(2),
                    pesoMuerto = if (it.isNull(3)) null else it.getDouble(3),
                    pressBanca = if (it.isNull(4)) null else it.getDouble(4),
                    pressMilitar = if (it.isNull(5)) null else it.getDouble(5),
                    dominadas = if (it.isNull(6)) null else it.getDouble(6)
                ))
            }
        }
        return list
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

    // --- Fixed measurement/PR history by column ---

    private val allowedMeasurementCols = setOf(
        "cintura","abdomen","gluteos","pecho","hombros","antebrazo","biceps","muslos","pantorrillas","cuello"
    )
    private val allowedPRCols = setOf(
        "sentadillas","peso_muerto","press_banca","press_militar","dominadas"
    )

    fun getMeasurementHistoryByColumn(column: String): List<Pair<Long, Double>> {
        if (column !in allowedMeasurementCols) return emptyList()
        val db = readableDatabase
        val list = mutableListOf<Pair<Long, Double>>()
        val cursor = db.rawQuery(
            "SELECT date, $column FROM body_measurements_history WHERE $column IS NOT NULL ORDER BY date ASC",
            null
        )
        cursor.use {
            while (it.moveToNext()) list.add(it.getLong(0) to it.getDouble(1))
        }
        return list
    }

    fun getPRHistoryByColumn(column: String): List<Pair<Long, Double>> {
        if (column !in allowedPRCols) return emptyList()
        val db = readableDatabase
        val list = mutableListOf<Pair<Long, Double>>()
        val cursor = db.rawQuery(
            "SELECT date, $column FROM personal_records_history WHERE $column IS NOT NULL ORDER BY date ASC",
            null
        )
        cursor.use {
            while (it.moveToNext()) list.add(it.getLong(0) to it.getDouble(1))
        }
        return list
    }

    // --- Custom Measurements ---

    fun getCustomMeasurements(): List<CustomMeasurement> {
        val db = readableDatabase
        val list = mutableListOf<CustomMeasurement>()
        val cursor = db.rawQuery(
            "SELECT id, name, value_cm, updated_at FROM custom_measurements ORDER BY name",
            null
        )
        cursor.use {
            while (it.moveToNext()) {
                list.add(CustomMeasurement(
                    id = it.getLong(0),
                    name = it.getString(1),
                    valueCm = it.getDouble(2),
                    updatedAt = it.getLong(3)
                ))
            }
        }
        return list
    }

    fun upsertCustomMeasurement(m: CustomMeasurement) {
        val db = writableDatabase
        val now = System.currentTimeMillis()
        val values = ContentValues().apply {
            put("name", m.name)
            put("value_cm", m.valueCm)
            put("updated_at", now)
        }
        val updated = db.update("custom_measurements", values, "name = ?", arrayOf(m.name))
        if (updated == 0) {
            db.insert("custom_measurements", null, values)
        }
        db.insert("custom_measurements_history", null, ContentValues().apply {
            put("name", m.name)
            put("value_cm", m.valueCm)
            put("date", now)
        })
    }

    fun deleteCustomMeasurement(name: String) {
        val db = writableDatabase
        db.delete("custom_measurements", "name = ?", arrayOf(name))
        db.delete("custom_measurements_history", "name = ?", arrayOf(name))
    }

    fun getCustomMeasurementHistory(name: String): List<CustomMeasurementHistoryPoint> {
        val db = readableDatabase
        val list = mutableListOf<CustomMeasurementHistoryPoint>()
        val cursor = db.rawQuery(
            "SELECT date, value_cm FROM custom_measurements_history WHERE name = ? ORDER BY date ASC",
            arrayOf(name)
        )
        cursor.use {
            while (it.moveToNext()) {
                list.add(CustomMeasurementHistoryPoint(
                    date = it.getLong(0),
                    valueCm = it.getDouble(1)
                ))
            }
        }
        return list
    }

    // --- Custom PRs ---

    fun getCustomPRs(): List<CustomPR> {
        val db = readableDatabase
        val list = mutableListOf<CustomPR>()
        val cursor = db.rawQuery(
            "SELECT id, exercise_name, weight_kg, reps, updated_at FROM custom_prs ORDER BY exercise_name",
            null
        )
        cursor.use {
            while (it.moveToNext()) {
                list.add(CustomPR(
                    id = it.getLong(0),
                    exerciseName = it.getString(1),
                    weightKg = it.getDouble(2),
                    reps = it.getInt(3),
                    updatedAt = it.getLong(4)
                ))
            }
        }
        return list
    }

    fun upsertCustomPR(pr: CustomPR) {
        val db = writableDatabase
        val now = System.currentTimeMillis()
        val values = ContentValues().apply {
            put("exercise_name", pr.exerciseName)
            put("weight_kg", pr.weightKg)
            put("reps", pr.reps)
            put("updated_at", now)
        }
        val updated = db.update("custom_prs", values, "exercise_name = ?", arrayOf(pr.exerciseName))
        if (updated == 0) {
            db.insert("custom_prs", null, values)
        }
        db.insert("custom_prs_history", null, ContentValues().apply {
            put("exercise_name", pr.exerciseName)
            put("weight_kg", pr.weightKg)
            put("reps", pr.reps)
            put("date", now)
        })
    }

    fun deleteCustomPR(exerciseName: String) {
        val db = writableDatabase
        db.delete("custom_prs", "exercise_name = ?", arrayOf(exerciseName))
        db.delete("custom_prs_history", "exercise_name = ?", arrayOf(exerciseName))
    }

    fun getCustomPRHistory(exerciseName: String): List<CustomPRHistoryPoint> {
        val db = readableDatabase
        val list = mutableListOf<CustomPRHistoryPoint>()
        val cursor = db.rawQuery(
            "SELECT date, weight_kg, reps FROM custom_prs_history WHERE exercise_name = ? ORDER BY date ASC",
            arrayOf(exerciseName)
        )
        cursor.use {
            while (it.moveToNext()) {
                list.add(CustomPRHistoryPoint(
                    date = it.getLong(0),
                    weightKg = it.getDouble(1),
                    reps = it.getInt(2)
                ))
            }
        }
        return list
    }

    companion object {
        const val DATABASE_NAME = "gimnasio.db"
        const val DATABASE_VERSION = 13

        @Volatile
        private var INSTANCE: GimnasioDatabase? = null

        fun getInstance(context: Context): GimnasioDatabase {
            return INSTANCE ?: synchronized(this) {
                GimnasioDatabase(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}
