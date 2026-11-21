rootProject.name = "nyan"

pluginManagement {
  repositories {
    mavenLocal()
    mavenCentral()
    gradlePluginPortal()
    maven("https://central.sonatype.com/repository/maven-snapshots/")
  }
}

dependencyResolutionManagement {
  @Suppress("UnstableApiUsage")
  repositories {
    mavenLocal()
    mavenCentral()
    google()
    maven("https://central.sonatype.com/repository/maven-snapshots/")
  }
}


plugins {
  id("org.gradle.toolchains.foojay-resolver-convention").version("1.0.0")

  // See https://splitties.github.io/refreshVersions
  id("de.fayard.refreshVersions").version("0.60.6")
}

include(
  ":shared",
  ":desktopApp",
)
