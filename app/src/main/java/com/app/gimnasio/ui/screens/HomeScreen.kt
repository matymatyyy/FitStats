package com.app.gimnasio.ui.screens

import android.graphics.BitmapFactory
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
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
import com.app.gimnasio.ui.viewmodel.NextWorkoutInfo
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
    weeklyCount: Int = 0,
    weeklyGoal: Int = 5,
    userName: String = "",
    userPhotoPath: String? = null,
    weeklyStats: WeeklyStats = WeeklyStats(),
    nextWorkout: NextWorkoutInfo? = null,
    hasPlan: Boolean = false,
    showInfoCard: Boolean = false,
    onDismissInfoCard: () -> Unit = {}
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

        // Weekly stats
        WeeklyStatsSection(stats = weeklyStats)

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
            onNavigateToTimer = { showSettingsSheet = false; onNavigateToTimer() }
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
                Icons.Default.Tune,
                contentDescription = "Ajustes",
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
            // Gym background gradient
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

            // Gym icon as placeholder for image
            Icon(
                Icons.Default.FitnessCenter,
                contentDescription = null,
                modifier = Modifier
                    .size(120.dp)
                    .align(Alignment.Center),
                tint = Color(0xFF2A3040)
            )

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

@Composable
private fun WeeklyStatsSection(stats: WeeklyStats) {
    Text(
        text = "Últimos 7 días",
        color = Color.White,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp
    )

    Spacer(modifier = Modifier.height(12.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        StatItem(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.FitnessCenter,
            value = "${stats.workouts}",
            label = "Entrenos"
        )
        StatItem(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.Timer,
            value = formatVolume(stats.totalSeconds),
            label = "Volumen"
        )
        StatItem(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.CheckCircle,
            value = "${stats.totalSets}",
            label = "Series"
        )
        StatItem(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.LocalFireDepartment,
            value = "${stats.calories}",
            label = "kcal"
        )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsBottomSheet(
    onDismiss: () -> Unit,
    onNavigateToRoutines: () -> Unit,
    onNavigateToPlan: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToActivity: () -> Unit,
    onNavigateToExercises: () -> Unit,
    onNavigateToTimer: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(),
        containerColor = DarkSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
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

