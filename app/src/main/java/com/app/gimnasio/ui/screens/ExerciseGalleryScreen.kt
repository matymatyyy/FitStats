package com.app.gimnasio.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.gimnasio.R
import com.app.gimnasio.data.model.MuscleGroup
import com.app.gimnasio.ui.theme.DarkBackground
import com.app.gimnasio.ui.theme.DarkCard
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
