package com.app.gimnasio.ui.screens

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.gimnasio.ui.theme.DarkBackground
import com.app.gimnasio.ui.theme.DarkCard
import com.app.gimnasio.ui.theme.DarkBorder
import com.app.gimnasio.ui.theme.LimeGreen
import com.app.gimnasio.ui.theme.TextGray
import com.app.gimnasio.ui.theme.TextDarkGray
import com.app.gimnasio.ui.viewmodel.ActivityViewModel
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
    val showWidgetBanner by viewModel.showWidgetBanner.collectAsState()

    val context = LocalContext.current
    var showManualDialog by remember { mutableStateOf(false) }

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
                    text = "Tu historial de entrenamientos",
                    color = TextGray,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Widget suggestion banner
            if (showWidgetBanner) {
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

            // Selected date workouts
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
                                text = "No hay entrenamientos este día",
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
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
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
                                if (log.exercisesSummary.isNotBlank() && log.exercisesSummary != log.routineName) {
                                    Text(
                                        text = log.exercisesSummary,
                                        color = TextGray,
                                        fontSize = 12.sp,
                                        maxLines = 1
                                    )
                                }
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
                    }
                }
            }

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
            // Month header with navigation
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

            // Day headers
            val dayNames = listOf("LUN", "MAR", "MIÉ", "JUE", "VIE", "SÁB", "DOM")
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

            // Calendar grid
            val cal = currentMonth.clone() as Calendar
            cal.set(Calendar.DAY_OF_MONTH, 1)
            val firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
            // Convert to Monday-based (Mon=0, Tue=1, ..., Sun=6)
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

@Composable
private fun ManualWorkoutDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var name by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkCard,
        title = {
            Text("Marcar entrenamiento", color = Color.White)
        },
        text = {
            Column {
                Text(
                    "Registrar un entrenamiento para hoy",
                    color = TextGray,
                    fontSize = 14.sp
                )
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
            ) {
                Text("Guardar", color = Color.Black)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = TextGray)
            }
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    Icons.Default.Widgets,
                    contentDescription = null,
                    tint = LimeGreen,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Widget en tu pantalla",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Agregalo para ver tu proximo entrenamiento sin abrir la app",
                        color = TextGray,
                        fontSize = 13.sp
                    )
                }
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Cerrar",
                        tint = TextGray,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onAdd,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = LimeGreen,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Agregar widget",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
