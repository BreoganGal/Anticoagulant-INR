package com.example.ui.screens
import com.example.ui.theme.LightTealContainer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppSettings
import com.example.data.DoseEntry
import com.example.data.InrAppointment
import com.example.language.LanguageManager
import com.example.ui.components.PillFractionView

import com.example.ui.theme.StatusDelayYellow
import com.example.ui.theme.StatusInrOrange
import com.example.ui.theme.StatusMissedRed
import com.example.ui.theme.StatusOnTimeGreen
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun CalendarioScreen(
    allDoses: List<DoseEntry>,
    appointments: List<InrAppointment>,
    settings: AppSettings,
    onEditDoseClick: (DoseEntry) -> Unit,
    onEditInrClick: (String, Float?) -> Unit,
    onOpenImportPautaDialog: () -> Unit
) {
    val lang = settings.language
    var currentYearMonth by remember { mutableStateOf(YearMonth.now()) }
    var selectedDateStr by remember { mutableStateOf(LocalDate.now().toString()) }

    val doseMap = remember(allDoses) { allDoses.associateBy { it.date } }
    val appointmentSet = remember(appointments) { appointments.map { it.date }.toSet() }

    // Find last registered dose date in schedule to place "Cita INR" on the day following it
    val lastScheduledDoseDateStr = remember(allDoses) {
        allDoses.filter { it.prescribedFraction.isNotBlank() }.maxByOrNull { it.date }?.date
    }
    val automaticInrApptDateStr = remember(lastScheduledDoseDateStr) {
        lastScheduledDoseDateStr?.let {
            try {
                LocalDate.parse(it).plusDays(1).toString()
            } catch (e: Exception) {
                null
            }
        }
    }

    val selectedDoseEntry = doseMap[selectedDateStr] ?: DoseEntry(
        date = selectedDateStr,
        prescribedFraction = "",
        scheduledTime = settings.reminderTime
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "CALENDARIO PAUTA",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Month Navigation Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val monthName = currentYearMonth.month.getDisplayName(TextStyle.FULL, Locale.forLanguageTag("es")).replaceFirstChar { it.uppercase() }
                    Text(
                        text = "$monthName ${currentYearMonth.year}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Row {
                        IconButton(
                            onClick = { currentYearMonth = currentYearMonth.minusMonths(1) },
                            modifier = Modifier
                                .size(36.dp)
                                .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                        ) {
                            Icon(Icons.Default.ChevronLeft, contentDescription = "Mes anterior")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = { currentYearMonth = currentYearMonth.plusMonths(1) },
                            modifier = Modifier
                                .size(36.dp)
                                .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                        ) {
                            Icon(Icons.Default.ChevronRight, contentDescription = "Mes siguiente")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Weekday Headers (D L M X J V S)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    val daysOfWeek = listOf("D", "L", "M", "X", "J", "V", "S")
                    daysOfWeek.forEach { day ->
                        Text(
                            text = day,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Monthly Days Grid (Rendered in Visual Dose Squares)
                val firstDayOfMonth = currentYearMonth.atDay(1)
                val dayOfWeekOffset = firstDayOfMonth.dayOfWeek.value % 7 // 0 for Sunday
                val daysInMonth = currentYearMonth.lengthOfMonth()

                val totalCells = ((dayOfWeekOffset + daysInMonth + 6) / 7) * 7
                val rows = totalCells / 7

                for (r in 0 until rows) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        for (c in 0..6) {
                            val cellIndex = r * 7 + c
                            val dayNum = cellIndex - dayOfWeekOffset + 1

                            if (dayNum in 1..daysInMonth) {
                                val dateObj = currentYearMonth.atDay(dayNum)
                                val dateKey = dateObj.toString()
                                val isSelected = dateKey == selectedDateStr
                                val isToday = dateKey == LocalDate.now().toString()

                                val entry = doseMap[dateKey]
                                val hasInrAppt = appointmentSet.contains(dateKey) || dateKey == automaticInrApptDateStr
                                val isRegistered = entry != null && entry.prescribedFraction.isNotBlank()

                                // Square Color Background Logic - Blank for unregistered doses
                                val squareBgColor = when {
                                    hasInrAppt -> LightTealContainer.copy(alpha = 0.4f)
                                    entry?.punctuality == "GREEN" -> StatusOnTimeGreen.copy(alpha = 0.15f)
                                    entry?.punctuality == "YELLOW" -> StatusDelayYellow.copy(alpha = 0.15f)
                                    entry?.punctuality == "RED" || entry?.punctuality == "MISSED" -> StatusMissedRed.copy(alpha = 0.15f)
                                    isRegistered -> MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                                    else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f) // Sin cubrir / en blanco
                                }

                                val squareBorderColor = when {
                                    isSelected -> StatusInrOrange
                                    isToday -> MaterialTheme.colorScheme.primary
                                    hasInrAppt -> StatusInrOrange.copy(alpha = 0.5f)
                                    else -> Color.Transparent
                                }

                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(54.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(squareBgColor)
                                        .border(
                                            width = if (isSelected) 2.5.dp else if (isToday) 1.5.dp else 0.5.dp,
                                            color = squareBorderColor,
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                        .clickable { selectedDateStr = dateKey }
                                        .padding(2.dp)
                                ) {
                                    Text(
                                        text = "$dayNum",
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected || isToday) FontWeight.ExtraBold else FontWeight.Bold,
                                        color = if (isSelected) StatusInrOrange else MaterialTheme.colorScheme.onSurface
                                    )

                                    val doseLabel = if (isRegistered) entry!!.prescribedFraction else ""
                                    Text(
                                        text = doseLabel,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (entry?.isTaken == true) StatusOnTimeGreen else MaterialTheme.colorScheme.primary
                                    )

                                    // Color Dot Indicator below day
                                    Row(
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(top = 1.dp)
                                    ) {
                                        if (hasInrAppt) {
                                            Box(
                                                modifier = Modifier
                                                    .size(6.dp)
                                                    .background(StatusInrOrange, CircleShape)
                                            )
                                        } else if (entry != null) {
                                            val dotColor = when (entry.punctuality) {
                                                "GREEN" -> StatusOnTimeGreen
                                                "YELLOW" -> StatusDelayYellow
                                                "RED", "MISSED" -> StatusMissedRed
                                                else -> MaterialTheme.colorScheme.primary
                                            }
                                            Box(
                                                modifier = Modifier
                                                    .size(5.dp)
                                                    .background(dotColor, CircleShape)
                                            )
                                        }
                                    }
                                }
                            } else {
                                Box(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Legend
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    DotLegendItem(color = MaterialTheme.colorScheme.primary, label = "Dosis")
                    DotLegendItem(color = StatusMissedRed, label = "Falta")
                    DotLegendItem(color = StatusInrOrange, label = "Cita INR")
                    DotLegendItem(color = StatusOnTimeGreen, label = "Puntual")
                    DotLegendItem(color = StatusDelayYellow, label = "Retraso")
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Selected Day Details Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                Text(
                    text = selectedDateStr.uppercase(),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Dose Box with Pill Fraction Graphic
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 6.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("DOSIS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = selectedDoseEntry.prescribedFraction.ifBlank { "Sin registrar" },
                                    fontSize = if (selectedDoseEntry.prescribedFraction.isNotBlank()) 22.sp else 14.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (selectedDoseEntry.prescribedFraction.isNotBlank()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = selectedDoseEntry.takenTime ?: selectedDoseEntry.scheduledTime,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            PillFractionView(
                                fractionStr = selectedDoseEntry.prescribedFraction.ifBlank { "0" },
                                size = 44.dp,
                                activeColor = MaterialTheme.colorScheme.primary,
                                inactiveColor = MaterialTheme.colorScheme.surface,
                                lineColor = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    // INR Box
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 6.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("INR", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = selectedDoseEntry.inrValue?.let { String.format("%.2f", it) } ?: "--",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = StatusInrOrange
                            )
                            Text(
                                text = if (selectedDoseEntry.inrValue != null) "Registrado" else "Sin datos",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedButton(
                        onClick = { onEditDoseClick(selectedDoseEntry) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(LanguageManager.getString("editar_dosis", lang), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    OutlinedButton(
                        onClick = { onEditInrClick(selectedDateStr, selectedDoseEntry.inrValue) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(LanguageManager.getString("editar_inr", lang), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // CARD: Import Pauta Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                Text(
                    text = LanguageManager.getString("importar_pauta", lang),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFFBDBDBD), RoundedCornerShape(12.dp))
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onOpenImportPautaDialog() }
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.CloudUpload,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Fotografiar pauta en papel (Cámara / OCR) o adjuntar PDF",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = LanguageManager.getString("toca_para_adjuntar", lang),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
private fun DotLegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .background(color, CircleShape)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
