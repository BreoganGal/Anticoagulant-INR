package com.example.ui.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.components.PillFractionView

import com.example.ui.theme.StatusMissedRed

@Composable
fun EditDoseDialog(
    dateStr: String,
    initialFraction: String,
    initialIsTaken: Boolean,
    initialTakenTime: String?,
    initialIsMissed: Boolean,
    onDismiss: () -> Unit,
    onSave: (fraction: String, isTaken: Boolean, takenTime: String?, isMissed: Boolean) -> Unit
) {
    val (initWhole, initBaseFrac) = parseInitialSelection(initialFraction)

    var wholeModifier by remember { mutableIntStateOf(initWhole) } // 0 (none), 1 (1 pastilla), 2 (2 pastillas), 3 (3 pastillas)
    var baseFraction by remember { mutableStateOf(initBaseFrac) } // "1/8", "1/4", "1/3", "1/2", "3/4", etc.

    val currentFractionStr = remember(wholeModifier, baseFraction) {
        computeFractionString(wholeModifier, baseFraction)
    }

    var isTaken by remember { mutableStateOf(initialIsTaken) }
    var isMissed by remember { mutableStateOf(initialIsMissed) }
    var takenTime by remember { mutableStateOf(initialTakenTime ?: java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"))) }

    val baseFractionOptions = listOf("1/8", "1/4", "1/3", "1/2", "5/8", "3/4", "7/8")
    val wholeModifierOptions = listOf(1 to "1 pastilla", 2 to "2 pastillas", 3 to "3 pastillas")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Editar pauta / toma",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = dateStr,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Pill Fraction Preview Box
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        PillFractionView(
                            fractionStr = currentFractionStr,
                            size = 72.dp,
                            activeColor = MaterialTheme.colorScheme.primary,
                            inactiveColor = MaterialTheme.colorScheme.surface,
                            lineColor = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "$currentFractionStr de pastilla",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "Verde = Tomado · Blanco = No tomado",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium,
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // WHOLE PILL MODIFIER ROW (1 pastilla, 2 pastillas, 3 pastillas)
                Text(
                    text = "Seleccionar cantidad de pastillas (Tocar de nuevo para desmarcar):",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    wholeModifierOptions.forEach { (modVal, label) ->
                        val isSel = wholeModifier == modVal
                        Surface(
                            color = if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    wholeModifier = if (wholeModifier == modVal) 0 else modVal
                                }
                        ) {
                            Text(
                                text = label,
                                color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 10.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // FRACTION ROW (1/8, 1/4, 1/3, 1/2, 3/4, 1)
                Text(
                    text = "Fracción de pastilla (Tocar de nuevo para desmarcar):",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    baseFractionOptions.forEach { fracOpt ->
                        val isSel = baseFraction == fracOpt
                        Surface(
                            color = if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    baseFraction = if (baseFraction == fracOpt) "" else fracOpt
                                }
                        ) {
                            Text(
                                text = fracOpt,
                                color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 10.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Checkboxes
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = isTaken,
                        onCheckedChange = {
                            isTaken = it
                            if (it) isMissed = false
                        },
                        colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
                    )
                    Text("Marcar como efectuada", fontSize = 13.sp)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = isMissed,
                        onCheckedChange = {
                            isMissed = it
                            if (it) isTaken = false
                        },
                        colors = CheckboxDefaults.colors(checkedColor = StatusMissedRed)
                    )
                    Text("Marcar como FALTA (no tomada)", fontSize = 13.sp, color = StatusMissedRed)
                }

                if (isTaken) {
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = takenTime,
                        onValueChange = { takenTime = it },
                        label = { Text("Hora de la toma (HH:mm)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(onClick = onDismiss) {
                        Text("Cancelar")
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Button(
                        onClick = {
                            onSave(currentFractionStr, isTaken, if (isTaken) takenTime else null, isMissed)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Guardar")
                    }
                }
            }
        }
    }
}

private fun parseInitialSelection(initialStr: String): Pair<Int, String> {
    val cleaned = initialStr.lowercase().replace("pastillas", "").replace("pastilla", "").trim()
    val parts = cleaned.split(" ").filter { it.isNotBlank() }
    if (parts.size >= 2) {
        val w = parts[0].toIntOrNull() ?: 0
        val frac = parts[1]
        return Pair(w, frac)
    }
    val numericInt = cleaned.toIntOrNull()
    if (numericInt != null) {
        return Pair(numericInt, "")
    }
    return Pair(0, if (cleaned.isBlank()) "3/4" else cleaned)
}

private fun computeFractionString(wholePills: Int, baseFraction: String): String {
    val cleanFrac = baseFraction.lowercase().replace("pastillas", "").replace("pastilla", "").trim()
    if (wholePills <= 0 && cleanFrac.isBlank()) return "0"
    if (wholePills <= 0) return cleanFrac
    if (cleanFrac.isBlank()) return "$wholePills"
    if (cleanFrac == "1") return "${wholePills + 1}"
    return "$wholePills $cleanFrac"
}
