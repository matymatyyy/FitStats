package com.app.gimnasio.ui.screens

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.gimnasio.data.model.BodyMeasurements
import com.app.gimnasio.data.model.PersonalRecords
import com.app.gimnasio.ui.theme.DarkBackground
import com.app.gimnasio.ui.theme.DarkBorder
import com.app.gimnasio.ui.theme.DarkCard
import com.app.gimnasio.ui.theme.LimeGreen
import com.app.gimnasio.ui.theme.TextDarkGray
import com.app.gimnasio.ui.theme.TextGray
import com.app.gimnasio.ui.viewmodel.ProfileViewModel
import java.io.File

@Composable
fun ProfileScreen(viewModel: ProfileViewModel) {
    val profile by viewModel.profile.collectAsState()
    val measurements by viewModel.measurements.collectAsState()
    val personalRecords by viewModel.personalRecords.collectAsState()
    val totalWorkouts by viewModel.totalWorkouts.collectAsState()

    var showEditDialog by remember { mutableStateOf(false) }
    var showMeasurementsDialog by remember { mutableStateOf(false) }
    var showPRDialog by remember { mutableStateOf(false) }

    val photoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.savePhoto(it) }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Spacer(modifier = Modifier.height(16.dp)) }

        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Perfil",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
                IconButton(onClick = { showEditDialog = true }) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar", tint = LimeGreen)
                }
            }
        }

        // Profile card
        item {
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
                    // Profile photo
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(LimeGreen.copy(alpha = 0.15f))
                            .clickable { photoLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        val photoPath = profile.photoPath
                        if (photoPath != null && File(photoPath).exists()) {
                            val bitmap = remember(photoPath) {
                                BitmapFactory.decodeFile(photoPath)
                            }
                            if (bitmap != null) {
                                Image(
                                    bitmap = bitmap.asImageBitmap(),
                                    contentDescription = "Foto de perfil",
                                    modifier = Modifier
                                        .size(100.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        } else {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                tint = LimeGreen,
                                modifier = Modifier.size(50.dp)
                            )
                        }

                        // Camera overlay
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(LimeGreen),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.CameraAlt,
                                contentDescription = "Cambiar foto",
                                tint = Color.Black,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = profile.name.ifBlank { "Sin nombre" },
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Age and gender
                    val infoItems = listOfNotNull(
                        profile.age?.let { "${it} años" },
                        profile.gender
                    )
                    if (infoItems.isNotEmpty()) {
                        Text(
                            text = infoItems.joinToString(" · "),
                            color = TextGray,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        // Stats card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(LimeGreen.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.FitnessCenter,
                            contentDescription = null,
                            tint = LimeGreen,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "$totalWorkouts",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp
                        )
                        Text(
                            text = "Entrenamientos totales",
                            color = TextGray,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        // Measurements section
        item {
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Straighten,
                                contentDescription = null,
                                tint = LimeGreen,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Medidas",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }
                        IconButton(onClick = { showMeasurementsDialog = true }) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar medidas", tint = LimeGreen, modifier = Modifier.size(20.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    val measurementsList = listOf(
                        "Cintura" to measurements.cintura,
                        "Abdomen" to measurements.abdomen,
                        "Glúteos" to measurements.gluteos,
                        "Pecho" to measurements.pecho,
                        "Hombros" to measurements.hombros,
                        "Antebrazo" to measurements.antebrazo,
                        "Bíceps" to measurements.biceps,
                        "Muslos" to measurements.muslos,
                        "Pantorrillas" to measurements.pantorrillas,
                        "Cuello" to measurements.cuello
                    )

                    measurementsList.forEach { (label, value) ->
                        MeasurementRow(label = label, value = value)
                    }
                }
            }
        }

        // Personal Records section
        item {
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.EmojiEvents,
                                contentDescription = null,
                                tint = LimeGreen,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Records Personales",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }
                        IconButton(onClick = { showPRDialog = true }) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar PRs", tint = LimeGreen, modifier = Modifier.size(20.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    val prList = listOf(
                        "Sentadillas" to personalRecords.sentadillas,
                        "Peso Muerto" to personalRecords.pesoMuerto,
                        "Press de Banca" to personalRecords.pressBanca,
                        "Press Militar" to personalRecords.pressMilitar,
                        "Dominadas" to personalRecords.dominadas
                    )

                    prList.forEach { (label, value) ->
                        PRRow(label = label, value = value)
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }

    if (showEditDialog) {
        EditProfileDialog(
            currentName = profile.name,
            currentAge = profile.age,
            currentGender = profile.gender,
            onDismiss = { showEditDialog = false },
            onSave = { name, age, gender ->
                viewModel.saveProfile(name, age, gender)
                showEditDialog = false
            }
        )
    }

    if (showMeasurementsDialog) {
        EditMeasurementsDialog(
            current = measurements,
            onDismiss = { showMeasurementsDialog = false },
            onSave = { m ->
                viewModel.saveMeasurements(m)
                showMeasurementsDialog = false
            }
        )
    }

    if (showPRDialog) {
        EditPersonalRecordsDialog(
            current = personalRecords,
            onDismiss = { showPRDialog = false },
            onSave = { pr ->
                viewModel.savePersonalRecords(pr)
                showPRDialog = false
            }
        )
    }
}

@Composable
private fun MeasurementRow(label: String, value: Double?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = TextGray, fontSize = 14.sp)
        Text(
            text = if (value != null) "$value cm" else "—",
            color = if (value != null) Color.White else TextDarkGray,
            fontWeight = if (value != null) FontWeight.Bold else FontWeight.Normal,
            fontSize = 14.sp
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditProfileDialog(
    currentName: String,
    currentAge: Int?,
    currentGender: String?,
    onDismiss: () -> Unit,
    onSave: (String, Int?, String?) -> Unit
) {
    var name by remember { mutableStateOf(currentName) }
    var ageText by remember { mutableStateOf(currentAge?.toString() ?: "") }
    var gender by remember { mutableStateOf(currentGender ?: "") }
    var genderExpanded by remember { mutableStateOf(false) }
    val genderOptions = listOf("Masculino", "Femenino", "Otro")

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkCard,
        title = { Text("Editar perfil", color = Color.White) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre") },
                    singleLine = true,
                    colors = profileFieldColors()
                )
                OutlinedTextField(
                    value = ageText,
                    onValueChange = { if (it.length <= 3 && it.all { c -> c.isDigit() }) ageText = it },
                    label = { Text("Edad") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = profileFieldColors()
                )
                ExposedDropdownMenuBox(
                    expanded = genderExpanded,
                    onExpandedChange = { genderExpanded = !genderExpanded }
                ) {
                    OutlinedTextField(
                        value = gender,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Género") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = genderExpanded) },
                        modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable),
                        colors = profileFieldColors()
                    )
                    ExposedDropdownMenu(
                        expanded = genderExpanded,
                        onDismissRequest = { genderExpanded = false },
                        containerColor = DarkCard
                    ) {
                        genderOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option, color = Color.White) },
                                onClick = {
                                    gender = option
                                    genderExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onSave(
                            name.trim(),
                            ageText.toIntOrNull(),
                            gender.ifBlank { null }
                        )
                    }
                },
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
private fun EditMeasurementsDialog(
    current: BodyMeasurements,
    onDismiss: () -> Unit,
    onSave: (BodyMeasurements) -> Unit
) {
    var cintura by remember { mutableStateOf(current.cintura?.toString() ?: "") }
    var abdomen by remember { mutableStateOf(current.abdomen?.toString() ?: "") }
    var gluteos by remember { mutableStateOf(current.gluteos?.toString() ?: "") }
    var pecho by remember { mutableStateOf(current.pecho?.toString() ?: "") }
    var hombros by remember { mutableStateOf(current.hombros?.toString() ?: "") }
    var antebrazo by remember { mutableStateOf(current.antebrazo?.toString() ?: "") }
    var biceps by remember { mutableStateOf(current.biceps?.toString() ?: "") }
    var muslos by remember { mutableStateOf(current.muslos?.toString() ?: "") }
    var pantorrillas by remember { mutableStateOf(current.pantorrillas?.toString() ?: "") }
    var cuello by remember { mutableStateOf(current.cuello?.toString() ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkCard,
        title = { Text("Medidas corporales (cm)", color = Color.White) },
        text = {
            Column(
                modifier = Modifier.height(400.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val fields = listOf(
                        "Cintura" to cintura,
                        "Abdomen" to abdomen,
                        "Glúteos" to gluteos,
                        "Pecho" to pecho,
                        "Hombros" to hombros,
                        "Antebrazo" to antebrazo,
                        "Bíceps" to biceps,
                        "Muslos" to muslos,
                        "Pantorrillas" to pantorrillas,
                        "Cuello" to cuello
                    )
                    items(fields.size) { index ->
                        val (label, value) = fields[index]
                        MeasurementField(
                            label = label,
                            value = value,
                            onValueChange = { newVal ->
                                when (index) {
                                    0 -> cintura = newVal
                                    1 -> abdomen = newVal
                                    2 -> gluteos = newVal
                                    3 -> pecho = newVal
                                    4 -> hombros = newVal
                                    5 -> antebrazo = newVal
                                    6 -> biceps = newVal
                                    7 -> muslos = newVal
                                    8 -> pantorrillas = newVal
                                    9 -> cuello = newVal
                                }
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        BodyMeasurements(
                            cintura = cintura.toDoubleOrNull(),
                            abdomen = abdomen.toDoubleOrNull(),
                            gluteos = gluteos.toDoubleOrNull(),
                            pecho = pecho.toDoubleOrNull(),
                            hombros = hombros.toDoubleOrNull(),
                            antebrazo = antebrazo.toDoubleOrNull(),
                            biceps = biceps.toDoubleOrNull(),
                            muslos = muslos.toDoubleOrNull(),
                            pantorrillas = pantorrillas.toDoubleOrNull(),
                            cuello = cuello.toDoubleOrNull()
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = LimeGreen)
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
private fun MeasurementField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = { newVal ->
            if (newVal.isEmpty() || newVal.matches(Regex("^\\d{0,3}(\\.\\d{0,1})?\$"))) {
                onValueChange(newVal)
            }
        },
        label = { Text(label) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        modifier = Modifier.fillMaxWidth(),
        colors = profileFieldColors(),
        suffix = { Text("cm", color = TextGray, fontSize = 12.sp) }
    )
}

@Composable
private fun PRRow(label: String, value: Double?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = TextGray, fontSize = 14.sp)
        Text(
            text = if (value != null) "$value kg" else "—",
            color = if (value != null) Color.White else TextDarkGray,
            fontWeight = if (value != null) FontWeight.Bold else FontWeight.Normal,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun EditPersonalRecordsDialog(
    current: PersonalRecords,
    onDismiss: () -> Unit,
    onSave: (PersonalRecords) -> Unit
) {
    var sentadillas by remember { mutableStateOf(current.sentadillas?.toString() ?: "") }
    var pesoMuerto by remember { mutableStateOf(current.pesoMuerto?.toString() ?: "") }
    var pressBanca by remember { mutableStateOf(current.pressBanca?.toString() ?: "") }
    var pressMilitar by remember { mutableStateOf(current.pressMilitar?.toString() ?: "") }
    var dominadas by remember { mutableStateOf(current.dominadas?.toString() ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkCard,
        title = { Text("Records Personales (kg)", color = Color.White) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                val fields = listOf(
                    "Sentadillas" to sentadillas,
                    "Peso Muerto" to pesoMuerto,
                    "Press de Banca" to pressBanca,
                    "Press Militar" to pressMilitar,
                    "Dominadas" to dominadas
                )
                fields.forEachIndexed { index, (label, value) ->
                    PRField(
                        label = label,
                        value = value,
                        onValueChange = { newVal ->
                            when (index) {
                                0 -> sentadillas = newVal
                                1 -> pesoMuerto = newVal
                                2 -> pressBanca = newVal
                                3 -> pressMilitar = newVal
                                4 -> dominadas = newVal
                            }
                        }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        PersonalRecords(
                            sentadillas = sentadillas.toDoubleOrNull(),
                            pesoMuerto = pesoMuerto.toDoubleOrNull(),
                            pressBanca = pressBanca.toDoubleOrNull(),
                            pressMilitar = pressMilitar.toDoubleOrNull(),
                            dominadas = dominadas.toDoubleOrNull()
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = LimeGreen)
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
private fun PRField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = { newVal ->
            if (newVal.isEmpty() || newVal.matches(Regex("^\\d{0,4}(\\.\\d{0,1})?\$"))) {
                onValueChange(newVal)
            }
        },
        label = { Text(label) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        modifier = Modifier.fillMaxWidth(),
        colors = profileFieldColors(),
        suffix = { Text("kg", color = TextGray, fontSize = 12.sp) }
    )
}

@Composable
private fun profileFieldColors() = OutlinedTextFieldDefaults.colors(
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
