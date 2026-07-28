with open("app/src/main/java/com/example/MainActivity.kt", "r") as f:
    content = f.read()

# fix BackHandler issue
content = content.replace("androidx.activity.compose.BackHandler(enabled = selectedTab != 0 || showTermsScreen) {", "var showTermsScreen by remember { mutableStateOf(false) }\n    androidx.activity.compose.BackHandler(enabled = selectedTab != 0 || showTermsScreen) {")
content = content.replace("    var showTermsScreen by remember { mutableStateOf(false) }\n\n    val context = LocalContext.current", "")

# fix context
content = content.replace("val context = LocalContext.current", "val contextLocal = androidx.compose.ui.platform.LocalContext.current")
content = content.replace("Toast.makeText(context, ", "Toast.makeText(contextLocal, ")
content = content.replace("context.contentResolver", "contextLocal.contentResolver")

with open("app/src/main/java/com/example/MainActivity.kt", "w") as f:
    f.write(content)
