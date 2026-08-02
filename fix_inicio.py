with open("app/src/main/java/com/example/ui/screens/InicioScreen.kt", "r") as f:
    content = f.read()

old_header = """                Text(
                    text = "Anticoagulant INR",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )"""

new_header = """                Column {
                    Text(
                        text = LanguageManager.getString("bienvenido_a", lang),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Anticoagulant INR",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }"""

content = content.replace(old_header, new_header)

with open("app/src/main/java/com/example/ui/screens/InicioScreen.kt", "w") as f:
    f.write(content)
