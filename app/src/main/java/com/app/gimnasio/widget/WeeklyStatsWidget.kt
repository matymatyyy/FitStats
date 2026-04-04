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
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.app.gimnasio.MainActivity

class WeeklyStatsWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val stats = WidgetDataHelper.getWeeklyStats(context)

        provideContent {
            GlanceTheme {
                WeeklyStatsContent(stats)
            }
        }
    }
}

@Composable
private fun WeeklyStatsContent(stats: WidgetWeeklyStats) {
    val bgColor = ColorProvider(Color(0xFF1A1F2E))
    val cardColor = ColorProvider(Color(0xFF232836))
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
            modifier = GlanceModifier.fillMaxSize()
        ) {
            // Header
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📊",
                    style = TextStyle(fontSize = 14.sp)
                )
                Spacer(modifier = GlanceModifier.width(6.dp))
                Text(
                    text = "ESTA SEMANA",
                    style = TextStyle(
                        color = limeGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                )
            }

            Spacer(modifier = GlanceModifier.height(12.dp))

            // Stats grid - 2x2
            Row(
                modifier = GlanceModifier.fillMaxWidth()
            ) {
                // Workouts
                Box(
                    modifier = GlanceModifier
                        .defaultWeight()
                        .cornerRadius(12.dp)
                        .background(cardColor)
                        .padding(10.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "🏋️",
                            style = TextStyle(fontSize = 14.sp)
                        )
                        Spacer(modifier = GlanceModifier.height(4.dp))
                        Text(
                            text = "${stats.workouts}",
                            style = TextStyle(
                                color = white,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                textAlign = TextAlign.Center
                            )
                        )
                        Text(
                            text = "Entrenos",
                            style = TextStyle(
                                color = gray,
                                fontSize = 10.sp,
                                textAlign = TextAlign.Center
                            )
                        )
                    }
                }

                Spacer(modifier = GlanceModifier.width(8.dp))

                // Minutes
                Box(
                    modifier = GlanceModifier
                        .defaultWeight()
                        .cornerRadius(12.dp)
                        .background(cardColor)
                        .padding(10.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "⏱️",
                            style = TextStyle(fontSize = 14.sp)
                        )
                        Spacer(modifier = GlanceModifier.height(4.dp))
                        Text(
                            text = "${stats.totalMinutes}",
                            style = TextStyle(
                                color = white,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                textAlign = TextAlign.Center
                            )
                        )
                        Text(
                            text = "Minutos",
                            style = TextStyle(
                                color = gray,
                                fontSize = 10.sp,
                                textAlign = TextAlign.Center
                            )
                        )
                    }
                }
            }

            Spacer(modifier = GlanceModifier.height(8.dp))

            Row(
                modifier = GlanceModifier.fillMaxWidth()
            ) {
                // Sets
                Box(
                    modifier = GlanceModifier
                        .defaultWeight()
                        .cornerRadius(12.dp)
                        .background(cardColor)
                        .padding(10.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "✅",
                            style = TextStyle(fontSize = 14.sp)
                        )
                        Spacer(modifier = GlanceModifier.height(4.dp))
                        Text(
                            text = "${stats.totalSets}",
                            style = TextStyle(
                                color = white,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                textAlign = TextAlign.Center
                            )
                        )
                        Text(
                            text = "Series",
                            style = TextStyle(
                                color = gray,
                                fontSize = 10.sp,
                                textAlign = TextAlign.Center
                            )
                        )
                    }
                }

                Spacer(modifier = GlanceModifier.width(8.dp))

                // Calories
                Box(
                    modifier = GlanceModifier
                        .defaultWeight()
                        .cornerRadius(12.dp)
                        .background(cardColor)
                        .padding(10.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "🔥",
                            style = TextStyle(fontSize = 14.sp)
                        )
                        Spacer(modifier = GlanceModifier.height(4.dp))
                        Text(
                            text = "${stats.calories}",
                            style = TextStyle(
                                color = white,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                textAlign = TextAlign.Center
                            )
                        )
                        Text(
                            text = "kcal",
                            style = TextStyle(
                                color = gray,
                                fontSize = 10.sp,
                                textAlign = TextAlign.Center
                            )
                        )
                    }
                }
            }
        }
    }
}

class WeeklyStatsWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = WeeklyStatsWidget()
}
