package com.app.gimnasio.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Group
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.gimnasio.R
import com.app.gimnasio.data.local.CommunityPlan
import com.app.gimnasio.data.local.CommunityPlans
import com.app.gimnasio.data.local.CommunityRoutine
import com.app.gimnasio.data.local.CommunityRoutines
import com.app.gimnasio.data.model.Exercise
import com.app.gimnasio.data.model.ExercisePhase
import com.app.gimnasio.data.model.MuscleGroup
import com.app.gimnasio.ui.theme.DarkBackground
import com.app.gimnasio.ui.theme.DarkCard
import com.app.gimnasio.ui.theme.DarkSurface
import com.app.gimnasio.ui.theme.LimeGreen
import com.app.gimnasio.ui.theme.TextGray
import com.app.gimnasio.ui.viewmodel.ExerciseGalleryViewModel

@Composable
fun ExerciseGalleryScreen(
    onMuscleGroupClick: (MuscleGroup) -> Unit,
    onImportRoutine: (name: String, description: String, exercises: List<Exercise>) -> Unit = { _, _, _ -> },
    onImportPlan: (plan: CommunityPlan) -> Unit = {},
    viewModel: ExerciseGalleryViewModel? = null
) {
    val counts by viewModel?.exerciseCounts?.collectAsState()
        ?: remember { mutableStateOf(emptyMap()) }

    var selectedTab by remember { mutableStateOf(0) }
    var selectedCommunityRoutine by remember { mutableStateOf<CommunityRoutine?>(null) }
    var selectedCommunityPlan by remember { mutableStateOf<CommunityPlan?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Ejercicios",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        GalleryTabs(
            selected = selectedTab,
            onSelect = { selectedTab = it }
        )

        Spacer(modifier = Modifier.height(12.dp))

        when (selectedTab) {
            0 -> MuscleGroupsList(counts = counts, onMuscleGroupClick = onMuscleGroupClick)
            1 -> CommunityRoutinesList(
                routines = CommunityRoutines.getAll(),
                onRoutineClick = { selectedCommunityRoutine = it }
            )
            2 -> CommunityPlansList(
                plans = CommunityPlans.getAll(),
                onPlanClick = { selectedCommunityPlan = it }
            )
        }
    }

    selectedCommunityRoutine?.let { routine ->
        CommunityRoutineSheet(
            routine = routine,
            onDismiss = { selectedCommunityRoutine = null },
            onImport = {
                onImportRoutine(routine.name, routine.description, routine.exercises)
                selectedCommunityRoutine = null
            }
        )
    }

    selectedCommunityPlan?.let { plan ->
        CommunityPlanSheet(
            plan = plan,
            onDismiss = { selectedCommunityPlan = null },
            onImport = {
                onImportPlan(plan)
                selectedCommunityPlan = null
            }
        )
    }
}

@Composable
private fun GalleryTabs(selected: Int, onSelect: (Int) -> Unit) {
    val tabs = listOf("Ejercicios", "Rutinas", "Planes")
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(20.dp)
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
                    fontSize = 15.sp
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

@Composable
private fun MuscleGroupsList(
    counts: Map<String, Int>,
    onMuscleGroupClick: (MuscleGroup) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(MuscleGroup.entries) { muscle ->
            val count = counts[muscle.name] ?: 0
            MuscleGroupCard(
                muscle = muscle,
                exerciseCount = count,
                onClick = { onMuscleGroupClick(muscle) }
            )
        }
        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
private fun CommunityRoutinesList(
    routines: List<CommunityRoutine>,
    onRoutineClick: (CommunityRoutine) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(LimeGreen.copy(alpha = 0.12f))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Group,
                    contentDescription = null,
                    tint = LimeGreen,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Probá rutinas armadas por la comunidad e importalas a tus rutinas.",
                    color = Color.White,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            }
        }
        items(routines) { routine ->
            CommunityRoutineCard(routine = routine, onClick = { onRoutineClick(routine) })
        }
        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
private fun CommunityRoutineCard(routine: CommunityRoutine, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(LimeGreen.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.FitnessCenter,
                        contentDescription = null,
                        tint = LimeGreen,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = routine.name,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = routine.author,
                        color = TextGray,
                        fontSize = 11.sp
                    )
                }
                Text(
                    text = "${routine.exercises.size} ejercicios",
                    color = LimeGreen,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = routine.description,
                color = TextGray,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                TagChip(
                    icon = Icons.AutoMirrored.Filled.TrendingUp,
                    text = routine.level,
                    tint = LimeGreen
                )
                TagChip(
                    icon = Icons.Default.Bolt,
                    text = routine.focus,
                    tint = Color(0xFFFF9800)
                )
            }
        }
    }
}

@Composable
private fun TagChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    tint: Color
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(tint.copy(alpha = 0.12f))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(12.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = text, color = tint, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun CommunityPlansList(
    plans: List<CommunityPlan>,
    onPlanClick: (CommunityPlan) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(LimeGreen.copy(alpha = 0.12f))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = LimeGreen,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Descargá un plan semanal completo — importa todas las rutinas de una sola vez.",
                    color = Color.White,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            }
        }
        items(plans) { plan ->
            CommunityPlanCard(plan = plan, onClick = { onPlanClick(plan) })
        }
        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
private fun CommunityPlanCard(plan: CommunityPlan, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(LimeGreen.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = LimeGreen,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = plan.name,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = plan.author,
                        color = TextGray,
                        fontSize = 11.sp
                    )
                }
                Text(
                    text = "${plan.daysPerWeek} días",
                    color = LimeGreen,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = plan.description,
                color = TextGray,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            TagChip(
                icon = Icons.AutoMirrored.Filled.TrendingUp,
                text = plan.level,
                tint = LimeGreen
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CommunityPlanSheet(
    plan: CommunityPlan,
    onDismiss: () -> Unit,
    onImport: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = DarkSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp)
        ) {
            Text(
                text = plan.name,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            )
            Text(
                text = "por ${plan.author}",
                color = TextGray,
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = plan.description,
                color = Color.White,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                TagChip(
                    icon = Icons.AutoMirrored.Filled.TrendingUp,
                    text = plan.level,
                    tint = LimeGreen
                )
                TagChip(
                    icon = Icons.Default.CalendarMonth,
                    text = "${plan.daysPerWeek} días/semana",
                    tint = Color(0xFFFF9800)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Cronograma semanal",
                color = TextGray,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))

            val uniqueRoutines = plan.days.map { it.routine }.distinctBy { it.name }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 260.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                (1..7).forEach { dow ->
                    val entry = plan.days.find { it.dayOfWeek == dow }
                    PlanDayRow(
                        dayName = com.app.gimnasio.data.local.CommunityPlans.dayLabel(dow),
                        routineName = entry?.routine?.name
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Se importarán ${uniqueRoutines.size} rutinas",
                color = TextGray,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onImport,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = LimeGreen,
                    contentColor = Color.Black
                )
            ) {
                Text("Descargar plan", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

@Composable
private fun PlanDayRow(dayName: String, routineName: String?) {
    val isRest = routineName == null
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(if (isRest) DarkCard.copy(alpha = 0.5f) else DarkCard)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = dayName,
            color = if (isRest) TextGray else Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            modifier = Modifier.width(90.dp)
        )
        Text(
            text = routineName ?: "Descanso",
            color = if (isRest) TextGray else LimeGreen,
            fontSize = 13.sp,
            fontWeight = if (isRest) FontWeight.Normal else FontWeight.Medium
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun CommunityRoutineSheet(
    routine: CommunityRoutine,
    onDismiss: () -> Unit,
    onImport: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = DarkSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp)
        ) {
            Text(
                text = routine.name,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            )
            Text(
                text = "por ${routine.author}",
                color = TextGray,
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = routine.description,
                color = Color.White,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                TagChip(
                    icon = Icons.AutoMirrored.Filled.TrendingUp,
                    text = routine.level,
                    tint = LimeGreen
                )
                TagChip(
                    icon = Icons.Default.Bolt,
                    text = routine.focus,
                    tint = Color(0xFFFF9800)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Ejercicios",
                color = TextGray,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 340.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                routine.exercises.forEachIndexed { index, ex ->
                    ExercisePreviewRow(index = index + 1, exercise = ex)
                    if (index < routine.exercises.lastIndex) {
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = onImport,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = LimeGreen,
                    contentColor = Color.Black
                )
            ) {
                Text("Agregar a mis rutinas", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

@Composable
private fun ExercisePreviewRow(index: Int, exercise: Exercise) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(DarkCard)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(LimeGreen.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$index",
                color = LimeGreen,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = exercise.name,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Text(
                text = exerciseDetailText(exercise),
                color = TextGray,
                fontSize = 12.sp
            )
        }
    }
}

private fun exerciseDetailText(ex: Exercise): String = when (ex.phase) {
    ExercisePhase.WARMUP -> ex.durationSeconds?.let { "Calentamiento · ${it}s" }
        ?: ex.reps?.let { "Calentamiento · $it reps" }
        ?: "Calentamiento"
    ExercisePhase.STRENGTH -> buildString {
        ex.sets?.let { append("$it series") }
        ex.strengthReps?.let {
            if (isNotEmpty()) append(" · ")
            append("$it reps")
        }
        ex.restSeconds?.let {
            if (isNotEmpty()) append(" · ")
            append("${it}s desc.")
        }
    }
}

@Composable
private fun MuscleGroupCard(
    muscle: MuscleGroup,
    exerciseCount: Int,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF151A26)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = muscleImageRes(muscle)),
                    contentDescription = muscle.displayName,
                    modifier = Modifier.size(44.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    text = muscle.displayName,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    text = "$exerciseCount ejercicios",
                    color = TextGray,
                    fontSize = 12.sp
                )
            }
        }
    }
}

private fun muscleImageRes(muscle: MuscleGroup): Int = when (muscle) {
    MuscleGroup.PECTORALES -> R.drawable.muscle_pectorales
    MuscleGroup.HOMBROS -> R.drawable.muscle_hombros
    MuscleGroup.TRICEPS -> R.drawable.muscle_triceps
    MuscleGroup.ESPALDA -> R.drawable.muscle_espalda
    MuscleGroup.BICEPS -> R.drawable.muscle_biceps
    MuscleGroup.TRAPECIO -> R.drawable.muscle_trapecio
    MuscleGroup.ANTEBRAZOS -> R.drawable.muscle_antebrazos
    MuscleGroup.CUADRICEPS -> R.drawable.muscle_cuadriceps
    MuscleGroup.ISQUIOTIBIALES -> R.drawable.muscle_isquiotibiales
    MuscleGroup.GLUTEOS -> R.drawable.muscle_gluteos
    MuscleGroup.ABDUCTORES -> R.drawable.muscle_abductores
    MuscleGroup.ADUCTORES -> R.drawable.muscle_aductores
    MuscleGroup.GEMELOS -> R.drawable.muscle_gemelos
    MuscleGroup.ABDOMINALES -> R.drawable.muscle_abdominales
    MuscleGroup.LUMBARES -> R.drawable.muscle_lumbares
}
