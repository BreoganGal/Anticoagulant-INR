import re

with open("app/build.gradle.kts", "r") as f:
    content = f.read()

# Add plugin
content = content.replace(
    'alias(libs.plugins.google.services)',
    'alias(libs.plugins.google.services)\n  alias(libs.plugins.kotlin.serialization)'
)

# Add dependencies
content = content.replace(
    'implementation(libs.retrofit)',
    'implementation(libs.retrofit)\n  implementation(libs.kotlinx.serialization.json)\n  implementation(libs.retrofit.converter.serialization)'
)

with open("app/build.gradle.kts", "w") as f:
    f.write(content)
