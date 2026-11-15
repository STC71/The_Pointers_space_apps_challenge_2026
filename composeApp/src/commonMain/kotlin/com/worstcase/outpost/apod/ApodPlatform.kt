package com.worstcase.outpost.apod

import coil3.PlatformContext

internal expect fun createApodStorage(context: PlatformContext): ApodStorage
internal expect suspend fun requestApodBytes(url: String, maxBytes: Int): ByteArray
