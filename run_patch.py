import re
with open("app/src/main/java/com/example/MainActivity.kt", "r") as f:
    content = f.read()

imports = """import com.example.utils.PdfGenerator
import java.io.OutputStream
import java.io.FileOutputStream
import java.io.File"""
content = content.replace("import com.example.data.MainViewModel", imports + "\nimport com.example.data.MainViewModel")

pdf_launcher = """
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
"""

content = content.replace("val exportLauncher = rememberLauncherForActivityResult", pdf_launcher + "\n    val exportLauncher = rememberLauncherForActivityResult")

pdf_handling = """    if (showExportPdfDialog) {
        ExportPdfDialog(
            onDismiss = { showExportPdfDialog = false },
            onExportConfirmed = { start, end ->
                showExportPdfDialog = false
                pendingPdfStartDate = start
                pendingPdfEndDate = end
                pdfLauncher.launch("Registro_INR_${start}_${end}.pdf")
            }
        )
    }"""

pattern = r'    if \(showExportPdfDialog\) \{.*?\n    \}'
content = re.sub(pattern, pdf_handling, content, flags=re.DOTALL)

with open("app/src/main/java/com/example/MainActivity.kt", "w") as f:
    f.write(content)
