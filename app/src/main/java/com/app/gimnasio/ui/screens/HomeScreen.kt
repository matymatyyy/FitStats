package com.app.gimnasio.ui.screens

import android.Manifest
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.gimnasio.ui.theme.DarkCard
import com.app.gimnasio.ui.theme.DarkBorder
import com.app.gimnasio.ui.theme.DarkSurface
import com.app.gimnasio.ui.theme.LimeGreen
import com.app.gimnasio.ui.theme.TextGray
import com.app.gimnasio.ui.theme.TextDarkGray
import com.app.gimnasio.notification.ReminderScheduler
import com.app.gimnasio.ui.viewmodel.NextWorkoutInfo
import com.app.gimnasio.widget.NextWorkoutWidgetReceiver
import com.app.gimnasio.widget.WeeklyStatsWidgetReceiver
import java.io.File

data class WeeklyStats(
    val workouts: Int = 0,
    val totalSeconds: Int = 0,
    val totalSets: Int = 0
) {
    val volumeHours: Double get() = totalSeconds / 3600.0
    val calories: Int get() = (volumeHours * 300).toInt()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToRoutines: () -> Unit,
    onNavigateToTimer: () -> Unit,
    onNavigateToPlan: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToActivity: () -> Unit = {},
    onNavigateToExercises: () -> Unit = {},
    onStartWorkout: (Long) -> Unit = {},
    onNavigateToPRCalculator: () -> Unit = {},
    weeklyCount: Int = 0,
    weeklyGoal: Int = 5,
    userName: String = "",
    userPhotoPath: String? = null,
    weeklyStats: WeeklyStats = WeeklyStats(),
    nextWorkout: NextWorkoutInfo? = null,
    hasPlan: Boolean = false,
    showInfoCard: Boolean = false,
    onDismissInfoCard: () -> Unit = {},
    periodDays: Int = 7,
    periodWorkouts: Int = 0,
    periodTotalSeconds: Int = 0,
    periodTotalSets: Int = 0,
    dailyCalories: List<Pair<String, Int>> = emptyList(),
    onPeriodChange: (Int) -> Unit = {},
    hasActiveWorkout: Boolean = false,
    onResumeWorkout: () -> Unit = {},
    onDiscardWorkout: () -> Unit = {}
) {
    var showSettingsSheet by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Top bar
        TopProfileBar(
            userName = userName,
            photoPath = userPhotoPath,
            onSettingsClick = { showSettingsSheet = true },
            onProfileClick = onNavigateToProfile
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Resume workout banner
        if (hasActiveWorkout) {
            ResumeWorkoutBanner(
                onResume = onResumeWorkout,
                onDiscard = onDiscardWorkout
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Info card
        if (showInfoCard) {
            InfoCard(onDismiss = onDismissInfoCard)
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Esta Semana section
        WeekSection(completedCount = weeklyCount, goal = weeklyGoal)

        Spacer(modifier = Modifier.height(16.dp))

        // Next workout card
        NextWorkoutCard(
            nextWorkout = nextWorkout,
            hasPlan = hasPlan,
            onClick = {
                if (nextWorkout != null) {
                    onStartWorkout(nextWorkout.routineId)
                } else {
                    onNavigateToPlan()
                }
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Small cards row: Rutinas + Planificación
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SmallActionCard(
                modifier = Modifier.weight(1f),
                title = "Rutinas",
                icon = Icons.Default.FitnessCenter,
                onClick = onNavigateToRoutines
            )
            SmallActionCard(
                modifier = Modifier.weight(1f),
                title = "Planificación",
                icon = Icons.Default.CalendarMonth,
                onClick = onNavigateToPlan
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Period stats with selector
        PeriodStatsSection(
            periodDays = periodDays,
            workouts = periodWorkouts,
            totalSeconds = periodTotalSeconds,
            totalSets = periodTotalSets,
            dailyCalories = dailyCalories,
            onPeriodChange = onPeriodChange
        )

        Spacer(modifier = Modifier.height(100.dp)) // space for bottom nav
    }

    // Settings bottom sheet
    if (showSettingsSheet) {
        SettingsBottomSheet(
            onDismiss = { showSettingsSheet = false },
            onNavigateToRoutines = { showSettingsSheet = false; onNavigateToRoutines() },
            onNavigateToPlan = { showSettingsSheet = false; onNavigateToPlan() },
            onNavigateToProfile = { showSettingsSheet = false; onNavigateToProfile() },
            onNavigateToActivity = { showSettingsSheet = false; onNavigateToActivity() },
            onNavigateToExercises = { showSettingsSheet = false; onNavigateToExercises() },
            onNavigateToTimer = { showSettingsSheet = false; onNavigateToTimer() },
            onNavigateToPRCalculator = { showSettingsSheet = false; onNavigateToPRCalculator() }
        )
    }
}

@Composable
private fun TopProfileBar(
    userName: String,
    photoPath: String?,
    onSettingsClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    val greeting = if (userName.isNotBlank()) "Hola, $userName" else "Hola"
    val initial = userName.firstOrNull()?.uppercase() ?: "G"

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Profile avatar
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF3B82F6))
                .clickable { onProfileClick() },
            contentAlignment = Alignment.Center
        ) {
            if (photoPath != null && File(photoPath).exists()) {
                val bitmap = remember(photoPath) {
                    BitmapFactory.decodeFile(photoPath)
                }
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Foto de perfil",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
            } else {
                Text(
                    text = initial,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = greeting,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.weight(1f)
        )

        IconButton(onClick = onSettingsClick) {
            Icon(
                Icons.Default.Menu,
                contentDescription = "Menú",
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
private fun InfoCard(onDismiss: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Cómo funciona",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "FitStats es tu anotador de gimnasio. Armá rutinas, registrá tus entrenamientos, llevá el control de tus medidas y records personales.",
                    color = TextGray,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }
            IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Cerrar",
                    tint = TextGray,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun WeekSection(completedCount: Int, goal: Int) {
    Text(
        text = "Esta Semana",
        color = Color.White,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp
    )

    Spacer(modifier = Modifier.height(12.dp))

    // Weekly progress bars
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        repeat(goal) { index ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(if (index < completedCount) LimeGreen else TextDarkGray)
            )
        }
    }

    Spacer(modifier = Modifier.height(8.dp))

    Text(
        text = "$completedCount de $goal completados esta semana",
        color = TextGray,
        fontSize = 14.sp
    )
}

@Composable
private fun NextWorkoutCard(
    nextWorkout: NextWorkoutInfo?,
    hasPlan: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1F2E))
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Background: routine image or gradient fallback
            val routineImagePath = nextWorkout?.imagePath
            val routineImageFile = routineImagePath?.let { File(it) }
            val hasImage = routineImageFile?.exists() == true

            if (hasImage) {
                val bitmap = remember(routineImagePath) {
                    BitmapFactory.decodeFile(routineImageFile!!.absolutePath)
                }
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    // Dark overlay for text readability
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0x40000000),
                                        Color(0x80000000),
                                        Color(0xCC000000)
                                    )
                                )
                            )
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF2A3040),
                                    Color(0xFF1A1F2E),
                                    Color(0xFF0D1117)
                                )
                            )
                        )
                )

                // Gym icon as placeholder
                Icon(
                    Icons.Default.FitnessCenter,
                    contentDescription = null,
                    modifier = Modifier
                        .size(120.dp)
                        .align(Alignment.Center),
                    tint = Color(0xFF2A3040)
                )
            }

            // Top badges
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Badge
                Box(
                    modifier = Modifier
                        .border(
                            width = 1.5.dp,
                            color = LimeGreen,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "PRÓXIMO ENTRENAMIENTO",
                        color = LimeGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                if (nextWorkout != null) {
                    // Day label badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0x80000000))
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = nextWorkout.dayLabel,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Bottom text
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                if (nextWorkout != null) {
                    Text(
                        text = nextWorkout.routineName.uppercase(),
                        color = LimeGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    if (nextWorkout.description.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = nextWorkout.description,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            lineHeight = 28.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Tocá para empezar",
                        color = TextGray,
                        fontSize = 13.sp
                    )
                } else if (!hasPlan) {
                    Text(
                        text = "SIN PLANIFICACIÓN",
                        color = LimeGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Creá tu\nplanificación",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        lineHeight = 28.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Tocá para configurar tus días de entrenamiento",
                        color = TextGray,
                        fontSize = 13.sp
                    )
                } else {
                    Text(
                        text = "DESCANSO",
                        color = LimeGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Día de\ndescanso",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        lineHeight = 28.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun SmallActionCard(
    modifier: Modifier = Modifier,
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = LimeGreen,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = title,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = TextGray,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PeriodStatsSection(
    periodDays: Int,
    workouts: Int,
    totalSeconds: Int,
    totalSets: Int,
    dailyCalories: List<Pair<String, Int>>,
    onPeriodChange: (Int) -> Unit
) {
    val periodLabel = when (periodDays) {
        7 -> "Últimos 7 días"
        14 -> "Últimos 14 días"
        30 -> "Últimos 30 días"
        else -> "Últimos $periodDays días"
    }
    val calories = (totalSeconds / 3600.0 * 300).toInt()

    Text(
        text = periodLabel,
        color = Color.White,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp
    )

    Spacer(modifier = Modifier.height(8.dp))

    // Period selector chips
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        listOf(7, 14, 30).forEach { days ->
            FilterChip(
                selected = periodDays == days,
                onClick = { onPeriodChange(days) },
                label = { Text("${days}d") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = LimeGreen.copy(alpha = 0.2f),
                    selectedLabelColor = LimeGreen,
                    containerColor = DarkCard,
                    labelColor = TextGray
                )
            )
        }
    }

    Spacer(modifier = Modifier.height(12.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        StatItem(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.FitnessCenter,
            value = "$workouts",
            label = "Entrenos"
        )
        StatItem(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.Timer,
            value = formatVolume(totalSeconds),
            label = "Volumen"
        )
        StatItem(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.CheckCircle,
            value = "$totalSets",
            label = "Series"
        )
        StatItem(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.LocalFireDepartment,
            value = "$calories",
            label = "kcal"
        )
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Calories chart
    CaloriesChart(dailyCalories = dailyCalories)
}

@Composable
private fun CaloriesChart(dailyCalories: List<Pair<String, Int>>) {
    if (dailyCalories.isEmpty()) return

    val maxCal = (dailyCalories.maxOfOrNull { it.second } ?: 1).coerceAtLeast(1)
    val totalCal = dailyCalories.sumOf { it.second }
    val limeGreen = LimeGreen

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.LocalFireDepartment,
                        contentDescription = null,
                        tint = LimeGreen,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Calorías quemadas",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
                Text(
                    text = "$totalCal kcal",
                    color = LimeGreen,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Bar chart
            val animProgress by animateFloatAsState(
                targetValue = 1f,
                animationSpec = tween(600),
                label = "chartAnim"
            )

            // Determine how many labels to show to avoid overlap
            val totalBars = dailyCalories.size
            val labelStep = when {
                totalBars <= 7 -> 1
                totalBars <= 14 -> 2
                else -> 5
            }

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
            ) {
                val barCount = dailyCalories.size
                val spacing = 2.dp.toPx()
                val totalSpacing = spacing * (barCount - 1)
                val barWidth = ((size.width - totalSpacing) / barCount).coerceAtLeast(2f)
                val cornerRad = CornerRadius(4.dp.toPx(), 4.dp.toPx())

                dailyCalories.forEachIndexed { index, (_, cal) ->
                    val fraction = (cal.toFloat() / maxCal) * animProgress
                    val barHeight = (fraction * size.height * 0.85f).coerceAtLeast(if (cal > 0) 4f else 0f)
                    val x = index * (barWidth + spacing)
                    val y = size.height - barHeight

                    // Bar background (subtle)
                    drawRoundRect(
                        color = limeGreen.copy(alpha = 0.08f),
                        topLeft = Offset(x, size.height * 0.15f),
                        size = Size(barWidth, size.height * 0.85f),
                        cornerRadius = cornerRad
                    )

                    // Actual bar
                    if (cal > 0) {
                        drawRoundRect(
                            color = limeGreen.copy(alpha = 0.5f + 0.5f * fraction),
                            topLeft = Offset(x, y),
                            size = Size(barWidth, barHeight),
                            cornerRadius = cornerRad
                        )
                    }
                }
            }

            // Day labels
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                dailyCalories.forEachIndexed { index, (label, _) ->
                    if (index % labelStep == 0) {
                        Text(
                            text = label,
                            color = TextGray,
                            fontSize = 9.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.width(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatItem(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    value: String,
    label: String
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = LimeGreen,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                textAlign = TextAlign.Center
            )
            Text(
                text = label,
                color = TextGray,
                fontSize = 11.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun SettingsBottomSheet(
    onDismiss: () -> Unit,
    onNavigateToRoutines: () -> Unit,
    onNavigateToPlan: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToActivity: () -> Unit,
    onNavigateToExercises: () -> Unit,
    onNavigateToTimer: () -> Unit,
    onNavigateToPRCalculator: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(),
        containerColor = DarkSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = "Accesos rápidos",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(16.dp))

            SettingsNavItem(
                icon = Icons.Default.FitnessCenter,
                title = "Mis Rutinas",
                subtitle = "Crear y gestionar rutinas",
                onClick = onNavigateToRoutines
            )
            SettingsNavItem(
                icon = Icons.Default.CalendarMonth,
                title = "Planificación",
                subtitle = "Organizar días de entrenamiento",
                onClick = onNavigateToPlan
            )
            SettingsNavItem(
                icon = Icons.Default.Person,
                title = "Perfil",
                subtitle = "Datos personales y medidas",
                onClick = onNavigateToProfile
            )
            SettingsNavItem(
                icon = Icons.Default.CalendarMonth,
                title = "Actividad",
                subtitle = "Historial de entrenamientos",
                onClick = onNavigateToActivity
            )
            SettingsNavItem(
                icon = Icons.AutoMirrored.Filled.List,
                title = "Ejercicios",
                subtitle = "Galería de ejercicios por músculo",
                onClick = onNavigateToExercises
            )
            SettingsNavItem(
                icon = Icons.Default.Timer,
                title = "Temporizador",
                subtitle = "Timer de descanso entre series",
                onClick = onNavigateToTimer
            )
            SettingsNavItem(
                icon = Icons.Default.Calculate,
                title = "Calculadora de PR",
                subtitle = "Estimá tu 1RM desde peso y reps",
                onClick = onNavigateToPRCalculator
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = DarkBorder
            )

            Text(
                text = "Configuración",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Widgets section
            val context = LocalContext.current
            val appWidgetManager = remember { AppWidgetManager.getInstance(context) }

            SettingsNavItem(
                icon = Icons.Default.Widgets,
                title = "Widget: Próximo Entreno",
                subtitle = "Agregar a la pantalla de inicio",
                onClick = {
                    if (appWidgetManager.isRequestPinAppWidgetSupported) {
                        val provider = ComponentName(context, NextWorkoutWidgetReceiver::class.java)
                        appWidgetManager.requestPinAppWidget(provider, null, null)
                    }
                }
            )
            SettingsNavItem(
                icon = Icons.Default.Widgets,
                title = "Widget: Esta Semana",
                subtitle = "Agregar resumen semanal",
                onClick = {
                    if (appWidgetManager.isRequestPinAppWidgetSupported) {
                        val provider = ComponentName(context, WeeklyStatsWidgetReceiver::class.java)
                        appWidgetManager.requestPinAppWidget(provider, null, null)
                    }
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Notification reminder toggle
            val prefs = remember { context.getSharedPreferences("fitstats_prefs", android.content.Context.MODE_PRIVATE) }
            var reminderEnabled by remember { mutableStateOf(prefs.getBoolean("daily_reminder_enabled", false)) }
            var reminderHour by remember { mutableIntStateOf(prefs.getInt("reminder_hour", 8)) }
            var reminderMinute by remember { mutableIntStateOf(prefs.getInt("reminder_minute", 0)) }

            // Notification permission launcher
            val permissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission()
            ) { granted ->
                if (granted) {
                    reminderEnabled = true
                    ReminderScheduler.schedule(context, reminderHour, reminderMinute)
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(DarkCard),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Notifications,
                        contentDescription = null,
                        tint = LimeGreen,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Recordatorio diario",
                        color = Color.White,
                        fontWeight = FontWeight.Medium,
                        fontSize = 15.sp
                    )
                    Text(
                        text = if (reminderEnabled) "Todos los días a las ${"%02d:%02d".format(reminderHour, reminderMinute)}" else "Desactivado",
                        color = TextGray,
                        fontSize = 12.sp
                    )
                }
                Switch(
                    checked = reminderEnabled,
                    onCheckedChange = { enabled ->
                        if (enabled) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                                ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
                            ) {
                                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            } else {
                                reminderEnabled = true
                                ReminderScheduler.schedule(context, reminderHour, reminderMinute)
                            }
                        } else {
                            reminderEnabled = false
                            ReminderScheduler.cancel(context)
                        }
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = LimeGreen,
                        uncheckedThumbColor = TextGray,
                        uncheckedTrackColor = DarkCard
                    )
                )
            }

            // Time picker for reminder
            if (reminderEnabled) {
                val presetHours = listOf(7 to 0, 8 to 0, 9 to 0, 10 to 0, 18 to 0, 20 to 0)
                val isPreset = presetHours.any { (h, m) -> h == reminderHour && m == reminderMinute }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 48.dp, top = 4.dp, bottom = 4.dp)
                ) {
                    Text("Hora:", color = TextGray, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        presetHours.forEach { (h, m) ->
                            val selected = reminderHour == h && reminderMinute == m
                            FilterChip(
                                selected = selected,
                                onClick = {
                                    reminderHour = h
                                    reminderMinute = m
                                    ReminderScheduler.schedule(context, h, m)
                                },
                                label = { Text("%02d:%02d".format(h, m)) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = LimeGreen.copy(alpha = 0.2f),
                                    selectedLabelColor = LimeGreen,
                                    containerColor = DarkCard,
                                    labelColor = TextGray
                                )
                            )
                        }
                        FilterChip(
                            selected = !isPreset,
                            onClick = {
                                android.app.TimePickerDialog(
                                    context,
                                    { _, h, m ->
                                        reminderHour = h
                                        reminderMinute = m
                                        ReminderScheduler.schedule(context, h, m)
                                    },
                                    reminderHour,
                                    reminderMinute,
                                    true
                                ).show()
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Schedule,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            label = {
                                Text(
                                    if (!isPreset) "Personalizada · %02d:%02d".format(reminderHour, reminderMinute)
                                    else "Personalizada"
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = LimeGreen.copy(alpha = 0.2f),
                                selectedLabelColor = LimeGreen,
                                selectedLeadingIconColor = LimeGreen,
                                containerColor = DarkCard,
                                labelColor = TextGray,
                                iconColor = TextGray
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "El objetivo semanal se ajusta automáticamente según tu planificación.",
                color = TextGray,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
private fun SettingsNavItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(vertical = 10.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(DarkCard),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = LimeGreen,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = Color.White,
                fontWeight = FontWeight.Medium,
                fontSize = 15.sp
            )
            Text(
                text = subtitle,
                color = TextGray,
                fontSize = 12.sp
            )
        }
        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = TextDarkGray,
            modifier = Modifier.size(18.dp)
        )
    }
}

private fun formatVolume(totalSeconds: Int): String {
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    return if (hours > 0) "${hours}h${minutes}m" else "${minutes}m"
}

@Composable
private fun ResumeWorkoutBanner(
    onResume: () -> Unit,
    onDiscard: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = LimeGreen.copy(alpha = 0.15f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.FitnessCenter,
                contentDescription = null,
                tint = LimeGreen,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Entrenamiento en curso",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Text(
                    text = "Tenés una rutina sin terminar",
                    color = TextGray,
                    fontSize = 13.sp
                )
            }
            IconButton(onClick = onDiscard, modifier = Modifier.size(32.dp)) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Descartar",
                    tint = TextGray,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Button(
                onClick = onResume,
                colors = ButtonDefaults.buttonColors(containerColor = LimeGreen),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Icon(
                    Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Retomar", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }
    }
}

