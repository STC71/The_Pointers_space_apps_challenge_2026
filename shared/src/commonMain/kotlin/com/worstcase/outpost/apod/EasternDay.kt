package com.worstcase.outpost.apod

// Fecha "de hoy" en hora del este de EE. UU. (la NASA publica el APOD a medianoche de allí).
// Calculada a mano porque Kotlin/Wasm no resuelve TimeZone.of("America/New_York").

private const val DAY_MS = 86_400_000L
private const val HOUR_MS = 3_600_000L

// Días desde 1970-01-01 para una fecha civil (algoritmo de Howard Hinnant)
private fun daysFromCivil(year: Int, month: Int, day: Int): Long {
    val y = if (month <= 2) year - 1 else year
    val era = y / 400
    val yoe = y - era * 400
    val mp = (month + 9) % 12
    val doy = (153 * mp + 2) / 5 + day - 1
    val doe = yoe * 365 + yoe / 4 - yoe / 100 + doy
    return era * 146097L + doe - 719468L
}

// (año, mes, día) a partir de días desde 1970-01-01
private fun civilFromDays(daysSinceEpoch: Long): Triple<Int, Int, Int> {
    val z = daysSinceEpoch + 719468L
    val era = z / 146097L
    val doe = (z - era * 146097L).toInt()
    val yoe = (doe - doe / 1460 + doe / 36524 - doe / 146096) / 365
    val doy = doe - (365 * yoe + yoe / 4 - yoe / 100)
    val mp = (5 * doy + 2) / 153
    val d = doy - (153 * mp + 2) / 5 + 1
    val m = if (mp < 10) mp + 3 else mp - 9
    val y = (yoe + era * 400).toInt() + if (m <= 2) 1 else 0
    return Triple(y, m, d)
}

// n-ésimo domingo del mes, en días desde 1970-01-01
private fun nthSunday(year: Int, month: Int, n: Int): Long {
    val first = daysFromCivil(year, month, 1)
    val dow = (((first % 7) + 4) % 7).toInt() // 0 = domingo (el 1970-01-01 fue jueves)
    return first + (7 - dow) % 7 + 7L * (n - 1)
}

internal fun easternDay(nowMillis: Long): String {
    val year = civilFromDays(nowMillis / DAY_MS).first
    // DST: 2.º domingo de marzo 07:00 UTC hasta 1.er domingo de noviembre 06:00 UTC
    val dstStart = nthSunday(year, 3, 2) * DAY_MS + 7 * HOUR_MS
    val dstEnd = nthSunday(year, 11, 1) * DAY_MS + 6 * HOUR_MS
    val offsetHours = if (nowMillis in dstStart until dstEnd) -4 else -5
    val (y, m, d) = civilFromDays((nowMillis + offsetHours * HOUR_MS) / DAY_MS)
    return "$y-${m.toString().padStart(2, '0')}-${d.toString().padStart(2, '0')}"
}
