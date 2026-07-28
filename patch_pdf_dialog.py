import re

with open("app/src/main/java/com/example/ui/dialogs/ExportPdfDialog.kt", "r") as f:
    content = f.read()

# find everything up to the last brace of ExportPdfDialog
content = re.sub(r'val shareIntent = Intent.createChooser\(sendIntent, "Exportar informe PDF"\).*', '', content, flags=re.DOTALL)

with open("app/src/main/java/com/example/ui/dialogs/ExportPdfDialog.kt", "w") as f:
    f.write(content)
