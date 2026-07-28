import re

with open("app/src/main/java/com/example/language/LanguageManager.kt", "r", encoding="utf-8") as f:
    content = f.read()

# Extract all keys and their English value
matches = re.findall(r'"([^"]+)"\s*to\s*mapOf\((.*?)\)', content, re.DOTALL)
for key, map_content in matches:
    en_match = re.search(r'"EN"\s*to\s*"([^"]+)"', map_content)
    en_val = en_match.group(1) if en_match else ""
    print(f"{key}: {en_val}")
