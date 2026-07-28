import re

with open("app/src/main/java/com/example/language/LanguageManager.kt", "r", encoding="utf-8") as f:
    content = f.read()

content = content.replace(
    'IT("IT", "Italiano")', 
    'IT("IT", "Italiano"),\n    PT("PT", "Português"),\n    PL("PL", "Polski")'
)

with open("app/src/main/java/com/example/language/LanguageManager.kt", "w", encoding="utf-8") as f:
    f.write(content)
