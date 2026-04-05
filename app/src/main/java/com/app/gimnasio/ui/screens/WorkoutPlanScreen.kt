package com.app.gimnasio.ui.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.gimnasio.data.model.ExercisePhase
import com.app.gimnasio.data.model.Routine
import com.app.gimnasio.data.model.WorkoutPlanDay
import androidx.compose.ui.window.Dialog
import com.app.gimnasio.ui.theme.DarkBackground
import com.app.gimnasio.ui.theme.DarkCard
import com.app.gimnasio.ui.theme.DarkSurface
import com.app.gimnasio.ui.theme.LimeGreen
import com.app.gimnasio.ui.theme.TextGray
import com.app.gimnasio.ui.theme.TextDarkGray
import com.app.gimnasio.ui.theme.DarkBorder
import com.app.gimnasio.ui.viewmodel.PlanViewModel

private val dayNames = listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom")
private val dayFullNames = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutPlanScreen(
    viewModel: PlanViewModel,
    onBack: () -> Unit
) {
    val routines by viewModel.routines.collectAsState()
    val currentPlan by viewModel.planDays.collectAsState()

    // Selected days (1-7) and their assigned routines
    val selectedDays = remember(currentPlan) {
        mutableStateListOf<Int>().apply {
            addAll(currentPlan.map { it.dayOfWeek })
        }
    }
    val dayRoutineMap = remember(currentPlan) {
        mutableStateOf(
            currentPlan.associate { it.dayOfWeek to it.routineId }.toMutableMap()
        )
    }

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = { Text("Planificación") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    // Save button
                    val canSave = selectedDays.isNotEmpty() &&
                            selectedDays.all { dayRoutineMap.value.containsKey(it) }
                    IconButton(
                        onClick = {
                            if (canSave) {
                                val days = selectedDays.map { dow ->
                                    WorkoutPlanDay(
                                        dayOfWeek = dow,
                                        routineId = dayRoutineMap.value[dow]!!
                                    )
                                }
                                viewModel.savePlan(days)
                                onBack()
                            }
                        },
                        enabled = canSave
                    ) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = "Guardar",
                            tint = if (canSave) LimeGreen else TextDarkGray
                        )
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
            // Instructions
            Text(
                text = "Seleccioná los días que entrenás",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Tocá un día para activarlo y asignale una rutina",
                color = TextGray,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Day selector row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                for (dow in 1..7) {
                    val isSelected = dow in selectedDays
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) LimeGreen else DarkCard)
                            .border(
                                width = 1.dp,
                                color = if (isSelected) LimeGreen else DarkBorder,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable {
                                if (isSelected) {
                                    selectedDays.remove(dow)
                                    val newMap = dayRoutineMap.value.toMutableMap()
                                    newMap.remove(dow)
                                    dayRoutineMap.value = newMap
                                } else {
                                    selectedDays.add(dow)
                                    selectedDays.sort()
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = dayNames[dow - 1],
                            color = if (isSelected) Color.Black else TextGray,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${selectedDays.size} día${if (selectedDays.size != 1) "s" else ""} de entrenamiento",
                color = TextGray,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (selectedDays.isEmpty()) {
                // Empty state
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.FitnessCenter,
                        contentDescription = null,
                        tint = TextDarkGray,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Seleccioná al menos un día",
                        color = TextGray,
                        fontSize = 15.sp
                    )
                }
            } else {
                // Routine assignment for each selected day
                Text(
                    text = "Asigná rutinas",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                if (routines.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = DarkCard),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No tenés rutinas creadas",
                                color = TextGray,
                                fontSize = 15.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Creá rutinas primero para poder asignarlas",
                                color = TextGray.copy(alpha = 0.7f),
                                fontSize = 13.sp
                            )
                        }
                    }
                } else {
                    selectedDays.sorted().forEach { dow ->
                        DayRoutineAssignment(
                            dayOfWeek = dow,
                            dayName = dayFullNames[dow - 1],
                            selectedRoutineId = dayRoutineMap.value[dow],
                            routines = routines,
                            onRoutineSelected = { routineId ->
                                val newMap = dayRoutineMap.value.toMutableMap()
                                newMap[dow] = routineId
                                dayRoutineMap.value = newMap
                            }
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }

            // Rest days info
            val restDays = (1..7).filter { it !in selectedDays }
            if (restDays.isNotEmpty() && selectedDays.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Días de descanso",
                    color = TextGray,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = restDays.joinToString(", ") { dayFullNames[it - 1] },
                    color = TextDarkGray,
                    fontSize = 14.sp
                )
            }

            // Clear plan button
            if (currentPlan.isNotEmpty()) {
                Spacer(modifier = Modifier.height(32.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.clearPlan(); onBack() },
                    colors = CardDefaults.cardColors(containerColor = DarkCard),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = null,
                            tint = Color(0xFFFF6B6B),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Eliminar planificación",
                            color = Color(0xFFFF6B6B),
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun DayRoutineAssignment(
    dayOfWeek: Int,
    dayName: String,
    selectedRoutineId: Long?,
    routines: List<Routine>,
    onRoutineSelected: (Long) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    val selectedRoutine = routines.find { it.id == selectedRoutineId }

    if (showDialog) {
        RoutinePickerDialog(
            dayName = dayName,
            routines = routines,
            selectedRoutineId = selectedRoutineId,
            onRoutineSelected = { routineId ->
                onRoutineSelected(routineId)
                showDialog = false
            },
            onDismiss = { showDialog = false }
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showDialog = true }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Day circle
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (selectedRoutine != null) LimeGreen else DarkBorder),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = dayName.take(2).uppercase(),
                    color = if (selectedRoutine != null) Color.Black else TextGray,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = dayName,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                if (selectedRoutine != null) {
                    Text(
                        text = selectedRoutine.name,
                        color = LimeGreen,
                        fontSize = 13.sp
                    )
                    val warmup = selectedRoutine.exercises.count { it.phase == ExercisePhase.WARMUP }
                    val strength = selectedRoutine.exercises.count { it.phase == ExercisePhase.STRENGTH }
                    Text(
                        text = "$warmup calentamiento · $strength fuerza",
                        color = TextGray,
                        fontSize = 11.sp
                    )
                } else {
                    Text(
                        text = "Tocá para asignar rutina",
                        color = TextGray,
                        fontSize = 13.sp
                    )
                }
            }

            Icon(
                Icons.Default.FitnessCenter,
                contentDescription = null,
                tint = if (selectedRoutine != null) LimeGreen else TextDarkGray,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun RoutinePickerDialog(
    dayName: String,
    routines: List<Routine>,
    selectedRoutineId: Long?,
    onRoutineSelected: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Rutina para $dayName",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = TextGray, modifier = Modifier.size(20.dp))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    modifier = Modifier.height((routines.size.coerceAtMost(5) * 100).dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(routines) { routine ->
                        val isSelected = routine.id == selectedRoutineId
                        val warmup = routine.exercises.count { it.phase == ExercisePhase.WARMUP }
                        val strength = routine.exercises.count { it.phase == ExercisePhase.STRENGTH }
                        val total = routine.exercises.size

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onRoutineSelected(routine.id) },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) LimeGreen.copy(alpha = 0.15f) else DarkCard
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = routine.name,
                                        color = if (isSelected) LimeGreen else Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                    if (routine.description.isNotBlank()) {
                                        Text(
                                            text = routine.description,
                                            color = TextGray,
                                            fontSize = 12.sp,
                                            maxLines = 1
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                        Text(
                                            text = "$total ejercicios",
                                            color = TextGray,
                                            fontSize = 12.sp
                                        )
                                        if (warmup > 0) {
                                            Text(
                                                text = "$warmup calent.",
                                                color = Color(0xFF4FC3F7),
                                                fontSize = 12.sp
                                            )
                                        }
                                        if (strength > 0) {
                                            Text(
                                                text = "$strength fuerza",
                                                color = Color(0xFFFF9800),
                                                fontSize = 12.sp
                                            )
                                        }
                                    }
                                }
                                if (isSelected) {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = null,
                                        tint = LimeGreen,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
