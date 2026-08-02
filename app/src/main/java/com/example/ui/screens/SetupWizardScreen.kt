package com.example.ui.screens

import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextAlign
import java.util.Calendar
import com.example.language.LanguageManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupWizardScreen(
    lang: String = "SYSTEM",
    onFinish: (mode: String, medication: String, time: String) -> Unit,
    onSkip: (mode: String) -> Unit,
    onImportPauta: (mode: String) -> Unit
) {
    var step by remember { mutableStateOf(1) }
    var selectedMode by remember { mutableStateOf("SIMPLE") }
    var medication by remember { mutableStateOf("Sintrom") }
    var customMedication by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("18:00") }

    val medicationOptions = listOf(
        "Sintrom", "Warfarina", "Coumadin", "Marevan", "Marcoumar", "Jantoven",
        LanguageManager.getString("pers_otro", lang)
    )

    val context = LocalContext.current

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (step == 1) {
                Text(
                    text = LanguageManager.getString("bienvenido_a", lang),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "Anticoagulant INR",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 24.dp).fillMaxWidth()
                )
                Text(
                    text = LanguageManager.getString("elige_modo", lang),
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 24.dp).fillMaxWidth()
                )

                OutlinedButton(
                    onClick = { selectedMode = "SIMPLE"; step = 2 },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = if (selectedMode == "SIMPLE") ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer) else ButtonDefaults.outlinedButtonColors()
                ) {
                    Text(LanguageManager.getString("modo_simple", lang), modifier = Modifier.padding(8.dp))
                }

                OutlinedButton(
                    onClick = { selectedMode = "ADVANCED"; step = 2 },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(LanguageManager.getString("modo_avanzado", lang), modifier = Modifier.padding(8.dp))
                }
            } else if (step == 2) {
                Text(
                    text = LanguageManager.getString("config_inicial", lang),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                Text(LanguageManager.getString("medicacion_tomas", lang), fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))
                var medExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = medExpanded,
                    onExpandedChange = { medExpanded = !medExpanded },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                ) {
                    OutlinedTextField(
                        value = medication,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = medExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = medExpanded,
                        onDismissRequest = { medExpanded = false }
                    ) {
                        medicationOptions.forEach { opt ->
                            DropdownMenuItem(
                                text = { Text(opt) },
                                onClick = {
                                    medication = opt
                                    medExpanded = false
                                }
                            )
                        }
                    }
                }

                if (medication == LanguageManager.getString("pers_otro", lang)) {
                    OutlinedTextField(
                        value = customMedication,
                        onValueChange = { customMedication = it },
                        label = { Text(LanguageManager.getString("nombre_medicacion", lang)) },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                    )
                }

                Text(LanguageManager.getString("hora_toma", lang), fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))
                var showTimePicker by remember { mutableStateOf(false) }
                OutlinedButton(
                    onClick = { showTimePicker = true },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
                ) {
                    Text(time)
                }

                if (showTimePicker) {
                    val cal = Calendar.getInstance()
                    val parts = time.split(":")
                    val h = parts.getOrNull(0)?.toIntOrNull() ?: 18
                    val m = parts.getOrNull(1)?.toIntOrNull() ?: 0
                    TimePickerDialog(
                        context,
                        { _, hour, min ->
                            time = String.format("%02d:%02d", hour, min)
                            showTimePicker = false
                        },
                        h, m, true
                    ).apply {
                        setOnCancelListener { showTimePicker = false }
                    }.show()
                }

                OutlinedButton(
                    onClick = {
                        onImportPauta(selectedMode)
                    },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                ) {
                    Text(LanguageManager.getString("importar_pauta", lang))
                }
                Button(
                    onClick = { 
                        val finalMed = if (medication == LanguageManager.getString("pers_otro", lang)) {
                            if (customMedication.isBlank()) "Sintrom" else customMedication
                        } else medication
                        onFinish(selectedMode, finalMed, time)
                    },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                ) {
                    Text(LanguageManager.getString("completar", lang))
                }

                TextButton(
                    onClick = { 
                        val finalMed = if (medication == LanguageManager.getString("pers_otro", lang)) {
                            if (customMedication.isBlank()) "Sintrom" else customMedication
                        } else medication
                        onFinish(selectedMode, finalMed, time)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(LanguageManager.getString("omitir", lang))
                }
            }
        }
    }
}
