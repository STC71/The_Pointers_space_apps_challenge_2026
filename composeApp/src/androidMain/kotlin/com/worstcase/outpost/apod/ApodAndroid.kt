package com.worstcase.outpost.apod

import coil3.PlatformContext
import java.io.File

internal actual fun createApodStorage(context: PlatformContext): ApodStorage =
    FileApodStorage(File(context.applicationContext.filesDir, "apod").toPath())
