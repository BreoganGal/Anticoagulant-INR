import re

# In MainActivity.kt, change todayDose fallback
with open("app/src/main/java/com/example/MainActivity.kt", "r") as f:
    content = f.read()

content = content.replace('''    val todayDose = allDoses.firstOrNull { it.date == todayDateStr } ?: DoseEntry(
        date = todayDateStr,
        prescribedFraction = "3/4",
        scheduledTime = settings.reminderTime
    )''', '''    val todayDose = allDoses.firstOrNull { it.date == todayDateStr }''')

with open("app/src/main/java/com/example/MainActivity.kt", "w") as f:
    f.write(content)
