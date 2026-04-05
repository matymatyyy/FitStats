package com.app.gimnasio.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Loop
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import com.app.gimnasio.data.model.Exercise
import com.app.gimnasio.data.model.ExercisePhase
import com.app.gimnasio.ui.theme.DarkBackground
import com.app.gimnasio.ui.theme.DarkCard
import com.app.gimnasio.ui.theme.DarkSurface
import com.app.gimnasio.ui.theme.LimeGreen
import com.app.gimnasio.ui.theme.TextGray
import com.app.gimnasio.ui.viewmodel.RoutinesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutineDetailScreen(
    viewModel: RoutinesViewModel,
    onBack: () -> Unit,
    onStartWorkout: (Long) -> Unit = {},
    onEdit: (Long) -> Unit = {}
) {
    val routine by viewModel.selectedRoutine.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            containerColor = DarkCard,
            title = { Text("Eliminar rutina", color = Color.White) },
            text = { Text("¿Estás seguro de que querés eliminar \"${routine?.name}\"? Esta acción no se puede deshacer.", color = TextGray) },
            confirmButton = {
                TextButton(onClick = {
                    routine?.let { viewModel.deleteRoutine(it.id) }
                    showDeleteDialog = false
                    onBack()
                }) {
                    Text("Eliminar", color = Color(0xFFFF6B6B))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar", color = LimeGreen)
                }
            }
        )
    }

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = { Text(routine?.name ?: "Rutina") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    routine?.let { r ->
                        IconButton(onClick = { onEdit(r.id) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar", tint = LimeGreen)
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color(0xFFFF6B6B))
                        }
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
        routine?.let { r ->
            val warmupExercises = r.exercises.filter { it.phase == ExercisePhase.WARMUP }
            val strengthExercises = r.exercises.filter { it.phase == ExercisePhase.STRENGTH }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (r.description.isNotBlank()) {
                    item {
                        Text(
                            text = r.description,
                            color = TextGray,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }

                if (warmupExercises.isNotEmpty()) {
                    item {
                        Text(
                            text = "CALENTAMIENTO",
                            color = LimeGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                    itemsIndexed(warmupExercises) { index, exercise ->
                        if (exercise.isCircuit) {
                            CircuitDetailItem(index = index + 1, exercise = exercise)
                        } else {
                            WarmupDetailItem(index = index + 1, exercise = exercise)
                        }
                    }
                    item { Spacer(modifier = Modifier.height(8.dp)) }
                }

                if (strengthExercises.isNotEmpty()) {
                    item {
                        Text(
                            text = "FUERZA",
                            color = LimeGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                    itemsIndexed(strengthExercises) { index, exercise ->
                        if (exercise.isCircuit) {
                            CircuitDetailItem(index = index + 1, exercise = exercise)
                        } else {
                            StrengthDetailItem(index = index + 1, exercise = exercise)
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { onStartWorkout(r.id) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = LimeGreen),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(
                            Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Iniciar Entrenamiento",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun WarmupDetailItem(index: Int, exercise: Exercise) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$index.",
                color = LimeGreen,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.padding(horizontal = 6.dp))
            Column {
                Text(exercise.name, color = Color.White, fontWeight = FontWeight.Bold)
                val detail = if (exercise.durationSeconds != null) {
                    "${exercise.durationSeconds} segundos"
                } else {
                    "${exercise.reps} repeticiones"
                }
                Text(detail, color = TextGray, fontSize = 13.sp)
            }
        }
    }
}

@Composable
private fun StrengthDetailItem(index: Int, exercise: Exercise) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "$index.",
                    color = LimeGreen,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.padding(horizontal = 6.dp))
                Text(exercise.name, color = Color.White, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(6.dp))

            val isCustom = exercise.weightPerSet != null || exercise.repsPerSet != null
            if (isCustom) {
                // Show custom sets with reps + weight per set
                val sets = exercise.sets ?: 0
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    InfoChip("Series", "$sets")
                    InfoChip("Desc.", if (exercise.restSeconds != null) "${exercise.restSeconds}s" else "-")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("Detalle por serie:", color = TextGray, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for (i in 0 until sets) {
                        val r = exercise.repsPerSet?.getOrNull(i) ?: exercise.strengthReps ?: 0
                        val w = exercise.weightPerSet?.getOrNull(i) ?: exercise.weightKg ?: 0.0
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "${r}r",
                                color = LimeGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                "${w}kg",
                                color = Color.White,
                                fontSize = 13.sp
                            )
                            Text(
                                "S${i + 1}",
                                color = TextGray,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    InfoChip("Series", "${exercise.sets ?: "-"}")
                    InfoChip("Reps", "${exercise.strengthReps ?: "-"}")
                    InfoChip("Peso", if (exercise.weightKg != null && exercise.weightKg > 0) "${exercise.weightKg} kg" else "-")
                    InfoChip("Desc.", if (exercise.restSeconds != null) "${exercise.restSeconds}s" else "-")
                }
            }
        }
    }
}

@Composable
private fun CircuitDetailItem(index: Int, exercise: Exercise) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "$index.",
                    color = LimeGreen,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.padding(horizontal = 6.dp))
                Icon(
                    Icons.Default.Loop,
                    contentDescription = null,
                    tint = Color(0xFFFF9800),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    "Circuito · ${exercise.circuitRounds} rondas",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            // Show circuit exercises with arrows
            Text(
                exercise.circuitExercises.joinToString(" → "),
                color = Color(0xFFFF9800),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Show exercise-specific info
            if (exercise.phase == ExercisePhase.STRENGTH) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    InfoChip("Reps", "${exercise.strengthReps ?: "-"}")
                    InfoChip("Rondas", "${exercise.circuitRounds ?: "-"}")
                    InfoChip("Desc.", if (exercise.restSeconds != null) "${exercise.restSeconds}s" else "-")
                }
            } else {
                val detail = if (exercise.durationSeconds != null) {
                    "${exercise.durationSeconds}s por ejercicio"
                } else if (exercise.reps != null) {
                    "${exercise.reps} reps por ejercicio"
                } else ""
                if (detail.isNotBlank()) {
                    Text(detail, color = TextGray, fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
private fun InfoChip(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            color = LimeGreen,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
        )
        Text(
            text = label,
            color = TextGray,
            fontSize = 11.sp
        )
    }
}
