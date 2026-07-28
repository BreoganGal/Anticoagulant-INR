import re

with open("app/src/main/java/com/example/ui/screens/RegistroScreen.kt", "r") as f:
    content = f.read()

# Fix 1: change baseDosesList to always use allDoses instead of recent7Logs if empty (well, if it's empty, it's empty)
# The user wants averages to be calculated with "historial de tomas, todas".
# The `baseDosesList` is currently: `val baseDosesList = if (allDoses.isNotEmpty()) allDoses else recent7Logs`
# Wait, if `allDoses` is empty, maybe `recent7Logs` is just sample data. 
# It's better to just use `allDoses` and if it's empty, averages are 0.
# We can change it: `val baseDosesList = allDoses`
content = content.replace("val baseDosesList = if (allDoses.isNotEmpty()) allDoses else recent7Logs", "val baseDosesList = allDoses")

# Fix 2: dailyAvg default when empty
content = content.replace("val dailyAvg = if (totalDays > 0 && allTakenEntries.isNotEmpty()) totalPills / totalDays else 0.72f", "val dailyAvg = if (totalDays > 0 && allTakenEntries.isNotEmpty()) totalPills / totalDays else 0f")

with open("app/src/main/java/com/example/ui/screens/RegistroScreen.kt", "w") as f:
    f.write(content)
