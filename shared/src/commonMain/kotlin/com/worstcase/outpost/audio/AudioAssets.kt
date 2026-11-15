package com.worstcase.outpost.audio

import kotlin.math.PI
import kotlin.math.sin

/** Packaged file names are shared by all three platform players. */
val SoundEffect.fileName: String?
    get() = when (this) {
        SoundEffect.QUINDAR_BEEP -> "sfx_quindar.mp3"
        SoundEffect.CRITICAL_ALARM -> "sfx_alarm.wav"
        SoundEffect.HABITAT_AMBIENT -> "ambient_habitat.ogg"
        SoundEffect.MUSIC_THEME -> "music_theme.ogg"
        SoundEffect.NASA_COUNTDOWN -> "nasa_countdown.mp3"
        SoundEffect.HOUSTON_PROBLEM -> "houston_we_hace_a_problem.mp3"
        SoundEffect.DECISION_CONFIRMED -> null
    }

private data class Tone(val hz: Double, val milliseconds: Int, val volume: Double = 0.2)

/** Small PCM WAV fallback, generated without codecs or audio devices. */
fun SoundEffect.fallbackWav(): ByteArray {
    val tones = when (this) {
        SoundEffect.QUINDAR_BEEP -> listOf(Tone(2525.0, 250, 0.35))
        SoundEffect.CRITICAL_ALARM -> List(6) { Tone(if (it % 2 == 0) 880.0 else 440.0, 140, 0.4) }
        SoundEffect.DECISION_CONFIRMED -> listOf(Tone(1200.0, 80), Tone(1600.0, 100))
        SoundEffect.HABITAT_AMBIENT -> listOf(Tone(120.0, 400, 0.1))
        SoundEffect.MUSIC_THEME -> listOf(
            Tone(110.0, 400, 0.12), Tone(146.8, 400, 0.12),
            Tone(164.8, 500, 0.12), Tone(220.0, 600, 0.12), Tone(0.0, 800)
        )
        SoundEffect.NASA_COUNTDOWN -> List(5) { Tone(680.0 + it * 40, 180) } + Tone(1320.0, 400)
        SoundEffect.HOUSTON_PROBLEM -> listOf(Tone(400.0, 200), Tone(300.0, 300), Tone(250.0, 400))
    }
    val rate = 22050
    val samples = tones.sumOf { rate * it.milliseconds / 1000 }
    val bytes = ByteArray(44 + samples * 2)
    fun ascii(offset: Int, value: String) = value.forEachIndexed { i, c -> bytes[offset + i] = c.code.toByte() }
    fun number(offset: Int, value: Int, size: Int) {
        repeat(size) { bytes[offset + it] = (value ushr (it * 8)).toByte() }
    }
    ascii(0, "RIFF"); number(4, bytes.size - 8, 4); ascii(8, "WAVE")
    ascii(12, "fmt "); number(16, 16, 4); number(20, 1, 2); number(22, 1, 2)
    number(24, rate, 4); number(28, rate * 2, 4); number(32, 2, 2); number(34, 16, 2)
    ascii(36, "data"); number(40, samples * 2, 4)
    var offset = 44
    for (tone in tones) {
        val count = rate * tone.milliseconds / 1000
        repeat(count) { i ->
            val envelope = minOf(1.0, i / 220.0, (count - 1 - i) / 220.0)
            val sample = (sin(2 * PI * tone.hz * i / rate) * 32767 * tone.volume * envelope).toInt()
            number(offset, sample, 2)
            offset += 2
        }
    }
    return bytes
}
