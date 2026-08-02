import re

with open("core/common/src/main/java/com/example/language/LanguageManager.kt", "r") as f:
    content = f.read()

new_translations = """        "idioma" to mapOf("ES" to "Idioma", "GL" to "Idioma", "CA" to "Idioma", "EU" to "Hizkuntza", "FR" to "Langue", "EN" to "Language", "DE" to "Sprache", "IT" to "Lingua", "PT" to "Idioma", "PL" to "Język"),\n        "idioma_sistema" to mapOf("ES" to "Idioma del sistema", "GL" to "Idioma do sistema", "CA" to "Idioma del sistema", "EU" to "Sistemaren hizkuntza", "FR" to "Langue du système", "EN" to "System language", "DE" to "Systemsprache", "IT" to "Lingua del sistema", "PT" to "Idioma do sistema", "PL" to "Język systemowy"),"""

content = content.replace('"idioma" to mapOf("ES" to "Idioma", "GL" to "Idioma", "CA" to "Idioma", "EU" to "Hizkuntza", "FR" to "Langue", "EN" to "Language", "DE" to "Sprache", "IT" to "Lingua", "PT" to "Idioma", "PL" to "Język"),', new_translations)

with open("core/common/src/main/java/com/example/language/LanguageManager.kt", "w") as f:
    f.write(content)
