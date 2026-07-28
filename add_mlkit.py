import re

with open("gradle/libs.versions.toml", "r") as f:
    content = f.read()

content = content.replace(
    '[versions]\n',
    '[versions]\nmlkitTextRecognition = "16.0.1"\n'
)

content = content.replace(
    '[libraries]\n',
    '[libraries]\nmlkit-text-recognition = { group = "com.google.mlkit", name = "text-recognition", version.ref = "mlkitTextRecognition" }\n'
)

with open("gradle/libs.versions.toml", "w") as f:
    f.write(content)

with open("app/build.gradle.kts", "r") as f:
    build_content = f.read()

build_content = build_content.replace(
    'implementation(libs.retrofit)',
    'implementation(libs.retrofit)\n  implementation(libs.mlkit.text.recognition)'
)

with open("app/build.gradle.kts", "w") as f:
    f.write(build_content)

