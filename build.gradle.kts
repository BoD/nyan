plugins {
  alias(libs.plugins.kotlin.multiplatform).apply(false)
  alias(libs.plugins.ksp).apply(false)
  alias(libs.plugins.apollo.execution).apply(false)
}

version = "1.0.1-SNAPSHOT"

// `./gradlew refreshVersions` to update dependencies
// `./gradlew apolloDumpSchema` to dump the GraphQL schema
// `./gradlew packageMacosAppArm64` or `:desktopApp:packageMacosAppX64` to create the macOS app installer
