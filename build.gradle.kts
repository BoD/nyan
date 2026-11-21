plugins {
  alias(libs.plugins.kotlin.multiplatform).apply(false)
  alias(libs.plugins.ksp).apply(false)
  alias(libs.plugins.apollo.execution).apply(false)
}

version = "1.0.0"

// `./gradlew refreshVersions` to update dependencies
// `./gradlew bundleMacApp` to bundle the MacOS app. Results in desktopApp/build/macApp/Nyan.app
