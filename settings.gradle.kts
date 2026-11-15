/**
 * ============================================================
 *  SETTINGS.GRADLE.KTS
 *  This file tells Gradle which modules belong to the project.
 *
 *  We have two main modules:
 *  - shared     → business logic (models, ViewModel, i18n…)
 *  - composeApp → user interface (Compose screens) + Android/Desktop
 * ============================================================
 */

rootProject.name = "WorstCaseOutpost"

// Plugin management – where Gradle downloads the plugins from
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

// Dependency resolution – where Gradle downloads libraries from
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

// The modules that form this project
include(":shared")
include(":composeApp")
