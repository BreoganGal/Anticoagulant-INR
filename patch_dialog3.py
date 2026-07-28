import re

with open("app/src/main/java/com/example/ui/dialogs/ImportPautaDialog.kt", "r") as f:
    content = f.read()

content = content.replace(
    'import com.example.gemini.analyzeImageWithGemini',
    'import com.example.mlkit.analyzeImageWithMlKit'
)
content = content.replace(
    'import com.example.gemini.ParsedPrescription',
    'import com.example.mlkit.ParsedPrescription'
)
content = content.replace(
    'analyzeImageWithGemini(bitmap)',
    'analyzeImageWithMlKit(bitmap)'
)

with open("app/src/main/java/com/example/ui/dialogs/ImportPautaDialog.kt", "w") as f:
    f.write(content)

