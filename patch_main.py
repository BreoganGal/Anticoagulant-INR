import re

with open("app/src/main/java/com/example/MainActivity.kt", "r") as f:
    content = f.read()

# Add imports for activity result
imports = """import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import java.io.OutputStreamWriter
import java.io.InputStreamReader
import android.widget.Toast"""
content = content.replace("import androidx.compose.ui.Modifier", imports + "\nimport androidx.compose.ui.Modifier")

# Find MainAppContent start
# Add launchers
launchers = """
    val context = LocalContext.current
    
    val exportLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
        uri?.let {
            try {
                val json = viewModel.exportDataToJson()
                context.contentResolver.openOutputStream(it)?.use { output ->
                    OutputStreamWriter(output).use { writer ->
                        writer.write(json)
                    }
                }
                Toast.makeText(context, "Datos exportados correctamente", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Error al exportar: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    val importLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let {
            try {
                val json = context.contentResolver.openInputStream(it)?.use { input ->
                    InputStreamReader(input).use { reader ->
                        reader.readText()
                    }
                }
                if (json != null) {
                    val success = viewModel.importDataFromJson(json)
                    if (success) {
                        Toast.makeText(context, "Datos importados correctamente", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(context, "Error de formato en archivo", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error al importar: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
"""
content = content.replace("var showTermsScreen by remember { mutableStateOf(false) }", "var showTermsScreen by remember { mutableStateOf(false) }\n" + launchers)

# Change onExportData and onImportData
new_onExportData = """                    onExportData = { 
                        exportLauncher.launch("anticoagulante_backup.json")
                    },
                    onImportData = {
                        importLauncher.launch(arrayOf("application/json", "*/*"))
                    }"""

old_onExportData = """                    onExportData = { 
                        val json = viewModel.exportDataToJson()
                        val sendIntent = android.content.Intent().apply {
                            action = android.content.Intent.ACTION_SEND
                            putExtra(android.content.Intent.EXTRA_TITLE, "Copia de seguridad Anticoagulant INR")
                            putExtra(android.content.Intent.EXTRA_TEXT, json)
                            type = "text/plain"
                        }
                        val shareIntent = android.content.Intent.createChooser(sendIntent, "Exportar copia de seguridad (JSON)")
                        context.startActivity(shareIntent)
                    },
                    onImportData = { showImportDataDialog = true }"""
                    
content = content.replace(old_onExportData, new_onExportData)

with open("app/src/main/java/com/example/MainActivity.kt", "w") as f:
    f.write(content)

