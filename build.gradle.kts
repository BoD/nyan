import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm")
    application
    id("com.github.johnrengelman.shadow")
}

group = "org.jraf"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(KotlinX.coroutines.core)
    implementation(KotlinX.coroutines.jdk8)
    implementation("com.google.code.gson:gson:_")
    testImplementation(kotlin("test"))
}

application {
    mainClass.set("org.jraf.nyan.MainKt")
}

tasks {
    wrapper {
        distributionType = Wrapper.DistributionType.ALL
        gradleVersion = "7.3"
    }

    test {
        useJUnitPlatform()
    }

    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
        dependsOn("generateVersionKt")
    }

    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
        dependsOn("generateVersionKt")
    }

    named<ShadowJar>("shadowJar") {
        minimize()
    }
}

// Generate a Version.kt file with a constant for the version name
tasks.register("generateVersionKt") {
    val outputDir = layout.buildDirectory.dir("generated/source/kotlin").get().asFile
    outputs.dir(outputDir)
    doFirst {
        val outputWithPackageDir = File(outputDir, "org/jraf/nyan").apply { mkdirs() }
        File(outputWithPackageDir, "Version.kt").writeText(
            """
                package org.jraf.nyan

                const val VERSION = "v${project.version}"
            """.trimIndent()
        )
    }
}

kotlin {
    sourceSets["main"].kotlin.srcDir(tasks.getByName("generateVersionKt").outputs.files)
}

// Implements https://github.com/brianm/really-executable-jars-maven-plugin maven plugin behaviour.
// To check details how it works, see http://skife.org/java/unix/2011/06/20/really_executable_jars.html.
tasks.register<DefaultTask>("shadowJarExecutable") {
    description = "Creates a self-executable file, that runs the generated shadow jar"
    group = "Distribution"

    inputs.files(tasks.named("shadowJar"))
    val origFile = inputs.files.singleFile
    outputs.files(File(origFile.parentFile, origFile.nameWithoutExtension + "-executable"))

    doLast {
        val execFile: File = outputs.files.files.first()
        val out = execFile.outputStream()
        out.write("#!/bin/sh\n\nexec java -jar \"\$0\" \"\$@\"\n\n".toByteArray())
        out.write(inputs.files.singleFile.readBytes())
        out.flush()
        out.close()
        execFile.setExecutable(true, false)
    }
}

// Run `./gradlew refreshVersions` to update dependencies
// Run `./gradlew shadowJarExecutable` to build the "really executable jar"
