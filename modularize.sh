#!/bin/bash
set -e

# Create directories
mkdir -p data/src/main/java/com/example
mkdir -p ui/src/main/java/com/example
mkdir -p feature/calendario/src/main/java/com/example/ui/screens
mkdir -p core/presentation/src/main/java/com/example/ui
mkdir -p core/common/src/main/java/com/example

# Move Data
mv app/src/main/java/com/example/data data/src/main/java/com/example/

# Move UI
mv app/src/main/java/com/example/ui/theme ui/src/main/java/com/example/ui/
mv app/src/main/java/com/example/ui/components ui/src/main/java/com/example/ui/
mv app/src/main/java/com/example/ui/dialogs ui/src/main/java/com/example/ui/

# Move Feature Calendario
mv app/src/main/java/com/example/ui/screens/CalendarioScreen.kt feature/calendario/src/main/java/com/example/ui/screens/

# Move Core Presentation
mv app/src/main/java/com/example/ui/MainViewModel.kt core/presentation/src/main/java/com/example/ui/

# Move Core Common
mv app/src/main/java/com/example/language core/common/src/main/java/com/example/
mv app/src/main/java/com/example/util core/common/src/main/java/com/example/
mv app/src/main/java/com/example/utils core/common/src/main/java/com/example/
mv app/src/main/java/com/example/mlkit core/common/src/main/java/com/example/

