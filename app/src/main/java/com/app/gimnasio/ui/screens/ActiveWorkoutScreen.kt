package com.app.gimnasio.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Loop
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.gimnasio.data.model.ExercisePhase
import com.app.gimnasio.ui.theme.DarkBackground
import com.app.gimnasio.ui.theme.DarkCard
import com.app.gimnasio.ui.theme.DarkSurface
import com.app.gimnasio.ui.theme.LimeGreen
import com.app.gimnasio.ui.theme.TextGray
import com.app.gimnasio.ui.viewmodel.ActiveWorkoutViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveWorkoutScreen(
    viewModel: ActiveWorkoutViewModel,
    onFinished: () -> Unit,
    onBack: () -> Unit
) {
    val routine by viewModel.routine.collectAsState()
    val currentStep by viewModel.currentStep.collectAsState()
    val currentStepIndex by viewModel.currentStepIndex.collectAsState()
    val totalSteps by viewModel.totalSteps.collectAsState()
    val elapsedSeconds by viewModel.elapsedSeconds.collectAsState()
    val isResting by viewModel.isResting.collectAsState()
    val restSecondsRemaining by viewModel.restSecondsRemaining.collectAsState()
    val isFinished by viewModel.isFinished.collectAsState()

    if (isFinished) {
        onFinished()
        return
    }

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(routine?.name ?: "Entrenamiento")
                },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.reset()
                        onBack()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    // Timer display in top bar
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 16.dp)
                    ) {
                        Icon(
                            Icons.Default.Timer,
                            contentDescription = null,
                            tint = LimeGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = viewModel.formatTime(elapsedSeconds),
                            color = LimeGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Progress bar
            val progress = if (totalSteps > 0) (currentStepIndex.toFloat() / totalSteps) else 0f
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = LimeGreen,
                trackColor = DarkCard
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${currentStepIndex + 1} de $totalSteps",
                color = TextGray,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Rest overlay
            AnimatedVisibility(
                visible = isResting,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkCard),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "DESCANSO",
                            color = LimeGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        // Show what's next
                        currentStep?.let { step ->
                            val nextLabel = if (step.isCircuitStep) {
                                "Siguiente: ${step.circuitExerciseName ?: ""}"
                            } else {
                                if (step.totalSets > 1) {
                                    "Siguiente: ${step.exercise.name} · Serie ${step.currentSet}/${step.totalSets}"
                                } else {
                                    "Siguiente: ${step.exercise.name}"
                                }
                            }
                            Text(
                                text = nextLabel,
                                color = TextGray,
                                fontSize = 13.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = viewModel.formatTime(restSecondsRemaining),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 48.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        TextButton(onClick = { viewModel.skipRest() }) {
                            Icon(
                                Icons.Default.SkipNext,
                                contentDescription = null,
                                tint = LimeGreen,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Saltar descanso", color = LimeGreen)
                        }
                    }
                }
            }

            if (!isResting) {
                currentStep?.let { step ->
                    if (step.isCircuitStep) {
                        // Circuit step display
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color(0xFFFF9800).copy(alpha = 0.15f))
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Loop,
                                    contentDescription = null,
                                    tint = Color(0xFFFF9800),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "CIRCUITO · Ronda ${step.circuitRound}/${step.circuitTotalRounds}",
                                    color = Color(0xFFFF9800),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // Exercise icon
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(DarkCard),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Loop,
                                contentDescription = null,
                                tint = Color(0xFFFF9800),
                                modifier = Modifier.size(40.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Exercise name
                        Text(
                            text = step.circuitExerciseName ?: "",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 26.sp,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Circuit details
                        if (step.exercise.phase == ExercisePhase.STRENGTH) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = DarkCard),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(20.dp),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    DetailColumn(
                                        label = "Repeticiones",
                                        value = "${step.exercise.strengthReps ?: "-"}"
                                    )
                                    DetailColumn(
                                        label = "Ronda",
                                        value = "${step.circuitRound}/${step.circuitTotalRounds}"
                                    )
                                }
                            }
                        } else {
                            val detail = if (step.exercise.durationSeconds != null) {
                                "${step.exercise.durationSeconds} segundos"
                            } else if (step.exercise.reps != null) {
                                "${step.exercise.reps} repeticiones"
                            } else ""
                            if (detail.isNotBlank()) {
                                Text(
                                    text = detail,
                                    color = TextGray,
                                    fontSize = 18.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Show sequence
                        Text(
                            text = step.exercise.circuitExercises.joinToString(" → ") {
                                if (it == step.circuitExerciseName) "[$it]" else it
                            },
                            color = TextGray,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                    } else {
                        // Regular exercise display
                        val phaseText = if (step.exercise.phase == ExercisePhase.WARMUP)
                            "CALENTAMIENTO" else "FUERZA"
                        val phaseColor = if (step.exercise.phase == ExercisePhase.WARMUP)
                            Color(0xFFFF9800) else LimeGreen

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(phaseColor.copy(alpha = 0.15f))
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = phaseText,
                                color = phaseColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // Exercise icon
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(DarkCard),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.FitnessCenter,
                                contentDescription = null,
                                tint = LimeGreen,
                                modifier = Modifier.size(40.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Exercise name
                        Text(
                            text = step.exercise.name,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 26.sp,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Set info
                        if (step.exercise.phase == ExercisePhase.STRENGTH) {
                            Text(
                                text = "Serie ${step.currentSet} de ${step.totalSets}",
                                color = LimeGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Spacer(modifier = Modifier.height(20.dp))

                            // Exercise details card
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = DarkCard),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(20.dp),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    val displayReps = step.repsForThisSet ?: step.exercise.strengthReps
                                    DetailColumn(
                                        label = "Repeticiones",
                                        value = "${displayReps ?: "-"}"
                                    )
                                    val displayWeight = step.weightForThisSet
                                    DetailColumn(
                                        label = "Peso",
                                        value = if (displayWeight != null && displayWeight > 0)
                                            "${displayWeight} kg" else "-"
                                    )
                                    DetailColumn(
                                        label = "Descanso",
                                        value = if (step.exercise.restSeconds != null)
                                            "${step.exercise.restSeconds}s" else "-"
                                    )
                                }
                            }
                        } else {
                            // Warmup details
                            val detail = if (step.exercise.durationSeconds != null) {
                                "${step.exercise.durationSeconds} segundos"
                            } else {
                                "${step.exercise.reps ?: "-"} repeticiones"
                            }
                            Text(
                                text = detail,
                                color = TextGray,
                                fontSize = 18.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Complete button
            Button(
                onClick = { viewModel.completeCurrentStep() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = LimeGreen),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (currentStepIndex + 1 >= totalSteps) "Finalizar" else "Completar",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            // Skip exercise button (skip all remaining sets of current exercise)
            currentStep?.let { step ->
                val hasMoreSets = step.totalSets > 1 && step.currentSet < step.totalSets
                val isCircuitWithMoreSteps = step.isCircuitStep
                if (hasMoreSets || isCircuitWithMoreSteps || isResting) {
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(
                        onClick = { viewModel.skipToNextExercise() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            Icons.Default.SkipNext,
                            contentDescription = null,
                            tint = TextGray,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Saltar ejercicio",
                            color = TextGray,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun DetailColumn(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            color = LimeGreen,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            color = TextGray,
            fontSize = 12.sp
        )
    }
}
