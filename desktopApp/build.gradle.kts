plugins {
  alias(libs.plugins.kotlin.multiplatform)
}

kotlin {
  macosArm64 {
    binaries {
      executable()
    }
  }

  sourceSets {
    commonMain {
      dependencies {
        implementation(project(":shared"))
      }
    }
  }
}
