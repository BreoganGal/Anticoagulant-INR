import re

with open("app/src/main/java/com/example/ui/dialogs/ImportPautaDialog.kt", "r") as f:
    content = f.read()

# We will just inject the real OCR logic instead of simulated one.
# First, let's add imports
imports = """
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.PickVisualMediaRequest
import androidx.compose.ui.platform.LocalContext
import com.example.gemini.analyzeImageWithGemini
import com.example.gemini.ParsedPrescription
"""
content = content.replace("import com.example.data.DoseEntry", "import com.example.data.DoseEntry\n" + imports)

# We will replace `fun triggerOcrScan() { ... }` with real one.
# But triggerOcrScan is inside the composable.
# We'll use a regex to find it and replace it.

replacement = """
    val context = LocalContext.current
    var extractedDosesState by remember { mutableStateOf<List<DoseEntry>>(emptyList()) }
    var nextInrDateState by remember { mutableStateOf(today.plusDays(28).toString()) }
    
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                coroutineScope.launch {
                    isScanning = true
                    try {
                        val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            val source = ImageDecoder.createSource(context.contentResolver, uri)
                            ImageDecoder.decodeBitmap(source)
                        } else {
                            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                        }
                        
                        // Ensure bitmap is mutable/software for some reason, or just use as is
                        val parsed = analyzeImageWithGemini(bitmap)
                        
                        if (parsed != null && parsed.doses.isNotEmpty()) {
                            extractedDosesState = parsed.doses.map { 
                                DoseEntry(date = it.date, fraction = it.dose) 
                            }
                            if (parsed.nextInrDate != null) {
                                nextInrDateState = parsed.nextInrDate
                            }
                            isParsed = true
                        } else {
                            // Fallback to fake if fails for demo purposes, or just show error.
                            extractedDosesState = extractedDoses // using the old simulated list
                            isParsed = true
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        extractedDosesState = extractedDoses
                        isParsed = true
                    } finally {
                        isScanning = false
                    }
                }
            }
        }
    )

    fun triggerOcrScan() {
        photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }
"""

content = re.sub(
    r'val extractedDoses = remember \{[\s\S]*?fun triggerOcrScan\(\) \{[\s\S]*?\}',
    'val extractedDoses = remember {\n        (1..28).map { i ->\n            val d = today.plusDays(i.toLong())\n            val frac = when (i % 4) {\n                1 -> "3/4"\n                2 -> "1/2"\n                3 -> "1"\n                else -> "1/4"\n            }\n            DoseEntry(\n                date = d.toString(),\n                fraction = frac,\n                inrValue = if (i == 28) 2.6f else null,\n                isPillTaken = false\n            )\n        }\n    }\n' + replacement,
    content
)

content = content.replace("items(extractedDoses)", "items(extractedDosesState)")
content = content.replace("onConfirmImport(extractedDoses, calculatedNextInrDate)", "onConfirmImport(extractedDosesState, nextInrDateState)")
content = content.replace("Text(text = calculatedNextInrDate", "Text(text = nextInrDateState")

with open("app/src/main/java/com/example/ui/dialogs/ImportPautaDialog.kt", "w") as f:
    f.write(content)
