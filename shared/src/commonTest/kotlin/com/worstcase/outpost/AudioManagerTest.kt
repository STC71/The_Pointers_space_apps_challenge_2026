package com.worstcase.outpost

import com.worstcase.outpost.audio.AudioManager
import com.worstcase.outpost.audio.SoundEffect
import com.worstcase.outpost.audio.SoundPlayer
import com.worstcase.outpost.audio.fallbackWav
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** Regression cases added for static review; not executed as part of this change. */
class AudioManagerTest {
    private class RecordingPlayer : SoundPlayer {
        var musicStarts = 0
        var stops = 0
        var released = false
        val effects = mutableListOf<SoundEffect>()
        val completions = mutableListOf<() -> Unit>()
        override fun playSound(effect: SoundEffect) { effects += effect }
        override fun playNarration(effect: SoundEffect, onFinished: () -> Unit) {
            effects += effect
            // Keep cancelled callbacks so tests can deliver them deliberately late.
            completions += onFinished
        }
        override fun startMusicLoop() { musicStarts++ }
        override fun stopAll() { stops++ }
        override fun release() { released = true; stopAll() }
    }

    private lateinit var player: RecordingPlayer

    @BeforeTest fun setUp() {
        player = RecordingPlayer()
        AudioManager.initialize(player)
    }

    @AfterTest fun tearDown() { AudioManager.release(player) }

    @Test fun countdownStartsThemeOnlyAfterCompletion() {
        assertEquals(0, player.musicStarts)
        AudioManager.play(SoundEffect.NASA_COUNTDOWN)
        assertEquals(0, player.musicStarts)
        player.completions.single().invoke()
        assertEquals(1, player.musicStarts)
        player.completions.single().invoke()
        assertEquals(1, player.musicStarts)
    }

    @Test fun muteCancelsEffectsAndRejectsLateNarrationCompletionAfterUnmute() {
        AudioManager.play(SoundEffect.NASA_COUNTDOWN)
        val obsolete = player.completions.single()
        AudioManager.setSoundOn(false)
        assertEquals(1, player.stops)
        AudioManager.play(SoundEffect.CRITICAL_ALARM)
        assertEquals(listOf(SoundEffect.NASA_COUNTDOWN), player.effects)
        AudioManager.setSoundOn(true)
        val starts = player.musicStarts
        obsolete()
        assertEquals(starts, player.musicStarts)
    }

    @Test fun latestNarrationOwnsMusicResume() {
        AudioManager.startBackgroundMusic()
        AudioManager.play(SoundEffect.HOUSTON_PROBLEM)
        AudioManager.play(SoundEffect.HOUSTON_PROBLEM)
        val starts = player.musicStarts
        player.completions.first().invoke()
        assertEquals(starts, player.musicStarts)
        player.completions.last().invoke()
        assertEquals(starts + 1, player.musicStarts)
    }

    @Test fun leavingIntroInvalidatesItsPendingCompletion() {
        AudioManager.play(SoundEffect.NASA_COUNTDOWN)
        AudioManager.startBackgroundMusic()
        val starts = player.musicStarts
        player.completions.single().invoke()
        assertEquals(starts, player.musicStarts)
    }

    @Test fun backgroundingStopsAllAndResumingRespectsMutePreference() {
        AudioManager.startBackgroundMusic()
        AudioManager.pause(player)
        assertEquals(1, player.stops)
        AudioManager.play(SoundEffect.CRITICAL_ALARM)
        assertTrue(player.effects.isEmpty())
        AudioManager.setSoundOn(false)
        val starts = player.musicStarts
        AudioManager.resume(player)
        assertEquals(starts, player.musicStarts)
        assertFalse(AudioManager.isSoundOn())
        AudioManager.setSoundOn(true)
        assertEquals(starts + 1, player.musicStarts)
    }

    @Test fun replacingPlayerDisposesOldPlayerAndRejectsOldLifecycleAndCallbacks() {
        AudioManager.play(SoundEffect.NASA_COUNTDOWN)
        val old = player
        player = RecordingPlayer()
        AudioManager.initialize(player)
        assertTrue(old.released)
        AudioManager.startBackgroundMusic()
        val starts = player.musicStarts
        old.completions.single().invoke()
        AudioManager.pause(old)
        AudioManager.release(old)
        assertEquals(starts, player.musicStarts)
        assertEquals(0, player.stops)
        assertFalse(player.released)
    }

    @Test fun releaseInvalidatesPendingNarrationAndCannotRestartPlayback() {
        AudioManager.play(SoundEffect.NASA_COUNTDOWN)
        AudioManager.release(player)
        player.completions.single().invoke()
        AudioManager.startBackgroundMusic()
        assertTrue(player.released)
        assertEquals(0, player.musicStarts)
    }

    @Test fun everyFallbackIsMonoPcmWithConsistentWaveLengths() {
        for (effect in SoundEffect.entries) {
            val bytes = effect.fallbackWav()
            fun number(offset: Int, size: Int): Int = (0 until size).fold(0) { value, i ->
                value or ((bytes[offset + i].toInt() and 255) shl (8 * i))
            }
            assertEquals("RIFF", bytes.copyOfRange(0, 4).decodeToString())
            assertEquals("WAVE", bytes.copyOfRange(8, 12).decodeToString())
            assertEquals(bytes.size - 8, number(4, 4))
            assertEquals(1, number(20, 2))
            assertEquals(1, number(22, 2))
            assertEquals(16, number(34, 2))
            assertEquals(bytes.size - 44, number(40, 4))
            assertTrue(bytes.size > 44)
        }
    }
}
