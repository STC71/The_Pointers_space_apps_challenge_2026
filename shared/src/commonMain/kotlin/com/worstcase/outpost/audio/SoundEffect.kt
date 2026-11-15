package com.worstcase.outpost.audio

/**
 * Sound effects used during the mission simulation.
 */
enum class SoundEffect(val id: String, val description: String) {
    QUINDAR_BEEP("quindar_beep", "NASA Quindar frequency intro/outro radio telemetry beep"),
    CRITICAL_ALARM("critical_alarm", "High-frequency outpost life-support breach klaxon"),
    DECISION_CONFIRMED("decision_confirmed", "Houston Flight Director console input confirmation"),
    HABITAT_AMBIENT("habitat_ambient", "Subtle pressurized habitat air circulation & telemetry drone"),
    MUSIC_THEME("music_theme", "Epic mission theme (looped in background)"),
    NASA_COUNTDOWN("nasa_countdown", "Real NASA launch countdown clip for intro"),
    HOUSTON_PROBLEM("houston_problem", "Houston we have a problem style call clip")
}

/**
 * Audio player interface implemented per platform (Desktop, Android, Web/Wasm).
 */
interface SoundPlayer {
    fun playSound(effect: SoundEffect)
    /** Completion runs on the platform UI thread, only for an uncancelled narration. */
    fun playNarration(effect: SoundEffect, onFinished: () -> Unit) {
        playSound(effect)
        onFinished()
    }
    fun stopNarration() {}
    fun startMusicLoop() {}
    fun stopMusicLoop() {}
    fun stopAll() { stopNarration(); stopMusicLoop() }
    fun release() { stopAll() }
}

/**
 * Global dispatcher. All calls and completion callbacks belong on the UI thread.
 * Platform players own cancellation and disposal of their asynchronous work.
 */
object AudioManager {
    private var player: SoundPlayer? = null
    private var soundOn: Boolean = true
    private var musicWanted = false
    private var foreground = true
    private var narrationActive = false
    private var narrationGeneration = 0L

    fun initialize(soundPlayer: SoundPlayer) {
        if (player === soundPlayer) return
        player?.release()
        narrationGeneration++
        narrationActive = false
        musicWanted = false
        foreground = true
        soundOn = true
        player = soundPlayer
    }

    fun isSoundOn(): Boolean = soundOn

    fun setSoundOn(value: Boolean) {
        soundOn = value
        if (!value) {
            cancelPlayback()
        } else updateMusic()
    }

    fun play(effect: SoundEffect) {
        if (!soundOn || !foreground) return
        val currentPlayer = player ?: return
        if (effect == SoundEffect.MUSIC_THEME) {
            startBackgroundMusic()
        } else if (effect == SoundEffect.NASA_COUNTDOWN || effect == SoundEffect.HOUSTON_PROBLEM) {
            val generation = ++narrationGeneration
            narrationActive = true
            // Keep the desired theme across mute / backgrounding during the intro.
            if (effect == SoundEffect.NASA_COUNTDOWN) musicWanted = true
            currentPlayer.stopNarration()
            currentPlayer.stopMusicLoop()
            currentPlayer.playNarration(effect) {
                if (player === currentPlayer && generation == narrationGeneration) {
                    narrationGeneration++
                    narrationActive = false
                    updateMusic()
                }
            }
        } else currentPlayer.playSound(effect)
    }

    /** Leaving the intro / restarting cancels narration before starting the theme. */
    fun startBackgroundMusic() {
        musicWanted = true
        narrationGeneration++
        narrationActive = false
        player?.stopNarration()
        updateMusic()
    }

    private fun updateMusic() {
        if (soundOn && foreground && musicWanted && !narrationActive) player?.startMusicLoop()
    }

    fun stopBackgroundMusic() {
        musicWanted = false
        player?.stopMusicLoop()
    }

    private fun cancelPlayback() {
        narrationGeneration++
        narrationActive = false
        player?.stopAll()
    }

    fun pause(soundPlayer: SoundPlayer) {
        if (player !== soundPlayer) return
        foreground = false
        cancelPlayback()
    }

    fun resume(soundPlayer: SoundPlayer) {
        if (player !== soundPlayer) return
        foreground = true
        updateMusic()
    }

    fun release(soundPlayer: SoundPlayer) {
        if (player !== soundPlayer) return
        cancelPlayback()
        soundPlayer.release()
        player = null
        musicWanted = false
    }
}
