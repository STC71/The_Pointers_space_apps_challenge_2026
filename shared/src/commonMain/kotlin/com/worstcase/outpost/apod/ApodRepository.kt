package com.worstcase.outpost.apod

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

data class ApodFrame<T>(val entry: ApodEntry, val image: T)

/** One app-scoped owner. A new frame is published only after a complete successful decode. */
class ApodRepository<T>(
    private val storage: ApodStorage,
    private val request: suspend (url: String, maxBytes: Int) -> ByteArray,
    private val decode: suspend (ByteArray) -> T,
    private val clock: ApodClock = SystemApodClock
) {
    private val mutex = Mutex()
    private val mutableFrame = MutableStateFlow<ApodFrame<T>?>(null)
    val frame: StateFlow<ApodFrame<T>?> = mutableFrame.asStateFlow()
    private var restored = false
    private var record = ApodRecord()

    suspend fun refreshIfNeeded() = mutex.withLock {
        val today = clock.publicationDay()
        require(validApodDate(today))
        if (!restored) {
            try {
                storage.read()?.let { raw ->
                    val cached = decodeApodRecord(raw)
                    require(cached.entry == null || cached.entry.date <= today)
                    val image = cached.bytes?.let { decode(it) }
                    currentCoroutineContext().ensureActive()
                    record = cached
                    if (image != null && cached.entry != null) mutableFrame.value = ApodFrame(cached.entry, image)
                }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                // Corrupt/unavailable cache is equivalent to a first run; packaged artwork remains visible.
            }
            restored = true
        }
        val now = clock.millis()
        if (record.attemptDay == today && (record.complete || record.attempts >= 3 ||
                (now >= record.attemptedAt && now - record.attemptedAt < 60 * 60 * 1000))) return@withLock
        record = record.copy(attemptDay = today, attemptedAt = now,
            attempts = if (record.attemptDay == today) record.attempts + 1 else 1, complete = false)
        persist()
        try {
            // At most five posts; recent imagery is useful when today's APOD is a video/iframe.
            val dayParameter = today.replace("-", "").drop(2)
            val bytes = request("$APOD_FEED?per_page=5&page=1&date_to=$dayParameter", APOD_MAX_FEED_BYTES)
            require(bytes.size <= APOD_MAX_FEED_BYTES)
            val feed = parseApodFeed(bytes.decodeToString(throwOnInvalidSequence = true), today)
            val candidate = feed.images.firstOrNull()
            val previous = record.entry
            if (candidate != null && (previous == null || candidate.date >= previous.date)) {
                if (previous != null && previous.imageUrl == candidate.imageUrl && previous.date == candidate.date) {
                    record = record.copy(entry = candidate)
                    mutableFrame.value?.let { mutableFrame.value = it.copy(entry = candidate) }
                } else {
                    val imageBytes = request(apodDownloadUrl(candidate.imageUrl), APOD_MAX_IMAGE_BYTES)
                    validateApodImage(imageBytes)
                    val decoded = decode(imageBytes)
                    currentCoroutineContext().ensureActive()
                    record = record.copy(entry = candidate, bytes = imageBytes)
                    mutableFrame.value = ApodFrame(candidate, decoded)
                }
            }
            // A missing publication can be retried later; a video is a valid daily response.
            record = record.copy(complete = feed.latestDate == today &&
                (feed.latestMediaType in setOf("video", "iframe") || candidate?.date == today))
            persist()
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (_: Exception) {
            // Network, unsupported media and decode failures retain the last validated frame.
            // The persisted attempt stamp limits retries across screen changes and app restarts.
        }
    }

    private suspend fun persist() {
        try {
            storage.write(encodeApodRecord(record))
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (_: Exception) {
            // Private browsing, quota limits or a read-only disk must never block the game.
        }
    }
}
