plugins {
  alias(libs.plugins.kotlin.multiplatform)
}

kotlin {
  jvm()
  macosArm64()

  sourceSets {
    commonMain {
      dependencies {
        implementation(libs.kotlinx.coroutines.core)
        implementation(libs.ktor.network)
        implementation(libs.klibnanolog)
        implementation(libs.kotlinx.io.core)
      }
    }
    commonTest {
      dependencies {
        implementation(libs.kotlin.test)
      }
    }
  }
}
