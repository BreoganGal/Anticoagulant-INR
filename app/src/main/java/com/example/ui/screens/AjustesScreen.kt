package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppSettings
import com.example.language.AppLanguage
import com.example.language.LanguageManager

import com.example.util.NotificationHelper

@Composable
fun AjustesScreen(
    settings: AppSettings,
    onSaveSettings: (AppSettings) -> Unit,
    onShowTerms: () -> Unit = {},
    onExportData: () -> Unit = {},
    onImportData: () -> Unit = {}
) {
    val context = LocalContext.current
    val lang = settings.language

    var reminderTime by remember(settings) { mutableStateOf(settings.reminderTime) }
    var notificationsEnabled by remember(settings) { mutableStateOf(settings.notificationsEnabled) }
    var vibrationEnabled by remember(settings) { mutableStateOf(settings.vibrationEnabled) }
    var snoozeMinutes by remember { mutableStateOf(15) }

    var inrFrequency by remember(settings) { mutableStateOf(settings.inrCheckFrequency) }
    var medicationName by remember(settings) { mutableStateOf(settings.medicationName) }
    var customMedicationName by remember(settings) { mutableStateOf(settings.customMedicationName) }

    var inrTargetMin by remember(settings) { mutableStateOf(settings.targetInrMin) }
    var inrTargetMax by remember(settings) { mutableStateOf(settings.targetInrMax) }

    var selectedLang by remember(settings) { mutableStateOf(settings.language) }
    var themeMode by remember(settings) { mutableStateOf(settings.themeMode) }
    var dateFormat by remember(settings) { mutableStateOf(settings.dateFormat) }

    // Single active dropdown/accordion section ID ("time", "snooze", "freq", "med", "lang", "dateformat", "theme")
    var expandedMenuId by remember { mutableStateOf<String?>(null) }
    var showTimePickerDialog by remember { mutableStateOf(false) }

    val medicationOptions = listOf(
        "Sintrom", "Warfarina", "Coumadin", "Marevan", "Marcoumar", "Jantoven",
        "Personaliza otro antagonista de la vitamina K (AVK)"
    )

    val snoozeOptions = listOf(
        15 to "15 minutos",
        30 to "30 minutos",
        60 to "1 hora",
        120 to "2 horas"
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
            text = LanguageManager.getString("tab_ajustes", lang),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        // SECTION 1: RECORDATORIOS & NOTIFICACIONES PUSH
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "RECORDATORIOS DE TOMA",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            expandedMenuId = if (expandedMenuId == "time") null else "time"
                        }
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Hora de la toma",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "Hora objetivo diaria para la medicación",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = reminderTime,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                if (expandedMenuId == "time") {
                    Spacer(modifier = Modifier.height(8.dp))
                    var customTimeInput by remember(reminderTime) { mutableStateOf(reminderTime) }
                    
                    Text("Ingresar hora personalizada o seleccionar habitual:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = customTimeInput,
                            onValueChange = { customTimeInput = it },
                            label = { Text("Hora (HH:mm)") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (customTimeInput.isNotBlank()) {
                                    reminderTime = customTimeInput
                                    onSaveSettings(settings.copy(reminderTime = customTimeInput))
                                    expandedMenuId = null
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("Guardar")
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    val commonTimes = listOf("08:00", "10:00", "12:00", "14:00", "16:00", "18:00", "20:00", "22:00")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        commonTimes.take(4).forEach { timeStr ->
                            Surface(
                                color = if (reminderTime == timeStr) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        reminderTime = timeStr
                                        customTimeInput = timeStr
                                        onSaveSettings(settings.copy(reminderTime = timeStr))
                                        expandedMenuId = null
                                    }
                            ) {
                                Text(
                                    text = timeStr,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (reminderTime == timeStr) Color.White else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(vertical = 6.dp),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        commonTimes.drop(4).forEach { timeStr ->
                            Surface(
                                color = if (reminderTime == timeStr) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        reminderTime = timeStr
                                        customTimeInput = timeStr
                                        onSaveSettings(settings.copy(reminderTime = timeStr))
                                        expandedMenuId = null
                                    }
                            ) {
                                Text(
                                    text = timeStr,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (reminderTime == timeStr) Color.White else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(vertical = 6.dp),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(14.dp))
        
        // NEW SECTION: RECORDATORIOS
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expandedMenuId = if (expandedMenuId == "recordatorios") null else "recordatorios" },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Recordatorios", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Icon(
                        imageVector = if (expandedMenuId == "recordatorios") androidx.compose.material.icons.Icons.Default.KeyboardArrowUp else androidx.compose.material.icons.Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (expandedMenuId == "recordatorios") {
                    Spacer(modifier = Modifier.height(10.dp))
                    Column(modifier = Modifier.fillMaxWidth()) {
                        androidx.compose.material3.HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant, thickness = 0.8.dp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Notificaciones push",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                            Switch(
                                checked = notificationsEnabled,
                                onCheckedChange = {
                                    notificationsEnabled = it
                                    onSaveSettings(settings.copy(notificationsEnabled = it))
                                },
                                colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.primary, checkedTrackColor = Color(0xFFB2DFDB))
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        androidx.compose.material3.HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant, thickness = 0.8.dp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Vibración",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                            Switch(
                                checked = vibrationEnabled,
                                onCheckedChange = {
                                    vibrationEnabled = it
                                    onSaveSettings(settings.copy(vibrationEnabled = it))
                                },
                                colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.primary, checkedTrackColor = Color(0xFFB2DFDB))
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // SECTION 2: MONITORIZACIÓN
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = LanguageManager.getString("monitorizacion", lang),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Frecuencia Control INR (Default Automática - Accordion style matching medicación)
                val inrFreqOptions = listOf(
                    "AUTOMATIC" to "Automática (sincronizado con el calendario)",
                    "MONTHLY" to "Mensual",
                    "FORTNIGHTLY" to "Quincenal",
                    "WEEKLY" to "Semanal"
                )
                val freqDisplayShort = when (inrFrequency) {
                    "AUTOMATIC" -> "Automática"
                    "WEEKLY" -> "Semanal"
                    "FORTNIGHTLY" -> "Quincenal"
                    else -> "Mensual"
                }

                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expandedMenuId = if (expandedMenuId == "freq") null else "freq" }
                            .padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = LanguageManager.getString("frecuencia_inr", lang),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = freqDisplayShort,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = if (expandedMenuId == "freq") Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    if (expandedMenuId == "freq") {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surface)
                        ) {
                            inrFreqOptions.forEach { (code, label) ->
                                androidx.compose.material3.HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant, thickness = 0.8.dp)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            inrFrequency = code
                                            expandedMenuId = null
                                            onSaveSettings(settings.copy(inrCheckFrequency = code))
                                        }
                                        .padding(vertical = 14.dp, horizontal = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = label,
                                        fontWeight = if (inrFrequency == code) FontWeight.Bold else FontWeight.Normal,
                                        color = if (inrFrequency == code) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                        fontSize = 14.sp
                                    )
                                    if (inrFrequency == code) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // GESTIONAR MEDICACIÓN
                val displayMedName = if (medicationName == "Personaliza otro antagonista de la vitamina K (AVK)") customMedicationName.ifBlank { "Personalizado" } else medicationName

                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expandedMenuId = if (expandedMenuId == "med") null else "med" }
                            .padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = LanguageManager.getString("gestionar_medicacion", lang),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = displayMedName,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = if (expandedMenuId == "med") androidx.compose.material.icons.Icons.Default.KeyboardArrowUp else androidx.compose.material.icons.Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    if (expandedMenuId == "med") {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surface)
                        ) {
                            medicationOptions.forEach { option ->
                                androidx.compose.material3.HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant, thickness = 0.8.dp)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            medicationName = option
                                            expandedMenuId = null
                                            onSaveSettings(settings.copy(medicationName = option))
                                        }
                                        .padding(vertical = 14.dp, horizontal = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = option,
                                        fontWeight = if (medicationName == option) FontWeight.Bold else FontWeight.Normal,
                                        color = if (medicationName == option) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                        fontSize = 14.sp
                                    )
                                    if (medicationName == option) {
                                        Icon(
                                            imageVector = androidx.compose.material.icons.Icons.Default.Check,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                if (medicationName == "Personaliza otro antagonista de la vitamina K (AVK)") {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = customMedicationName,
                        onValueChange = {
                            customMedicationName = it
                            onSaveSettings(settings.copy(customMedicationName = it))
                        },
                        label = { Text("Nombre del medicamento personalizado") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // RANGO OBJETIVO INR
                var isInrRangeExpanded by remember { mutableStateOf(false) }
                var minInputStr by remember(inrTargetMin) { mutableStateOf(String.format(java.util.Locale.US, "%.1f", inrTargetMin)) }
                var maxInputStr by remember(inrTargetMax) { mutableStateOf(String.format(java.util.Locale.US, "%.1f", inrTargetMax)) }

                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isInrRangeExpanded = !isInrRangeExpanded }
                            .padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = LanguageManager.getString("rango_objetivo_inr", lang),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${String.format(java.util.Locale.US, "%.1f", inrTargetMin)} - ${String.format(java.util.Locale.US, "%.1f", inrTargetMax)}",
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = if (isInrRangeExpanded) androidx.compose.material.icons.Icons.Default.KeyboardArrowUp else androidx.compose.material.icons.Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    if (isInrRangeExpanded) {
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = "Mín", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            color = MaterialTheme.colorScheme.surface,
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.width(60.dp)
                                        ) {
                                            androidx.compose.foundation.text.BasicTextField(
                                                value = minInputStr,
                                                onValueChange = { minInputStr = it },
                                                textStyle = androidx.compose.ui.text.TextStyle(
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 15.sp,
                                                    color = MaterialTheme.colorScheme.onSurface,
                                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                                ),
                                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                                singleLine = true
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(text = "—", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Spacer(modifier = Modifier.width(8.dp))

                                        Text(text = "Máx", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            color = MaterialTheme.colorScheme.surface,
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.width(60.dp)
                                        ) {
                                            androidx.compose.foundation.text.BasicTextField(
                                                value = maxInputStr,
                                                onValueChange = { maxInputStr = it },
                                                textStyle = androidx.compose.ui.text.TextStyle(
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 15.sp,
                                                    color = MaterialTheme.colorScheme.onSurface,
                                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                                ),
                                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                                singleLine = true
                                            )
                                        }
                                    }

                                    Button(
                                        onClick = {
                                            val parsedMin = minInputStr.replace(",", ".").toFloatOrNull() ?: inrTargetMin
                                            val parsedMax = maxInputStr.replace(",", ".").toFloatOrNull() ?: inrTargetMax
                                            inrTargetMin = parsedMin
                                            inrTargetMax = parsedMax
                                            onSaveSettings(settings.copy(targetInrMin = parsedMin, targetInrMax = parsedMax))
                                            Toast.makeText(context, "Rango guardado (${parsedMin} - ${parsedMax})", Toast.LENGTH_SHORT).show()
                                            isInrRangeExpanded = false
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                        shape = RoundedCornerShape(10.dp),
                                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Text("GUARDAR CAMBIOS", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                RangeSlider(
                                    value = inrTargetMin..inrTargetMax,
                                    onValueChange = { range ->
                                        inrTargetMin = range.start
                                        inrTargetMax = range.endInclusive
                                        minInputStr = String.format(java.util.Locale.US, "%.1f", range.start)
                                        maxInputStr = String.format(java.util.Locale.US, "%.1f", range.endInclusive)
                                    },
                                    valueRange = 1.0f..5.0f,
                                    colors = SliderDefaults.colors(
                                        thumbColor = MaterialTheme.colorScheme.primary,
                                        activeTrackColor = MaterialTheme.colorScheme.primary
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (settings.appMode != "SIMPLE") {
        // SECTION 3: APARIENCIA
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = LanguageManager.getString("apariencia", lang),
                    style = MaterialTheme.typography.labelSmall,
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
                    Text(text = LanguageManager.getString("tema", lang), fontWeight = FontWeight.Bold, fontSize = 13.sp)

                    Row {
                        listOf("LIGHT" to LanguageManager.getString("tema_claro", lang), "DARK" to LanguageManager.getString("tema_oscuro", lang), "SYSTEM" to LanguageManager.getString("tema_sistema", lang)).forEach { (mode, label) ->
                            val isSel = themeMode == mode
                            Surface(
                                color = if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .padding(start = 4.dp)
                                    .clickable {
                                        themeMode = mode
                                        onSaveSettings(settings.copy(themeMode = mode))
                                    }
                            ) {
                                Text(
                                    text = label,
                                    color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        }
        Spacer(modifier = Modifier.height(14.dp))
        if (settings.appMode != "SIMPLE") {
        // SECTION 4: IDIOMA
        val currLangDisplay = if (selectedLang == "SYSTEM") LanguageManager.getString("idioma_sistema", lang) else AppLanguage.entries.firstOrNull { it.code == selectedLang }?.displayName ?: "Español"

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expandedMenuId = if (expandedMenuId == "lang") null else "lang" },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = LanguageManager.getString("idioma", lang), fontWeight = FontWeight.Bold, fontSize = 14.sp)

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = currLangDisplay, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = if (expandedMenuId == "lang") androidx.compose.material.icons.Icons.Default.KeyboardArrowUp else androidx.compose.material.icons.Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (expandedMenuId == "lang") {
                    Spacer(modifier = Modifier.height(10.dp))
                    Column(modifier = Modifier.fillMaxWidth()) {
                        val sortedLangs = listOf(AppLanguage.SYSTEM) + AppLanguage.entries.filter { it != AppLanguage.SYSTEM }.sortedBy { it.displayName }
                        sortedLangs.forEach { appLang ->
                            androidx.compose.material3.HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant, thickness = 0.8.dp)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedLang = appLang.code
                                        expandedMenuId = null
                                        onSaveSettings(settings.copy(language = appLang.code))
                                    }
                                    .padding(vertical = 12.dp, horizontal = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (appLang.code == "SYSTEM") LanguageManager.getString("idioma_sistema", lang) else appLang.displayName,
                                    fontWeight = if (selectedLang == appLang.code) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedLang == appLang.code) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                    fontSize = 14.sp
                                )
                                if (selectedLang == appLang.code) {
                                    Icon(
                                        imageVector = androidx.compose.material.icons.Icons.Default.Check,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        }
        Spacer(modifier = Modifier.height(14.dp))
        // SECTION 5: DATOS Y FORMATO DE FECHA
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                if (settings.appMode != "SIMPLE") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expandedMenuId = if (expandedMenuId == "dateformat") null else "dateformat" },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = LanguageManager.getString("formato_fecha", lang), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = dateFormat, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = if (expandedMenuId == "dateformat") androidx.compose.material.icons.Icons.Default.KeyboardArrowUp else androidx.compose.material.icons.Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    if (expandedMenuId == "dateformat") {
                        Spacer(modifier = Modifier.height(10.dp))
                        Column(modifier = Modifier.fillMaxWidth()) {
                            listOf("DD/MM/YYYY", "YYYY-MM-DD", "MM/DD/YYYY", "d/m/y").forEach { fmt ->
                                androidx.compose.material3.HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant, thickness = 0.8.dp)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            dateFormat = fmt
                                            expandedMenuId = null
                                            onSaveSettings(settings.copy(dateFormat = fmt))
                                        }
                                        .padding(vertical = 12.dp, horizontal = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = fmt,
                                        fontWeight = if (dateFormat == fmt) FontWeight.Bold else FontWeight.Normal,
                                        color = if (dateFormat == fmt) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                        fontSize = 14.sp
                                    )
                                    if (dateFormat == fmt) {
                                        Icon(
                                            imageVector = androidx.compose.material.icons.Icons.Default.Check,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(18.dp))
                    androidx.compose.material3.HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant, thickness = 0.8.dp)
                    Spacer(modifier = Modifier.height(18.dp))
                }
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expandedMenuId = if (expandedMenuId == "appmode") null else "appmode" },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = LanguageManager.getString("modo_aplicacion", lang), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = if (settings.appMode == "SIMPLE") LanguageManager.getString("simple", lang) else LanguageManager.getString("avanzada", lang), fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = if (expandedMenuId == "appmode") androidx.compose.material.icons.Icons.Default.KeyboardArrowUp else androidx.compose.material.icons.Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                if (expandedMenuId == "appmode") {
                    Spacer(modifier = Modifier.height(10.dp))
                    Column(modifier = Modifier.fillMaxWidth()) {
                        listOf("SIMPLE", "ADVANCED").forEach { modeStr ->
                            androidx.compose.material3.HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant, thickness = 0.8.dp)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        expandedMenuId = null
                                        onSaveSettings(settings.copy(appMode = modeStr))
                                    }
                                    .padding(vertical = 12.dp, horizontal = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (modeStr == "SIMPLE") LanguageManager.getString("simple", lang) else LanguageManager.getString("avanzada", lang),
                                    fontWeight = if (settings.appMode == modeStr) FontWeight.Bold else FontWeight.Normal,
                                    color = if (settings.appMode == modeStr) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                    fontSize = 14.sp
                                )
                                if (settings.appMode == modeStr) {
                                    Icon(
                                        imageVector = androidx.compose.material.icons.Icons.Default.Check,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(14.dp))
        // SECTION 6: DATOS Y PRIVACIDAD
        Text(
            text = "DATOS Y PRIVACIDAD",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onExportData() }
                        .padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Exportar datos", fontSize = 14.sp)
                }
                androidx.compose.material3.HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant, thickness = 0.8.dp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onImportData() }
                        .padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Importar datos", fontSize = 14.sp)
                }
                androidx.compose.material3.HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant, thickness = 0.8.dp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onShowTerms() }
                        .padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Términos y avisos legales", fontSize = 14.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // SECTION 7: APLICACIÓN
        Text(
            text = "APLICACIÓN",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { /* Rating logic */ }
                        .padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Calificar la aplicación", fontSize = 14.sp)
                }
                androidx.compose.material3.HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant, thickness = 0.8.dp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val intent = android.content.Intent(android.content.Intent.ACTION_SENDTO).apply {
                                data = android.net.Uri.parse("mailto:soporte@breogangal.eu")
                            }
                            context.startActivity(intent)
                        }
                        .padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Informar de un problema o sugerencia", fontSize = 14.sp)
                }
                androidx.compose.material3.HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant, thickness = 0.8.dp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = LanguageManager.getString("version_aplicacion", lang), fontSize = 14.sp)
                    Text(text = "0.2", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}
