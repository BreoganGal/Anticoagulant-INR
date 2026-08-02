package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DoseEntry

import com.example.ui.theme.StatusInrOrange
import com.example.ui.theme.StatusMissedRed

@Composable
fun FourteenDayChart(
    doses: List<DoseEntry>,
    modifier: Modifier = Modifier
) {
    // Sort oldest to newest (last 14 days)
    val sortedDoses = doses.sortedBy { it.date }.takeLast(14)

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ÚLTIMOS 14 DÍAS",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "MG / TOMA",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 14-Bar Canvas Chart
            val placeholderBarColor = MaterialTheme.colorScheme.surfaceVariant
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
            ) {
                val primaryColor = androidx.compose.material3.MaterialTheme.colorScheme.primary

                Canvas(modifier = Modifier.fillMaxWidth().height(110.dp)) {
                    val width = size.width
                    val height = size.height
                    val barCount = 14
                    val barWidth = 14.dp.toPx()
                    val totalBarSpace = barWidth * barCount
                    val spacing = (width - totalBarSpace) / (barCount - 1)

                    val maxHeightFactor = 80.dp.toPx()

                    for (i in 0 until barCount) {
                        val entry = sortedDoses.getOrNull(i)
                        val x = i * (barWidth + spacing)

                        if (entry != null) {
                            val isMissed = entry.punctuality == "MISSED" || (!entry.isTaken && entry.punctuality != "PENDING")
                            val isToday = i == barCount - 1

                            // Dose bar height based on fraction
                            val fractionVal = when (entry.prescribedFraction) {
                                "1/8" -> 0.2f
                                "1/4" -> 0.35f
                                "1/2", "2/4" -> 0.55f
                                "3/4" -> 0.8f
                                "1" -> 1.0f
                                else -> 0.7f
                            }
                            val barH = maxOf(18.dp.toPx(), maxHeightFactor * fractionVal)
                            val y = height - barH

                            if (isMissed) {
                                // Draw Solid Red Bar for Missed Day
                                drawRoundRect(
                                    color = StatusMissedRed,
                                    topLeft = Offset(x, y),
                                    size = Size(barWidth, barH),
                                    cornerRadius = CornerRadius(4.dp.toPx())
                                )
                            } else {
                                // Solid Bar for Taken / Prescribed (Gold/Amber for Today, Teal for past days)
                                val barColor = if (isToday) Color(0xFFFFB300) else primaryColor
                                drawRoundRect(
                                    color = barColor,
                                    topLeft = Offset(x, y),
                                    size = Size(barWidth, barH),
                                    cornerRadius = CornerRadius(4.dp.toPx())
                                )
                                if (isToday) {
                                    // Highlight stroke for today
                                    drawRoundRect(
                                        color = Color.White,
                                        topLeft = Offset(x - 1.dp.toPx(), y - 1.dp.toPx()),
                                        size = Size(barWidth + 2.dp.toPx(), barH + 2.dp.toPx()),
                                        cornerRadius = CornerRadius(5.dp.toPx()),
                                        style = Stroke(width = 1.5.dp.toPx())
                                    )
                                }
                            }

                            // Draw Orange Dot above bar if INR Test was recorded on that day
                            if (entry.inrValue != null) {
                                drawCircle(
                                    color = StatusInrOrange,
                                    radius = 4.dp.toPx(),
                                    center = Offset(x + barWidth / 2, maxOf(8.dp.toPx(), y - 12.dp.toPx()))
                                )
                            }
                        } else {
                            // Placeholder faint line for missing day record
                            drawRoundRect(
                                color = placeholderBarColor,
                                topLeft = Offset(x, height - 20.dp.toPx()),
                                size = Size(barWidth, 20.dp.toPx()),
                                cornerRadius = CornerRadius(4.dp.toPx())
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Chart Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LegendItem(color = androidx.compose.material3.MaterialTheme.colorScheme.primary, label = "Toma")
                Spacer(modifier = Modifier.width(12.dp))
                LegendItem(color = Color(0xFFFFB300), label = "Hoy")
                Spacer(modifier = Modifier.width(12.dp))
                LegendItem(color = StatusInrOrange, label = "Test INR", isCircle = true)
                Spacer(modifier = Modifier.width(12.dp))
                LegendItem(color = StatusMissedRed, label = "Falta")
            }
        }
    }
}

@Composable
private fun LegendItem(
    color: Color,
    label: String,
    isCircle: Boolean = false,
    isDotted: Boolean = false
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (isCircle) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(color, CircleShape)
            )
        } else if (isDotted) {
            Box(
                modifier = Modifier
                    .size(10.dp, 10.dp)
                    .background(Color.Transparent)
            ) {
                val primaryColor = androidx.compose.material3.MaterialTheme.colorScheme.primary

                Canvas(modifier = Modifier.size(10.dp)) {
                    drawRoundRect(
                        color = color,
                        cornerRadius = CornerRadius(2.dp.toPx()),
                        style = Stroke(
                            width = 1.5.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f), 0f)
                        )
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .size(10.dp, 10.dp)
                    .background(color, RoundedCornerShape(2.dp))
            )
        }
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
