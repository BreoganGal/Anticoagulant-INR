import os
import re

for root, dirs, files in os.walk("app/src/main/java/com/example/"):
    for file in files:
        if file.endswith(".kt"):
            path = os.path.join(root, file)
            with open(path, "r") as f:
                content = f.read()
            
            # Remove bad imports
            content = re.sub(r'import com\.example\.ui\.theme\.MaterialTheme\.colorScheme\.primary.*?\n', '', content)
            
            # If MaterialTheme is used but not imported from compose, import it
            if "MaterialTheme." in content and "import androidx.compose.material3.MaterialTheme" not in content:
                # Add import right after package statement
                content = re.sub(r'(package .*?\n)', r'\1\nimport androidx.compose.material3.MaterialTheme\n', content)
                
            with open(path, "w") as f:
                f.write(content)

