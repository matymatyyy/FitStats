package com.app.gimnasio.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.gimnasio.ui.theme.DarkBackground
import com.app.gimnasio.ui.theme.DarkBorder
import com.app.gimnasio.ui.theme.DarkCard
import com.app.gimnasio.ui.theme.DarkSurface
import com.app.gimnasio.ui.theme.LimeGreen
import com.app.gimnasio.ui.theme.TextGray
import com.app.gimnasio.ui.viewmodel.RestTimerViewModel

private val presetTimes = listOf(30, 45, 60, 75, 90, 120, 150, 180, 240, 300)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun RestTimerScreen(
    viewModel: RestTimerViewModel,
    onBack: () -> Unit
) {
    val secondsRemaining by viewModel.secondsRemaining.collectAsState()
    val isRunning by viewModel.isRunning.collectAsState()
    val selectedSeconds by viewModel.selectedSeconds.collectAsState()

    val finished = secondsRemaining == 0 && !isRunning && selectedSeconds > 0
    val displaySeconds = if (finished) selectedSeconds else secondsRemaining
    val progress = if (selectedSeconds > 0) secondsRemaining.toFloat() / selectedSeconds else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(600),
        label = "progress"
    )

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Timer de Descanso",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkSurface,
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            TimerStatusBadge(
                isRunning = isRunning,
                finished = finished
            )

            Spacer(modifier = Modifier.height(20.dp))

            TimerRing(
                progress = animatedProgress,
                timeText = viewModel.formatTime(displaySeconds),
                selectedText = viewModel.formatTime(selectedSeconds),
                finished = finished
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Quick adjust buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                AdjustButton(
                    label = "-15s",
                    enabled = !isRunning && selectedSeconds > 15,
                    onClick = { viewModel.setDuration((selectedSeconds - 15).coerceAtLeast(5)) }
                )
                Spacer(modifier = Modifier.width(12.dp))
                AdjustButton(
                    label = "+15s",
                    enabled = !isRunning,
                    onClick = { viewModel.setDuration(selectedSeconds + 15) }
                )
                Spacer(modifier = Modifier.width(12.dp))
                AdjustButton(
                    label = "+30s",
                    enabled = !isRunning,
                    onClick = { viewModel.setDuration(selectedSeconds + 30) }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Preset chips
            Text(
                text = "Presets",
                color = TextGray,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 4.dp, bottom = 8.dp)
            )
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                presetTimes.forEach { seconds ->
                    PresetChip(
                        text = viewModel.formatTime(seconds),
                        selected = selectedSeconds == seconds,
                        enabled = !isRunning,
                        onClick = { viewModel.setDuration(seconds) }
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Control buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { viewModel.reset() },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.White
                    )
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Reset", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { if (isRunning) viewModel.pause() else viewModel.start() },
                    modifier = Modifier
                        .weight(2f)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LimeGreen,
                        contentColor = Color.Black
                    )
                ) {
                    Icon(
                        if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        if (isRunning) "Pausar" else "Iniciar",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun TimerStatusBadge(isRunning: Boolean, finished: Boolean) {
    val (text, bgColor, textColor) = when {
        finished -> Triple("¡Listo!", LimeGreen, Color.Black)
        isRunning -> Triple("Corriendo", LimeGreen.copy(alpha = 0.15f), LimeGreen)
        else -> Triple("En pausa", DarkCard, TextGray)
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun TimerRing(
    progress: Float,
    timeText: String,
    selectedText: String,
    finished: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .aspectRatio(1f),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 22.dp.toPx()
            val radius = (size.minDimension - strokeWidth) / 2f
            val topLeft = Offset(
                (size.width - radius * 2) / 2f,
                (size.height - radius * 2) / 2f
            )
            val arcSize = Size(radius * 2, radius * 2)

            // Background track
            drawArc(
                color = DarkBorder,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Progress
            drawArc(
                color = LimeGreen,
                startAngle = -90f,
                sweepAngle = 360f * progress.coerceIn(0f, 1f),
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = timeText,
                fontSize = 68.sp,
                fontWeight = FontWeight.Bold,
                color = if (finished) LimeGreen else Color.White
            )
            Text(
                text = if (finished) "Tiempo finalizado" else "de $selectedText",
                fontSize = 13.sp,
                color = TextGray
            )
        }
    }
}

@Composable
private fun AdjustButton(
    label: String,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val bg = if (enabled) DarkCard else DarkCard.copy(alpha = 0.4f)
    val fg = if (enabled) Color.White else TextGray
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(bg)
            .clickable(enabled = enabled) { onClick() }
            .padding(horizontal = 18.dp, vertical = 10.dp)
    ) {
        Text(
            text = label,
            color = fg,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun PresetChip(
    text: String,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val bg = when {
        !enabled -> DarkCard.copy(alpha = 0.4f)
        selected -> LimeGreen
        else -> DarkCard
    }
    val fg = when {
        selected -> Color.Black
        !enabled -> TextGray
        else -> Color.White
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .clickable(enabled = enabled) { onClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Text(
            text = text,
            color = fg,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
