package com.app.gimnasio.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.gimnasio.data.model.MuscleGroup
import com.app.gimnasio.ui.theme.DarkBackground
import com.app.gimnasio.ui.theme.DarkCard
import com.app.gimnasio.ui.theme.LimeGreen
import com.app.gimnasio.ui.theme.TextGray
import com.app.gimnasio.ui.viewmodel.ExerciseGalleryViewModel

@Composable
fun ExerciseGalleryScreen(
    onMuscleGroupClick: (MuscleGroup) -> Unit,
    viewModel: ExerciseGalleryViewModel? = null
) {
    val counts by viewModel?.exerciseCounts?.collectAsState()
        ?: androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(emptyMap()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Ejercicios por grupo muscular",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

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
            // Muscle illustration
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF151A26)),
                contentAlignment = Alignment.Center
            ) {
                MuscleIcon(muscle = muscle, modifier = Modifier.size(44.dp))
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

@Composable
private fun MuscleIcon(muscle: MuscleGroup, modifier: Modifier = Modifier) {
    val bodyGray = Color(0xFF3A4050)
    val highlight = LimeGreen

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        drawBodyBase(w, h, bodyGray)
        drawMuscleHighlight(muscle, w, h, highlight)
    }
}

private fun DrawScope.drawBodyBase(w: Float, h: Float, color: Color) {
    // Head
    drawCircle(color, radius = w * 0.07f, center = Offset(w * 0.5f, h * 0.1f))
    // Neck
    drawRect(color, Offset(w * 0.46f, h * 0.15f), Size(w * 0.08f, h * 0.05f))
    // Torso
    drawRoundRect(color, Offset(w * 0.3f, h * 0.2f), Size(w * 0.4f, h * 0.32f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.04f))
    // Left arm
    drawRoundRect(color, Offset(w * 0.17f, h * 0.22f), Size(w * 0.12f, h * 0.28f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.04f))
    // Right arm
    drawRoundRect(color, Offset(w * 0.71f, h * 0.22f), Size(w * 0.12f, h * 0.28f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.04f))
    // Left forearm
    drawRoundRect(color, Offset(w * 0.18f, h * 0.48f), Size(w * 0.1f, h * 0.16f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.03f))
    // Right forearm
    drawRoundRect(color, Offset(w * 0.72f, h * 0.48f), Size(w * 0.1f, h * 0.16f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.03f))
    // Left leg
    drawRoundRect(color, Offset(w * 0.32f, h * 0.54f), Size(w * 0.16f, h * 0.34f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.04f))
    // Right leg
    drawRoundRect(color, Offset(w * 0.52f, h * 0.54f), Size(w * 0.16f, h * 0.34f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.04f))
    // Left calf
    drawRoundRect(color, Offset(w * 0.33f, h * 0.78f), Size(w * 0.13f, h * 0.18f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.04f))
    // Right calf
    drawRoundRect(color, Offset(w * 0.54f, h * 0.78f), Size(w * 0.13f, h * 0.18f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.04f))
}

private fun DrawScope.drawMuscleHighlight(muscle: MuscleGroup, w: Float, h: Float, color: Color) {
    when (muscle) {
        MuscleGroup.PECTORALES -> {
            // Chest area
            drawRoundRect(color, Offset(w * 0.32f, h * 0.22f), Size(w * 0.16f, h * 0.12f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.03f))
            drawRoundRect(color, Offset(w * 0.52f, h * 0.22f), Size(w * 0.16f, h * 0.12f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.03f))
        }
        MuscleGroup.HOMBROS -> {
            // Shoulders - top of arms
            drawOval(color, Offset(w * 0.22f, h * 0.19f), Size(w * 0.14f, h * 0.08f))
            drawOval(color, Offset(w * 0.64f, h * 0.19f), Size(w * 0.14f, h * 0.08f))
        }
        MuscleGroup.TRICEPS -> {
            // Back of arms
            drawRoundRect(color, Offset(w * 0.18f, h * 0.28f), Size(w * 0.1f, h * 0.16f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.03f))
            drawRoundRect(color, Offset(w * 0.72f, h * 0.28f), Size(w * 0.1f, h * 0.16f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.03f))
        }
        MuscleGroup.BICEPS -> {
            // Front of arms
            drawRoundRect(color, Offset(w * 0.18f, h * 0.24f), Size(w * 0.1f, h * 0.14f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.03f))
            drawRoundRect(color, Offset(w * 0.72f, h * 0.24f), Size(w * 0.1f, h * 0.14f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.03f))
        }
        MuscleGroup.ESPALDA -> {
            // Upper back area
            drawRoundRect(color, Offset(w * 0.33f, h * 0.22f), Size(w * 0.34f, h * 0.2f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.04f))
        }
        MuscleGroup.TRAPECIO -> {
            // Traps - between neck and shoulders
            val path = Path().apply {
                moveTo(w * 0.38f, h * 0.16f)
                lineTo(w * 0.5f, h * 0.12f)
                lineTo(w * 0.62f, h * 0.16f)
                lineTo(w * 0.68f, h * 0.22f)
                lineTo(w * 0.5f, h * 0.2f)
                lineTo(w * 0.32f, h * 0.22f)
                close()
            }
            drawPath(path, color, style = Fill)
        }
        MuscleGroup.ANTEBRAZOS -> {
            // Forearms
            drawRoundRect(color, Offset(w * 0.18f, h * 0.48f), Size(w * 0.1f, h * 0.16f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.03f))
            drawRoundRect(color, Offset(w * 0.72f, h * 0.48f), Size(w * 0.1f, h * 0.16f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.03f))
        }
        MuscleGroup.CUADRICEPS -> {
            // Front of thighs
            drawRoundRect(color, Offset(w * 0.33f, h * 0.55f), Size(w * 0.14f, h * 0.22f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.04f))
            drawRoundRect(color, Offset(w * 0.53f, h * 0.55f), Size(w * 0.14f, h * 0.22f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.04f))
        }
        MuscleGroup.ISQUIOTIBIALES -> {
            // Back of thighs
            drawRoundRect(color, Offset(w * 0.34f, h * 0.62f), Size(w * 0.12f, h * 0.18f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.04f))
            drawRoundRect(color, Offset(w * 0.54f, h * 0.62f), Size(w * 0.12f, h * 0.18f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.04f))
        }
        MuscleGroup.GLUTEOS -> {
            // Glutes
            drawOval(color, Offset(w * 0.32f, h * 0.48f), Size(w * 0.17f, h * 0.1f))
            drawOval(color, Offset(w * 0.51f, h * 0.48f), Size(w * 0.17f, h * 0.1f))
        }
        MuscleGroup.ABDUCTORES -> {
            // Outer thighs
            drawRoundRect(color, Offset(w * 0.3f, h * 0.56f), Size(w * 0.06f, h * 0.2f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.02f))
            drawRoundRect(color, Offset(w * 0.64f, h * 0.56f), Size(w * 0.06f, h * 0.2f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.02f))
        }
        MuscleGroup.ADUCTORES -> {
            // Inner thighs
            drawRoundRect(color, Offset(w * 0.43f, h * 0.56f), Size(w * 0.06f, h * 0.2f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.02f))
            drawRoundRect(color, Offset(w * 0.51f, h * 0.56f), Size(w * 0.06f, h * 0.2f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.02f))
        }
        MuscleGroup.GEMELOS -> {
            // Calves
            drawRoundRect(color, Offset(w * 0.34f, h * 0.8f), Size(w * 0.11f, h * 0.14f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.03f))
            drawRoundRect(color, Offset(w * 0.55f, h * 0.8f), Size(w * 0.11f, h * 0.14f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.03f))
        }
        MuscleGroup.ABDOMINALES -> {
            // Abs - center of torso
            drawRoundRect(color, Offset(w * 0.4f, h * 0.32f), Size(w * 0.2f, h * 0.18f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.03f))
        }
        MuscleGroup.LUMBARES -> {
            // Lower back
            drawRoundRect(color, Offset(w * 0.36f, h * 0.4f), Size(w * 0.28f, h * 0.1f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.03f))
        }
    }
}
