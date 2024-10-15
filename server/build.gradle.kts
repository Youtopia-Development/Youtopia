import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnPlugin
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnRootExtension

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    kotlin("plugin.serialization") version "1.9.23"
    id("app.cash.zipline")
    id("com.android.library")
}

kotlin {
    jvm()
    androidTarget()

    js {
        browser()
        binaries.executable()
    }

    sourceSets {
        commonMain {
            dependencies {
                val kotlinVersion: String by project
                val kotlinxSerializationVersion: String by project
                val ziplineVersion: String by project
                implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
                implementation("app.cash.zipline:zipline:$ziplineVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:$kotlinxSerializationVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinxSerializationVersion")
                implementation(project(":shared"))
            }
        }
        val jvmMain by getting {
            dependencies {
                val ziplineVersion: String by project
                implementation("app.cash.zipline:zipline-loader:$ziplineVersion")
            }
        }
        val androidMain by getting {
            dependencies {
                val ziplineVersion: String by project
                val libgdxVersion: String by project
                implementation("app.cash.zipline:zipline-loader:$ziplineVersion")
                api("com.badlogicgames.gdx:gdx:$libgdxVersion")
            }
        }
    }
}

android {
    compileSdk = 34
    namespace = "org.youtopia.server"
    defaultConfig {
        minSdk = 26
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

zipline {
    mainFunction.set("org.youtopia.server.zipline.launchCommandsService")
    version.set("1.0.0")
    metadata.put("build_timestamp", "2023-10-25T12:00:00T")
}

plugins.withType<YarnPlugin> {
    the<YarnRootExtension>().yarnLockAutoReplace = true
}
