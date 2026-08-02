#!/bin/bash
sed -i '/\/\/ SECTION 5: DATOS Y FORMATO DE FECHA/,/if (expandedMenuId == "dateformat") {/!b; /Column(modifier = Modifier.padding(18.dp)) {/a\
                if (settings.appMode != "SIMPLE") {
' app/src/main/java/com/example/ui/screens/AjustesScreen.kt

sed -i '/if (expandedMenuId == "dateformat") {/,/}/!b; /} \/\/ End of dateformat/!b' app/src/main/java/com/example/ui/screens/AjustesScreen.kt
# Wait, this is getting complicated with sed. Let's just use a Python script.
