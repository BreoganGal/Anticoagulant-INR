with open("app/src/main/java/com/example/ui/components/FourteenDayChart.kt", "r") as f:
    content = f.read()

# FourteenDayChart.kt
# We just need to make sure primaryColor is declared inside the Composable, outside Canvas.
# Wait, let's just use the hardcoded color for now or replace with Color.Red to be safe, no, let's fix the Kotlin correctly.
# The error says `@Composable invocations can only happen from the context of a @Composable function` for line 128
# Line 128: `val placeholderBarColor = MaterialTheme.colorScheme.surfaceVariant`
# Ah! Box(modifier = ...) { ... Canvas(...) }
# Let's fix that.

import re

# In FourteenDayChart, replace all `MaterialTheme.colorScheme.xxx` inside the non-composable blocks.
# It is better to just declare them at the top of the Composable.
# But FourteenDayChart is a Composable.
# Box { Canvas { ... } } -> Box and Canvas are Composables. But `Canvas` lambda is `DrawScope`, not `@Composable`.
# So inside `Canvas { ... }` we can't call `MaterialTheme.colorScheme.xxx`.
# Line 128 was: `val primaryColor = MaterialTheme.colorScheme.primary` inside `Canvas {` !

content = re.sub(r'(\s+)val primaryColor = MaterialTheme\.colorScheme\.primary\n\s+Canvas', r'\1val primaryColor = MaterialTheme.colorScheme.primary\n\1Canvas', content)
content = re.sub(r'Canvas\(modifier = Modifier\.fillMaxWidth\(\)\.height\(110\.dp\)\) \{\n\s+val primaryColor = MaterialTheme\.colorScheme\.primary', r'val primaryColor = MaterialTheme.colorScheme.primary\n            Canvas(modifier = Modifier.fillMaxWidth().height(110.dp)) {', content)
content = re.sub(r'Canvas\(modifier = Modifier\.size\(10\.dp\)\) \{\n\s+val primaryColor = MaterialTheme\.colorScheme\.primary', r'val primaryColor = MaterialTheme.colorScheme.primary\n            Canvas(modifier = Modifier.size(10.dp)) {', content)

with open("app/src/main/java/com/example/ui/components/FourteenDayChart.kt", "w") as f:
    f.write(content)

with open("app/src/main/java/com/example/ui/components/PillFractionView.kt", "r") as f:
    content = f.read()
    
content = content.replace("MaterialTheme.colorScheme.primaryDark", "MaterialTheme.colorScheme.primary")

with open("app/src/main/java/com/example/ui/components/PillFractionView.kt", "w") as f:
    f.write(content)

