import re

with open("gradle/libs.versions.toml", "r") as f:
    content = f.read()

content = content.replace(
    'retrofit-converter-serialization = { group = "com.jakewharton.retrofit", name = "retrofit2-kotlinx-serialization-converter", version.ref = "retrofitConverterSerialization" }',
    'retrofit-converter-kotlinx-serialization = { group = "com.squareup.retrofit2", name = "converter-kotlinx-serialization", version.ref = "retrofit" }'
)

with open("gradle/libs.versions.toml", "w") as f:
    f.write(content)

