package com.worstcase.outpost.apod

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.test.runTest
import kotlin.test.*

/** Controlled fixtures only. Authored but not executed, as requested. No NASA requests. */
class ApodTest {
    private class Clock : ApodClock {
        var day = "2026-11-14"
        var time = 10_000_000L
        override fun publicationDay() = day
        override fun millis() = time
        fun hour() { time += 3_600_001 }
    }
    private class MemoryStorage(var value: String? = null) : ApodStorage {
        var unavailable = false
        override suspend fun read(): String? {
            check(!unavailable)
            return value
        }
        override suspend fun write(value: String) {
            check(!unavailable)
            this.value = value
        }
    }
    private val imageUrl = "https://assets.science.nasa.gov/dynamicimage/test.jpg?w=9000&h=9000&fit=clip"
    private val pageUrl = "https://science.nasa.gov/image-article/test/"
    private fun post(day: String = "2026-11-14", media: String = "image", image: String = imageUrl) = """
        {"date":"$day","media_type":"$media","title":"<b>Stars &amp; Moon</b>",
        "credit":"<a href='https://example.org'>A. Scientist</a> &copy; 2026",
        "copyright":"A. Scientist","permalink":"$pageUrl","url":"$pageUrl","hdurl":"$image",
        "extra":"ignored"}
    """.trimIndent()
    // Header-only fixture: the injected decoder is fake. Production must also perform a full decode.
    private fun png(width: Int = 800, height: Int = 600): ByteArray {
        val bytes = ByteArray(24)
        listOf(137, 80, 78, 71, 13, 10, 26, 10).forEachIndexed { i, v -> bytes[i] = v.toByte() }
        "IHDR".encodeToByteArray().copyInto(bytes, 12)
        for (n in 0..3) {
            bytes[16 + n] = (width ushr ((3 - n) * 8)).toByte()
            bytes[20 + n] = (height ushr ((3 - n) * 8)).toByte()
        }
        return bytes
    }

    @Test fun feedUsesHdurlAndKeepsPlainCreditsAndActualDate() {
        val result = parseApodFeed("[${post()}]", "2026-11-14").images.single()
        assertEquals(imageUrl, result.imageUrl)
        assertEquals(pageUrl, result.publicationUrl)
        assertEquals("Stars & Moon", result.title)
        assertEquals("A. Scientist © 2026", result.credit)
        assertEquals("2026-11-14", result.date)
        assertEquals("A. Scientist", result.copyright)
        assertTrue(apodDownloadUrl(result.imageUrl).contains("w=1600&h=1200"))
        assertTrue(apodDownloadUrl(result.imageUrl).contains("fit=clip"))
        assertEquals("https://example.org/a.jpg", apodDownloadUrl("https://example.org/a.jpg"))
    }

    @Test fun videosIframesAndMissingHdurlNeverUseTheArticleAsAnImage() {
        for (media in listOf("video", "iframe")) {
            assertTrue(parseApodFeed("[${post(media = media)}]", "2026-11-14").images.isEmpty())
        }
        assertTrue(parseApodFeed("[${post(image = "")}]", "2026-11-14").images.isEmpty())
        assertTrue(parseApodFeed("[${post(image = "javascript:alert(1)")}]", "2026-11-14").images.isEmpty())
        assertEquals("Safe", apodPlainText("<script>bad()</script><style>bad</style><b>Safe</b>"))
    }

    @Test fun datesAreValidatedSortedAndFuturePostsExcluded() {
        val raw = "[${post("2026-11-12")},${post("2026-11-15")},${post("2026-11-14", "video")},${post("2026-11-13")}]"
        val result = parseApodFeed(raw, "2026-11-14")
        assertEquals("2026-11-14", result.latestDate)
        assertEquals(listOf("2026-11-13", "2026-11-12"), result.images.map { it.date })
        assertFails { parseApodFeed("not-json", "2026-11-14") }
        assertFails { parseApodFeed("{\"code\":\"unavailable\"}", "2026-11-14") }
        assertFails { parseApodFeed("[${post("2026-02-30")}]", "2026-11-14") }
        assertFails { parseApodFeed("[]", "2026-11-14") }
    }

    @Test fun imageBoundsAreCheckedBeforeDecode() {
        validateApodImage(png())
        assertFails { validateApodImage(png(9000, 600)) }
        assertFails { validateApodImage(png(8000, 8000)) }
        assertFails { validateApodImage(png(0, 600)) }
        assertFails { validateApodImage("<html>this is an article, not an image</html>".encodeToByteArray()) }
        assertFails { validateApodImage(ByteArray(APOD_MAX_IMAGE_BYTES + 1)) }
    }

    @Test fun repeatedAndConcurrentRefreshesShareOneDailyDownload() = runTest {
        val clock = Clock()
        val storage = MemoryStorage()
        val started = CompletableDeferred<Unit>()
        val release = CompletableDeferred<Unit>()
        var feeds = 0
        var downloads = 0
        val repo = ApodRepository(storage, { url, _ ->
            if (url.startsWith(APOD_FEED)) {
                feeds++
                started.complete(Unit)
                release.await()
                "[${post(clock.day)}]".encodeToByteArray()
            } else { downloads++; png() }
        }, { "decoded" }, clock)
        val first = async { repo.refreshIfNeeded() }
        started.await()
        val second = async { repo.refreshIfNeeded() }
        release.complete(Unit)
        first.await(); second.await()
        repo.refreshIfNeeded()
        assertEquals(1, feeds)
        assertEquals(1, downloads)
        assertEquals("decoded", repo.frame.value?.image)
        // Restart on the same day restores the bytes and does not fetch again.
        val restarted = ApodRepository(storage, { _, _ -> error("Unexpected network call") }, { "from cache" }, clock)
        restarted.refreshIfNeeded()
        assertEquals("from cache", restarted.frame.value?.image)
    }

    @Test fun offlineFirstRunAndFailedRefreshKeepFallbackAndBoundRetriesAcrossRestarts() = runTest {
        val clock = Clock()
        val storage = MemoryStorage()
        var calls = 0
        fun repo() = ApodRepository(storage, { _, _ -> calls++; error("Offline / HTTP error / timeout") }, { "decoded" }, clock)
        val first = repo()
        first.refreshIfNeeded()
        assertNull(first.frame.value)
        repo().refreshIfNeeded()
        assertEquals(1, calls)
        repeat(5) { clock.hour(); repo().refreshIfNeeded() }
        assertEquals(3, calls)
        clock.day = "2026-11-15"
        repo().refreshIfNeeded()
        assertEquals(4, calls)
    }

    @Test fun failedDownloadOrDecodeAndVideoPreservePreviouslyValidatedImage() = runTest {
        val clock = Clock()
        val storage = MemoryStorage()
        var failDecode = false
        var failTransfer = false
        var media = "image"
        val repo = ApodRepository(storage, { url, _ ->
            if (url.startsWith(APOD_FEED)) "[${post(clock.day, media)}]".encodeToByteArray()
            else { check(!failTransfer); png() }
        }, { check(!failDecode); "valid" }, clock)
        repo.refreshIfNeeded()
        clock.day = "2026-11-15"
        failTransfer = true
        repo.refreshIfNeeded()
        assertEquals("2026-11-14", repo.frame.value?.entry?.date)
        clock.hour(); failTransfer = false; failDecode = true
        repo.refreshIfNeeded()
        assertEquals("2026-11-14", repo.frame.value?.entry?.date)
        // The persisted old bytes and metadata also survive a failed new decode.
        assertEquals("2026-11-14", decodeApodRecord(storage.value!!).entry?.date)
        clock.day = "2026-11-16"; failDecode = false; media = "iframe"
        repo.refreshIfNeeded()
        assertEquals("2026-11-14", repo.frame.value?.entry?.date)
        assertTrue(decodeApodRecord(storage.value!!).complete)
        val offline = ApodRepository(storage, { _, _ -> error("Offline") }, { "restored" }, clock)
        offline.refreshIfNeeded()
        assertEquals("restored", offline.frame.value?.image)
    }

    @Test fun delayedPublicationAndPartialImageMetadataAreRetriedButRemainBounded() = runTest {
        val clock = Clock()
        val storage = MemoryStorage()
        var response = "[${post("2026-11-13")}]"
        var calls = 0
        val repo = ApodRepository(storage, { url, _ ->
            if (url.startsWith(APOD_FEED)) { calls++; response.encodeToByteArray() } else png()
        }, { "decoded" }, clock)
        repo.refreshIfNeeded()
        assertEquals("2026-11-13", repo.frame.value?.entry?.date)
        assertFalse(decodeApodRecord(storage.value!!).complete)
        response = "[${post(image = "")}]"
        clock.hour(); repo.refreshIfNeeded()
        assertFalse(decodeApodRecord(storage.value!!).complete)
        response = "[${post()}]"
        clock.hour(); repo.refreshIfNeeded()
        assertEquals(3, calls)
        assertEquals("2026-11-14", repo.frame.value?.entry?.date)
    }

    @Test fun corruptOrUnavailableStorageDoesNotPreventValidNetworkImage() = runTest {
        for (unavailable in listOf(false, true)) {
            val storage = MemoryStorage("{broken-json").also { it.unavailable = unavailable }
            val repo = ApodRepository(storage, { url, _ ->
                if (url.startsWith(APOD_FEED)) "[${post()}]".encodeToByteArray() else png()
            }, { "decoded" }, Clock())
            repo.refreshIfNeeded()
            assertEquals("decoded", repo.frame.value?.image)
        }
    }

    @Test fun cancellationIsPropagatedRatherThanMarkedAsSuccessful() = runTest {
        val storage = MemoryStorage()
        val repo = ApodRepository(storage, { _, _ -> throw CancellationException("Closed") }, { "decoded" }, Clock())
        try {
            repo.refreshIfNeeded()
            fail("Cancellation was swallowed")
        } catch (_: CancellationException) {
            assertFalse(decodeApodRecord(storage.value!!).complete)
        }
    }
}
