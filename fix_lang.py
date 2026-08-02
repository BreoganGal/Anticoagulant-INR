import re

with open("core/common/src/main/java/com/example/language/LanguageManager.kt", "r") as f:
    content = f.read()

new_translations = """        "completar" to mapOf("ES" to "Completar", "GL" to "Completar", "CA" to "Completar", "EU" to "Osatu", "FR" to "Terminer", "EN" to "Complete", "DE" to "Abschließen", "IT" to "Completa", "PT" to "Concluir", "PL" to "Zakończ"),
        "version_aplicacion" to mapOf("ES" to "Versión de la aplicación", "GL" to "Versión da aplicación", "CA" to "Versió de l'aplicació", "EU" to "Aplikazioaren bertsioa", "FR" to "Version de l'application", "EN" to "App version", "DE" to "App-Version", "IT" to "Versione dell'app", "PT" to "Versão da app", "PL" to "Wersja aplikacji")"""

content = content.replace('"version_aplicacion" to mapOf("ES" to "Versión de la aplicación", "GL" to "Versión da aplicación", "CA" to "Versió de l'aplicació", "EU" to "Aplikazioaren bertsioa", "FR" to "Version de l'application", "EN" to "App version", "DE" to "App-Version", "IT" to "Versione dell'app", "PT" to "Versão da app", "PL" to "Wersja aplikacji")', new_translations)

with open("core/common/src/main/java/com/example/language/LanguageManager.kt", "w") as f:
    f.write(content)
