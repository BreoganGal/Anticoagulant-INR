plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.google.devtools.ksp)
}
android {
  namespace = "com.example.core.common"
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
  implementation(libs.mlkit.text.recognition)
  implementation(libs.androidx.core.ktx)
}
dependencies {
  implementation(project(":data"))
  implementation(libs.kotlinx.coroutines.play.services)
  implementation(libs.kotlinx.coroutines.core)
}
