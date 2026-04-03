package com.app.gimnasio.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.app.gimnasio.data.model.ExerciseInfo
import com.app.gimnasio.ui.theme.DarkBackground
import com.app.gimnasio.ui.theme.DarkSurface
import com.app.gimnasio.ui.theme.LimeGreen
import com.app.gimnasio.ui.theme.TextGray
import com.app.gimnasio.ui.viewmodel.ExerciseGalleryViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseDetailScreen(
    exerciseId: Long,
    viewModel: ExerciseGalleryViewModel,
    onBack: () -> Unit
) {
    val exercise by viewModel.selectedExercise.collectAsState()
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    LaunchedEffect(exerciseId) {
        viewModel.loadExercise(exerciseId)
    }

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = { Text(exercise?.name ?: "") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { showEditDialog = true }) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar", tint = LimeGreen)
                    }
                    IconButton(onClick = { showDeleteConfirm = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color(0xFFFF6B6B))
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
        val ex = exercise
        if (ex != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                if (ex.imagePath != null) {
                    AsyncImage(
                        model = File(ex.imagePath),
                        contentDescription = ex.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16f / 9f)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                }

                Text(
                    text = ex.muscleGroup.displayName,
                    color = LimeGreen,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = ex.name,
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                if (ex.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Descripción",
                        color = TextGray,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = ex.description,
                        color = Color.White,
                        fontSize = 16.sp,
                        lineHeight = 24.sp
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Cargando...", color = TextGray)
            }
        }
    }

    val ex = exercise
    if (showEditDialog && ex != null) {
        EditExerciseInfoDialog(
            exercise = ex,
            onDismiss = { showEditDialog = false },
            onConfirm = { name, description, newImageUri ->
                viewModel.updateExercise(
                    ex.copy(name = name, description = description),
                    newImageUri
                )
                showEditDialog = false
            }
        )
    }

    if (showDeleteConfirm && ex != null) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            containerColor = DarkSurface,
            title = { Text("Eliminar ejercicio", color = Color.White) },
            text = { Text("¿Estás seguro de que querés eliminar \"${ex.name}\"?", color = TextGray) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteExercise(ex)
                    showDeleteConfirm = false
                    onBack()
                }) {
                    Text("Eliminar", color = Color(0xFFFF6B6B))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancelar", color = TextGray)
                }
            }
        )
    }
}

@Composable
private fun EditExerciseInfoDialog(
    exercise: ExerciseInfo,
    onDismiss: () -> Unit,
    onConfirm: (String, String, Uri?) -> Unit
) {
    var name by remember { mutableStateOf(exercise.name) }
    var description by remember { mutableStateOf(exercise.description) }
    var newImageUri by remember { mutableStateOf<Uri?>(null) }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> uri?.let { newImageUri = it } }

    val dialogTextFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = LimeGreen,
        unfocusedBorderColor = TextGray.copy(alpha = 0.5f),
        focusedLabelColor = LimeGreen,
        unfocusedLabelColor = TextGray,
        cursorColor = LimeGreen,
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        title = { Text("Editar ejercicio", color = Color.White) },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre del ejercicio") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = dialogTextFieldColors
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3,
                    colors = dialogTextFieldColors
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = { imagePicker.launch("image/*") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        if (newImageUri != null) Icons.Default.Image
                        else if (exercise.imagePath != null) Icons.Default.Image
                        else Icons.Default.AddPhotoAlternate,
                        contentDescription = null,
                        tint = LimeGreen
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        when {
                            newImageUri != null -> "Nueva imagen seleccionada"
                            exercise.imagePath != null -> "Cambiar imagen"
                            else -> "Agregar imagen"
                        },
                        color = LimeGreen
                    )
                }
            }
        },
        confirmButton = {
            val isValid = name.isNotBlank()
            TextButton(
                onClick = { onConfirm(name.trim(), description.trim(), newImageUri) },
                enabled = isValid
            ) {
                Text("Guardar", color = if (isValid) LimeGreen else TextGray)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = TextGray)
            }
        }
    )
}
