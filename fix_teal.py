import re

def fix(file):
    with open(file, 'r') as f:
        content = f.read()
    
    # We will replace `PrimaryTeal` with `MaterialTheme.colorScheme.primary` 
    # except inside Canvas or if it causes issues, but wait...
    # Let's just find and replace specific matches.
    
    content = content.replace("contentColor = PrimaryTeal", "contentColor = MaterialTheme.colorScheme.primary")
    content = content.replace("color = PrimaryTeal", "color = MaterialTheme.colorScheme.primary")
    content = content.replace("color = if (isSel) PrimaryTeal", "color = if (isSel) MaterialTheme.colorScheme.primary")
    content = content.replace("color = if (item.punctuality == \"MISSED\") StatusMissedRed else PrimaryTeal", "color = if (item.punctuality == \"MISSED\") StatusMissedRed else MaterialTheme.colorScheme.primary")
    content = content.replace("containerColor = if (dateRangeMode == \"7DAYS\") PrimaryTeal", "containerColor = if (dateRangeMode == \"7DAYS\") MaterialTheme.colorScheme.primary")
    content = content.replace("containerColor = if (dateRangeMode == \"1MONTH\") PrimaryTeal", "containerColor = if (dateRangeMode == \"1MONTH\") MaterialTheme.colorScheme.primary")
    content = content.replace("containerColor = if (dateRangeMode == \"CUSTOM\") PrimaryTeal", "containerColor = if (dateRangeMode == \"CUSTOM\") MaterialTheme.colorScheme.primary")
    content = content.replace("containerColor = PrimaryTeal", "containerColor = MaterialTheme.colorScheme.primary")
    
    # Revert in Canvas if we accidentally changed it. 
    # Let's just manually replace the Canvas lines back
    # In RegistroScreen, the canvas lines are:
    # drawLine(
    #     color = PrimaryTeal,
    #     start = Offset...
    content = content.replace("drawLine(\n                    color = MaterialTheme.colorScheme.primary,", "drawLine(\n                    color = PrimaryTeal,")
    content = content.replace("drawLine(color = MaterialTheme.colorScheme.primary,", "drawLine(color = PrimaryTeal,")

    with open(file, 'w') as f:
        f.write(content)

fix("app/src/main/java/com/example/ui/screens/RegistroScreen.kt")
fix("app/src/main/java/com/example/ui/screens/CalendarioScreen.kt")
fix("app/src/main/java/com/example/ui/dialogs/ImportPautaDialog.kt")
fix("app/src/main/java/com/example/ui/screens/AjustesScreen.kt")
