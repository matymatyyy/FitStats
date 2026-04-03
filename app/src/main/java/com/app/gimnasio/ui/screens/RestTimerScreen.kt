package com.app.gimnasio.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.gimnasio.ui.viewmodel.RestTimerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestTimerScreen(
    viewModel: RestTimerViewModel,
    onBack: () -> Unit
) {
    val secondsRemaining by viewModel.secondsRemaining.collectAsState()
    val isRunning by viewModel.isRunning.collectAsState()
    val selectedSeconds by viewModel.selectedSeconds.collectAsState()

    val presetTimes = listOf(30, 60, 90, 120, 180)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Timer de Descanso") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Timer display
            Text(
                text = viewModel.formatTime(secondsRemaining),
                fontSize = 80.sp,
                fontWeight = FontWeight.Bold,
                color = if (secondsRemaining == 0 && !isRunning && selectedSeconds > 0)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Preset time chips
            Text(
                text = "Tiempo de descanso",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                presetTimes.forEach { seconds ->
                    FilterChip(
                        selected = selectedSeconds == seconds,
                        onClick = { viewModel.setDuration(seconds) },
                        label = { Text(viewModel.formatTime(seconds)) },
                        enabled = !isRunning
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Control buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                if (isRunning) {
                    Button(
                        onClick = { viewModel.pause() },
                        modifier = Modifier.size(width = 140.dp, height = 50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Text("Pausar", fontSize = 18.sp)
                    }
                } else {
                    Button(
                        onClick = { viewModel.start() },
                        modifier = Modifier.size(width = 140.dp, height = 50.dp)
                    ) {
                        Text("Iniciar", fontSize = 18.sp)
                    }
                }

                OutlinedButton(
                    onClick = { viewModel.reset() },
                    modifier = Modifier.size(width = 140.dp, height = 50.dp)
                ) {
                    Text("Reset", fontSize = 18.sp)
                }
            }
        }
    }
}
