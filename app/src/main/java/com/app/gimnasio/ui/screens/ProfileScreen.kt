package com.app.gimnasio.ui.screens

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.automirrored.filled.ShowChart
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
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import com.app.gimnasio.data.model.BodyMeasurements
import com.app.gimnasio.data.model.CustomMeasurement
import com.app.gimnasio.data.model.CustomMeasurementHistoryPoint
import com.app.gimnasio.data.model.CustomPR
import com.app.gimnasio.data.model.CustomPRHistoryPoint
import com.app.gimnasio.data.model.PRHistoryEntry
import com.app.gimnasio.data.model.PersonalRecords
import com.app.gimnasio.ui.theme.DarkBackground
import com.app.gimnasio.ui.theme.DarkBorder
import com.app.gimnasio.ui.theme.DarkCard
import com.app.gimnasio.ui.theme.DarkSurface
import com.app.gimnasio.ui.theme.LimeGreen
import com.app.gimnasio.ui.theme.TextDarkGray
import com.app.gimnasio.ui.theme.TextGray
import com.app.gimnasio.ui.viewmodel.ProfileViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private fun formatDate(millis: Long?): String? {
    if (millis == null) return null
    return SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(millis))
}

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onNavigateToPRCalculator: () -> Unit = {}
) {
    val profile by viewModel.profile.collectAsState()
    val measurements by viewModel.measurements.collectAsState()
    val personalRecords by viewModel.personalRecords.collectAsState()
    val totalWorkouts by viewModel.totalWorkouts.collectAsState()
    val prHistory by viewModel.prHistory.collectAsState()
    val customMeasurements by viewModel.customMeasurements.collectAsState()
    val customPRs by viewModel.customPRs.collectAsState()

    var showEditDialog by remember { mutableStateOf(false) }
    var showMeasurementsDialog by remember { mutableStateOf(false) }
    var showPRDialog by remember { mutableStateOf(false) }
    var showPRChartDialog by remember { mutableStateOf(false) }
    var showGoogleDialog by remember { mutableStateOf(false) }
    var showMessagesDialog by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf(0) } // 0 = Perfil, 1 = PR y Medidas
    var editingCustomMeasurement by remember { mutableStateOf<CustomMeasurement?>(null) }
    var showAddCustomMeasurement by remember { mutableStateOf(false) }
    var editingCustomPR by remember { mutableStateOf<CustomPR?>(null) }
    var showAddCustomPR by remember { mutableStateOf(false) }
    var customMeasurementChart by remember { mutableStateOf<CustomMeasurement?>(null) }
    var customPRChart by remember { mutableStateOf<CustomPR?>(null) }
    var editingFixedMeasurement by remember { mutableStateOf<String?>(null) }
    var editingFixedPR by remember { mutableStateOf<String?>(null) }
    var confirmDelete by remember { mutableStateOf<Pair<String, () -> Unit>?>(null) }
    var fixedMeasurementChart by remember { mutableStateOf<String?>(null) }
    var fixedPRChart by remember { mutableStateOf<String?>(null) }

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
                if (selectedTab == 0) {
                Row {
                    IconButton(onClick = { showMessagesDialog = true }) {
                        Icon(
                            Icons.Default.Email,
                            contentDescription = "Mensajes",
                            tint = LimeGreen,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    IconButton(onClick = { showGoogleDialog = true }) {
                        Icon(
                            painter = androidx.compose.ui.res.painterResource(id = com.app.gimnasio.R.drawable.ic_google),
                            contentDescription = "Conectar con Google",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    IconButton(onClick = { showEditDialog = true }) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar", tint = LimeGreen)
                    }
                }
                }
            }
        }

        // Tabs: Perfil / PR y Medidas
        item {
            ProfileTabs(
                selected = selectedTab,
                onSelect = { selectedTab = it }
            )
        }

        if (selectedTab == 0) {
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
                            .size(110.dp)
                            .clickable { photoLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .background(LimeGreen.copy(alpha = 0.15f)),
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
                        }

                        // Camera icon outside the circle
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(32.dp)
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

        } // fin tab Perfil

        if (selectedTab == 1) {
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
                    }

                    // Show last updated date
                    val measDate = formatDate(measurements.updatedAt)
                    if (measDate != null) {
                        Text(
                            text = "Actualizado: $measDate",
                            color = TextGray,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

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
                        MeasurementRow(
                            label = label,
                            value = value,
                            onEdit = { editingFixedMeasurement = label },
                            onDelete = {
                                confirmDelete = label to {
                                    viewModel.saveMeasurements(setMeasurementByLabel(measurements, label, null))
                                }
                            },
                            onChart = { fixedMeasurementChart = label }
                        )
                    }

                    // Custom measurements
                    customMeasurements.forEach { cm ->
                        CustomMeasurementRow(
                            item = cm,
                            onEdit = { editingCustomMeasurement = cm },
                            onDelete = {
                                confirmDelete = cm.name to { viewModel.deleteCustomMeasurement(cm.name) }
                            },
                            onChart = { customMeasurementChart = cm }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(
                        onClick = { showAddCustomMeasurement = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = LimeGreen, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Agregar medida", color = LimeGreen, fontWeight = FontWeight.Bold, fontSize = 13.sp)
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
                    }

                    // Show last updated date
                    val prDate = formatDate(personalRecords.updatedAt)
                    if (prDate != null) {
                        Text(
                            text = "Actualizado: $prDate",
                            color = TextGray,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    val prList = listOf(
                        "Sentadillas" to personalRecords.sentadillas,
                        "Peso Muerto" to personalRecords.pesoMuerto,
                        "Press de Banca" to personalRecords.pressBanca,
                        "Press Militar" to personalRecords.pressMilitar,
                        "Dominadas" to personalRecords.dominadas
                    )

                    prList.forEach { (label, value) ->
                        PRRow(
                            label = label,
                            value = value,
                            onEdit = { editingFixedPR = label },
                            onDelete = {
                                confirmDelete = label to {
                                    viewModel.savePersonalRecords(setPRByLabel(personalRecords, label, null))
                                }
                            },
                            onChart = { fixedPRChart = label }
                        )
                    }

                    // Custom PRs
                    customPRs.forEach { cpr ->
                        CustomPRRow(
                            item = cpr,
                            onEdit = { editingCustomPR = cpr },
                            onDelete = {
                                confirmDelete = cpr.exerciseName to { viewModel.deleteCustomPR(cpr.exerciseName) }
                            },
                            onChart = { customPRChart = cpr }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(
                        onClick = { showAddCustomPR = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = LimeGreen, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Agregar PR", color = LimeGreen, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }

        // PR Calculator button
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToPRCalculator() },
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(LimeGreen.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Calculate,
                            contentDescription = null,
                            tint = LimeGreen,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Calculadora de PR",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Estimá tu 1RM desde peso y reps",
                            color = TextGray,
                            fontSize = 12.sp
                        )
                    }
                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = TextGray
                    )
                }
            }
        }

        } // fin tab PR y Medidas

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

    if (showPRChartDialog) {
        PRChartDialog(
            history = prHistory,
            onDismiss = { showPRChartDialog = false }
        )
    }

    if (showGoogleDialog) {
        GoogleAccountDialog(
            onDismiss = { showGoogleDialog = false }
        )
    }

    if (showMessagesDialog) {
        MessagesDialog(
            onDismiss = { showMessagesDialog = false }
        )
    }

    if (showAddCustomMeasurement) {
        EditCustomMeasurementDialog(
            existing = null,
            onDismiss = { showAddCustomMeasurement = false },
            onSave = { name, value ->
                viewModel.saveCustomMeasurement(name, value)
                showAddCustomMeasurement = false
            }
        )
    }

    editingCustomMeasurement?.let { cm ->
        EditCustomMeasurementDialog(
            existing = cm,
            onDismiss = { editingCustomMeasurement = null },
            onSave = { name, value ->
                viewModel.saveCustomMeasurement(name, value, oldName = cm.name)
                editingCustomMeasurement = null
            }
        )
    }

    if (showAddCustomPR) {
        EditCustomPRDialog(
            existing = null,
            onDismiss = { showAddCustomPR = false },
            onSave = { name, weight, reps ->
                viewModel.saveCustomPR(name, weight, reps)
                showAddCustomPR = false
            }
        )
    }

    editingCustomPR?.let { cpr ->
        EditCustomPRDialog(
            existing = cpr,
            onDismiss = { editingCustomPR = null },
            onSave = { name, weight, reps ->
                viewModel.saveCustomPR(name, weight, reps, oldName = cpr.exerciseName)
                editingCustomPR = null
            }
        )
    }

    customMeasurementChart?.let { cm ->
        CustomMeasurementChartDialog(
            item = cm,
            loadHistory = { viewModel.getCustomMeasurementHistory(cm.name) },
            onDismiss = { customMeasurementChart = null }
        )
    }

    customPRChart?.let { cpr ->
        CustomPRChartDialog(
            item = cpr,
            loadHistory = { viewModel.getCustomPRHistory(cpr.exerciseName) },
            onDismiss = { customPRChart = null }
        )
    }

    editingFixedMeasurement?.let { label ->
        val current = getMeasurementByLabel(measurements, label)
        SingleValueEditDialog(
            title = "Editar $label",
            currentValue = current?.toString() ?: "",
            unit = "cm",
            regex = Regex("^\\d{0,3}(\\.\\d{0,2})?\$"),
            onDismiss = { editingFixedMeasurement = null },
            onSave = { v ->
                viewModel.saveMeasurements(setMeasurementByLabel(measurements, label, v))
                editingFixedMeasurement = null
            }
        )
    }

    editingFixedPR?.let { label ->
        val current = getPRByLabel(personalRecords, label)
        SingleValueEditDialog(
            title = "Editar $label",
            currentValue = current?.toString() ?: "",
            unit = "kg",
            regex = Regex("^\\d{0,4}(\\.\\d{0,1})?\$"),
            onDismiss = { editingFixedPR = null },
            onSave = { v ->
                viewModel.savePersonalRecords(setPRByLabel(personalRecords, label, v))
                editingFixedPR = null
            }
        )
    }

    confirmDelete?.let { (label, action) ->
        ConfirmDeleteDialog(
            itemLabel = label,
            onDismiss = { confirmDelete = null },
            onConfirm = {
                action()
                confirmDelete = null
            }
        )
    }

    fixedMeasurementChart?.let { label ->
        val column = measurementLabelToColumn(label)
        FixedHistoryChartDialog(
            title = label,
            unit = "cm",
            loadHistory = { viewModel.getMeasurementHistoryByColumn(column) },
            onDismiss = { fixedMeasurementChart = null }
        )
    }

    fixedPRChart?.let { label ->
        val column = prLabelToColumn(label)
        FixedHistoryChartDialog(
            title = label,
            unit = "kg",
            loadHistory = { viewModel.getPRHistoryByColumn(column) },
            onDismiss = { fixedPRChart = null }
        )
    }
}

@Composable
private fun MeasurementRow(
    label: String,
    value: Double?,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onChart: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = TextGray, fontSize = 14.sp, modifier = Modifier.weight(1f))
        Text(
            text = if (value != null) "$value cm" else "—",
            color = if (value != null) Color.White else TextDarkGray,
            fontWeight = if (value != null) FontWeight.Bold else FontWeight.Normal,
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.width(4.dp))
        IconButton(onClick = onChart, modifier = Modifier.size(28.dp)) {
            Icon(Icons.AutoMirrored.Filled.ShowChart, contentDescription = "Estadísticas", tint = LimeGreen, modifier = Modifier.size(16.dp))
        }
        IconButton(onClick = onEdit, modifier = Modifier.size(28.dp)) {
            Icon(Icons.Default.Edit, contentDescription = "Editar", tint = LimeGreen, modifier = Modifier.size(16.dp))
        }
        IconButton(onClick = onDelete, modifier = Modifier.size(28.dp), enabled = value != null) {
            Icon(
                Icons.Default.Delete,
                contentDescription = "Eliminar",
                tint = if (value != null) TextGray else TextDarkGray,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun MessagesDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkCard,
        icon = {
            Icon(
                Icons.Default.Email,
                contentDescription = null,
                tint = LimeGreen,
                modifier = Modifier.size(40.dp)
            )
        },
        title = {
            Text(
                "Mensajes",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "No tienes mensajes nuevos",
                    color = TextGray,
                    fontSize = 14.sp
                )
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.Email,
                            contentDescription = null,
                            tint = TextGray.copy(alpha = 0.5f),
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = "Proximamente",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Podras recibir mensajes de tus amigos, compartir rutinas y motivarse mutuamente.",
                            color = TextGray,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Entendido", color = LimeGreen)
            }
        }
    )
}

@Composable
private fun GoogleAccountDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkCard,
        icon = {
            Icon(
                painter = androidx.compose.ui.res.painterResource(id = com.app.gimnasio.R.drawable.ic_google),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(40.dp)
            )
        },
        title = {
            Text(
                "Conectar con Google",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Vincula tu cuenta de Google para sincronizar tus entrenamientos, guardar tu progreso en la nube y acceder desde cualquier dispositivo.",
                    color = TextGray,
                    fontSize = 14.sp
                )
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = androidx.compose.ui.res.painterResource(id = com.app.gimnasio.R.drawable.ic_google),
                            contentDescription = null,
                            tint = Color.Unspecified,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Cuenta no vinculada", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                            Text("Inicia sesion para activar", color = TextGray, fontSize = 12.sp)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4285F4)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    painter = androidx.compose.ui.res.painterResource(id = com.app.gimnasio.R.drawable.ic_google),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Iniciar sesion con Google", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Ahora no", color = TextGray)
            }
        }
    )
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
                        onSave(name.trim(), ageText.toIntOrNull(), gender.ifBlank { null })
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = LimeGreen),
                enabled = name.isNotBlank()
            ) {
                Text("Guardar", color = Color.Black)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar", color = TextGray) }
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
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    val fields = listOf(
                        "Cintura" to cintura, "Abdomen" to abdomen, "Glúteos" to gluteos,
                        "Pecho" to pecho, "Hombros" to hombros, "Antebrazo" to antebrazo,
                        "Bíceps" to biceps, "Muslos" to muslos, "Pantorrillas" to pantorrillas,
                        "Cuello" to cuello
                    )
                    items(fields.size) { index ->
                        val (label, value) = fields[index]
                        MeasurementField(
                            label = label, value = value,
                            onValueChange = { newVal ->
                                when (index) {
                                    0 -> cintura = newVal; 1 -> abdomen = newVal
                                    2 -> gluteos = newVal; 3 -> pecho = newVal
                                    4 -> hombros = newVal; 5 -> antebrazo = newVal
                                    6 -> biceps = newVal; 7 -> muslos = newVal
                                    8 -> pantorrillas = newVal; 9 -> cuello = newVal
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
                    onSave(BodyMeasurements(
                        cintura = cintura.toDoubleOrNull(), abdomen = abdomen.toDoubleOrNull(),
                        gluteos = gluteos.toDoubleOrNull(), pecho = pecho.toDoubleOrNull(),
                        hombros = hombros.toDoubleOrNull(), antebrazo = antebrazo.toDoubleOrNull(),
                        biceps = biceps.toDoubleOrNull(), muslos = muslos.toDoubleOrNull(),
                        pantorrillas = pantorrillas.toDoubleOrNull(), cuello = cuello.toDoubleOrNull()
                    ))
                },
                colors = ButtonDefaults.buttonColors(containerColor = LimeGreen)
            ) { Text("Guardar", color = Color.Black) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar", color = TextGray) }
        }
    )
}

@Composable
private fun MeasurementField(label: String, value: String, onValueChange: (String) -> Unit) {
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
private fun PRRow(
    label: String,
    value: Double?,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onChart: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = TextGray, fontSize = 14.sp, modifier = Modifier.weight(1f))
        Text(
            text = if (value != null) "$value kg" else "—",
            color = if (value != null) Color.White else TextDarkGray,
            fontWeight = if (value != null) FontWeight.Bold else FontWeight.Normal,
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.width(4.dp))
        IconButton(onClick = onChart, modifier = Modifier.size(28.dp)) {
            Icon(Icons.AutoMirrored.Filled.ShowChart, contentDescription = "Estadísticas", tint = LimeGreen, modifier = Modifier.size(16.dp))
        }
        IconButton(onClick = onEdit, modifier = Modifier.size(28.dp)) {
            Icon(Icons.Default.Edit, contentDescription = "Editar", tint = LimeGreen, modifier = Modifier.size(16.dp))
        }
        IconButton(onClick = onDelete, modifier = Modifier.size(28.dp), enabled = value != null) {
            Icon(
                Icons.Default.Delete,
                contentDescription = "Eliminar",
                tint = if (value != null) TextGray else TextDarkGray,
                modifier = Modifier.size(16.dp)
            )
        }
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
                    "Sentadillas" to sentadillas, "Peso Muerto" to pesoMuerto,
                    "Press de Banca" to pressBanca, "Press Militar" to pressMilitar,
                    "Dominadas" to dominadas
                )
                fields.forEachIndexed { index, (label, value) ->
                    PRField(label = label, value = value, onValueChange = { newVal ->
                        when (index) {
                            0 -> sentadillas = newVal; 1 -> pesoMuerto = newVal
                            2 -> pressBanca = newVal; 3 -> pressMilitar = newVal
                            4 -> dominadas = newVal
                        }
                    })
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(PersonalRecords(
                        sentadillas = sentadillas.toDoubleOrNull(),
                        pesoMuerto = pesoMuerto.toDoubleOrNull(),
                        pressBanca = pressBanca.toDoubleOrNull(),
                        pressMilitar = pressMilitar.toDoubleOrNull(),
                        dominadas = dominadas.toDoubleOrNull()
                    ))
                },
                colors = ButtonDefaults.buttonColors(containerColor = LimeGreen)
            ) { Text("Guardar", color = Color.Black) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar", color = TextGray) }
        }
    )
}

@Composable
private fun PRField(label: String, value: String, onValueChange: (String) -> Unit) {
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

// --- PR Chart ---

@Composable
private fun PRChartDialog(
    history: List<PRHistoryEntry>,
    onDismiss: () -> Unit
) {
    val exercises = listOf(
        "Sentadillas" to Color(0xFF4CAF50),
        "Peso Muerto" to Color(0xFF2196F3),
        "Press Banca" to Color(0xFFFF9800),
        "Press Militar" to Color(0xFFE91E63),
        "Dominadas" to Color(0xFF9C27B0)
    )

    var selectedExercises by remember {
        mutableStateOf(setOf("Sentadillas", "Peso Muerto", "Press Banca", "Press Militar", "Dominadas"))
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        title = { Text("Progreso de Records", color = Color.White) },
        text = {
            Column {
                // Exercise filter chips
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    exercises.forEach { (name, color) ->
                        FilterChip(
                            selected = name in selectedExercises,
                            onClick = {
                                selectedExercises = if (name in selectedExercises) {
                                    selectedExercises - name
                                } else {
                                    selectedExercises + name
                                }
                            },
                            label = { Text(name, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = color.copy(alpha = 0.25f),
                                selectedLabelColor = color
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Chart
                if (history.size >= 2) {
                    PRLineChart(
                        history = history,
                        selectedExercises = selectedExercises,
                        exerciseColors = exercises.toMap(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                    )
                } else {
                    Text(
                        "Se necesitan al menos 2 registros para mostrar el gráfico.",
                        color = TextGray,
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Legend
                exercises.filter { it.first in selectedExercises }.forEach { (name, color) ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(color)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        val lastValue = history.lastOrNull()?.let { entry ->
                            when (name) {
                                "Sentadillas" -> entry.sentadillas
                                "Peso Muerto" -> entry.pesoMuerto
                                "Press Banca" -> entry.pressBanca
                                "Press Militar" -> entry.pressMilitar
                                "Dominadas" -> entry.dominadas
                                else -> null
                            }
                        }
                        Text(
                            "$name: ${lastValue?.let { "${it}kg" } ?: "—"}",
                            color = Color.White,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Cerrar", color = LimeGreen) }
        },
        dismissButton = {}
    )
}

@Composable
private fun PRLineChart(
    history: List<PRHistoryEntry>,
    selectedExercises: Set<String>,
    exerciseColors: Map<String, Color>,
    modifier: Modifier = Modifier
) {
    val dateFormat = SimpleDateFormat("dd/MM", Locale.getDefault())

    // Extract data series
    data class Series(val name: String, val color: Color, val values: List<Pair<Int, Double>>)

    val allSeries = mutableListOf<Series>()
    val extractors: List<Triple<String, Color, (PRHistoryEntry) -> Double?>> = listOf(
        Triple("Sentadillas", exerciseColors["Sentadillas"] ?: LimeGreen) { it.sentadillas },
        Triple("Peso Muerto", exerciseColors["Peso Muerto"] ?: LimeGreen) { it.pesoMuerto },
        Triple("Press Banca", exerciseColors["Press Banca"] ?: LimeGreen) { it.pressBanca },
        Triple("Press Militar", exerciseColors["Press Militar"] ?: LimeGreen) { it.pressMilitar },
        Triple("Dominadas", exerciseColors["Dominadas"] ?: LimeGreen) { it.dominadas },
    )

    for ((name, color, extractor) in extractors) {
        if (name !in selectedExercises) continue
        val values = history.mapIndexedNotNull { index, entry ->
            extractor(entry)?.let { index to it }
        }
        if (values.size >= 2) {
            allSeries.add(Series(name, color, values))
        }
    }

    // Find global min/max
    val allValues = allSeries.flatMap { s -> s.values.map { it.second } }
    if (allValues.isEmpty()) {
        Text("No hay datos suficientes para los ejercicios seleccionados.", color = TextGray, fontSize = 13.sp)
        return
    }
    val minVal = allValues.min()
    val maxVal = allValues.max()
    val range = if (maxVal - minVal < 1.0) 1.0 else maxVal - minVal
    val totalPoints = history.size

    Canvas(modifier = modifier) {
        val chartLeft = 45f
        val chartRight = size.width - 16f
        val chartTop = 16f
        val chartBottom = size.height - 30f
        val chartWidth = chartRight - chartLeft
        val chartHeight = chartBottom - chartTop

        // Draw grid lines
        val gridLines = 4
        for (i in 0..gridLines) {
            val y = chartTop + chartHeight * (1f - i.toFloat() / gridLines)
            drawLine(
                color = Color.White.copy(alpha = 0.1f),
                start = Offset(chartLeft, y),
                end = Offset(chartRight, y),
                strokeWidth = 1f
            )
            // Y axis labels
            val label = (minVal + range * i / gridLines).let { "%.0f".format(it) }
            drawContext.canvas.nativeCanvas.drawText(
                label,
                4f,
                y + 4f,
                android.graphics.Paint().apply {
                    this.color = android.graphics.Color.GRAY
                    textSize = 24f
                    isAntiAlias = true
                }
            )
        }

        // Draw X axis labels (dates)
        val maxLabels = (chartWidth / 100f).toInt().coerceIn(2, totalPoints)
        val step = if (totalPoints > maxLabels) totalPoints / maxLabels else 1
        for (i in history.indices step step) {
            val x = chartLeft + chartWidth * i / (totalPoints - 1).coerceAtLeast(1)
            val dateStr = dateFormat.format(Date(history[i].date))
            drawContext.canvas.nativeCanvas.drawText(
                dateStr,
                x - 20f,
                size.height - 2f,
                android.graphics.Paint().apply {
                    this.color = android.graphics.Color.GRAY
                    textSize = 22f
                    isAntiAlias = true
                }
            )
        }

        // Draw lines for each series
        for (series in allSeries) {
            val path = Path()
            var first = true
            for ((index, value) in series.values) {
                val x = chartLeft + chartWidth * index / (totalPoints - 1).coerceAtLeast(1)
                val y = chartTop + chartHeight * (1f - ((value - minVal) / range).toFloat())
                if (first) {
                    path.moveTo(x, y)
                    first = false
                } else {
                    path.lineTo(x, y)
                }
            }
            drawPath(
                path = path,
                color = series.color,
                style = Stroke(width = 3f, cap = StrokeCap.Round)
            )
            // Draw dots
            for ((index, value) in series.values) {
                val x = chartLeft + chartWidth * index / (totalPoints - 1).coerceAtLeast(1)
                val y = chartTop + chartHeight * (1f - ((value - minVal) / range).toFloat())
                drawCircle(color = series.color, radius = 5f, center = Offset(x, y))
            }
        }
    }
}

@Composable
private fun ProfileTabs(
    selected: Int,
    onSelect: (Int) -> Unit
) {
    val tabs = listOf("Perfil", "PR y Medidas")
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(24.dp)
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
                    fontSize = 16.sp
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

// --- Fixed label getters/setters ---

private fun getMeasurementByLabel(m: BodyMeasurements, label: String): Double? = when (label) {
    "Cintura" -> m.cintura
    "Abdomen" -> m.abdomen
    "Glúteos" -> m.gluteos
    "Pecho" -> m.pecho
    "Hombros" -> m.hombros
    "Antebrazo" -> m.antebrazo
    "Bíceps" -> m.biceps
    "Muslos" -> m.muslos
    "Pantorrillas" -> m.pantorrillas
    "Cuello" -> m.cuello
    else -> null
}

private fun setMeasurementByLabel(m: BodyMeasurements, label: String, value: Double?): BodyMeasurements = when (label) {
    "Cintura" -> m.copy(cintura = value)
    "Abdomen" -> m.copy(abdomen = value)
    "Glúteos" -> m.copy(gluteos = value)
    "Pecho" -> m.copy(pecho = value)
    "Hombros" -> m.copy(hombros = value)
    "Antebrazo" -> m.copy(antebrazo = value)
    "Bíceps" -> m.copy(biceps = value)
    "Muslos" -> m.copy(muslos = value)
    "Pantorrillas" -> m.copy(pantorrillas = value)
    "Cuello" -> m.copy(cuello = value)
    else -> m
}

private fun getPRByLabel(pr: PersonalRecords, label: String): Double? = when (label) {
    "Sentadillas" -> pr.sentadillas
    "Peso Muerto" -> pr.pesoMuerto
    "Press de Banca" -> pr.pressBanca
    "Press Militar" -> pr.pressMilitar
    "Dominadas" -> pr.dominadas
    else -> null
}

private fun setPRByLabel(pr: PersonalRecords, label: String, value: Double?): PersonalRecords = when (label) {
    "Sentadillas" -> pr.copy(sentadillas = value)
    "Peso Muerto" -> pr.copy(pesoMuerto = value)
    "Press de Banca" -> pr.copy(pressBanca = value)
    "Press Militar" -> pr.copy(pressMilitar = value)
    "Dominadas" -> pr.copy(dominadas = value)
    else -> pr
}

// --- Single value edit dialog ---

@Composable
private fun SingleValueEditDialog(
    title: String,
    currentValue: String,
    unit: String,
    regex: Regex,
    onDismiss: () -> Unit,
    onSave: (Double) -> Unit
) {
    var text by remember { mutableStateOf(currentValue) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkCard,
        title = { Text(title, color = Color.White) },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { if (it.isEmpty() || it.matches(regex)) text = it },
                label = { Text("Valor") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                colors = profileFieldColors(),
                suffix = { Text(unit, color = TextGray, fontSize = 12.sp) }
            )
        },
        confirmButton = {
            val v = text.toDoubleOrNull()
            Button(
                onClick = { if (v != null) onSave(v) },
                colors = ButtonDefaults.buttonColors(containerColor = LimeGreen),
                enabled = v != null
            ) { Text("Guardar", color = Color.Black) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar", color = TextGray) }
        }
    )
}

// --- Confirm delete dialog ---

@Composable
private fun ConfirmDeleteDialog(
    itemLabel: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkCard,
        title = { Text("¿Eliminar?", color = Color.White, fontWeight = FontWeight.Bold) },
        text = {
            Text(
                "¿Seguro que querés eliminar \"$itemLabel\"? Esta acción no se puede deshacer.",
                color = TextGray,
                fontSize = 14.sp
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE57373))
            ) { Text("Eliminar", color = Color.Black, fontWeight = FontWeight.Bold) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar", color = TextGray) }
        }
    )
}

// --- Custom Measurement Row ---

@Composable
private fun CustomMeasurementRow(
    item: CustomMeasurement,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onChart: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = item.name, color = TextGray, fontSize = 14.sp, modifier = Modifier.weight(1f))
        Text(
            text = "${item.valueCm} cm",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.width(4.dp))
        IconButton(onClick = onChart, modifier = Modifier.size(28.dp)) {
            Icon(Icons.AutoMirrored.Filled.ShowChart, contentDescription = "Progreso", tint = LimeGreen, modifier = Modifier.size(16.dp))
        }
        IconButton(onClick = onEdit, modifier = Modifier.size(28.dp)) {
            Icon(Icons.Default.Edit, contentDescription = "Editar", tint = LimeGreen, modifier = Modifier.size(16.dp))
        }
        IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
            Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = TextGray, modifier = Modifier.size(16.dp))
        }
    }
}

// --- Custom PR Row ---

@Composable
private fun CustomPRRow(
    item: CustomPR,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onChart: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = item.exerciseName, color = TextGray, fontSize = 14.sp)
            Text(
                text = "${item.reps} reps",
                color = TextDarkGray,
                fontSize = 11.sp
            )
        }
        Text(
            text = "${item.weightKg} kg",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.width(4.dp))
        IconButton(onClick = onChart, modifier = Modifier.size(28.dp)) {
            Icon(Icons.AutoMirrored.Filled.ShowChart, contentDescription = "Progreso", tint = LimeGreen, modifier = Modifier.size(16.dp))
        }
        IconButton(onClick = onEdit, modifier = Modifier.size(28.dp)) {
            Icon(Icons.Default.Edit, contentDescription = "Editar", tint = LimeGreen, modifier = Modifier.size(16.dp))
        }
        IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
            Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = TextGray, modifier = Modifier.size(16.dp))
        }
    }
}

// --- Add/Edit Custom Measurement Dialog ---

@Composable
private fun EditCustomMeasurementDialog(
    existing: CustomMeasurement?,
    onDismiss: () -> Unit,
    onSave: (String, Double) -> Unit
) {
    var name by remember { mutableStateOf(existing?.name ?: "") }
    var valueText by remember { mutableStateOf(existing?.valueCm?.toString() ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkCard,
        title = {
            Text(
                if (existing == null) "Nueva medida" else "Editar medida",
                color = Color.White
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre (ej: Brazo izquierdo)") },
                    singleLine = true,
                    colors = profileFieldColors()
                )
                OutlinedTextField(
                    value = valueText,
                    onValueChange = { newVal ->
                        if (newVal.isEmpty() || newVal.matches(Regex("^\\d{0,3}(\\.\\d{0,2})?\$"))) {
                            valueText = newVal
                        }
                    },
                    label = { Text("Valor") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    colors = profileFieldColors(),
                    suffix = { Text("cm", color = TextGray, fontSize = 12.sp) }
                )
            }
        },
        confirmButton = {
            val value = valueText.toDoubleOrNull()
            Button(
                onClick = {
                    if (name.isNotBlank() && value != null) onSave(name.trim(), value)
                },
                colors = ButtonDefaults.buttonColors(containerColor = LimeGreen),
                enabled = name.isNotBlank() && value != null
            ) { Text("Guardar", color = Color.Black) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar", color = TextGray) }
        }
    )
}

// --- Add/Edit Custom PR Dialog ---

@Composable
private fun EditCustomPRDialog(
    existing: CustomPR?,
    onDismiss: () -> Unit,
    onSave: (String, Double, Int) -> Unit
) {
    var name by remember { mutableStateOf(existing?.exerciseName ?: "") }
    var weightText by remember { mutableStateOf(existing?.weightKg?.toString() ?: "") }
    var repsText by remember { mutableStateOf(existing?.reps?.toString() ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkCard,
        title = {
            Text(
                if (existing == null) "Nuevo PR" else "Editar PR",
                color = Color.White
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Ejercicio (ej: Press banca)") },
                    singleLine = true,
                    colors = profileFieldColors()
                )
                OutlinedTextField(
                    value = weightText,
                    onValueChange = { newVal ->
                        if (newVal.isEmpty() || newVal.matches(Regex("^\\d{0,4}(\\.\\d{0,1})?\$"))) {
                            weightText = newVal
                        }
                    },
                    label = { Text("Peso") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    colors = profileFieldColors(),
                    suffix = { Text("kg", color = TextGray, fontSize = 12.sp) }
                )
                OutlinedTextField(
                    value = repsText,
                    onValueChange = { newVal ->
                        if (newVal.isEmpty() || (newVal.all { it.isDigit() } && newVal.length <= 3)) {
                            repsText = newVal
                        }
                    },
                    label = { Text("Reps") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = profileFieldColors(),
                    suffix = { Text("reps", color = TextGray, fontSize = 12.sp) }
                )
            }
        },
        confirmButton = {
            val weight = weightText.toDoubleOrNull()
            val reps = repsText.toIntOrNull()
            Button(
                onClick = {
                    if (name.isNotBlank() && weight != null && reps != null && reps > 0) {
                        onSave(name.trim(), weight, reps)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = LimeGreen),
                enabled = name.isNotBlank() && weight != null && reps != null && reps > 0
            ) { Text("Guardar", color = Color.Black) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar", color = TextGray) }
        }
    )
}

// --- Custom Measurement Chart Dialog ---

@Composable
private fun CustomMeasurementChartDialog(
    item: CustomMeasurement,
    loadHistory: suspend () -> List<CustomMeasurementHistoryPoint>,
    onDismiss: () -> Unit
) {
    var history by remember { mutableStateOf<List<CustomMeasurementHistoryPoint>>(emptyList()) }
    androidx.compose.runtime.LaunchedEffect(item.name) {
        history = loadHistory()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        title = { Text("Progreso: ${item.name}", color = Color.White) },
        text = {
            Column {
                if (history.size < 2) {
                    Text(
                        "Se necesitan al menos 2 registros. Actualmente: ${history.size}.",
                        color = TextGray,
                        fontSize = 13.sp
                    )
                } else {
                    SimpleLineChart(
                        values = history.map { it.date to it.valueCm },
                        unit = "cm",
                        color = LimeGreen,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    val first = history.first().valueCm
                    val last = history.last().valueCm
                    val diff = last - first
                    val sign = if (diff >= 0) "+" else ""
                    Text(
                        "Cambio: $sign${"%.1f".format(diff)} cm",
                        color = if (diff >= 0) LimeGreen else Color(0xFFE57373),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Cerrar", color = LimeGreen) }
        }
    )
}

// --- Custom PR Chart Dialog ---

@Composable
private fun CustomPRChartDialog(
    item: CustomPR,
    loadHistory: suspend () -> List<CustomPRHistoryPoint>,
    onDismiss: () -> Unit
) {
    var history by remember { mutableStateOf<List<CustomPRHistoryPoint>>(emptyList()) }
    androidx.compose.runtime.LaunchedEffect(item.exerciseName) {
        history = loadHistory()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        title = { Text("Progreso: ${item.exerciseName}", color = Color.White) },
        text = {
            Column {
                if (history.size < 2) {
                    Text(
                        "Se necesitan al menos 2 registros. Actualmente: ${history.size}.",
                        color = TextGray,
                        fontSize = 13.sp
                    )
                } else {
                    SimpleLineChart(
                        values = history.map { it.date to it.weightKg },
                        unit = "kg",
                        color = LimeGreen,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Column {
                        val first = history.first()
                        val last = history.last()
                        val wDiff = last.weightKg - first.weightKg
                        val wSign = if (wDiff >= 0) "+" else ""
                        Text(
                            "Peso: $wSign${"%.1f".format(wDiff)} kg (${first.weightKg}kg x${first.reps} → ${last.weightKg}kg x${last.reps})",
                            color = if (wDiff >= 0) LimeGreen else Color(0xFFE57373),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Cerrar", color = LimeGreen) }
        }
    )
}

// --- Simple reusable line chart ---

@Composable
private fun SimpleLineChart(
    values: List<Pair<Long, Double>>,
    unit: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    if (values.size < 2) return
    val dateFormat = SimpleDateFormat("dd/MM", Locale.getDefault())
    val minV = values.minOf { it.second }
    val maxV = values.maxOf { it.second }
    val range = if (maxV - minV < 0.01) 1.0 else maxV - minV

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("${"%.1f".format(maxV)} $unit", color = TextGray, fontSize = 10.sp)
            Text("${"%.1f".format(minV)} $unit", color = TextGray, fontSize = 10.sp)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Canvas(modifier = modifier) {
            val pad = 8f
            val w = size.width
            val h = size.height
            val path = Path()
            values.forEachIndexed { i, (_, v) ->
                val x = pad + (w - pad * 2) * i / (values.size - 1)
                val y = h - pad - (h - pad * 2) * ((v - minV) / range).toFloat()
                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
            drawPath(path, color, style = Stroke(width = 3f, cap = StrokeCap.Round))
            values.forEachIndexed { i, (_, v) ->
                val x = pad + (w - pad * 2) * i / (values.size - 1)
                val y = h - pad - (h - pad * 2) * ((v - minV) / range).toFloat()
                drawCircle(color, radius = 5f, center = Offset(x, y))
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val indices = if (values.size <= 6) values.indices.toList()
            else listOf(0, values.size / 2, values.lastIndex)
            indices.forEach { i ->
                Text(dateFormat.format(Date(values[i].first)), color = TextGray, fontSize = 10.sp)
            }
        }
    }
}

private fun measurementLabelToColumn(label: String): String = when (label) {
    "Cintura" -> "cintura"
    "Abdomen" -> "abdomen"
    "Glúteos" -> "gluteos"
    "Pecho" -> "pecho"
    "Hombros" -> "hombros"
    "Antebrazo" -> "antebrazo"
    "Bíceps" -> "biceps"
    "Muslos" -> "muslos"
    "Pantorrillas" -> "pantorrillas"
    "Cuello" -> "cuello"
    else -> ""
}

private fun prLabelToColumn(label: String): String = when (label) {
    "Sentadillas" -> "sentadillas"
    "Peso Muerto" -> "peso_muerto"
    "Press de Banca" -> "press_banca"
    "Press Militar" -> "press_militar"
    "Dominadas" -> "dominadas"
    else -> ""
}

@Composable
private fun FixedHistoryChartDialog(
    title: String,
    unit: String,
    loadHistory: suspend () -> List<Pair<Long, Double>>,
    onDismiss: () -> Unit
) {
    var history by remember { mutableStateOf<List<Pair<Long, Double>>>(emptyList()) }
    var loaded by remember { mutableStateOf(false) }

    androidx.compose.runtime.LaunchedEffect(title) {
        history = loadHistory()
        loaded = true
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkCard,
        title = { Text("Progreso: $title", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp) },
        text = {
            if (!loaded) {
                Text("Cargando...", color = TextGray)
            } else if (history.size < 2) {
                Text("Se necesitan al menos 2 registros para mostrar el gráfico.", color = TextGray, fontSize = 14.sp)
            } else {
                Column {
                    SimpleLineChart(
                        values = history,
                        unit = unit,
                        color = LimeGreen,
                        modifier = Modifier.fillMaxWidth().height(180.dp)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar", color = LimeGreen)
            }
        }
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
