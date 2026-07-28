with open("app/src/main/java/com/example/MainActivity.kt", "r") as f:
    content = f.read()

# Add imports
imports = """import com.example.utils.PdfGenerator
import java.io.OutputStream
import java.io.FileOutputStream
import java.io.File"""

content = content.replace("import com.example.data.MainViewModel", imports + "\nimport com.example.data.MainViewModel")

# Add pdfLauncher
pdf_launcher = """
    var pendingPdfStartDate by remember { mutableStateOf("") }
    var pendingPdfEndDate by remember { mutableStateOf("") }
    
    val pdfLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/pdf")) { uri ->
        uri?.let {
            try {
                val startDate = pendingPdfStartDate
                val endDate = pendingPdfEndDate
                val filteredDoses = allDoses.filter { it.date in startDate..endDate }.sortedBy { it.date }
                
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
    
content = re.sub(r'    if \(showExportPdfDialog\) \{[\s\S]*?\}\n        \)', pdf_handling, content) # Wait, re is not imported, let's just do python script with re.

with open("app/src/main/java/com/example/MainActivity.kt", "w") as f:
    f.write(content)
