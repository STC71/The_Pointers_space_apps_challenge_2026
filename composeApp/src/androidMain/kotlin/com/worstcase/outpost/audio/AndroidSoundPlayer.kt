package com.worstcase.outpost.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import com.worstcase.outpost.resources.Res
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.ExperimentalResourceApi
import java.io.File

/** MediaPlayer state belongs to the main thread; file loading belongs to IO. */
@OptIn(ExperimentalResourceApi::class)
class AndroidSoundPlayer(context: Context) : SoundPlayer {
    private val cacheDirectory = context.applicationContext.cacheDir
    private val handler = Handler(Looper.getMainLooper())
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val active = mutableSetOf<Playback>()
    private var music: Playback? = null
    private var narration: Playback? = null
    private var released = false

    private class Playback(
        val effect: SoundEffect,
        val loop: Boolean = false,
        val onFinished: (() -> Unit)? = null
    ) {
        @Volatile var cancelled = false
        var job: Job? = null
        var player: MediaPlayer? = null
        var file: File? = null
        var timeout: Runnable? = null
        var loadGeneration = 0
    }

    private fun onMain(action: () -> Unit) {
        if (Looper.myLooper() == handler.looper) action() else handler.post { action() }
    }

    override fun playSound(effect: SoundEffect) = onMain {
        if (effect == SoundEffect.MUSIC_THEME) startMusicLoop()
        else if (!released) start(Playback(effect))
    }

    override fun playNarration(effect: SoundEffect, onFinished: () -> Unit) = onMain {
        stopNarration()
        if (!released) {
            val playback = Playback(effect, onFinished = onFinished)
            narration = playback
            start(playback)
        }
    }

    override fun startMusicLoop() = onMain {
        if (!released && music == null) {
            val playback = Playback(SoundEffect.MUSIC_THEME, loop = true)
            music = playback
            start(playback)
        }
    }

    override fun stopNarration() = onMain { narration?.let { cancel(it) } }
    override fun stopMusicLoop() = onMain { music?.let { cancel(it) } }
    override fun stopAll() = onMain { active.toList().forEach { cancel(it) } }
    override fun release() = onMain {
        released = true
        stopAll()
        scope.cancel()
    }

    private fun start(playback: Playback) {
        if (active.size >= 8) active.firstOrNull { it !== music && it !== narration }?.let { cancel(it) }
        active += playback
        load(playback, fallback = playback.effect.fileName == null)
    }

    private fun load(playback: Playback, fallback: Boolean) {
        val generation = ++playback.loadGeneration
        playback.job = scope.launch {
            var temporary: File? = null
            try {
                val bytes = if (fallback) playback.effect.fallbackWav()
                    else Res.readBytes("files/${playback.effect.fileName}")
                ensureActive()
                val file = File.createTempFile("wco_", if (fallback) ".wav" else ".${playback.effect.fileName!!.substringAfterLast('.')}", cacheDirectory)
                temporary = file
                file.writeBytes(bytes)
                ensureActive()
                if (handler.post {
                    if (released || playback.cancelled || generation != playback.loadGeneration) file.delete()
                    else {
                        playback.file = file
                        prepare(playback, fallback)
                    }
                }) temporary = null // Main-thread callback owns the file now.
            } catch (_: Exception) {
                handler.post {
                    if (generation == playback.loadGeneration) failed(playback, fallback)
                }
            } finally {
                temporary?.delete()
            }
        }
    }

    private fun prepare(playback: Playback, fallback: Boolean) {
        try {
            val player = MediaPlayer()
            playback.player = player
            player.setAudioAttributes(AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build())
            player.setDataSource(playback.file!!.absolutePath)
            player.isLooping = playback.loop
            player.setOnPreparedListener {
                if (playback.cancelled || released || playback.player !== it) return@setOnPreparedListener
                playback.timeout?.let { handler.removeCallbacks(it) }
                try {
                    it.start()
                    if (!playback.loop) {
                        val timeout = Runnable { if (playback.player === player) failed(playback, fallback) }
                        playback.timeout = timeout
                        handler.postDelayed(timeout, it.duration.toLong().coerceIn(1000L, 180_000L) + 5000L)
                    }
                } catch (_: Exception) { failed(playback, fallback) }
            }
            player.setOnCompletionListener { if (playback.player === it) finish(playback) }
            player.setOnErrorListener { failedPlayer, _, _ ->
                if (playback.player === failedPlayer) failed(playback, fallback)
                true
            }
            val timeout = Runnable { if (playback.player === player) failed(playback, fallback) }
            playback.timeout = timeout
            handler.postDelayed(timeout, 15_000L)
            player.prepareAsync()
        } catch (_: Exception) { failed(playback, fallback) }
    }

    private fun failed(playback: Playback, fallback: Boolean) {
        if (playback.cancelled || released) return
        disposeMedia(playback)
        if (!fallback) load(playback, fallback = true) else finish(playback)
    }

    private fun disposeMedia(playback: Playback) {
        playback.timeout?.let { handler.removeCallbacks(it) }
        playback.timeout = null
        playback.player?.let { player ->
            player.setOnPreparedListener(null)
            player.setOnCompletionListener(null)
            player.setOnErrorListener(null)
            try { player.release() } catch (_: Exception) {}
        }
        playback.player = null
        playback.file?.delete()
        playback.file = null
    }

    private fun cancel(playback: Playback) {
        playback.cancelled = true
        playback.job?.cancel()
        disposeMedia(playback)
        active -= playback
        if (music === playback) music = null
        if (narration === playback) narration = null
    }

    private fun finish(playback: Playback) {
        if (playback.cancelled) return
        val completion = playback.onFinished
        cancel(playback)
        completion?.invoke()
    }
}
