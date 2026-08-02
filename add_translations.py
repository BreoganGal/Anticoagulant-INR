import re

with open("core/common/src/main/java/com/example/language/LanguageManager.kt", "r") as f:
    content = f.read()

new_translations = """        "omitir" to mapOf("ES" to "Omitir", "GL" to "Omitir", "CA" to "Ometre", "EU" to "Saltatu", "FR" to "Passer", "EN" to "Skip", "DE" to "Überspringen", "IT" to "Salta", "PT" to "Ignorar", "PL" to "Pomiń"),
        "modo_aplicacion" to mapOf("ES" to "Modo de la aplicación", "GL" to "Modo da aplicación", "CA" to "Mode de l'aplicació", "EU" to "Aplikazioaren modua", "FR" to "Mode de l'application", "EN" to "App mode", "DE" to "App-Modus", "IT" to "Modalità app", "PT" to "Modo da app", "PL" to "Tryb aplikacji"),
        "simple" to mapOf("ES" to "Simple", "GL" to "Simple", "CA" to "Simple", "EU" to "Sinplea", "FR" to "Simple", "EN" to "Simple", "DE" to "Einfach", "IT" to "Semplice", "PT" to "Simples", "PL" to "Prosty"),
        "avanzada" to mapOf("ES" to "Avanzada", "GL" to "Avanzada", "CA" to "Avançada", "EU" to "Aurreratua", "FR" to "Avancée", "EN" to "Advanced", "DE" to "Erweitert", "IT" to "Avanzata", "PT" to "Avançada", "PL" to "Zaawansowany"),
        "version_aplicacion" to mapOf("ES" to "Versión de la aplicación", "GL" to "Versión da aplicación", "CA" to "Versió de l'aplicació", "EU" to "Aplikazioaren bertsioa", "FR" to "Version de l'application", "EN" to "App version", "DE" to "App-Version", "IT" to "Versione dell'app", "PT" to "Versão da app", "PL" to "Wersja aplikacji")"""

content = content.replace('"omitir" to mapOf("ES" to "Omitir", "GL" to "Omitir", "CA" to "Ometre", "EU" to "Saltatu", "FR" to "Passer", "EN" to "Skip", "DE" to "Überspringen", "IT" to "Salta", "PT" to "Ignorar", "PL" to "Pomiń")', new_translations)

with open("core/common/src/main/java/com/example/language/LanguageManager.kt", "w") as f:
    f.write(content)
