#!/bin/bash
set -e

create_manifest() {
  mkdir -p $1/src/main
  cat << XML > $1/src/main/AndroidManifest.xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android" />
XML
}

create_build_gradle() {
  cat << 'GRADLE' > $1/build.gradle.kts
plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.kotlin.android)
  id("com.google.devtools.ksp")
}
android {
  namespace = "com.example.$2"
  compileSdk = 36
  defaultConfig {
    minSdk = 24
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
}
dependencies {
GRADLE

  # Add compose if ui
  if [[ "$2" == "ui" || "$2" == "feature.calendario" ]]; then
    cat << 'GRADLE' >> $1/build.gradle.kts
  implementation(platform(libs.androidx.compose.bom))
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.ui.graphics)
  implementation(libs.androidx.compose.ui.tooling.preview)
  implementation(libs.androidx.core.ktx)
GRADLE
  fi
  
  if [[ "$2" == "core.presentation" ]]; then
    cat << 'GRADLE' >> $1/build.gradle.kts
  implementation(libs.androidx.lifecycle.viewmodel.compose)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.kotlinx.coroutines.core)
GRADLE
  fi

  if [[ "$2" == "data" ]]; then
    cat << 'GRADLE' >> $1/build.gradle.kts
  implementation(libs.androidx.room.runtime)
  implementation(libs.androidx.room.ktx)
  ksp(libs.androidx.room.compiler)
GRADLE
  fi

  if [[ "$2" == "core.common" ]]; then
    cat << 'GRADLE' >> $1/build.gradle.kts
  implementation(libs.mlkit.text.recognition)
  implementation(libs.androidx.core.ktx)
GRADLE
  fi

  echo "}" >> $1/build.gradle.kts
}

create_manifest data
create_build_gradle data data

create_manifest ui
create_build_gradle ui ui
echo "dependencies { implementation(project(\":data\")) }" >> ui/build.gradle.kts
echo "dependencies { implementation(project(\":core:common\")) }" >> ui/build.gradle.kts

create_manifest feature/calendario
create_build_gradle feature/calendario feature.calendario
echo "dependencies { implementation(project(\":data\")); implementation(project(\":ui\")); implementation(project(\":core:common\")); implementation(project(\":core:presentation\")) }" >> feature/calendario/build.gradle.kts

create_manifest core/presentation
create_build_gradle core/presentation core.presentation
echo "dependencies { implementation(project(\":data\")); implementation(project(\":core:common\")); implementation(project(\":ui\")) }" >> core/presentation/build.gradle.kts

create_manifest core/common
create_build_gradle core/common core.common

