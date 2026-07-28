import re

with open("gradle/libs.versions.toml", "r") as f:
    content = f.read()

# Add versions
content = content.replace(
    '[versions]\n',
    '[versions]\nkotlinxSerializationJson = "1.6.3"\nretrofitConverterSerialization = "1.0.0"\n'
)

# Add libraries
content = content.replace(
    '[libraries]\n',
    '[libraries]\nkotlinx-serialization-json = { group = "org.jetbrains.kotlinx", name = "kotlinx-serialization-json", version.ref = "kotlinxSerializationJson" }\nretrofit-converter-serialization = { group = "com.jakewharton.retrofit", name = "retrofit2-kotlinx-serialization-converter", version.ref = "retrofitConverterSerialization" }\n'
)

# Add plugins
content = content.replace(
    '[plugins]\n',
    '[plugins]\nkotlin-serialization = { id = "org.jetbrains.kotlin.plugin.serialization", version.ref = "kotlin" }\n'
)

with open("gradle/libs.versions.toml", "w") as f:
    f.write(content)
