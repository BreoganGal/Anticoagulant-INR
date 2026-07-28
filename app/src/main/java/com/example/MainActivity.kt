package com.example

import androidx.compose.material3.MaterialTheme

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.utils.PdfGenerator
import java.io.OutputStream
import java.io.FileOutputStream
import java.io.File

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import java.io.OutputStreamWriter
import java.io.InputStreamReader
import android.widget.Toast
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.DoseEntry
import com.example.language.LanguageManager
import com.example.ui.MainViewModel
import com.example.ui.dialogs.EditDoseDialog
import com.example.ui.dialogs.EditInrDialog
import com.example.ui.dialogs.ExportPdfDialog
import com.example.ui.dialogs.ImportPautaDialog
import com.example.ui.screens.AjustesScreen
import com.example.ui.screens.CalendarioScreen
import com.example.ui.screens.InicioScreen
import com.example.ui.screens.RegistroScreen
import com.example.ui.screens.TermsScreen
import com.example.ui.theme.AnticoagulantTheme

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val settings by viewModel.settings.collectAsStateWithLifecycle()

            AnticoagulantTheme(themeMode = settings.themeMode) {
                MainAppContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppContent(viewModel: MainViewModel) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val allDoses by viewModel.allDoses.collectAsStateWithLifecycle()
    val recent14Days by viewModel.recent14Days.collectAsStateWithLifecycle()
    val recent7Logs by viewModel.recent7Logs.collectAsStateWithLifecycle()
    val appointments by viewModel.appointments.collectAsStateWithLifecycle()

    val lang = settings.language
    var selectedTab by remember { mutableIntStateOf(0) }

    var showTermsScreen by remember { mutableStateOf(false) }
    androidx.activity.compose.BackHandler(enabled = selectedTab != 0 || showTermsScreen) {
        if (showTermsScreen) {
            showTermsScreen = false
        } else {
            selectedTab = 0
        }
    }

    // Dialog States
    var editingDoseEntry by remember { mutableStateOf<DoseEntry?>(null) }
    var editingInrDate by remember { mutableStateOf<String?>(null) }
    var editingInrVal by remember { mutableStateOf<Float?>(null) }

    var showImportPautaDialog by remember { mutableStateOf(false) }
    var showExportPdfDialog by remember { mutableStateOf(false) }
    var showImportDataDialog by remember { mutableStateOf(false) }

    
    val contextLocal = androidx.compose.ui.platform.LocalContext.current
    
    var pendingPdfStartDate by remember { mutableStateOf("") }
    var pendingPdfEndDate by remember { mutableStateOf("") }
    
    val pdfLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/pdf")) { uri ->
        uri?.let {
            try {
                val startDate = pendingPdfStartDate
                val endDate = pendingPdfEndDate
                val filteredDoses = allDoses.filter { it.date >= startDate && it.date <= endDate }.sortedBy { it.date }
                
                contextLocal.contentResolver.openOutputStream(it)?.use { output ->
                    PdfGenerator.generatePdf(contextLocal, output, startDate, endDate, filteredDoses)
                }
                Toast.makeText(contextLocal, "PDF guardado correctamente", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(contextLocal, "Error al generar PDF: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    val exportLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
        uri?.let {
            try {
                val json = viewModel.exportDataToJson()
                contextLocal.contentResolver.openOutputStream(it)?.use { output ->
                    OutputStreamWriter(output).use { writer ->
                        writer.write(json)
                    }
                }
                Toast.makeText(contextLocal, "Datos exportados correctamente", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(contextLocal, "Error al exportar: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    val importLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let {
            try {
                val json = contextLocal.contentResolver.openInputStream(it)?.use { input ->
                    InputStreamReader(input).use { reader ->
                        reader.readText()
                    }
                }
                if (json != null) {
                    val success = viewModel.importDataFromJson(json)
                    if (success) {
                        Toast.makeText(contextLocal, "Datos importados correctamente", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(contextLocal, "Error de formato en archivo", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(contextLocal, "Error al importar: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }


    val context = androidx.compose.ui.platform.LocalContext.current

    val todayDateStr = java.time.LocalDate.now().toString()
    val todayDose = allDoses.firstOrNull { it.date == todayDateStr }

    val daysUntilInr = viewModel.getDaysUntilNextInr()

    if (!settings.termsAccepted) {
        TermsScreen(
            isFirstLaunch = true,
            onAccept = {
                viewModel.updateSettings(settings.copy(termsAccepted = true))
            }
        )
        return
    }
    
    if (showTermsScreen) {
        TermsScreen(
            isFirstLaunch = false,
            onAccept = {},
            onClose = { showTermsScreen = false }
        )
        return
    }

    if (settings.termsAccepted && !settings.tourCompleted) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { viewModel.updateSettings(settings.copy(tourCompleted = true)) },
            title = {
                Text("¡Bienvenido a Anticoagulant INR!", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
            },
            text = {
                androidx.compose.foundation.layout.Column {
                    Text("Para empezar, te recomendamos:")
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 8.dp))
                    Text("1. Ve a la pestaña de Ajustes (engranaje) para marcar tu hora de toma habitual y tu medicación (Sintrom, Aldocumar...).")
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 8.dp))
                    Text("2. En el Calendario, podrás añadir tus tomas programadas manualmente o importar una pauta médica.")
                }
            },
            confirmButton = {
                androidx.compose.material3.Button(
                    onClick = {
                        viewModel.updateSettings(settings.copy(tourCompleted = true))
                        selectedTab = 3 // go to Ajustes
                    },
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Ir a Ajustes")
                }
            },
            dismissButton = {
                androidx.compose.material3.OutlinedButton(
                    onClick = { viewModel.updateSettings(settings.copy(tourCompleted = true)) }
                ) {
                    Text("Omitir")
                }
            }
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                val navItems = listOf(
                    Triple(0, LanguageManager.getString("tab_inicio", lang), Icons.Default.Home),
                    Triple(1, LanguageManager.getString("tab_calendario", lang), Icons.Default.CalendarMonth),
                    Triple(2, LanguageManager.getString("tab_registro", lang), Icons.AutoMirrored.Filled.ListAlt),
                    Triple(3, LanguageManager.getString("tab_ajustes", lang), Icons.Default.Settings)
                )

                navItems.forEach { (index, title, icon) ->
                    val isSelected = selectedTab == index
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { selectedTab = index },
                        icon = {
                            Icon(
                                imageVector = icon,
                                contentDescription = title,
                                tint = if (isSelected) androidx.compose.material3.MaterialTheme.colorScheme.primary else androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        label = {
                            Text(
                                text = title,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Normal,
                                color = if (isSelected) androidx.compose.material3.MaterialTheme.colorScheme.primary else androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = androidx.compose.material3.MaterialTheme.colorScheme.primaryContainer,
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> InicioScreen(
                    todayDose = todayDose,
                    recent14Days = recent14Days,
                    recent4Logs = recent7Logs.take(4),
                    daysUntilInr = daysUntilInr,
                    settings = settings,
                    onRegisterTodayTaken = { viewModel.markTodayTaken() },
                    onNavigateToRegistro = { selectedTab = 2 },
                    onEditTodayDoseClick = { editingDoseEntry = todayDose }
                )

                1 -> CalendarioScreen(
                    allDoses = allDoses,
                    appointments = appointments,
                    settings = settings,
                    onEditDoseClick = { entry -> editingDoseEntry = entry },
                    onEditInrClick = { date, valInr ->
                        editingInrDate = date
                        editingInrVal = valInr
                    },
                    onOpenImportPautaDialog = { showImportPautaDialog = true }
                )

                2 -> RegistroScreen(
                    recent7Logs = recent7Logs,
                    allDoses = allDoses,
                    settings = settings,
                    onOpenExportPdfDialog = { showExportPdfDialog = true },
                    onDeleteDose = { entry -> viewModel.deleteDose(entry) },
                    onEditDose = { entry -> editingDoseEntry = entry }
                )

                3 -> AjustesScreen(
                    settings = settings,
                    onSaveSettings = { updated -> viewModel.updateSettings(updated) },
                    onShowTerms = { showTermsScreen = true },
                    onExportData = { 
                        exportLauncher.launch("anticoagulante_backup.json")
                    },
                    onImportData = {
                        importLauncher.launch(arrayOf("application/json", "*/*"))
                    }
                )
            }
        }
    }

    // Dialog Overlays
    editingDoseEntry?.let { entry ->
        EditDoseDialog(
            dateStr = entry.date,
            initialFraction = entry.prescribedFraction,
            initialIsTaken = entry.isTaken,
            initialTakenTime = entry.takenTime,
            initialIsMissed = entry.punctuality == "MISSED",
            onDismiss = { editingDoseEntry = null },
            onSave = { fraction, isTaken, takenTime, isMissed ->
                viewModel.updateDose(
                    date = entry.date,
                    fraction = fraction,
                    isTaken = isTaken,
                    takenTime = takenTime,
                    isMissed = isMissed
                )
                editingDoseEntry = null
            }
        )
    }

    editingInrDate?.let { date ->
        EditInrDialog(
            dateStr = date,
            initialInr = editingInrVal,
            onDismiss = {
                editingInrDate = null
                editingInrVal = null
            },
            onSave = { newInr ->
                viewModel.updateInr(date, newInr)
                editingInrDate = null
                editingInrVal = null
            }
        )
    }

    if (showImportPautaDialog) {
        ImportPautaDialog(
            onDismiss = { showImportPautaDialog = false },
            onConfirmImport = { doses, nextInrDate ->
                viewModel.importPauta(doses, nextInrDate)
                showImportPautaDialog = false
            }
        )
    }

    if (showImportDataDialog) {
        com.example.ui.dialogs.ImportDataDialog(
            onDismiss = { showImportDataDialog = false },
            onImportJson = { jsonStr ->
                val success = viewModel.importDataFromJson(jsonStr)
                android.widget.Toast.makeText(contextLocal, if (success) "Datos importados correctamente" else "Error al importar los datos (JSON inválido)", android.widget.Toast.LENGTH_SHORT).show()
                showImportDataDialog = false
            }
        )
    }

    if (showExportPdfDialog) {
        ExportPdfDialog(
            onDismiss = { showExportPdfDialog = false },
            onExportConfirmed = { start, end ->
                showExportPdfDialog = false
                pendingPdfStartDate = start
                pendingPdfEndDate = end
                pdfLauncher.launch("Registro_INR_${start}_${end}.pdf")
            }
        )
    }
}
