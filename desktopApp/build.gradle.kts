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
  dependsOn("linkReleaseExecutableMacosArm64")
  into(layout.buildDirectory.dir("macApp/Nyan.app"))

  from("src/macosMain/resources/Info.plist") {
    into("Contents")
  }

  from(layout.buildDirectory.file("bin/macosArm64/releaseExecutable/desktopApp.kexe")) {
    into("Contents/MacOS")
    rename { "nyan-server" }
    filePermissions { unix("755") }
  }
}
