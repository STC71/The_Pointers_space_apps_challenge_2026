package com.worstcase.outpost.apod

import coil3.PlatformContext
import java.io.File

internal actual fun createApodStorage(context: PlatformContext): ApodStorage {
    val home = System.getProperty("user.home")
    val os = System.getProperty("os.name").lowercase()
    val base = when {
        os.contains("mac") -> File(home, "Library/Application Support")
        os.contains("win") -> System.getenv("LOCALAPPDATA")?.takeIf { it.isNotBlank() }?.let(::File)
            ?: File(home, "AppData/Local")
        else -> System.getenv("XDG_DATA_HOME")?.takeIf { it.startsWith("/") }?.let(::File)
            ?: File(home, ".local/share")
    }
    return FileApodStorage(File(base, "WorstCaseOutpost/apod").toPath())
}
