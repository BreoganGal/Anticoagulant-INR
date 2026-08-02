import re

with open("app/src/main/java/com/example/ui/screens/AjustesScreen.kt", "r") as f:
    content = f.read()

# Fix currLangDisplay
old_currLangDisplay = 'val currLangDisplay = AppLanguage.entries.firstOrNull { it.code == selectedLang }?.displayName ?: "Español"'
new_currLangDisplay = 'val currLangDisplay = if (selectedLang == "SYSTEM") LanguageManager.getString("idioma_sistema", lang) else AppLanguage.entries.firstOrNull { it.code == selectedLang }?.displayName ?: "Español"'
content = content.replace(old_currLangDisplay, new_currLangDisplay)

# Fix sorting
old_loop = 'AppLanguage.entries.sortedBy { it.displayName }.forEach { appLang ->'
new_loop = '''val sortedLangs = listOf(AppLanguage.SYSTEM) + AppLanguage.entries.filter { it != AppLanguage.SYSTEM }.sortedBy { it.displayName }
                        sortedLangs.forEach { appLang ->'''
content = content.replace(old_loop, new_loop)

# Fix Text inside loop
old_text = '''                                Text(
                                    text = appLang.displayName,'''
new_text = '''                                Text(
                                    text = if (appLang.code == "SYSTEM") LanguageManager.getString("idioma_sistema", lang) else appLang.displayName,'''
content = content.replace(old_text, new_text)

with open("app/src/main/java/com/example/ui/screens/AjustesScreen.kt", "w") as f:
    f.write(content)
