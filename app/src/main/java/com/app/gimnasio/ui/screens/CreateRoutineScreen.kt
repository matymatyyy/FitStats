package com.app.gimnasio.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    onSave: (String, String, List<Exercise>) -> Unit,
    onBack: () -> Unit
) {
    val name by viewModel.routineName.collectAsState()
    val description by viewModel.routineDescription.collectAsState()
    val warmupExercises by viewModel.warmupExercises.collectAsState()
    val strengthExercises by viewModel.strengthExercises.collectAsState()
    val isEditMode = viewModel.editingRoutineId != null

    var showWarmupDialog by remember { mutableStateOf(false) }
    var showStrengthDialog by remember { mutableStateOf(false) }
    var editingWarmupIndex by remember { mutableStateOf<Int?>(null) }
    var editingStrengthIndex by remember { mutableStateOf<Int?>(null) }

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

            Spacer(modifier = Modifier.height(24.dp))

            // --- CALENTAMIENTO / MOVILIDAD ---
            SectionHeader(
                title = "Calentamiento / Movilidad",
                subtitle = "Ejercicios por repeticiones o tiempo"
            )

            Spacer(modifier = Modifier.height(8.dp))

            warmupExercises.forEachIndexed { index, exercise ->
                WarmupExerciseItem(
                    exercise = exercise,
                    onClick = { editingWarmupIndex = index },
                    onDelete = { viewModel.removeWarmupExercise(index) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            AddExerciseButton(
                text = "Agregar ejercicio de calentamiento",
                onClick = { showWarmupDialog = true }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --- FUERZA ---
            SectionHeader(
                title = "Fuerza",
                subtitle = "Ejercicios con series, reps, peso y descanso"
            )

            Spacer(modifier = Modifier.height(8.dp))

            strengthExercises.forEachIndexed { index, exercise ->
                StrengthExerciseItem(
                    exercise = exercise,
                    onClick = { editingStrengthIndex = index },
                    onDelete = { viewModel.removeStrengthExercise(index) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            AddExerciseButton(
                text = "Agregar ejercicio de fuerza",
                onClick = { showStrengthDialog = true }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Botón guardar
            Button(
                onClick = {
                    if (viewModel.isValid()) {
                        onSave(name, description, viewModel.getAllExercises())
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

    editingWarmupIndex?.let { index ->
        AddWarmupExerciseDialog(
            initialExercise = warmupExercises[index],
            galleryExercises = galleryExercises,
            onDismiss = { editingWarmupIndex = null },
            onConfirm = { exercise ->
                viewModel.updateWarmupExercise(index, exercise)
                editingWarmupIndex = null
            }
        )
    }

    editingStrengthIndex?.let { index ->
        AddStrengthExerciseDialog(
            initialExercise = strengthExercises[index],
            galleryExercises = galleryExercises,
            onDismiss = { editingStrengthIndex = null },
            onConfirm = { exercise ->
                viewModel.updateStrengthExercise(index, exercise)
                editingStrengthIndex = null
            }
        )
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
private fun AddExerciseButton(text: String, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        border = ButtonDefaults.outlinedButtonBorder(true).copy(
            width = 1.dp
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Icon(Icons.Default.Add, contentDescription = null, tint = LimeGreen)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, color = LimeGreen)
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
            Text(if (isEditing) "Editar ejercicio" else "Ejercicio de fuerza", color = Color.White)
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
        },
        confirmButton = {
            val isValid = name.isNotBlank() && sets.isNotBlank() && reps.isNotBlank()
            TextButton(
                onClick = {
                    onConfirm(
                        Exercise(
                            name = name.trim(),
                            phase = ExercisePhase.STRENGTH,
                            sets = sets.toIntOrNull(),
                            strengthReps = reps.toIntOrNull(),
                            weightKg = weight.toDoubleOrNull() ?: 0.0,
                            restSeconds = rest.toIntOrNull() ?: 90
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
