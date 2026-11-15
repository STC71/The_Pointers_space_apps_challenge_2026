/**
 * ============================================================
 *  COMPOSEAPP / BUILD.GRADLE.KTS
 *  This module contains the Compose Multiplatform UI and the
 *  entry points for Android and Desktop.
 * ============================================================
 */

import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    // ---------- Android ----------
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
            }
        }
    }

    // ---------- Desktop (JVM) ----------
    jvm("desktop")

    // ---------- WebAssembly (Wasm/JS) ----------
    @OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
    wasmJs {
        moduleName = "worstcaseoutpost"
        browser {
            val projectRootDir = project.rootDir.path
            val projectWebServerRootDir = project.projectDir.resolve("src/wasmJsMain/resources")
            commonWebpackConfig {
                outputFileName = "worstcaseoutpost.js"
                devServer = (devServer ?: org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig.DevServer()).apply {
                    static = (static ?: mutableListOf()).apply {
                        add(projectRootDir)
                        add(projectWebServerRootDir.path)
                    }
                }
            }
        }
        binaries.executable()
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                // Our business logic module
                implementation(project(":shared"))

                // Compose Multiplatform libraries
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
				implementation(compose.materialIconsExtended)
                implementation(compose.ui)
                implementation(compose.components.resources)
                implementation(compose.components.uiToolingPreview)

                // Coroutines
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.coil.compose.core)
            }
        }

        val androidMain by getting {
            kotlin.srcDir("src/jvmSharedMain/kotlin")
            dependencies {
                implementation(libs.androidx.activity.compose)
                implementation(libs.androidx.lifecycle.runtime.compose)
            }
        }

        val desktopTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(compose.desktop.uiTestJUnit4)
            }
        }

        val desktopMain by getting {
            kotlin.srcDir("src/jvmSharedMain/kotlin")
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(libs.kotlinx.coroutines.swing)
            }
        }
    }
}

android {
    namespace = "com.worstcase.outpost"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.worstcase.outpost"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

// Desktop packaging configuration
compose.resources {
    publicResClass = true
    packageOfResClass = "com.worstcase.outpost.resources"
}

compose.desktop {
    application {
        mainClass = "com.worstcase.outpost.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "WorstCaseOutpost"
            packageVersion = "1.0.0"
        }
    }
}
