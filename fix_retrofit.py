import re

with open("gradle/libs.versions.toml", "r") as f:
    content = f.read()

# For Retrofit 2.12+, we should use the built-in converter, but we're on 2.12.0
content = content.replace('retrofitConverterSerialization = "1.0.0"', 'retrofitConverterSerialization = "1.0.0"')

with open("app/build.gradle.kts", "r") as f:
    build_content = f.read()

build_content = build_content.replace('implementation(libs.retrofit.converter.serialization)', 'implementation(libs.retrofit.converter.kotlinx.serialization)')

with open("app/build.gradle.kts", "w") as f:
    f.write(build_content)

