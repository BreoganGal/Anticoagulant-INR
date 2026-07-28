import re

def fix_file(filepath):
    with open(filepath, "r") as f:
        content = f.read()
    
    # Check if Canvas is used and MaterialTheme is used inside it
    if "Canvas(" in content:
        # We need to capture the primary color before the Canvas block
        # For simplicity, let's just use a regex to replace MaterialTheme.colorScheme.primary with primaryColor inside Canvas block
        # and add val primaryColor = MaterialTheme.colorScheme.primary before Canvas
        
        # This is a bit tricky, let's just replace MaterialTheme.colorScheme.primary with primaryColor everywhere 
        # inside the composable, and define it at the top of the composable.
        # Wait, that's too broad. Let's just fix the specific lines 789, 796 in RegistroScreen.kt
        if "RegistroScreen.kt" in filepath:
            content = content.replace("Canvas(modifier = Modifier", "val primaryColor = MaterialTheme.colorScheme.primary\n                    Canvas(modifier = Modifier")
            content = re.sub(r'color = MaterialTheme\.colorScheme\.primary,(\s*start = Offset\(leftPadding, topTargetY\))', r'color = primaryColor,\1', content)
            content = re.sub(r'color = MaterialTheme\.colorScheme\.primary,(\s*start = Offset\(leftPadding, bottomTargetY\))', r'color = primaryColor,\1', content)
            
        if "FourteenDayChart.kt" in filepath:
            content = content.replace("Canvas(modifier = Modifier", "val primaryColor = MaterialTheme.colorScheme.primary\n            Canvas(modifier = Modifier")
            # Replace inside the file
            content = content.replace("color = MaterialTheme.colorScheme.primary", "color = primaryColor")
            
    with open(filepath, "w") as f:
        f.write(content)

fix_file("app/src/main/java/com/example/ui/screens/RegistroScreen.kt")
fix_file("app/src/main/java/com/example/ui/components/FourteenDayChart.kt")
