plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.kotlin.compose)
  alias(libs.plugins.google.devtools.ksp)
}
android {
  namespace = "com.example.feature.calendario"
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
  implementation(platform(libs.androidx.compose.bom))
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.ui.graphics)
  implementation(libs.androidx.compose.ui.tooling.preview)
  implementation(libs.androidx.core.ktx)
}
dependencies { implementation(project(":data")); implementation(project(":ui")); implementation(project(":core:common")); implementation(project(":core:presentation")) }
dependencies {
  implementation(libs.androidx.compose.material.icons.extended)
  implementation(libs.androidx.compose.material.icons.core)
}
