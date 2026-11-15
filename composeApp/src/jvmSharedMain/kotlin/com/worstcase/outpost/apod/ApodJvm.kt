package com.worstcase.outpost.apod

import java.io.ByteArrayOutputStream
import java.net.HttpURLConnection
import java.net.URI
import java.nio.file.AtomicMoveNotSupportedException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext

internal class FileApodStorage(private val directory: Path) : ApodStorage {
    private val file get() = directory.resolve("last-valid.json")
    override suspend fun read(): String? = withContext(Dispatchers.IO) {
        if (!Files.isRegularFile(file)) return@withContext null
        require(Files.size(file) <= APOD_MAX_CACHE_CHARS * 4L)
        // Bound bytes as well as characters even if a file was modified externally.
        val bytes = Files.newInputStream(file).use { input ->
            val output = ByteArrayOutputStream()
            val buffer = ByteArray(8192)
            while (true) {
                currentCoroutineContext().ensureActive()
                val count = input.read(buffer)
                if (count < 0) break
                require(output.size() + count <= APOD_MAX_CACHE_CHARS * 4)
                output.write(buffer, 0, count)
            }
            output.toByteArray()
        }
        bytes.decodeToString(throwOnInvalidSequence = true).also { require(it.length <= APOD_MAX_CACHE_CHARS) }
    }

    override suspend fun write(value: String) = withContext(Dispatchers.IO) {
        require(value.length <= APOD_MAX_CACHE_CHARS)
        Files.createDirectories(directory)
        // Remove this cache's incomplete writes left by an interrupted process.
        Files.newDirectoryStream(directory, "apod-*.tmp").use { leftovers ->
            for (leftover in leftovers) Files.deleteIfExists(leftover)
        }
        val temp = Files.createTempFile(directory, "apod-", ".tmp")
        try {
            Files.write(temp, value.toByteArray(Charsets.UTF_8))
            currentCoroutineContext().ensureActive()
            try {
                Files.move(temp, file, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING)
            } catch (_: AtomicMoveNotSupportedException) {
                Files.move(temp, file, StandardCopyOption.REPLACE_EXISTING)
            }
        } finally {
            Files.deleteIfExists(temp)
        }
        Unit
    }
}

internal actual suspend fun requestApodBytes(url: String, maxBytes: Int): ByteArray = withContext(Dispatchers.IO) {
    val uri = URI(url)
    require(uri.scheme == "https" && uri.host != null && uri.userInfo == null)
    require(maxBytes in 1..APOD_MAX_IMAGE_BYTES)
    val connection = uri.toURL().openConnection() as HttpURLConnection
    connection.connectTimeout = 10_000
    connection.readTimeout = 10_000
    connection.instanceFollowRedirects = false
    connection.useCaches = false
    connection.setRequestProperty("Accept", "application/json,image/jpeg,image/png")
    connection.setRequestProperty("Accept-Encoding", "identity")
    val started = System.nanoTime()
    try {
        require(connection.responseCode == 200) { "APOD HTTP status" }
        val type = connection.contentType.orEmpty().substringBefore(';').lowercase()
        require(type == "application/json" || type.startsWith("image/")) { "APOD content type" }
        val length = connection.contentLengthLong
        require(length <= maxBytes) { "APOD content length" }
        connection.inputStream.use { input ->
            val output = ByteArrayOutputStream()
            val buffer = ByteArray(8192)
            while (true) {
                currentCoroutineContext().ensureActive()
                val remainingMillis = (20_000_000_000L - (System.nanoTime() - started)) / 1_000_000L
                require(remainingMillis > 0) { "APOD transfer timeout" }
                connection.readTimeout = remainingMillis.coerceAtMost(10_000).toInt()
                val count = input.read(buffer)
                if (count < 0) break
                require(output.size() + count <= maxBytes) { "APOD transfer limit" }
                output.write(buffer, 0, count)
            }
            output.toByteArray()
        }
    } finally {
        connection.disconnect()
    }
}
