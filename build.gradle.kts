plugins {
    kotlin("multiplatform") version "1.6.10"
}

group = "org.jraf"
version = "1.0.0"

repositories {
    mavenCentral()
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
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
                implementation("com.autodesk:coroutineworker:0.8.0")

            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
    }
}
