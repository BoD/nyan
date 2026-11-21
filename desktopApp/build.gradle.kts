import org.apache.tools.ant.filters.ReplaceTokens

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

tasks.register<Sync>("bundleMacApp") {
  description = "Bundles the MacOS app. Results in build/macApp/Nyan.app"
  inputs.property("version", rootProject.version.toString())
  dependsOn("linkReleaseExecutableMacosArm64")
  into(layout.buildDirectory.dir("macApp/Nyan.app"))

  from("src/macosMain/resources/Info.plist") {
    into("Contents")
    filter<ReplaceTokens>("tokens" to mapOf("VERSION" to rootProject.version.toString()))
  }

  from("src/macosMain/resources/Nyan.icns") {
    into("Contents/Resources")
  }

  from(layout.buildDirectory.file("bin/macosArm64/releaseExecutable/desktopApp.kexe")) {
    into("Contents/MacOS")
    rename { "nyan-server" }
    filePermissions { unix("755") }
  }

  from(rootProject.layout.projectDirectory.dir("etc/images")) {
    include("*.png")
    into("Contents/Resources/images")
  }


  from(rootProject.layout.projectDirectory.dir("etc/scripts")) {
    into("Contents/Resources/scripts")
    filePermissions { unix("755") }
  }
}
