package com.app.gimnasio.ui.screens

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.gimnasio.data.model.WorkoutSetLog
import com.app.gimnasio.ui.theme.DarkBackground
import com.app.gimnasio.ui.theme.DarkCard
import com.app.gimnasio.ui.theme.DarkBorder
import com.app.gimnasio.ui.theme.DarkSurface
import com.app.gimnasio.ui.theme.LimeGreen
import com.app.gimnasio.ui.theme.TextGray
import com.app.gimnasio.ui.theme.TextDarkGray
import com.app.gimnasio.ui.viewmodel.ActivityViewModel
import com.app.gimnasio.ui.viewmodel.ExerciseProgressPoint
import com.app.gimnasio.widget.NextWorkoutWidgetReceiver
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun ActivityScreen(viewModel: ActivityViewModel) {
    val currentMonth by viewModel.currentMonth.collectAsState()
    val monthWorkouts by viewModel.monthWorkouts.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val selectedDateWorkouts by viewModel.selectedDateWorkouts.collectAsState()
    val selectedDateSetLogs by viewModel.selectedDateSetLogs.collectAsState()
    val showWidgetBanner by viewModel.showWidgetBanner.collectAsState()
    val periodDays by viewModel.periodDays.collectAsState()
    val periodSummary by viewModel.periodSummary.collectAsState()
    val loggedExercises by viewModel.loggedExercises.collectAsState()

    val context = LocalContext.current
    var showManualDialog by remember { mutableStateOf(false) }
    var selectedProgressExercise by remember { mutableStateOf<String?>(null) }
    var selectedTab by remember { mutableStateOf(0) } // 0 = Historial, 1 = Estadisticas

    Scaffold(
        containerColor = DarkBackground,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showManualDialog = true },
                containerColor = LimeGreen
            ) {
                Icon(Icons.Default.Add, contentDescription = "Marcar entrenamiento", tint = Color.Black)
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Text(
                    text = "Actividad",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Tu historial y estadisticas",
                    color = TextGray,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Tabs: Historial / Estadisticas
                ActivityTabs(
                    selected = selectedTab,
                    onSelect = { selectedTab = it }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Widget suggestion banner (solo en Historial)
            if (selectedTab == 0 && showWidgetBanner) {
                item {
                    WidgetSuggestionBanner(
                        onAdd = {
                            val appWidgetManager = AppWidgetManager.getInstance(context)
                            if (appWidgetManager.isRequestPinAppWidgetSupported) {
                                val provider = ComponentName(context, NextWorkoutWidgetReceiver::class.java)
                                appWidgetManager.requestPinAppWidget(provider, null, null)
                            }
                            viewModel.dismissWidgetBanner()
                        },
                        onDismiss = { viewModel.dismissWidgetBanner() }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            // ===== TAB HISTORIAL =====
            if (selectedTab == 0) {
            // Calendar
            item {
                CalendarCard(
                    currentMonth = currentMonth,
                    workoutDates = monthWorkouts.keys,
                    selectedDate = selectedDate,
                    onPreviousMonth = { viewModel.previousMonth() },
                    onNextMonth = { viewModel.nextMonth() },
                    onDateClick = { date -> viewModel.selectDate(date) }
                )
            }

            // Selected date detail with set logs
            if (selectedDate != null) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val dateFormat = SimpleDateFormat("d 'de' MMMM", Locale("es"))
                        Text(
                            text = dateFormat.format(selectedDate!!),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        IconButton(onClick = { viewModel.clearSelection() }) {
                            Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = TextGray)
                        }
                    }
                }

                if (selectedDateWorkouts.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = DarkCard),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "No hay entrenamientos este dia",
                                color = TextGray,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }

                items(selectedDateWorkouts) { log ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = DarkCard),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(LimeGreen.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.FitnessCenter,
                                        contentDescription = null,
                                        tint = LimeGreen,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = log.routineName,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Default.Timer,
                                            contentDescription = null,
                                            tint = TextGray,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = viewModel.formatDuration(log.durationSeconds),
                                            color = TextGray,
                                            fontSize = 12.sp
                                        )
                                        if (log.totalSets > 0) {
                                            Text(
                                                text = " · ${log.totalSets} series",
                                                color = TextGray,
                                                fontSize = 12.sp
                                            )
                                        }
                                    }
                                }
                                IconButton(onClick = { viewModel.deleteWorkout(log.id) }) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Eliminar",
                                        tint = TextGray,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            // Show set-level detail for this workout
                            val workoutSets = selectedDateSetLogs.filter { it.workoutLogId == log.id }
                            if (workoutSets.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(10.dp))
                                SetLogDetail(workoutSets)
                            }
                        }
                    }
                }
            }

            } // fin tab Historial

            // ===== TAB ESTADISTICAS =====
            if (selectedTab == 1) {
            // Period stats section
            item {
                Text(
                    text = "Resumen del periodo",
                    color = LimeGreen,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Period selector chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(7, 14, 30, 90).forEach { days ->
                        val label = when (days) {
                            7 -> "7 dias"
                            14 -> "14 dias"
                            30 -> "30 dias"
                            else -> "90 dias"
                        }
                        FilterChip(
                            selected = periodDays == days,
                            onClick = { viewModel.setPeriod(days) },
                            label = { Text(label, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = LimeGreen,
                                selectedLabelColor = Color.Black,
                                containerColor = DarkCard,
                                labelColor = TextGray
                            )
                        )
                    }
                }
            }

            // Summary stat cards
            periodSummary?.let { summary ->
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MiniStatCard(Icons.Default.FitnessCenter, "Entrenos", "${summary.workouts}", Modifier.weight(1f))
                        MiniStatCard(Icons.Default.Timer, "Tiempo", viewModel.formatDuration(summary.totalSeconds), Modifier.weight(1f))
                    }
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MiniStatCard(Icons.Default.Repeat, "Series", "${summary.totalSets}", Modifier.weight(1f))
                        MiniStatCard(Icons.Default.TrendingUp, "Volumen", viewModel.formatVolume(summary.totalVolume), Modifier.weight(1f))
                    }
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MiniStatCard(Icons.Default.Repeat, "Reps", "${summary.totalReps}", Modifier.weight(1f))
                        MiniStatCard(Icons.Default.Timer, "Prom/entreno", viewModel.formatDuration(summary.avgDurationSeconds), Modifier.weight(1f))
                    }
                }

                // Volume per day chart
                if (summary.volumePerDay.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Volumen por dia (kg)", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        VolumeBarChart(data = summary.volumePerDay)
                    }
                }

                // Exercise breakdown
                if (summary.exerciseBreakdown.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Desglose por ejercicio", color = LimeGreen, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    val maxVol = summary.exerciseBreakdown.maxOf { it.totalVolume }.coerceAtLeast(1.0)
                    items(summary.exerciseBreakdown) { item ->
                        ExerciseBreakdownCard(item, maxVol)
                    }
                }

                // Exercise progression
                if (summary.exerciseProgress.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Progresion por ejercicio", color = LimeGreen, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(8.dp))

                        // Exercise selector
                        val exercises = summary.exerciseProgress.keys.toList()
                        if (selectedProgressExercise == null || selectedProgressExercise !in exercises) {
                            selectedProgressExercise = exercises.firstOrNull()
                        }

                        ExerciseDropdown(
                            exercises = exercises,
                            selected = selectedProgressExercise ?: "",
                            onSelect = { selectedProgressExercise = it }
                        )
                    }

                    selectedProgressExercise?.let { exName ->
                        val points = summary.exerciseProgress[exName] ?: emptyList()
                        if (points.isNotEmpty()) {
                            // Max weight chart
                            item {
                                Spacer(modifier = Modifier.height(12.dp))
                                Text("Peso maximo (kg)", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                Spacer(modifier = Modifier.height(6.dp))
                                ProgressLineChart(
                                    points = points,
                                    valueSelector = { it.maxWeight },
                                    lineColor = LimeGreen,
                                    formatY = { "${it.toInt()} kg" }
                                )
                            }
                            // Volume chart
                            item {
                                Spacer(modifier = Modifier.height(12.dp))
                                Text("Volumen (kg)", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                Spacer(modifier = Modifier.height(6.dp))
                                ProgressLineChart(
                                    points = points,
                                    valueSelector = { it.totalVolume },
                                    lineColor = Color(0xFF64B5F6),
                                    formatY = { "${it.toInt()} kg" }
                                )
                            }
                            // Reps chart
                            item {
                                Spacer(modifier = Modifier.height(12.dp))
                                Text("Reps totales", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                Spacer(modifier = Modifier.height(6.dp))
                                ProgressLineChart(
                                    points = points,
                                    valueSelector = { it.totalReps.toDouble() },
                                    lineColor = Color(0xFFFF9800),
                                    formatY = { "${it.toInt()}" }
                                )
                            }
                            // Evolution summary
                            item {
                                Spacer(modifier = Modifier.height(12.dp))
                                EvolutionSummaryCard(points)
                            }
                        }
                    }
                }
            }

            } // fin tab Estadisticas

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }

    if (showManualDialog) {
        ManualWorkoutDialog(
            onDismiss = { showManualDialog = false },
            onConfirm = { name ->
                val today = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis
                viewModel.markManualWorkout(today, name)
                showManualDialog = false
            }
        )
    }
}

// ===== SET LOG DETAIL =====

@Composable
private fun SetLogDetail(sets: List<WorkoutSetLog>) {
    val byExercise = sets.groupBy { it.exerciseName }
    byExercise.forEach { (name, exerciseSets) ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSurface, RoundedCornerShape(8.dp))
                .padding(10.dp)
        ) {
            Text(name, color = LimeGreen, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(4.dp))
            exerciseSets.forEach { s ->
                val detail = buildString {
                    append("Serie ${s.setNumber}")
                    s.reps?.let { append(" · $it reps") }
                    s.weightKg?.let { if (it > 0) append(" · $it kg") }
                    s.durationSeconds?.let { append(" · ${it}s") }
                }
                Text(detail, color = TextGray, fontSize = 12.sp)
            }
            // Exercise mini summary
            val totalReps = exerciseSets.mapNotNull { it.reps }.sum()
            val maxW = exerciseSets.mapNotNull { it.weightKg }.maxOrNull()
            val vol = exerciseSets.sumOf { (it.reps ?: 0) * (it.weightKg ?: 0.0) }
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("${exerciseSets.size} series", color = TextGray.copy(alpha = 0.7f), fontSize = 11.sp)
                Text("$totalReps reps", color = TextGray.copy(alpha = 0.7f), fontSize = 11.sp)
                if (maxW != null && maxW > 0) Text("max $maxW kg", color = TextGray.copy(alpha = 0.7f), fontSize = 11.sp)
                if (vol > 0) Text("${vol.toInt()} kg vol", color = LimeGreen.copy(alpha = 0.7f), fontSize = 11.sp)
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
    }
}

// ===== STAT CARDS =====

@Composable
private fun MiniStatCard(icon: ImageVector, label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, null, tint = LimeGreen, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.height(6.dp))
            Text(value, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp, textAlign = TextAlign.Center)
            Text(label, color = TextGray, fontSize = 11.sp)
        }
    }
}

// ===== EXERCISE BREAKDOWN =====

@Composable
private fun ExerciseBreakdownCard(
    item: com.app.gimnasio.ui.viewmodel.ExerciseStats,
    maxVolume: Double
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.name,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${item.totalVolume.toInt()} kg",
                    color = LimeGreen,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("${item.totalSets} series", color = TextGray, fontSize = 12.sp)
                Text("${item.totalReps} reps", color = TextGray, fontSize = 12.sp)
                if (item.maxWeight > 0) Text("max ${item.maxWeight} kg", color = TextGray, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { (item.totalVolume / maxVolume).toFloat().coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                color = LimeGreen,
                trackColor = DarkSurface
            )
        }
    }
}

// ===== VOLUME BAR CHART =====

@Composable
private fun VolumeBarChart(data: List<Pair<Long, Double>>) {
    val maxVal = data.maxOfOrNull { it.second }?.coerceAtLeast(1.0) ?: 1.0
    val dateFormat = remember { SimpleDateFormat("dd/MM", Locale("es")) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            data.forEach { (date, value) ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(40.dp)
                ) {
                    Text("${value.toInt()}", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    val barHeight = ((value / maxVal) * 100).coerceAtLeast(4.0)
                    Box(
                        modifier = Modifier
                            .width(24.dp)
                            .height(barHeight.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(LimeGreen)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(dateFormat.format(date), color = TextGray, fontSize = 9.sp, textAlign = TextAlign.Center)
                }
            }
        }
    }
}

// ===== EXERCISE DROPDOWN =====

@Composable
private fun ExerciseDropdown(
    exercises: List<String>,
    selected: String,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        OutlinedButton(
            onClick = { expanded = true },
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = selected.ifEmpty { "Seleccionar" },
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            containerColor = DarkSurface
        ) {
            exercises.forEach { name ->
                DropdownMenuItem(
                    text = {
                        Text(
                            name,
                            color = if (name == selected) LimeGreen else Color.White,
                            fontWeight = if (name == selected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    onClick = {
                        onSelect(name)
                        expanded = false
                    }
                )
            }
        }
    }
}

// ===== LINE CHART =====

@Composable
private fun ProgressLineChart(
    points: List<ExerciseProgressPoint>,
    valueSelector: (ExerciseProgressPoint) -> Double,
    lineColor: Color,
    formatY: (Double) -> String
) {
    val values = points.map { valueSelector(it) }
    if (values.size < 2) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = formatY(values.firstOrNull() ?: 0.0) + " (1 sesion)",
                color = Color.White,
                fontSize = 14.sp,
                modifier = Modifier.padding(16.dp)
            )
        }
        return
    }

    val dateFormat = remember { SimpleDateFormat("dd/MM", Locale("es")) }
    val minY = values.min()
    val maxY = values.max()
    val rangeY = (maxY - minY).coerceAtLeast(1.0)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(formatY(maxY), color = TextGray, fontSize = 10.sp)
                Text(formatY(minY), color = TextGray, fontSize = 10.sp)
            }
            Spacer(modifier = Modifier.height(4.dp))

            Canvas(
                modifier = Modifier.fillMaxWidth().height(120.dp)
            ) {
                val w = size.width
                val h = size.height
                val pad = 8f

                val pts = values.mapIndexed { i, v ->
                    val x = pad + (w - pad * 2) * i / (values.size - 1).coerceAtLeast(1)
                    val y = h - pad - (h - pad * 2) * ((v - minY) / rangeY).toFloat()
                    Offset(x, y)
                }

                val path = Path().apply {
                    pts.forEachIndexed { i, p ->
                        if (i == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y)
                    }
                }
                drawPath(path, lineColor, style = Stroke(width = 3f))
                pts.forEach { drawCircle(lineColor, radius = 5f, center = it) }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val labels = if (points.size <= 6) points.indices.toList()
                else listOf(0, points.size / 2, points.lastIndex)
                labels.forEach { i ->
                    Text(dateFormat.format(points[i].date), color = TextGray, fontSize = 10.sp)
                }
            }
        }
    }
}

// ===== EVOLUTION SUMMARY =====

@Composable
private fun EvolutionSummaryCard(points: List<ExerciseProgressPoint>) {
    val first = points.first()
    val last = points.last()
    val weightDiff = last.maxWeight - first.maxWeight
    val volDiff = last.totalVolume - first.totalVolume

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text("Evolucion", color = LimeGreen, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Spacer(modifier = Modifier.height(8.dp))
            SummaryRow("Sesiones registradas", "${points.size}")
            SummaryRow("Peso maximo alcanzado", "${last.maxWeight} kg")
            if (points.size > 1) {
                val wSign = if (weightDiff >= 0) "+" else ""
                SummaryRow("Cambio en peso", "$wSign${String.format("%.1f", weightDiff)} kg")
                val vSign = if (volDiff >= 0) "+" else ""
                SummaryRow("Cambio en volumen", "$vSign${volDiff.toInt()} kg")
            }
            SummaryRow("Mejor volumen/sesion", "${points.maxOf { it.totalVolume }.toInt()} kg")
            SummaryRow("Max reps/sesion", "${points.maxOf { it.totalReps }}")
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = TextGray, fontSize = 13.sp)
        Text(value, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
    }
}

// ===== CALENDAR =====

@Composable
private fun CalendarCard(
    currentMonth: Calendar,
    workoutDates: Set<Long>,
    selectedDate: Long?,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onDateClick: (Long) -> Unit
) {
    val monthFormat = SimpleDateFormat("MMMM yyyy", Locale("es"))

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onPreviousMonth) {
                    Icon(Icons.Default.ChevronLeft, contentDescription = "Anterior", tint = Color.White)
                }
                Text(
                    text = monthFormat.format(currentMonth.time).replaceFirstChar { it.uppercase() },
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                IconButton(onClick = onNextMonth) {
                    Icon(Icons.Default.ChevronRight, contentDescription = "Siguiente", tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            val dayNames = listOf("LUN", "MAR", "MIE", "JUE", "VIE", "SAB", "DOM")
            Row(modifier = Modifier.fillMaxWidth()) {
                dayNames.forEach { day ->
                    Text(
                        text = day,
                        color = TextGray,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            val cal = currentMonth.clone() as Calendar
            cal.set(Calendar.DAY_OF_MONTH, 1)
            val firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
            val offset = if (firstDayOfWeek == Calendar.SUNDAY) 6 else firstDayOfWeek - Calendar.MONDAY
            val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)

            val today = Calendar.getInstance()
            val isCurrentMonth = today.get(Calendar.YEAR) == currentMonth.get(Calendar.YEAR) &&
                    today.get(Calendar.MONTH) == currentMonth.get(Calendar.MONTH)
            val todayDay = today.get(Calendar.DAY_OF_MONTH)

            val totalCells = offset + daysInMonth
            val rows = (totalCells + 6) / 7

            for (row in 0 until rows) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    for (col in 0..6) {
                        val cellIndex = row * 7 + col
                        val dayNumber = cellIndex - offset + 1

                        if (dayNumber in 1..daysInMonth) {
                            val dayCal = currentMonth.clone() as Calendar
                            dayCal.set(Calendar.DAY_OF_MONTH, dayNumber)
                            dayCal.set(Calendar.HOUR_OF_DAY, 0)
                            dayCal.set(Calendar.MINUTE, 0)
                            dayCal.set(Calendar.SECOND, 0)
                            dayCal.set(Calendar.MILLISECOND, 0)
                            val dateMillis = dayCal.timeInMillis

                            val hasWorkout = workoutDates.contains(dateMillis)
                            val isToday = isCurrentMonth && dayNumber == todayDay
                            val isSelected = selectedDate == dateMillis

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .padding(2.dp)
                                    .clip(CircleShape)
                                    .then(
                                        when {
                                            isSelected -> Modifier.background(LimeGreen)
                                            hasWorkout -> Modifier.background(LimeGreen.copy(alpha = 0.2f))
                                            isToday -> Modifier.border(1.dp, LimeGreen, CircleShape)
                                            else -> Modifier
                                        }
                                    )
                                    .clickable { onDateClick(dateMillis) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$dayNumber",
                                    color = when {
                                        isSelected -> Color.Black
                                        hasWorkout -> LimeGreen
                                        isToday -> LimeGreen
                                        else -> Color.White
                                    },
                                    fontSize = 14.sp,
                                    fontWeight = if (hasWorkout || isToday) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        } else {
                            Spacer(modifier = Modifier.weight(1f).aspectRatio(1f))
                        }
                    }
                }
            }
        }
    }
}

// ===== DIALOGS & BANNERS =====

@Composable
private fun ManualWorkoutDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var name by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkCard,
        title = { Text("Marcar entrenamiento", color = Color.White) },
        text = {
            Column {
                Text("Registrar un entrenamiento para hoy", color = TextGray, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre del entrenamiento") },
                    placeholder = { Text("Ej: Piernas, Cardio...") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LimeGreen,
                        unfocusedBorderColor = DarkBorder,
                        cursorColor = LimeGreen,
                        focusedLabelColor = LimeGreen,
                        unfocusedLabelColor = TextGray,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedPlaceholderColor = TextDarkGray,
                        unfocusedPlaceholderColor = TextDarkGray
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { if (name.isNotBlank()) onConfirm(name.trim()) },
                colors = ButtonDefaults.buttonColors(containerColor = LimeGreen),
                enabled = name.isNotBlank()
            ) { Text("Guardar", color = Color.Black) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar", color = TextGray) }
        }
    )
}

@Composable
private fun WidgetSuggestionBanner(
    onAdd: () -> Unit,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = LimeGreen.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Icon(Icons.Default.Widgets, contentDescription = null, tint = LimeGreen, modifier = Modifier.size(28.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Widget en tu pantalla", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Agregalo para ver tu proximo entrenamiento sin abrir la app", color = TextGray, fontSize = 13.sp)
                }
                IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = TextGray, modifier = Modifier.size(18.dp))
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onAdd,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = LimeGreen, contentColor = Color.Black),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Agregar widget", fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ===== TABS =====

@Composable
private fun ActivityTabs(
    selected: Int,
    onSelect: (Int) -> Unit
) {
    val tabs = listOf("Historial", "Estadisticas")
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        tabs.forEachIndexed { index, label ->
            val isSelected = selected == index
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .clickable { onSelect(index) }
                    .padding(vertical = 8.dp, horizontal = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = label,
                    color = if (isSelected) Color.White else TextGray,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .height(2.dp)
                        .width(32.dp)
                        .background(
                            if (isSelected) LimeGreen else Color.Transparent,
                            RoundedCornerShape(1.dp)
                        )
                )
            }
        }
    }
}
