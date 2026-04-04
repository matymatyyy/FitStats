package com.app.gimnasio.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.clickable
import androidx.glance.action.actionStartActivity
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.app.gimnasio.MainActivity

class NextWorkoutWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val nextWorkout = WidgetDataHelper.getNextWorkout(context)

        provideContent {
            GlanceTheme {
                NextWorkoutContent(nextWorkout)
            }
        }
    }
}

@Composable
private fun NextWorkoutContent(nextWorkout: WidgetNextWorkout?) {
    val bgColor = ColorProvider(Color(0xFF1A1F2E))
    val limeGreen = ColorProvider(Color(0xFFC8FF00))
    val white = ColorProvider(Color.White)
    val gray = ColorProvider(Color(0xFF9E9E9E))

    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .cornerRadius(20.dp)
            .background(bgColor)
            .clickable(actionStartActivity<MainActivity>())
            .padding(16.dp)
    ) {
        Column(
            modifier = GlanceModifier.fillMaxSize(),
            verticalAlignment = Alignment.Top
        ) {
            // Header
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🏋️",
                    style = TextStyle(fontSize = 16.sp)
                )
                Spacer(modifier = GlanceModifier.width(6.dp))
                Text(
                    text = "PRÓXIMO ENTRENAMIENTO",
                    style = TextStyle(
                        color = limeGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                )
            }

            Spacer(modifier = GlanceModifier.height(12.dp))

            if (nextWorkout != null) {
                // Day badge
                Text(
                    text = nextWorkout.dayLabel,
                    style = TextStyle(
                        color = gray,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                )

                Spacer(modifier = GlanceModifier.height(4.dp))

                // Routine name
                Text(
                    text = nextWorkout.routineName,
                    style = TextStyle(
                        color = white,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    ),
                    maxLines = 2
                )

                if (nextWorkout.description.isNotBlank()) {
                    Spacer(modifier = GlanceModifier.height(4.dp))
                    Text(
                        text = nextWorkout.description,
                        style = TextStyle(
                            color = gray,
                            fontSize = 12.sp
                        ),
                        maxLines = 2
                    )
                }

                Spacer(modifier = GlanceModifier.defaultWeight())

                Text(
                    text = "Tocá para abrir →",
                    style = TextStyle(
                        color = limeGreen,
                        fontSize = 11.sp
                    )
                )
            } else {
                Text(
                    text = "Sin entrenamiento\npendiente",
                    style = TextStyle(
                        color = white,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                )

                Spacer(modifier = GlanceModifier.defaultWeight())

                Text(
                    text = "Día de descanso 💤",
                    style = TextStyle(
                        color = gray,
                        fontSize = 12.sp
                    )
                )
            }
        }
    }
}

class NextWorkoutWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = NextWorkoutWidget()
}
