package com.worstcase.outpost.apod

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.serialization.json.*
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

const val APOD_FEED = "https://science.nasa.gov/wp-json/wp/v2/apod-basic"
const val APOD_MAX_FEED_BYTES = 1_048_576
const val APOD_MAX_IMAGE_BYTES = 3_145_728
const val APOD_MAX_CACHE_CHARS = 4_300_000

data class ApodEntry(
    val date: String,
    val title: String,
    val credit: String,
    val copyright: String,
    val imageUrl: String,
    val publicationUrl: String
)

data class ApodFeed(val latestDate: String, val latestMediaType: String, val images: List<ApodEntry>)

interface ApodStorage {
    suspend fun read(): String?
    /** Replace the entire record atomically; metadata and bytes must stay together. */
    suspend fun write(value: String)
}

interface ApodClock {
    fun millis(): Long
    fun publicationDay(): String
}

object SystemApodClock : ApodClock {
    override fun millis() = Clock.System.now().toEpochMilliseconds()

    // NASA Science publishes this feed at midnight US Eastern, including daylight saving.
    // Calculado a mano (EasternDay.kt) porque Kotlin/Wasm no resuelve "America/New_York".
    override fun publicationDay() = easternDay(millis())
}

internal fun validApodDate(value: String): Boolean =
    Regex("\\d{4}-\\d{2}-\\d{2}").matches(value) && runCatching { LocalDate.parse(value) }.isSuccess

internal fun safeApodUrl(value: String): Boolean = value.length <= 2048 &&
    value.none { it.code < 32 || it.code == 127 } &&
    Regex("https://[A-Za-z0-9.-]+(?::443)?(?:[/?#][^\\s]*)?").matches(value)

/** Keep credit text, never render markup or load content from basic_html. */
internal fun apodPlainText(value: String): String {
    val stripped = value.replace(Regex("<(script|style)\\b[^>]*>[\\s\\S]*?</\\1>", RegexOption.IGNORE_CASE), " ")
        .replace(Regex("<[^>]*>"), " ")
    return Regex("&(#x[0-9a-fA-F]+|#[0-9]+|[a-zA-Z]+);").replace(stripped) { match ->
        when (val entity = match.groupValues[1]) {
            "amp" -> "&"; "lt" -> "<"; "gt" -> ">"; "quot" -> "\""; "apos" -> "'"; "nbsp" -> " "
            "copy" -> "©"; "ndash" -> "–"; "mdash" -> "—"; "rsquo" -> "’"; "lsquo" -> "‘"
            "rdquo" -> "”"; "ldquo" -> "“"
            else -> {
                val code = when {
                    entity.startsWith("#x") -> entity.drop(2).toIntOrNull(16)
                    entity.startsWith("#") -> entity.drop(1).toIntOrNull()
                    else -> null
                }
                when {
                    code == null || code !in 32..0x10FFFF || code in 0xD800..0xDFFF -> match.value
                    code <= 0xFFFF -> code.toChar().toString()
                    else -> {
                        val n = code - 0x10000
                        "${(0xD800 + (n shr 10)).toChar()}${(0xDC00 + (n and 1023)).toChar()}"
                    }
                }
            }
        }
    }.replace(Regex("\\s+"), " ").trim().take(4000)
}

private val apodJson = Json { ignoreUnknownKeys = true }
private fun JsonObject.string(key: String): String = when (val value = get(key)) {
    is JsonPrimitive -> if (value.isString) value.content else ""
    is JsonObject -> (value["rendered"] as? JsonPrimitive)?.content.orEmpty()
    else -> ""
}

fun parseApodFeed(raw: String, today: String): ApodFeed {
    require(raw.length <= APOD_MAX_FEED_BYTES && validApodDate(today))
    val root = apodJson.parseToJsonElement(raw)
    val posts = when (root) {
        is JsonArray -> root.take(5)
        is JsonObject -> listOf(root)
        else -> error("Invalid APOD response")
    }.mapNotNull { it as? JsonObject }.filter {
        validApodDate(it.string("date")) && it.string("date") <= today
    }.sortedByDescending { it.string("date") }
    require(posts.isNotEmpty()) { "No dated APOD publication" }
    val images = posts.mapNotNull { post ->
        if (post.string("media_type") != "image") return@mapNotNull null
        // In the new Basic API, url is an ARTICLE, not an image. Only hdurl is imagery.
        val image = post.string("hdurl")
        val page = post.string("permalink").ifEmpty { post.string("url") }
        val title = apodPlainText(post.string("title"))
        if (!safeApodUrl(image) || !safeApodUrl(page) || title.isEmpty()) return@mapNotNull null
        ApodEntry(post.string("date"), title, apodPlainText(post.string("credit")),
            apodPlainText(post.string("copyright")), image, page)
    }
    return ApodFeed(posts.first().string("date"), posts.first().string("media_type"), images)
}

/** Ask NASA's image service for a screen rendition, retaining crop/fit parameters. */
fun apodDownloadUrl(url: String): String {
    require(safeApodUrl(url))
    if (!url.startsWith("https://assets.science.nasa.gov/dynamicimage/")) return url
    val base = url.substringBefore('?').substringBefore('#')
    val params = url.substringAfter('?', "").substringBefore('#').split('&').filter {
        it.isNotEmpty() && it.substringBefore('=').lowercase() !in setOf("w", "h")
    }
    return base + "?" + (params + listOf("w=1600", "h=1200")).joinToString("&")
}

/** Reject oversized source bitmaps BEFORE handing compressed bytes to a decoder. JPEG/PNG only. */
fun validateApodImage(bytes: ByteArray) {
    require(bytes.size in 24..APOD_MAX_IMAGE_BYTES) { "APOD image size" }
    fun u(i: Int) = bytes[i].toInt() and 255
    fun be16(i: Int) = (u(i) shl 8) or u(i + 1)
    fun be32(i: Int) = (u(i).toLong() shl 24) or (u(i + 1).toLong() shl 16) or
        (u(i + 2).toLong() shl 8) or u(i + 3).toLong()
    val dimensions: Pair<Long, Long> = if (
        bytes.take(8) == listOf(137, 80, 78, 71, 13, 10, 26, 10).map { it.toByte() } &&
        bytes.sliceArray(12..15).decodeToString() == "IHDR"
    ) {
        be32(16) to be32(20)
    } else {
        require(u(0) == 255 && u(1) == 216) { "Unsupported APOD format" }
        var offset = 2
        var found: Pair<Long, Long>? = null
        while (offset + 3 < bytes.size) {
            require(u(offset) == 255)
            while (offset < bytes.size && u(offset) == 255) offset++
            require(offset < bytes.size)
            val marker = u(offset++)
            if (marker == 0xD9 || marker == 0xDA) break
            if (marker == 0x01 || marker in 0xD0..0xD7) continue
            require(offset + 1 < bytes.size)
            val size = be16(offset)
            require(size >= 2 && offset + size <= bytes.size)
            if (marker in listOf(0xC0, 0xC1, 0xC2, 0xC3, 0xC5, 0xC6, 0xC7, 0xC9, 0xCA, 0xCB, 0xCD, 0xCE, 0xCF)) {
                require(size >= 8)
                found = be16(offset + 5).toLong() to be16(offset + 3).toLong()
                break
            }
            offset += size
        }
        requireNotNull(found) { "No JPEG dimensions" }
    }
    val (width, height) = dimensions
    require(width in 1L..8192L && height in 1L..8192L && width * height <= 12_000_000) {
        "APOD bitmap dimensions"
    }
}

internal data class ApodRecord(
    val entry: ApodEntry? = null,
    val bytes: ByteArray? = null,
    val attemptDay: String = "",
    val attemptedAt: Long = 0,
    val attempts: Int = 0,
    val complete: Boolean = false
)

@OptIn(ExperimentalEncodingApi::class)
internal fun encodeApodRecord(record: ApodRecord): String = buildJsonObject {
    put("version", 1)
    put("day", record.attemptDay); put("at", record.attemptedAt)
    put("attempts", record.attempts); put("complete", record.complete)
    record.entry?.let {
        put("entry", buildJsonObject {
            put("date", it.date); put("title", it.title); put("credit", it.credit)
            put("copyright", it.copyright); put("hdurl", it.imageUrl); put("permalink", it.publicationUrl)
        })
        put("bytes", Base64.encode(requireNotNull(record.bytes)))
    }
}.toString().also { require(it.length <= APOD_MAX_CACHE_CHARS) }

@OptIn(ExperimentalEncodingApi::class)
internal fun decodeApodRecord(raw: String): ApodRecord {
    require(raw.length <= APOD_MAX_CACHE_CHARS)
    val obj = apodJson.parseToJsonElement(raw).jsonObject
    require(obj["version"]?.jsonPrimitive?.int == 1)
    val entry = (obj["entry"] as? JsonObject)?.let {
        val date = it.string("date")
        require(validApodDate(date) && apodPlainText(it.string("title")).isNotBlank() &&
            safeApodUrl(it.string("hdurl")) && safeApodUrl(it.string("permalink")))
        ApodEntry(date, apodPlainText(it.string("title")), apodPlainText(it.string("credit")),
            apodPlainText(it.string("copyright")), it.string("hdurl"), it.string("permalink"))
    }
    val bytes = if (entry != null) Base64.decode(obj.string("bytes")).also(::validateApodImage) else null
    val day = obj.string("day")
    require(day.isEmpty() || validApodDate(day))
    return ApodRecord(entry, bytes, day, obj["at"]?.jsonPrimitive?.long ?: 0,
        (obj["attempts"]?.jsonPrimitive?.int ?: 0).coerceIn(0, 3), obj["complete"]?.jsonPrimitive?.boolean ?: false)
}
