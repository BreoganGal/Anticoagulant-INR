import re

with open("app/src/main/java/com/example/MainActivity.kt", "r") as f:
    lines = f.readlines()

new_lines = []
for i, line in enumerate(lines):
    if "val exportLauncher =" in line:
        new_lines.append("    val contextLocal = androidx.compose.ui.platform.LocalContext.current\n")
    new_lines.append(line)

with open("app/src/main/java/com/example/MainActivity.kt", "w") as f:
    f.writelines(new_lines)
