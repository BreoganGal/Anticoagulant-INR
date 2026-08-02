import re

with open("core/common/src/main/java/com/example/language/LanguageManager.kt", "r") as f:
    content = f.read()

new_translations = """        "completar" to mapOf("ES" to "Completar", "GL" to "Completar", "CA" to "Completar", "EU" to "Osatu", "FR" to "Terminer", "EN" to "Complete", "DE" to "Abschließen", "IT" to "Completa", "PT" to "Concluir", "PL" to "Zakończ"),\n        "version_aplicacion\""""

content = content.replace('"version_aplicacion"', new_translations)

with open("core/common/src/main/java/com/example/language/LanguageManager.kt", "w") as f:
    f.write(content)
