import re

with open("gradle/libs.versions.toml", "r") as f:
    content = f.read()

content = content.replace(
    '[versions]\n',
    '[versions]\nkotlinxCoroutinesPlayServices = "1.10.2"\n'
)

content = content.replace(
    '[libraries]\n',
    '[libraries]\nkotlinx-coroutines-play-services = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-play-services", version.ref = "kotlinxCoroutinesPlayServices" }\n'
)

with open("gradle/libs.versions.toml", "w") as f:
    f.write(content)

with open("app/build.gradle.kts", "r") as f:
    build_content = f.read()

build_content = build_content.replace(
    'implementation(libs.mlkit.text.recognition)',
    'implementation(libs.mlkit.text.recognition)\n  implementation(libs.kotlinx.coroutines.play.services)'
)

with open("app/build.gradle.kts", "w") as f:
    f.write(build_content)

