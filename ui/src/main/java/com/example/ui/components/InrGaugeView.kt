package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.ui.theme.StatusDelayYellow
import com.example.ui.theme.StatusOnTimeGreen
import com.example.ui.theme.StatusOnTimeGreenBg

@Composable
fun InrGaugeCard(
    latestInr: Float?,
    dateStr: String,
    targetMin: Float = 2.0f,
    targetMax: Float = 3.5f,
    modifier: Modifier = Modifier
) {
    val inrVal = latestInr ?: 2.67f
    val isInRange = inrVal in targetMin..targetMax
    val statusText = if (isInRange) "EN RANGO" else if (inrVal < targetMin) "BAJO" else "ALTO"
    val statusColor = if (isInRange) StatusOnTimeGreen else StatusDelayYellow
    val statusBg = if (isInRange) StatusOnTimeGreenBg else Color(0xFFFFF3E0)

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
                    text = "ÚLTIMO INR",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.sp
                )
                Text(
                    text = dateStr,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = String.format("%.2f", inrVal),
                    fontSize = 36.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.width(12.dp))
                Surface(
                    color = statusBg,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = statusText,
                        color = statusColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Range Bar
            val totalSpan = 4.5f // 1.0 to 5.5
            val minPos = ((targetMin - 1.0f) / totalSpan).coerceIn(0f, 1f)
            val maxPos = ((targetMax - 1.0f) / totalSpan).coerceIn(0f, 1f)
            val valPos = ((inrVal - 1.0f) / totalSpan).coerceIn(0f, 1f)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFFE0E0E0))
            ) {
                // Highlight Target Range
                Box(
                    modifier = Modifier
                        .fillMaxWidth(maxPos - minPos)
                        .height(8.dp)
                        .padding(start = 0.dp)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                )
                // Marker
                Box(
                    modifier = Modifier
                        .padding(start = (valPos * 280).dp) // Scaled
                        .width(4.dp)
                        .height(8.dp)
                        .background(statusColor, RoundedCornerShape(2.dp))
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("1.0", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("↑ TARGET $targetMin - $targetMax", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Text("5.5", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
