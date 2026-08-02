with open("app/src/main/java/com/example/ui/screens/AjustesScreen.kt", "r") as f:
    content = f.read()

# Replace language sorting
content = content.replace("AppLanguage.entries.forEach { appLang ->", "AppLanguage.entries.sortedBy { it.displayName }.forEach { appLang ->")

# Replace App Mode text
content = content.replace('Text(text = "Modo de la aplicación"', 'Text(text = LanguageManager.getString("modo_aplicacion", lang)')

content = content.replace('Text(text = if (settings.appMode == "SIMPLE") "Simple" else "Avanzada"', 'Text(text = if (settings.appMode == "SIMPLE") LanguageManager.getString("simple", lang) else LanguageManager.getString("avanzada", lang)')
content = content.replace('text = if (modeStr == "SIMPLE") "Simple" else "Avanzada",', 'text = if (modeStr == "SIMPLE") LanguageManager.getString("simple", lang) else LanguageManager.getString("avanzada", lang),')

# Replace Version text
content = content.replace('Text(text = "Versión de la aplicación"', 'Text(text = LanguageManager.getString("version_aplicacion", lang)')

with open("app/src/main/java/com/example/ui/screens/AjustesScreen.kt", "w") as f:
    f.write(content)
