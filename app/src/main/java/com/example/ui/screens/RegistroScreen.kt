package com.example.ui.screens

import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppSettings
import com.example.data.DoseEntry
import com.example.language.LanguageManager
import com.example.ui.components.InrGaugeCard

import com.example.ui.theme.StatusDelayYellow
import com.example.ui.theme.StatusDelayYellowBg
import com.example.ui.theme.StatusInrOrange
import com.example.ui.theme.StatusInrOrangeBg
import com.example.ui.theme.StatusMissedRed
import com.example.ui.theme.StatusMissedRedBg
import com.example.ui.theme.StatusOnTimeGreen
import com.example.ui.theme.StatusOnTimeGreenBg

@Composable
fun RegistroScreen(
    recent7Logs: List<DoseEntry>,
    allDoses: List<DoseEntry>,
    settings: AppSettings,
    onOpenExportPdfDialog: () -> Unit,
    onDeleteDose: (DoseEntry) -> Unit = {},
    onEditDose: (DoseEntry) -> Unit = {}
) {
    val lang = settings.language
    var selectedSubTab by remember { mutableIntStateOf(0) } // 0 = Historial, 1 = Estadísticas
    var selectedPeriodIndex by remember { mutableIntStateOf(1) } // 0=1M, 1=3M, 2=6M, 3=1Y, 4=Todo
    var selectedFilterIndex by remember { mutableIntStateOf(0) } // 0=Todas, 1=Tomas, 2=Faltas, 3=Citas INR

    var dateRangeMode by remember { mutableStateOf("7DAYS") } // "7DAYS", "1MONTH", "CUSTOM"
    var isCustomDateMenuOpen by remember { mutableStateOf(false) }
    var startDateInput by remember { mutableStateOf(java.time.LocalDate.now().minusDays(6).toString()) }
    var endDateInput by remember { mutableStateOf(java.time.LocalDate.now().toString()) }

    var doseToDelete by remember { mutableStateOf<DoseEntry?>(null) }

    val periods = listOf("1 Mes", "3 Meses", "6 Meses", "1 Año", "Todo")
    val filters = listOf("Todas", "Tomas", "Faltas", "Citas INR")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Subtab Row: HISTORIAL vs ESTADÍSTICAS
        TabRow(
            selectedTabIndex = selectedSubTab,
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onSurface,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedSubTab]),
                    color = MaterialTheme.colorScheme.primary,
                    height = 3.dp
                )
            }
        ) {
            Tab(
                selected = selectedSubTab == 0,
                onClick = { selectedSubTab = 0 },
                text = {
                    Text(
                        text = LanguageManager.getString("historial", lang),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            )
            Tab(
                selected = selectedSubTab == 1,
                onClick = { selectedSubTab = 1 },
                text = {
                    Text(
                        text = LanguageManager.getString("estadisticas", lang),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (selectedSubTab == 0) {
            // SUBTAB 0: HISTORIAL DE TOMAS
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
                        text = "HISTORIAL DE TOMAS Y REGISTROS",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Filter selector chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        filters.forEachIndexed { idx, filterLabel ->
                            val isSel = selectedFilterIndex == idx
                            Surface(
                                color = if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.clickable { selectedFilterIndex = idx }
                            ) {
                                Text(
                                    text = filterLabel,
                                    color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    val rawLogs = (if (allDoses.isNotEmpty()) allDoses else recent7Logs)
                        .sortedByDescending { it.date }

                    val todayStr = java.time.LocalDate.now().toString()
                    val today7DaysAgo = java.time.LocalDate.now().minusDays(6).toString()
                    val today30DaysAgo = java.time.LocalDate.now().minusDays(30).toString()

                    val dateFiltered = rawLogs.filter { item ->
                        when (dateRangeMode) {
                            "1MONTH" -> item.date in today30DaysAgo..todayStr
                            "CUSTOM" -> item.date in startDateInput..endDateInput
                            else -> item.date in today7DaysAgo..todayStr // Default 7 days
                        }
                    }

                    val filteredLogs = dateFiltered.filter { item ->
                        when (selectedFilterIndex) {
                            1 -> item.isTaken && item.punctuality != "MISSED"
                            2 -> item.punctuality == "MISSED"
                            3 -> item.inrValue != null
                            else -> item.date <= todayStr
                        }
                    }

                    if (filteredLogs.isEmpty()) {
                        Text(
                            text = "No hay registros para este filtro o rango de fechas.",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                    } else {
                        filteredLogs.forEachIndexed { index, item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { doseToDelete = item }
                                    .padding(vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.date.uppercase(),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(top = 2.dp)
                                    ) {
                                        val timeStr = item.takenTime ?: item.scheduledTime
                                        Text(
                                            text = "$timeStr · ",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )

                                        // Punctuality Badge
                                        val (statusText, textColor, bgColor) = when (item.punctuality) {
                                            "GREEN" -> Triple(LanguageManager.getString("puntual", lang), StatusOnTimeGreen, StatusOnTimeGreenBg)
                                            "YELLOW" -> Triple(LanguageManager.getString("retraso", lang), StatusDelayYellow, StatusDelayYellowBg)
                                            "PENDING" -> Triple(LanguageManager.getString("pendiente", lang) ?: "PENDIENTE", MaterialTheme.colorScheme.onSurfaceVariant, MaterialTheme.colorScheme.surfaceVariant)
                                            else -> Triple(LanguageManager.getString("falta", lang), StatusMissedRed, StatusMissedRedBg)
                                        }

                                        Surface(
                                            color = bgColor,
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = statusText,
                                                color = textColor,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 10.sp,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (item.inrValue != null) {
                                        Surface(
                                            color = StatusInrOrangeBg,
                                            shape = RoundedCornerShape(6.dp),
                                            modifier = Modifier.padding(end = 8.dp)
                                        ) {
                                            Text(
                                                text = "INR ${item.inrValue}",
                                                color = StatusInrOrange,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 10.sp,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                            )
                                        }
                                    }

                                    Text(
                                        text = if (item.punctuality == "MISSED") "FALTA" else "${item.prescribedFraction} pastilla",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 14.sp,
                                        color = if (item.punctuality == "MISSED") StatusMissedRed else MaterialTheme.colorScheme.primary
                                    )
                                }
                            }

                            if (index < filteredLogs.size - 1) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(1.dp)
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // DATE RANGE FILTER SELECTOR CARD (AT END OF REGISTRO)
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
                        text = "FILTRAR HISTORIAL POR RANGO DE FECHAS",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Button(
                            onClick = {
                                dateRangeMode = "7DAYS"
                                isCustomDateMenuOpen = false
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (dateRangeMode == "7DAYS") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Text(
                                "Últimos 7 días",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (dateRangeMode == "7DAYS") Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Button(
                            onClick = {
                                dateRangeMode = "1MONTH"
                                isCustomDateMenuOpen = false
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (dateRangeMode == "1MONTH") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Text(
                                "1 Mes",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (dateRangeMode == "1MONTH") Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Button(
                            onClick = {
                                dateRangeMode = "CUSTOM"
                                isCustomDateMenuOpen = true
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (dateRangeMode == "CUSTOM") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Text(
                                "Personalizado",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (dateRangeMode == "CUSTOM") Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    if (dateRangeMode == "CUSTOM" || isCustomDateMenuOpen) {
                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "SELECCIONAR RANGO PERSONALIZADO (DESDE / HASTA)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = startDateInput,
                                onValueChange = {
                                    startDateInput = it
                                    dateRangeMode = "CUSTOM"
                                },
                                label = { Text("Fecha Inicio (AAAA-MM-DD)", fontSize = 10.sp) },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = endDateInput,
                                onValueChange = {
                                    endDateInput = it
                                    dateRangeMode = "CUSTOM"
                                },
                                label = { Text("Fecha Fin (AAAA-MM-DD)", fontSize = 10.sp) },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // EXPORT REGISTRO EN PDF Card
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
                        text = LanguageManager.getString("exportar_registro", lang),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = onOpenExportPdfDialog,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PictureAsPdf,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = LanguageManager.getString("exportar_pdf_btn", lang),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        } else {
            // SUBTAB 1: ESTADÍSTICAS COMPLETAS & TENDENCIAS INR

            // Period Filter selector chips [1M] [3M] [6M] [1Y] [Todo]
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                periods.forEachIndexed { idx, p ->
                    val isSel = selectedPeriodIndex == idx
                    Surface(
                        color = if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .clickable { selectedPeriodIndex = idx }
                            .border(
                                width = if (isSel) 0.dp else 1.dp,
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(20.dp)
                            )
                    ) {
                        Text(
                            text = p,
                            color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Dynamic Stats calculations based on selected period
            val cutoffDate = when (selectedPeriodIndex) {
                0 -> java.time.LocalDate.now().minusDays(30).toString()
                1 -> java.time.LocalDate.now().minusDays(90).toString()
                2 -> java.time.LocalDate.now().minusDays(180).toString()
                3 -> java.time.LocalDate.now().minusDays(365).toString()
                else -> "2000-01-01"
            }
            val baseDosesList = allDoses
            val periodDoses = baseDosesList.filter { it.date >= cutoffDate && it.date <= java.time.LocalDate.now().toString() }

            val inrEntries = periodDoses.filter { it.inrValue != null }.sortedBy { it.date }
            val latestInrVal = inrEntries.lastOrNull()?.inrValue ?: 2.50f
            val latestInrDateStr = inrEntries.lastOrNull()?.date ?: "Sin datos"
            
            val targetMin = settings.targetInrMin
            val targetMax = settings.targetInrMax
            val inRangeCount = inrEntries.count { (it.inrValue ?: 0f) in targetMin..targetMax }
            val ttrPercent = if (inrEntries.isNotEmpty()) ((inRangeCount.toFloat() / inrEntries.size) * 100).toInt() else 0

            fun calcFraction(str: String): Float {
                return when (str.trim()) {
                    "1/8" -> 0.125f
                    "1/4" -> 0.25f
                    "1/3" -> 0.33f
                    "1/2" -> 0.5f
                    "3/4" -> 0.75f
                    "1" -> 1.0f
                    "1 1/2" -> 1.5f
                    "2" -> 2.0f
                    "2 1/2" -> 2.5f
                    "3" -> 3.0f
                    else -> str.toFloatOrNull() ?: 1.0f
                }
            }

            val allTakenEntries = baseDosesList.filter { it.isTaken }
            val totalPills = allTakenEntries.sumOf { calcFraction(it.prescribedFraction).toDouble() }.toFloat()
            val totalDays = baseDosesList.map { it.date }.distinct().size.coerceAtLeast(1)
            val dailyAvg = if (totalDays > 0 && allTakenEntries.isNotEmpty()) totalPills / totalDays else 0f
            val weeklyAvg = dailyAvg * 7
            val monthlyAvg = dailyAvg * 30

            val totalEntries = periodDoses.size.coerceAtLeast(1)
            val onTimeCount = periodDoses.count { it.isTaken && it.punctuality != "DELAYED" && it.punctuality != "MISSED" }
            val delayedCount = periodDoses.count { it.isTaken && it.punctuality == "DELAYED" }
            val missedCount = periodDoses.count { !it.isTaken || it.punctuality == "MISSED" }

            val onTimePct = if (periodDoses.isNotEmpty()) (onTimeCount * 100) / totalEntries else 94
            val delayedPct = if (periodDoses.isNotEmpty()) (delayedCount * 100) / totalEntries else 4
            val missedPct = if (periodDoses.isNotEmpty()) (missedCount * 100) / totalEntries else 2

            InrGaugeCard(
                latestInr = latestInrVal,
                dateStr = if (latestInrDateStr == "Sin datos") "Sin datos de INR" else "ÚLTIMO INR: $latestInrDateStr",
                targetMin = targetMin,
                targetMax = targetMax
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Trend Chart for INR over time
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.AutoMirrored.Filled.ShowChart, contentDescription = null, tint = StatusInrOrange)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "TENDENCIA DE INR EN EL TIEMPO",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text("Rango ${targetMin}-${targetMax}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    val dataPoints = inrEntries.takeLast(5).map { Pair(it.date.substring(5).replace("-", "/"), it.inrValue!!) }
                    InrTrendLineChart(targetMin = targetMin, targetMax = targetMax, dataPoints = dataPoints)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Dose Averages (Promedio Diario, Semanal, Mensual)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(18.dp)) {
                    Text(
                        text = "PROMEDIOS DE DOSIS DE MEDICACIÓN",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        DoseAvgTile(title = "Promedio Diario", valStr = String.format("%.2f", dailyAvg), unit = "pastillas/día")
                        DoseAvgTile(title = "Promedio Semanal", valStr = String.format("%.2f", weeklyAvg), unit = "pastillas/sem")
                        DoseAvgTile(title = "Promedio Mensual", valStr = String.format("%.1f", monthlyAvg), unit = "pastillas/mes")
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Adherence Stats Card (TTR, Tomas a tiempo, retrasos)
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
                        text = "ADHERENCIA Y TIEMPO EN RANGO (TTR)",
                        style = MaterialTheme.typography.labelMedium,
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
                        Column {
                            Text(
                                text = "$ttrPercent%",
                                fontSize = 36.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Tiempo en Rango Terapéutico (TTR)",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Surface(
                            color = StatusOnTimeGreenBg,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = if (ttrPercent >= 70) "Control Óptimo" else "Atención Requerida",
                                color = if (ttrPercent >= 70) StatusOnTimeGreen else StatusInrOrange,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StatTile(value = "$onTimePct%", label = "A tiempo", color = StatusOnTimeGreen)
                        StatTile(value = "$delayedPct%", label = "Con retraso", color = StatusDelayYellow)
                        StatTile(value = "$missedPct%", label = "Omitidas", color = StatusMissedRed)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }

    // Delete Confirmation Dialog
    doseToDelete?.let { entry ->
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { doseToDelete = null },
            title = { Text("Eliminar registro", fontWeight = FontWeight.Bold) },
            text = { Text("¿Deseas eliminar la toma del día ${entry.date}? Esta acción no se puede deshacer.") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteDose(entry)
                        doseToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusMissedRed)
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { doseToDelete = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun InrTrendLineChart(targetMin: Float, targetMax: Float, dataPoints: List<Pair<String, Float>>) {
    val samplePoints = if (dataPoints.size >= 2) dataPoints else if (dataPoints.size == 1) listOf(dataPoints[0], dataPoints[0]) else listOf("N/A" to targetMin, "N/A" to targetMin)

    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            val primaryColor = MaterialTheme.colorScheme.primary
                    Canvas(modifier = Modifier.fillMaxSize()) {
                val leftPadding = 30.dp.toPx()
                val bottomPadding = 24.dp.toPx()
                val topPadding = 24.dp.toPx()
                val w = size.width - leftPadding
                val h = size.height - bottomPadding - topPadding

                val minY = 1.0f
                val maxY = 4.5f

                fun getY(valInr: Float): Float {
                    val clamped = valInr.coerceIn(minY, maxY)
                    val ratio = (clamped - minY) / (maxY - minY)
                    return topPadding + (h - (ratio * h))
                }

                fun getX(index: Int): Float {
                    if (samplePoints.size <= 1) return leftPadding + (w / 2f)
                    val step = w / (samplePoints.size - 1)
                    return leftPadding + (index * step)
                }

                // Shaded band for target range
                val topTargetY = getY(targetMax)
                val bottomTargetY = getY(targetMin)

                drawRect(
                    color = Color(0x22009688),
                    topLeft = Offset(leftPadding, topTargetY),
                    size = androidx.compose.ui.geometry.Size(w, bottomTargetY - topTargetY)
                )

                // Dotted lines for target range
                val dashEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
                drawLine(
                    color = primaryColor,
                    start = Offset(leftPadding, topTargetY),
                    end = Offset(size.width, topTargetY),
                    pathEffect = dashEffect,
                    strokeWidth = 1.5.dp.toPx()
                )
                drawLine(
                    color = primaryColor,
                    start = Offset(leftPadding, bottomTargetY),
                    end = Offset(size.width, bottomTargetY),
                    pathEffect = dashEffect,
                    strokeWidth = 1.5.dp.toPx()
                )

                // Draw vertical grid drop lines down to date axis
                samplePoints.forEachIndexed { i, _ ->
                    val px = getX(i)
                    drawLine(
                        color = Color.LightGray.copy(alpha = 0.5f),
                        start = Offset(px, topPadding),
                        end = Offset(px, topPadding + h),
                        pathEffect = dashEffect,
                        strokeWidth = 1.dp.toPx()
                    )
                }

                // Trend line path
                val trendPath = Path()
                samplePoints.forEachIndexed { i, pt ->
                    val px = getX(i)
                    val py = getY(pt.second)
                    if (i == 0) trendPath.moveTo(px, py) else trendPath.lineTo(px, py)
                }

                drawPath(
                    path = trendPath,
                    color = StatusInrOrange,
                    style = Stroke(width = 3.dp.toPx())
                )

                // Trend nodes and circles
                samplePoints.forEachIndexed { i, pt ->
                    val px = getX(i)
                    val py = getY(pt.second)
                    drawCircle(color = StatusInrOrange, radius = 6.dp.toPx(), center = Offset(px, py))
                    drawCircle(color = Color.White, radius = 3.dp.toPx(), center = Offset(px, py))
                }
            }

            // Overlay values and dates as Composable elements for crisp typography
            val leftPadDp = 30.dp
            val topPadDp = 24.dp
            val chartH = 180.dp - 48.dp

            samplePoints.forEachIndexed { i, pt ->
                val fraction = i.toFloat() / (samplePoints.size - 1)
                // Values above nodes
                val clamped = pt.second.coerceIn(1.0f, 4.5f)
                val yRatio = (clamped - 1.0f) / (4.5f - 1.0f)

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = leftPadDp, top = topPadDp, bottom = 24.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .align(
                                androidx.compose.ui.BiasAlignment(
                                    horizontalBias = (fraction * 2) - 1f,
                                    verticalBias = (1f - yRatio * 2f) - 0.22f
                                )
                            )
                    ) {
                        Surface(
                            color = StatusInrOrange,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = String.format(java.util.Locale.US, "%.1f", pt.second),
                                color = Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }

                    // Date labels below X axis
                    Text(
                        text = pt.first,
                        fontSize = 9.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .align(
                                androidx.compose.ui.BiasAlignment(
                                    horizontalBias = (fraction * 2) - 1f,
                                    verticalBias = 1.25f
                                )
                            )
                    )
                }
            }
        }
    }
}

@Composable
private fun DoseAvgTile(title: String, valStr: String, unit: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = title, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = valStr, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
        Text(text = unit, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun StatTile(value: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = color)
        Text(text = label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
