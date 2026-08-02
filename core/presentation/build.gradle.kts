plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.kotlin.compose)
  alias(libs.plugins.google.devtools.ksp)
}
android {
  namespace = "com.example.core.presentation"
  compileSdk = 36
  defaultConfig {
    minSdk = 26
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
}
dependencies {
  implementation(libs.androidx.lifecycle.viewmodel.compose)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.kotlinx.coroutines.core)
}
dependencies { implementation(project(":data")); implementation(project(":core:common")); implementation(project(":ui")) }
dependencies {
  implementation(libs.androidx.room.runtime)
  implementation(libs.moshi.kotlin)
  implementation(libs.converter.moshi)
}
