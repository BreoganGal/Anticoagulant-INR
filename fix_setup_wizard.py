with open("app/src/main/java/com/example/ui/screens/SetupWizardScreen.kt", "r") as f:
    content = f.read()

# Replace hardcoded text in SetupWizardScreen.kt if any
old_button = """                OutlinedButton(
                    onClick = {
                        onImportPauta(selectedMode)
                    },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                ) {
                    Text(LanguageManager.getString("importar_pauta", lang))
                }"""

new_button = """                OutlinedButton(
                    onClick = {
                        onImportPauta(selectedMode)
                    },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                ) {
                    Text(LanguageManager.getString("importar_pauta", lang))
                }
                Button(
                    onClick = { 
                        val finalMed = if (medication == LanguageManager.getString("pers_otro", lang)) {
                            if (customMedication.isBlank()) "Sintrom" else customMedication
                        } else medication
                        onFinish(selectedMode, finalMed, time)
                    },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                ) {
                    Text(LanguageManager.getString("completar", lang))
                }"""

content = content.replace(old_button, new_button)

with open("app/src/main/java/com/example/ui/screens/SetupWizardScreen.kt", "w") as f:
    f.write(content)
