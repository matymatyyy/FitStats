package com.app.gimnasio.ui.screens

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Loop
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.io.File
import java.util.UUID
import com.app.gimnasio.data.model.Exercise
import com.app.gimnasio.data.model.ExerciseInfo
import com.app.gimnasio.data.model.ExercisePhase
import com.app.gimnasio.ui.theme.DarkBackground
import com.app.gimnasio.ui.theme.DarkCard
import com.app.gimnasio.ui.theme.DarkSurface
import com.app.gimnasio.ui.theme.LimeGreen
import com.app.gimnasio.ui.theme.TextGray
import com.app.gimnasio.ui.viewmodel.CreateRoutineViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateRoutineScreen(
    viewModel: CreateRoutineViewModel,
    galleryExercises: List<ExerciseInfo>,
    onSave: (String, String, List<Exercise>, String?) -> Unit,
    onBack: () -> Unit
) {
    val name by viewModel.routineName.collectAsState()
    val description by viewModel.routineDescription.collectAsState()
    val imagePath by viewModel.routineImagePath.collectAsState()
    val warmupExercises by viewModel.warmupExercises.collectAsState()
    val strengthExercises by viewModel.strengthExercises.collectAsState()
    val isEditMode = viewModel.editingRoutineId != null

    var showWarmupDialog by remember { mutableStateOf(false) }
    var showStrengthDialog by remember { mutableStateOf(false) }
    var showWarmupCircuitDialog by remember { mutableStateOf(false) }
    var showStrengthCircuitDialog by remember { mutableStateOf(false) }
    var editingWarmupIndex by remember { mutableStateOf<Int?>(null) }
    var editingStrengthIndex by remember { mutableStateOf<Int?>(null) }

    val context = LocalContext.current
    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val dir = File(context.filesDir, "routine_images")
            if (!dir.exists()) dir.mkdirs()
            val file = File(dir, "routine_${UUID.randomUUID()}.jpg")
            context.contentResolver.openInputStream(it)?.use { input ->
                file.outputStream().use { output -> input.copyTo(output) }
            }
            viewModel.routineImagePath.value = file.absolutePath
        }
    }

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = LimeGreen,
        unfocusedBorderColor = TextGray.copy(alpha = 0.5f),
        focusedLabelColor = LimeGreen,
        unfocusedLabelColor = TextGray,
        cursorColor = LimeGreen,
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White
    )

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = { Text(if (isEditMode) "Editar Rutina" else "Crear Rutina") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkSurface,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Nombre y descripción
            OutlinedTextField(
                value = name,
                onValueChange = { viewModel.routineName.value = it },
                label = { Text("Nombre de la rutina") },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors,
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { viewModel.routineDescription.value = it },
                label = { Text("Descripción (opcional)") },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Image picker
            Text(
                text = "Imagen (opcional)",
                color = TextGray,
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            if (imagePath != null) {
                val file = File(imagePath!!)
                if (file.exists()) {
                    val bitmap = remember(imagePath) {
                        BitmapFactory.decodeFile(file.absolutePath)
                    }
                    if (bitmap != null) {
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "Imagen de rutina",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { imageLauncher.launch("image/*") },
                            contentScale = ContentScale.Crop
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row {
                    TextButton(onClick = { imageLauncher.launch("image/*") }) {
                        Text("Cambiar", color = LimeGreen, fontSize = 13.sp)
                    }
                    TextButton(onClick = { viewModel.routineImagePath.value = null }) {
                        Text("Quitar", color = Color(0xFFFF6B6B), fontSize = 13.sp)
                    }
                }
            } else {
                OutlinedButton(
                    onClick = { imageLauncher.launch("image/*") },
                    modifier = Modifier.fillMaxWidth(),
                    border = ButtonDefaults.outlinedButtonBorder(true).copy(width = 1.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = LimeGreen)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Agregar imagen", color = LimeGreen)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- CALENTAMIENTO / MOVILIDAD ---
            SectionHeader(
                title = "Calentamiento / Movilidad",
                subtitle = "Ejercicios por repeticiones o tiempo"
            )

            Spacer(modifier = Modifier.height(8.dp))

            warmupExercises.forEachIndexed { index, exercise ->
                if (exercise.isCircuit) {
                    CircuitExerciseItem(
                        exercise = exercise,
                        onClick = { editingWarmupIndex = index },
                        onDelete = { viewModel.removeWarmupExercise(index) }
                    )
                } else {
                    WarmupExerciseItem(
                        exercise = exercise,
                        onClick = { editingWarmupIndex = index },
                        onDelete = { viewModel.removeWarmupExercise(index) }
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AddExerciseButton(
                    text = "Ejercicio",
                    onClick = { showWarmupDialog = true },
                    modifier = Modifier.weight(1f)
                )
                AddExerciseButton(
                    text = "Circuito",
                    onClick = { showWarmupCircuitDialog = true },
                    modifier = Modifier.weight(1f),
                    icon = true
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- FUERZA ---
            SectionHeader(
                title = "Fuerza",
                subtitle = "Ejercicios con series, reps, peso y descanso"
            )

            Spacer(modifier = Modifier.height(8.dp))

            strengthExercises.forEachIndexed { index, exercise ->
                if (exercise.isCircuit) {
                    CircuitExerciseItem(
                        exercise = exercise,
                        onClick = { editingStrengthIndex = index },
                        onDelete = { viewModel.removeStrengthExercise(index) }
                    )
                } else {
                    StrengthExerciseItem(
                        exercise = exercise,
                        onClick = { editingStrengthIndex = index },
                        onDelete = { viewModel.removeStrengthExercise(index) }
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AddExerciseButton(
                    text = "Ejercicio",
                    onClick = { showStrengthDialog = true },
                    modifier = Modifier.weight(1f)
                )
                AddExerciseButton(
                    text = "Circuito",
                    onClick = { showStrengthCircuitDialog = true },
                    modifier = Modifier.weight(1f),
                    icon = true
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Botón guardar
            Button(
                onClick = {
                    if (viewModel.isValid()) {
                        onSave(name, description, viewModel.getAllExercises(), imagePath)
                        viewModel.reset()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = viewModel.isValid(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = LimeGreen,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    if (isEditMode) "Guardar Cambios" else "Guardar Rutina",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // --- Diálogos de ejercicio individual ---
    if (showWarmupDialog) {
        AddWarmupExerciseDialog(
            galleryExercises = galleryExercises,
            onDismiss = { showWarmupDialog = false },
            onConfirm = { exercise ->
                viewModel.addWarmupExercise(exercise)
                showWarmupDialog = false
            }
        )
    }

    if (showStrengthDialog) {
        AddStrengthExerciseDialog(
            galleryExercises = galleryExercises,
            onDismiss = { showStrengthDialog = false },
            onConfirm = { exercise ->
                viewModel.addStrengthExercise(exercise)
                showStrengthDialog = false
            }
        )
    }

    // --- Diálogos de circuito ---
    if (showWarmupCircuitDialog) {
        AddCircuitDialog(
            phase = ExercisePhase.WARMUP,
            galleryExercises = galleryExercises,
            onDismiss = { showWarmupCircuitDialog = false },
            onConfirm = { exercise ->
                viewModel.addWarmupExercise(exercise)
                showWarmupCircuitDialog = false
            }
        )
    }

    if (showStrengthCircuitDialog) {
        AddCircuitDialog(
            phase = ExercisePhase.STRENGTH,
            galleryExercises = galleryExercises,
            onDismiss = { showStrengthCircuitDialog = false },
            onConfirm = { exercise ->
                viewModel.addStrengthExercise(exercise)
                showStrengthCircuitDialog = false
            }
        )
    }

    // --- Edición de ejercicio existente ---
    editingWarmupIndex?.let { index ->
        val exercise = warmupExercises[index]
        if (exercise.isCircuit) {
            AddCircuitDialog(
                phase = ExercisePhase.WARMUP,
                initialExercise = exercise,
                galleryExercises = galleryExercises,
                onDismiss = { editingWarmupIndex = null },
                onConfirm = { updated ->
                    viewModel.updateWarmupExercise(index, updated)
                    editingWarmupIndex = null
                }
            )
        } else {
            AddWarmupExerciseDialog(
                initialExercise = exercise,
                galleryExercises = galleryExercises,
                onDismiss = { editingWarmupIndex = null },
                onConfirm = { updated ->
                    viewModel.updateWarmupExercise(index, updated)
                    editingWarmupIndex = null
                }
            )
        }
    }

    editingStrengthIndex?.let { index ->
        val exercise = strengthExercises[index]
        if (exercise.isCircuit) {
            AddCircuitDialog(
                phase = ExercisePhase.STRENGTH,
                initialExercise = exercise,
                galleryExercises = galleryExercises,
                onDismiss = { editingStrengthIndex = null },
                onConfirm = { updated ->
                    viewModel.updateStrengthExercise(index, updated)
                    editingStrengthIndex = null
                }
            )
        } else {
            AddStrengthExerciseDialog(
                initialExercise = exercise,
                galleryExercises = galleryExercises,
                onDismiss = { editingStrengthIndex = null },
                onConfirm = { updated ->
                    viewModel.updateStrengthExercise(index, updated)
                    editingStrengthIndex = null
                }
            )
        }
    }
}

@Composable
private fun SectionHeader(title: String, subtitle: String) {
    Text(
        text = title,
        color = LimeGreen,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp
    )
    Text(
        text = subtitle,
        color = TextGray,
        fontSize = 13.sp
    )
}

@Composable
private fun AddExerciseButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: Boolean = false
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        border = ButtonDefaults.outlinedButtonBorder(true).copy(width = 1.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Icon(
            if (icon) Icons.Default.Loop else Icons.Default.Add,
            contentDescription = null,
            tint = LimeGreen,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text, color = LimeGreen, fontSize = 13.sp)
    }
}

@Composable
private fun WarmupExerciseItem(exercise: Exercise, onClick: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(exercise.name, color = Color.White, fontWeight = FontWeight.Bold)
                val detail = if (exercise.durationSeconds != null) {
                    "${exercise.durationSeconds}s"
                } else {
                    "${exercise.reps} reps"
                }
                Text(detail, color = TextGray, fontSize = 13.sp)
            }
            IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color(0xFFFF6B6B), modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
private fun StrengthExerciseItem(exercise: Exercise, onClick: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(exercise.name, color = Color.White, fontWeight = FontWeight.Bold)
                val isCustom = exercise.weightPerSet != null || exercise.repsPerSet != null
                if (isCustom) {
                    val sets = exercise.sets ?: 0
                    val parts = (0 until sets).map { i ->
                        val r = exercise.repsPerSet?.getOrNull(i) ?: exercise.strengthReps ?: 0
                        val w = exercise.weightPerSet?.getOrNull(i) ?: exercise.weightKg ?: 0.0
                        "${r}r×${w}kg"
                    }
                    val rest = if (exercise.restSeconds != null) " · ${exercise.restSeconds}s desc" else ""
                    Text(
                        parts.joinToString(" | ") + rest,
                        color = TextGray,
                        fontSize = 12.sp
                    )
                } else {
                    val weight = if (exercise.weightKg != null && exercise.weightKg > 0)
                        " · ${exercise.weightKg} kg" else ""
                    val rest = if (exercise.restSeconds != null)
                        " · ${exercise.restSeconds}s desc" else ""
                    Text(
                        "${exercise.sets} series × ${exercise.strengthReps} reps$weight$rest",
                        color = TextGray,
                        fontSize = 13.sp
                    )
                }
            }
            IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color(0xFFFF6B6B), modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
private fun CircuitExerciseItem(exercise: Exercise, onClick: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Loop,
                contentDescription = null,
                tint = Color(0xFFFF9800),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Circuito · ${exercise.circuitRounds} rondas",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    exercise.circuitExercises.joinToString(" → "),
                    color = TextGray,
                    fontSize = 13.sp
                )
                if (exercise.phase == ExercisePhase.STRENGTH) {
                    val info = buildString {
                        if (exercise.strengthReps != null) append("${exercise.strengthReps} reps")
                        if (exercise.restSeconds != null) append(" · ${exercise.restSeconds}s desc")
                    }
                    if (info.isNotBlank()) {
                        Text(info, color = TextGray, fontSize = 12.sp)
                    }
                } else {
                    val info = if (exercise.durationSeconds != null) {
                        "${exercise.durationSeconds}s por ejercicio"
                    } else if (exercise.reps != null) {
                        "${exercise.reps} reps por ejercicio"
                    } else null
                    if (info != null) {
                        Text(info, color = TextGray, fontSize = 12.sp)
                    }
                }
            }
            IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color(0xFFFF6B6B), modifier = Modifier.size(20.dp))
            }
        }
    }
}

// --- DIÁLOGOS ---

@Composable
private fun GalleryPickerDialog(
    exercises: List<ExerciseInfo>,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var search by remember { mutableStateOf("") }
    val filtered = if (search.isBlank()) exercises
    else exercises.filter { it.name.contains(search, ignoreCase = true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        title = { Text("Seleccionar ejercicio", color = Color.White) },
        text = {
            Column(modifier = Modifier.height(400.dp)) {
                OutlinedTextField(
                    value = search,
                    onValueChange = { search = it },
                    placeholder = { Text("Buscar...") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextGray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LimeGreen,
                        unfocusedBorderColor = TextGray.copy(alpha = 0.5f),
                        cursorColor = LimeGreen,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedPlaceholderColor = TextGray,
                        unfocusedPlaceholderColor = TextGray
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                if (filtered.isEmpty()) {
                    Text(
                        "No se encontraron ejercicios",
                        color = TextGray,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        items(filtered) { exercise ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onSelect(exercise.name) }
                                    .padding(vertical = 10.dp, horizontal = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    exercise.name,
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    exercise.muscleGroup.displayName,
                                    color = TextGray,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = TextGray)
            }
        }
    )
}

@Composable
private fun AddWarmupExerciseDialog(
    initialExercise: Exercise? = null,
    galleryExercises: List<ExerciseInfo> = emptyList(),
    onDismiss: () -> Unit,
    onConfirm: (Exercise) -> Unit
) {
    var name by remember { mutableStateOf(initialExercise?.name ?: "") }
    var isTimeBased by remember { mutableStateOf(initialExercise?.durationSeconds != null) }
    var reps by remember { mutableStateOf(initialExercise?.reps?.toString() ?: "") }
    var duration by remember { mutableStateOf(initialExercise?.durationSeconds?.toString() ?: "") }
    val isEditing = initialExercise != null
    var showGalleryPicker by remember { mutableStateOf(false) }

    val dialogTextFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = LimeGreen,
        unfocusedBorderColor = TextGray.copy(alpha = 0.5f),
        focusedLabelColor = LimeGreen,
        unfocusedLabelColor = TextGray,
        cursorColor = LimeGreen,
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        title = {
            Text(if (isEditing) "Editar ejercicio" else "Ejercicio de calentamiento", color = Color.White)
        },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre del ejercicio") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = dialogTextFieldColors
                )

                if (galleryExercises.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(onClick = { showGalleryPicker = true }) {
                        Icon(Icons.Default.Search, contentDescription = null, tint = LimeGreen, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Elegir de galería", color = LimeGreen, fontSize = 13.sp)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = !isTimeBased,
                        onClick = { isTimeBased = false },
                        label = { Text("Repeticiones") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = LimeGreen.copy(alpha = 0.2f),
                            selectedLabelColor = LimeGreen
                        )
                    )
                    FilterChip(
                        selected = isTimeBased,
                        onClick = { isTimeBased = true },
                        label = { Text("Tiempo") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = LimeGreen.copy(alpha = 0.2f),
                            selectedLabelColor = LimeGreen
                        )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (isTimeBased) {
                    OutlinedTextField(
                        value = duration,
                        onValueChange = { duration = it.filter { c -> c.isDigit() } },
                        label = { Text("Duración (segundos)") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        colors = dialogTextFieldColors
                    )
                } else {
                    OutlinedTextField(
                        value = reps,
                        onValueChange = { reps = it.filter { c -> c.isDigit() } },
                        label = { Text("Repeticiones") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        colors = dialogTextFieldColors
                    )
                }
            }
        },
        confirmButton = {
            val isValid = name.isNotBlank() && if (isTimeBased) duration.isNotBlank() else reps.isNotBlank()
            TextButton(
                onClick = {
                    onConfirm(
                        Exercise(
                            name = name.trim(),
                            phase = ExercisePhase.WARMUP,
                            durationSeconds = if (isTimeBased) duration.toIntOrNull() else null,
                            reps = if (!isTimeBased) reps.toIntOrNull() else null
                        )
                    )
                },
                enabled = isValid
            ) {
                Text(if (isEditing) "Guardar" else "Agregar", color = if (isValid) LimeGreen else TextGray)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = TextGray)
            }
        }
    )

    if (showGalleryPicker) {
        GalleryPickerDialog(
            exercises = galleryExercises,
            onSelect = { selectedName ->
                name = selectedName
                showGalleryPicker = false
            },
            onDismiss = { showGalleryPicker = false }
        )
    }
}

@Composable
private fun AddStrengthExerciseDialog(
    initialExercise: Exercise? = null,
    galleryExercises: List<ExerciseInfo> = emptyList(),
    onDismiss: () -> Unit,
    onConfirm: (Exercise) -> Unit
) {
    var name by remember { mutableStateOf(initialExercise?.name ?: "") }
    var sets by remember { mutableStateOf(initialExercise?.sets?.toString() ?: "") }
    var reps by remember { mutableStateOf(initialExercise?.strengthReps?.toString() ?: "") }
    var weight by remember { mutableStateOf(initialExercise?.weightKg?.let { if (it > 0) it.toString() else "" } ?: "") }
    var rest by remember { mutableStateOf(initialExercise?.restSeconds?.toString() ?: "90") }
    var customSets by remember { mutableStateOf(initialExercise?.weightPerSet != null || initialExercise?.repsPerSet != null) }
    val weightPerSetList = remember {
        mutableStateListOf<String>().apply {
            initialExercise?.weightPerSet?.forEach { add(it.toString()) }
        }
    }
    val repsPerSetList = remember {
        mutableStateListOf<String>().apply {
            initialExercise?.repsPerSet?.forEach { add(it.toString()) }
        }
    }
    val isEditing = initialExercise != null
    var showGalleryPicker by remember { mutableStateOf(false) }

    // Sync lists size with sets count
    val setsInt = sets.toIntOrNull() ?: 0
    if (customSets && weightPerSetList.size != setsInt) {
        val currentSize = weightPerSetList.size
        if (setsInt > currentSize) {
            val defaultWeight = weight.ifBlank { "0" }
            val defaultReps = reps.ifBlank { "0" }
            repeat(setsInt - currentSize) {
                weightPerSetList.add(defaultWeight)
                repsPerSetList.add(defaultReps)
            }
        } else if (setsInt < currentSize) {
            repeat(currentSize - setsInt) {
                if (weightPerSetList.isNotEmpty()) weightPerSetList.removeAt(weightPerSetList.lastIndex)
                if (repsPerSetList.isNotEmpty()) repsPerSetList.removeAt(repsPerSetList.lastIndex)
            }
        }
    }
    // Also sync repsPerSetList independently if sizes diverge
    if (customSets && repsPerSetList.size != setsInt) {
        val currentSize = repsPerSetList.size
        if (setsInt > currentSize) {
            val defaultReps = reps.ifBlank { "0" }
            repeat(setsInt - currentSize) { repsPerSetList.add(defaultReps) }
        } else if (setsInt < currentSize) {
            repeat(currentSize - setsInt) {
                if (repsPerSetList.isNotEmpty()) repsPerSetList.removeAt(repsPerSetList.lastIndex)
            }
        }
    }

    val dialogTextFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = LimeGreen,
        unfocusedBorderColor = TextGray.copy(alpha = 0.5f),
        focusedLabelColor = LimeGreen,
        unfocusedLabelColor = TextGray,
        cursorColor = LimeGreen,
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        title = {
            Text(if (isEditing) "Editar ejercicio" else "Ejercicio de fuerza", color = Color.White)
        },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre del ejercicio") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = dialogTextFieldColors
                )

                if (galleryExercises.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(onClick = { showGalleryPicker = true }) {
                        Icon(Icons.Default.Search, contentDescription = null, tint = LimeGreen, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Elegir de galería", color = LimeGreen, fontSize = 13.sp)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = sets,
                        onValueChange = { sets = it.filter { c -> c.isDigit() } },
                        label = { Text("Series") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        colors = dialogTextFieldColors
                    )
                    OutlinedTextField(
                        value = reps,
                        onValueChange = { reps = it.filter { c -> c.isDigit() } },
                        label = { Text("Reps") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        colors = dialogTextFieldColors
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Custom sets toggle
                if (setsInt > 1) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { customSets = !customSets }
                    ) {
                        Checkbox(
                            checked = customSets,
                            onCheckedChange = { customSets = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = LimeGreen,
                                uncheckedColor = TextGray
                            )
                        )
                        Text(
                            "Series personalizadas",
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }

                if (customSets && setsInt > 1) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Serie", color = TextGray, fontSize = 12.sp, modifier = Modifier.width(40.dp))
                        Text("Reps", color = TextGray, fontSize = 12.sp, modifier = Modifier.weight(1f))
                        Text("Peso (kg)", color = TextGray, fontSize = 12.sp, modifier = Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(4.dp))

                    for (i in 0 until setsInt) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                "${i + 1}",
                                color = LimeGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                modifier = Modifier.width(40.dp)
                            )
                            OutlinedTextField(
                                value = repsPerSetList.getOrElse(i) { "" },
                                onValueChange = {
                                    val filtered = it.filter { c -> c.isDigit() }
                                    if (i < repsPerSetList.size) {
                                        repsPerSetList[i] = filtered
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                colors = dialogTextFieldColors
                            )
                            OutlinedTextField(
                                value = weightPerSetList.getOrElse(i) { "" },
                                onValueChange = {
                                    val filtered = it.filter { c -> c.isDigit() || c == '.' }
                                    if (i < weightPerSetList.size) {
                                        weightPerSetList[i] = filtered
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                singleLine = true,
                                colors = dialogTextFieldColors
                            )
                        }
                        if (i < setsInt - 1) Spacer(modifier = Modifier.height(4.dp))
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = rest,
                        onValueChange = { rest = it.filter { c -> c.isDigit() } },
                        label = { Text("Descanso (s)") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        colors = dialogTextFieldColors
                    )
                } else {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = weight,
                            onValueChange = { weight = it.filter { c -> c.isDigit() || c == '.' } },
                            label = { Text("Peso (kg)") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            colors = dialogTextFieldColors
                        )
                        OutlinedTextField(
                            value = rest,
                            onValueChange = { rest = it.filter { c -> c.isDigit() } },
                            label = { Text("Descanso (s)") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            colors = dialogTextFieldColors
                        )
                    }
                }
            }
        },
        confirmButton = {
            val isValid = name.isNotBlank() && sets.isNotBlank() &&
                    (reps.isNotBlank() || (customSets && setsInt > 1))
            TextButton(
                onClick = {
                    val isCustom = customSets && setsInt > 1
                    val parsedWeightPerSet = if (isCustom) {
                        weightPerSetList.map { it.toDoubleOrNull() ?: 0.0 }
                    } else null
                    val parsedRepsPerSet = if (isCustom) {
                        repsPerSetList.map { it.toIntOrNull() ?: 0 }
                    } else null

                    onConfirm(
                        Exercise(
                            name = name.trim(),
                            phase = ExercisePhase.STRENGTH,
                            sets = sets.toIntOrNull(),
                            strengthReps = if (isCustom) null else reps.toIntOrNull(),
                            weightKg = if (isCustom) null else (weight.toDoubleOrNull() ?: 0.0),
                            restSeconds = rest.toIntOrNull() ?: 90,
                            weightPerSet = parsedWeightPerSet,
                            repsPerSet = parsedRepsPerSet
                        )
                    )
                },
                enabled = isValid
            ) {
                Text(if (isEditing) "Guardar" else "Agregar", color = if (isValid) LimeGreen else TextGray)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = TextGray)
            }
        }
    )

    if (showGalleryPicker) {
        GalleryPickerDialog(
            exercises = galleryExercises,
            onSelect = { selectedName ->
                name = selectedName
                showGalleryPicker = false
            },
            onDismiss = { showGalleryPicker = false }
        )
    }
}

// --- Diálogo de circuito ---

@Composable
private fun AddCircuitDialog(
    phase: ExercisePhase,
    initialExercise: Exercise? = null,
    galleryExercises: List<ExerciseInfo> = emptyList(),
    onDismiss: () -> Unit,
    onConfirm: (Exercise) -> Unit
) {
    val exerciseNames = remember {
        mutableStateListOf<String>().apply {
            initialExercise?.circuitExercises?.forEach { add(it) }
        }
    }
    var rounds by remember { mutableStateOf(initialExercise?.circuitRounds?.toString() ?: "3") }
    var newExerciseName by remember { mutableStateOf("") }
    var showGalleryPicker by remember { mutableStateOf(false) }
    val isEditing = initialExercise != null

    // Warmup specific
    var isTimeBased by remember { mutableStateOf(initialExercise?.durationSeconds != null) }
    var reps by remember { mutableStateOf(initialExercise?.reps?.toString() ?: "") }
    var duration by remember { mutableStateOf(initialExercise?.durationSeconds?.toString() ?: "") }

    // Strength specific
    var strengthReps by remember { mutableStateOf(initialExercise?.strengthReps?.toString() ?: "") }
    var rest by remember { mutableStateOf(initialExercise?.restSeconds?.toString() ?: "60") }

    val dialogTextFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = LimeGreen,
        unfocusedBorderColor = TextGray.copy(alpha = 0.5f),
        focusedLabelColor = LimeGreen,
        unfocusedLabelColor = TextGray,
        cursorColor = LimeGreen,
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        title = {
            Text(
                if (isEditing) "Editar circuito" else "Nuevo circuito",
                color = Color.White
            )
        },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Text(
                    "Ejercicios del circuito",
                    color = LimeGreen,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                // List of exercises in circuit
                exerciseNames.forEachIndexed { index, exName ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "${index + 1}.",
                            color = Color(0xFFFF9800),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.width(24.dp)
                        )
                        Text(
                            exName,
                            color = Color.White,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = { exerciseNames.removeAt(index) },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Quitar",
                                tint = Color(0xFFFF6B6B),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                if (exerciseNames.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        exerciseNames.joinToString(" → "),
                        color = TextGray,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Add exercise to circuit
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = newExerciseName,
                        onValueChange = { newExerciseName = it },
                        label = { Text("Nombre") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = dialogTextFieldColors
                    )
                    IconButton(
                        onClick = {
                            if (newExerciseName.isNotBlank()) {
                                exerciseNames.add(newExerciseName.trim())
                                newExerciseName = ""
                            }
                        },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Agregar", tint = LimeGreen)
                    }
                }

                if (galleryExercises.isNotEmpty()) {
                    TextButton(onClick = { showGalleryPicker = true }) {
                        Icon(Icons.Default.Search, contentDescription = null, tint = LimeGreen, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Elegir de galería", color = LimeGreen, fontSize = 13.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Rounds
                OutlinedTextField(
                    value = rounds,
                    onValueChange = { rounds = it.filter { c -> c.isDigit() } },
                    label = { Text("Rondas") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = dialogTextFieldColors
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Phase-specific fields
                if (phase == ExercisePhase.WARMUP) {
                    Text("Parámetros por ejercicio", color = TextGray, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = !isTimeBased,
                            onClick = { isTimeBased = false },
                            label = { Text("Repeticiones") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = LimeGreen.copy(alpha = 0.2f),
                                selectedLabelColor = LimeGreen
                            )
                        )
                        FilterChip(
                            selected = isTimeBased,
                            onClick = { isTimeBased = true },
                            label = { Text("Tiempo") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = LimeGreen.copy(alpha = 0.2f),
                                selectedLabelColor = LimeGreen
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    if (isTimeBased) {
                        OutlinedTextField(
                            value = duration,
                            onValueChange = { duration = it.filter { c -> c.isDigit() } },
                            label = { Text("Duración por ejercicio (s)") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            colors = dialogTextFieldColors
                        )
                    } else {
                        OutlinedTextField(
                            value = reps,
                            onValueChange = { reps = it.filter { c -> c.isDigit() } },
                            label = { Text("Reps por ejercicio") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            colors = dialogTextFieldColors
                        )
                    }
                } else {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = strengthReps,
                            onValueChange = { strengthReps = it.filter { c -> c.isDigit() } },
                            label = { Text("Reps") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            colors = dialogTextFieldColors
                        )
                        OutlinedTextField(
                            value = rest,
                            onValueChange = { rest = it.filter { c -> c.isDigit() } },
                            label = { Text("Descanso (s)") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            colors = dialogTextFieldColors
                        )
                    }
                }
            }
        },
        confirmButton = {
            val isValid = exerciseNames.size >= 2 && rounds.isNotBlank() && (rounds.toIntOrNull() ?: 0) > 0
            TextButton(
                onClick = {
                    val circuitName = "Circuito: ${exerciseNames.joinToString(", ")}"
                    onConfirm(
                        Exercise(
                            name = circuitName,
                            phase = phase,
                            isCircuit = true,
                            circuitExercises = exerciseNames.toList(),
                            circuitRounds = rounds.toIntOrNull() ?: 3,
                            durationSeconds = if (phase == ExercisePhase.WARMUP && isTimeBased) duration.toIntOrNull() else null,
                            reps = if (phase == ExercisePhase.WARMUP && !isTimeBased) reps.toIntOrNull() else null,
                            strengthReps = if (phase == ExercisePhase.STRENGTH) strengthReps.toIntOrNull() else null,
                            restSeconds = if (phase == ExercisePhase.STRENGTH) (rest.toIntOrNull() ?: 60) else null
                        )
                    )
                },
                enabled = isValid
            ) {
                Text(
                    if (isEditing) "Guardar" else "Agregar",
                    color = if (isValid) LimeGreen else TextGray
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = TextGray)
            }
        }
    )

    if (showGalleryPicker) {
        GalleryPickerDialog(
            exercises = galleryExercises,
            onSelect = { selectedName ->
                exerciseNames.add(selectedName)
                showGalleryPicker = false
            },
            onDismiss = { showGalleryPicker = false }
        )
    }
}
