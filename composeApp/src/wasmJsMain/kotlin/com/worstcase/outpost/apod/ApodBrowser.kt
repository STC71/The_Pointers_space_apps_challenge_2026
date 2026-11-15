package com.worstcase.outpost.apod

import coil3.PlatformContext
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

private external fun wcoApodReadCache(done: (String?) -> Unit)
private external fun wcoApodWriteCache(value: String, done: (Boolean) -> Unit)
private external fun wcoApodFetch(url: String, maxBytes: Int, done: (String?) -> Unit): Int
private external fun wcoApodAbort(id: Int)

internal actual fun createApodStorage(context: PlatformContext): ApodStorage = object : ApodStorage {
    override suspend fun read(): String? = suspendCancellableCoroutine { continuation ->
        wcoApodReadCache { value -> if (continuation.isActive) continuation.resume(value) }
    }
    override suspend fun write(value: String) {
        require(value.length <= APOD_MAX_CACHE_CHARS)
        suspendCancellableCoroutine<Unit> { continuation ->
            wcoApodWriteCache(value) { success ->
                if (continuation.isActive) {
                    if (success) continuation.resume(Unit)
                    else continuation.resumeWithException(IllegalStateException("APOD cache unavailable"))
                }
            }
        }
    }
}

@OptIn(ExperimentalEncodingApi::class)
internal actual suspend fun requestApodBytes(url: String, maxBytes: Int): ByteArray {
    val encoded = suspendCancellableCoroutine<String> { continuation ->
        val id = wcoApodFetch(url, maxBytes) { value ->
            if (continuation.isActive) {
                if (value != null) continuation.resume(value)
                else continuation.resumeWithException(IllegalStateException("APOD download unavailable"))
            }
        }
        continuation.invokeOnCancellation { wcoApodAbort(id) }
    }
    require(encoded.length <= ((maxBytes + 2) / 3) * 4)
    return Base64.decode(encoded).also { require(it.size <= maxBytes) }
}
