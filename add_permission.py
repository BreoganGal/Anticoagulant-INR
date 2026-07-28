import re

with open("app/src/main/AndroidManifest.xml", "r") as f:
    content = f.read()

content = content.replace(
    '<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />',
    '<uses-permission android:name="android.permission.INTERNET" />\n    <uses-permission android:name="android.permission.POST_NOTIFICATIONS" />'
)

with open("app/src/main/AndroidManifest.xml", "w") as f:
    f.write(content)
