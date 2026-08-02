package com.example.ui.components

import androidx.compose.material3.MaterialTheme

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun PillFractionView(
    fractionStr: String,
    modifier: Modifier = Modifier,
    size: Dp = 64.dp,
    activeColor: Color = MaterialTheme.colorScheme.primary,
    inactiveColor: Color = Color.White,
    lineColor: Color = Color(0xFF004D40)
) {
    val (numWhole, fractionVal) = parseFraction(fractionStr)
    val totalDose = numWhole + fractionVal

    if (numWhole >= 3 || totalDose > 3.0f) {
        // 4 Pills Graphic (e.g., 3 pastillas completas + fracción seleccionada, total 4 círculos)
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val pillSize = (size * 0.55f)
            SinglePillGraphic(fillFraction = 1.0f, size = pillSize, activeColor = activeColor, inactiveColor = inactiveColor, lineColor = lineColor)
            SinglePillGraphic(fillFraction = 1.0f, size = pillSize, activeColor = activeColor, inactiveColor = inactiveColor, lineColor = lineColor)
            SinglePillGraphic(fillFraction = 1.0f, size = pillSize, activeColor = activeColor, inactiveColor = inactiveColor, lineColor = lineColor)
            val fourthPillFill = if (numWhole >= 3) fractionVal.coerceIn(0f, 1f) else (totalDose - 3.0f).coerceIn(0f, 1f)
            SinglePillGraphic(fillFraction = fourthPillFill, size = pillSize, activeColor = activeColor, inactiveColor = inactiveColor, lineColor = lineColor)
        }
    } else if (numWhole == 2 || totalDose > 2.0f) {
        // 3 Pills Graphic (e.g., 2 pastillas completas + fracción)
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val pillSize = (size * 0.65f)
            SinglePillGraphic(fillFraction = 1.0f, size = pillSize, activeColor = activeColor, inactiveColor = inactiveColor, lineColor = lineColor)
            SinglePillGraphic(fillFraction = 1.0f, size = pillSize, activeColor = activeColor, inactiveColor = inactiveColor, lineColor = lineColor)
            val thirdPillFill = if (numWhole >= 2) fractionVal.coerceIn(0f, 1f) else (totalDose - 2.0f).coerceIn(0f, 1f)
            SinglePillGraphic(fillFraction = thirdPillFill, size = pillSize, activeColor = activeColor, inactiveColor = inactiveColor, lineColor = lineColor)
        }
    } else if (numWhole == 1 || totalDose > 1.0f) {
        // 2 Pills Graphic (e.g., 1 pastilla + fracción)
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val pillSize = (size * 0.72f)
            SinglePillGraphic(
                fillFraction = 1.0f,
                size = pillSize,
                activeColor = activeColor,
                inactiveColor = inactiveColor,
                lineColor = lineColor
            )
            val secondPillFill = if (numWhole >= 1) fractionVal.coerceIn(0f, 1f) else (totalDose - 1.0f).coerceIn(0f, 1f)
            SinglePillGraphic(
                fillFraction = secondPillFill,
                size = pillSize,
                activeColor = activeColor,
                inactiveColor = inactiveColor,
                lineColor = lineColor
            )
        }
    } else {
        // Single Pill graphic (e.g. 1/8, 1/4, 1/3, 1/2, 3/4, 1)
        SinglePillGraphic(
            fillFraction = fractionVal.coerceIn(0f, 1f),
            size = size,
            activeColor = activeColor,
            inactiveColor = inactiveColor,
            lineColor = lineColor,
            modifier = modifier
        )
    }
}

@Composable
private fun SinglePillGraphic(
    fillFraction: Float,
    size: Dp,
    activeColor: Color,
    inactiveColor: Color,
    lineColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val center = Offset(size.toPx() / 2, size.toPx() / 2)
            val radius = (size.toPx() / 2) - 2.dp.toPx()
            val topLx = center.x - radius
            val topLy = center.y - radius
            val rectSize = Size(radius * 2, radius * 2)

            // Background circle (unfilled white/inactive area = NOT TAKEN / BLANCO)
            drawCircle(
                color = inactiveColor,
                radius = radius,
                center = center
            )

            // Fill exact fraction taken (COLOREADO = TAKEN / TOMADO)
            if (fillFraction > 0f) {
                val sweepAngle = (fillFraction * 360f).coerceIn(0f, 360f)

                if (sweepAngle >= 359f) {
                    drawCircle(
                        color = activeColor,
                        radius = radius,
                        center = center
                    )
                } else if (sweepAngle > 0f) {
                    drawArc(
                        color = activeColor,
                        startAngle = -90f, // Start at top 12 o'clock
                        sweepAngle = sweepAngle,
                        useCenter = true,
                        topLeft = Offset(topLx, topLy),
                        size = rectSize
                    )
                }
            }

            // Crosshair score lines separating pill quarters
            drawLine(
                color = lineColor,
                start = Offset(center.x, center.y - radius),
                end = Offset(center.x, center.y + radius),
                strokeWidth = 1.5.dp.toPx()
            )
            drawLine(
                color = lineColor,
                start = Offset(center.x - radius, center.y),
                end = Offset(center.x + radius, center.y),
                strokeWidth = 1.5.dp.toPx()
            )

            // Outer ring stroke
            drawCircle(
                color = lineColor,
                radius = radius,
                center = center,
                style = Stroke(width = 1.5.dp.toPx())
            )
        }
    }
}

@Composable
fun FractionSelectorChips(
    selectedFraction: String,
    onFractionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val options = listOf("1/8", "1/4", "1/3", "1/2", "5/8", "3/4", "7/8")

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        options.forEach { option ->
            val isSelected = selectedFraction == option || (selectedFraction == "2/4" && option == "1/2")
            Surface(
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable { onFractionSelected(option) },
                color = if (isSelected) Color.White else Color(0x22FFFFFF),
                shape = CircleShape
            ) {
                Text(
                    text = option,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }
    }
}

fun parseFraction(fractionStr: String): Pair<Int, Float> {
    val cleaned = fractionStr.lowercase().replace("pastillas", "").replace("pastilla", "").trim()
    if (cleaned.isBlank()) return Pair(0, 0f)
    val parts = cleaned.split(" ").filter { it.isNotBlank() }
    var whole = 0
    var fracStr = cleaned

    if (parts.size > 1) {
        whole = parts[0].toIntOrNull() ?: 0
        fracStr = parts[1]
    }

    val fractionVal = when (fracStr) {
        "1/8" -> 0.125f
        "1/4" -> 0.25f
        "1/3" -> 0.3333f
        "1/2", "2/4" -> 0.5f
        "5/8" -> 0.625f
        "2/3" -> 0.6667f
        "3/4" -> 0.75f
        "7/8" -> 0.875f
        "1" -> 1.0f
        else -> {
            val f = fracStr.toFloatOrNull()
            if (f != null) {
                if (parts.size == 1 && f >= 1.0f) {
                    whole = f.toInt()
                    f - whole
                } else {
                    f
                }
            } else {
                0.75f
            }
        }
    }

    return Pair(whole, fractionVal)
}
