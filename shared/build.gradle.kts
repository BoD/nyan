plugins {
  alias(libs.plugins.kotlin.multiplatform)
  alias(libs.plugins.ksp)
  alias(libs.plugins.apollo.execution)
}

val generateVersionKtTask: TaskProvider<Task> = tasks.register("generateVersionKt") {
  description = "Generates Version.kt"
  val outputDir = layout.buildDirectory.dir("generated/source/kotlin").get().asFile
  outputs.dir(outputDir)
  val versionProvider = providers.provider { rootProject.version.toString() }
  inputs.property("version", versionProvider)
  doFirst {
    val outputWithPackageDir = File(outputDir, "org/jraf/nyan").apply { mkdirs() }
    File(outputWithPackageDir, "Version.kt").writeText(
      """
        package org.jraf.nyan
        const val VERSION = "v${versionProvider.get()}"
      """.trimIndent()
    )
  }
}

kotlin {
  jvm()
  macosArm64()

  sourceSets {
    commonMain {
      kotlin.srcDir(generateVersionKtTask)

      dependencies {
        implementation(libs.kotlinx.coroutines.core)
        implementation(libs.ktor.server.cio)
        implementation(libs.ktor.server.defaultHeaders)
        implementation(libs.apollo.execution.runtime)
        implementation(libs.apollo.execution.ktor)
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

apolloExecution {
  service("nyan") {
    packageName = "org.jraf.nyan.graphql"
    schemaFile.set(file("src/commonMain/graphql/schema.graphqls"))
  }
}


tasks.configureEach {
  if (name.startsWith("kspKotlin", ignoreCase = true)) {
    dependsOn("kspCommonMainKotlinMetadata")
  }
}
