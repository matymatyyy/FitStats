package com.app.gimnasio.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.gimnasio.ui.theme.DarkBackground
import com.app.gimnasio.ui.theme.DarkBorder
import com.app.gimnasio.ui.theme.DarkCard
import com.app.gimnasio.ui.theme.DarkSurface
import com.app.gimnasio.ui.theme.LimeGreen
import com.app.gimnasio.ui.theme.TextGray
import kotlin.math.pow
import kotlin.math.round

private data class FormulaResult(
    val name: String,
    val value: Double,
    val description: String
)

private fun roundTo(value: Double, decimals: Int = 1): Double {
    val factor = 10.0.pow(decimals)
    return round(value * factor) / factor
}

private fun epley(weight: Double, reps: Int): Double =
    weight * (1 + 0.0333 * reps)

private fun brzycki(weight: Double, reps: Int): Double =
    weight * (36.0 / (37 - reps))

private fun lombardi(weight: Double, reps: Int): Double =
    weight * reps.toDouble().pow(0.10)

private val percentageTable = listOf(
    1 to 100,
    2 to 95,
    3 to 93,
    4 to 90,
    5 to 87,
    6 to 85,
    8 to 80,
    10 to 75,
    12 to 70,
    15 to 65
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PRCalculatorScreen(onBack: () -> Unit) {
    var weightInput by remember { mutableStateOf("") }
    var repsInput by remember { mutableStateOf("") }

    val weight = weightInput.replace(",", ".").toDoubleOrNull()
    val reps = repsInput.toIntOrNull()

    val valid = weight != null && weight > 0 && reps != null && reps in 1..20

    val results: List<FormulaResult> = if (valid) {
        val w = weight!!
        val r = reps!!
        listOf(
            FormulaResult("Epley", epley(w, r), "La más usada y recomendada"),
            FormulaResult("Brzycki", brzycki(w, r), "Precisa con menos de 10 reps"),
            FormulaResult("Lombardi", lombardi(w, r), "Más conservadora")
        )
    } else emptyList()

    val average = if (results.isNotEmpty()) results.map { it.value }.average() else 0.0

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = { Text("Calculadora de PR", color = Color.White) },
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(DarkBackground),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { HeaderCard() }

            item {
                InputsCard(
                    weight = weightInput,
                    reps = repsInput,
                    onWeightChange = { input ->
                        weightInput = input.filter { c -> c.isDigit() || c == '.' || c == ',' }
                    },
                    onRepsChange = { input ->
                        repsInput = input.filter { c -> c.isDigit() }.take(2)
                    }
                )
            }

            if (valid) {
                item { EstimatedOneRMCard(average) }
                item {
                    Text(
                        text = "Según cada fórmula",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                items(results) { result ->
                    FormulaResultCard(result)
                }
                item {
                    Text(
                        text = "Porcentajes del 1RM",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                item { PercentageTableCard(oneRM = average) }
            } else {
                item { HintCard() }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

@Composable
private fun FormulaResultCard(result: FormulaResult) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = result.name,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = result.description,
                    color = TextGray,
                    fontSize = 12.sp
                )
            }
            Text(
                text = "${roundTo(result.value, 1)} kg",
                color = LimeGreen,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
    }
}

@Composable
private fun HeaderCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
            Spacer(modifier = Modifier.size(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Estimá tu 1RM",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    text = "Ingresá el peso que levantaste y cuántas repeticiones hiciste",
                    color = TextGray,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun InputsCard(
    weight: String,
    reps: String,
    onWeightChange: (String) -> Unit,
    onRepsChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            OutlinedTextField(
                value = weight,
                onValueChange = onWeightChange,
                label = { Text("Peso levantado (kg)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                colors = calcTextFieldColors()
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = reps,
                onValueChange = onRepsChange,
                label = { Text("Repeticiones") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = calcTextFieldColors()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Para resultados precisos usá entre 1 y 10 repeticiones.",
                color = TextGray,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun calcTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = LimeGreen,
    unfocusedBorderColor = DarkBorder,
    focusedLabelColor = LimeGreen,
    unfocusedLabelColor = TextGray,
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White,
    cursorColor = LimeGreen
)

@Composable
private fun EstimatedOneRMCard(oneRM: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = LimeGreen.copy(alpha = 0.15f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "1RM estimado",
                color = TextGray,
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${roundTo(oneRM, 1)} kg",
                color = LimeGreen,
                fontWeight = FontWeight.Bold,
                fontSize = 40.sp
            )
            Text(
                text = "Promedio de las 3 fórmulas",
                color = TextGray,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun HintCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "¿Qué es el 1RM?",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Es el peso máximo que podés levantar en una sola repetición. Esta calculadora lo estima a partir de un set que no llevaste al fallo, usando las fórmulas Epley, Brzycki y Lombardi.",
                color = TextGray,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
private fun PercentageTableCard(oneRM: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Reps",
                    color = TextGray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "%",
                    color = TextGray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Peso",
                    color = TextGray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.End
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            percentageTable.forEach { (reps, pct) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {
                    Text(
                        text = "$reps",
                        color = Color.White,
                        fontSize = 14.sp,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "$pct%",
                        color = TextGray,
                        fontSize = 14.sp,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "${roundTo(oneRM * pct / 100.0, 1)} kg",
                        color = LimeGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.End
                    )
                }
            }
        }
    }
}
