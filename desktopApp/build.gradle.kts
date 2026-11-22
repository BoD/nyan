import org.apache.tools.ant.filters.ReplaceTokens
import java.nio.file.Files
import java.nio.file.Path

plugins {
  alias(libs.plugins.kotlin.multiplatform)
}

kotlin {
  macosArm64 {
    binaries {
      executable()
    }
  }

  @Suppress("DEPRECATION")
  macosX64 {
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

fun registerMacAppBundle(architecture: String) {
  val architectureName = architecture.lowercase()
  val version = rootProject.version.toString()
  val appBundle = layout.buildDirectory.dir("macosApp/$architectureName/Nyan.app")
  val installerPackage = layout.buildDirectory.file("macosApp/$architectureName/Nyan-$architectureName-$version.pkg")
  val installerRoot = layout.buildDirectory.dir("macosInstaller/$architectureName")
  val installerComponents = layout.projectDirectory.file("src/macosMain/installer-components.plist")
  val bundleTask = tasks.register<Sync>("bundleMacosApp$architecture") {
    group = LifecycleBasePlugin.BUILD_GROUP
    description = "Bundles the macOS $architecture app. Results in build/macosApp/$architectureName/Nyan.app"
    inputs.property("version", rootProject.version.toString())
    dependsOn("linkReleaseExecutableMacos$architecture")
    into(appBundle)

    from("src/macosMain/resources/Info.plist") {
      into("Contents")
      filter<ReplaceTokens>("tokens" to mapOf("VERSION" to rootProject.version.toString()))
    }

    from("src/macosMain/resources/Nyan.icns") {
      into("Contents/Resources")
    }

    from(layout.buildDirectory.file("bin/macos$architecture/releaseExecutable/desktopApp.kexe")) {
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

  val stageInstaller = tasks.register<Sync>("stageMacosInstaller$architecture") {
    dependsOn(bundleTask)
    into(installerRoot)
    from(appBundle) {
      into("Applications/Nyan.app")
    }
    doLast {
      val binDirectory = installerRoot.get().asFile.toPath().resolve("usr/local/bin")
      Files.createDirectories(binDirectory)
      val commandLink = binDirectory.resolve("nyan")
      Files.deleteIfExists(commandLink)
      Files.createSymbolicLink(commandLink, Path.of("../../../Applications/Nyan.app/Contents/Resources/scripts/nyan"))
    }
  }

  tasks.register<Exec>("packageMacosApp$architecture") {
    group = "distribution"
    description =
      "Creates an unsigned macOS $architecture installer at build/macosApp/$architectureName/Nyan-$architectureName-$version.pkg"
    dependsOn(stageInstaller)
    inputs.dir(installerRoot)
    inputs.file(installerComponents)
    outputs.file(installerPackage)
    commandLine(
      "/usr/bin/pkgbuild",
      "--root", installerRoot.get().asFile.absolutePath,
      "--component-plist", installerComponents.asFile.absolutePath,
      "--identifier", "org.jraf.nyan",
      "--version", version,
      "--install-location", "/",
      installerPackage.get().asFile.absolutePath,
    )
  }
}

registerMacAppBundle("Arm64")
registerMacAppBundle("X64")

tasks.register("bundleMacosApp") {
  description = "Bundles the macOS app for all architectures. Results in build/macosApp/Arm64/Nyan.app and build/macosApp/X64/Nyan.app"
  dependsOn("bundleMacosAppArm64", "bundleMacosAppX64")
}

tasks.register("packageMacosApp") {
  group = "distribution"
  description = "Creates unsigned macOS installers for Apple Silicon and Intel."
  dependsOn("packageMacosAppArm64", "packageMacosAppX64")
}
