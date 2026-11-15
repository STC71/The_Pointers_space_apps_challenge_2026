package com.worstcase.outpost.audio

import com.worstcase.outpost.resources.Res
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import org.jetbrains.compose.resources.ExperimentalResourceApi

// The bridge is loaded before the Wasm bundle by index.html. Only primitive values
// and callbacks cross the JS/Wasm boundary, supported by Kotlin 2.1.
private external fun wcoPlayAudio(url: String, fallback: String, loop: Boolean, channel: String, onFinished: () -> Unit)
private external fun wcoStopAudio(channel: String)
private external fun wcoReleaseAudio()
private external fun wcoInstallAudioLifecycle(onPause: () -> Unit, onResume: () -> Unit)

@OptIn(ExperimentalResourceApi::class, ExperimentalEncodingApi::class)
class BrowserSoundPlayer : SoundPlayer {
    private val fallbacks = mutableMapOf<SoundEffect, String>()
    private var released = false

    fun attachLifecycle() {
        wcoInstallAudioLifecycle({ AudioManager.pause(this) }, { AudioManager.resume(this) })
    }

    private fun play(effect: SoundEffect, loop: Boolean, channel: String, onFinished: () -> Unit = {}) {
        if (released) return
        val fallback = fallbacks.getOrPut(effect) {
            "data:audio/wav;base64," + Base64.Default.encode(effect.fallbackWav())
        }
        val uri = effect.fileName?.let { Res.getUri("files/$it") } ?: ""
        wcoPlayAudio(uri, fallback, loop, channel, onFinished)
    }

    override fun playSound(effect: SoundEffect) {
        if (effect == SoundEffect.MUSIC_THEME) startMusicLoop()
        else play(effect, false, "effect")
    }
    override fun playNarration(effect: SoundEffect, onFinished: () -> Unit) = play(effect, false, "narration", onFinished)
    override fun startMusicLoop() = play(SoundEffect.MUSIC_THEME, true, "music")
    override fun stopMusicLoop() { wcoStopAudio("music") }
    override fun stopNarration() { wcoStopAudio("narration") }
    override fun stopAll() { wcoStopAudio("all") }
    override fun release() {
        released = true
        wcoReleaseAudio()
        fallbacks.clear()
    }
}
