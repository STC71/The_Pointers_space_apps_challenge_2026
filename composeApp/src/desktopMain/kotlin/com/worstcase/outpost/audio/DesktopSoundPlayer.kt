package com.worstcase.outpost.audio

import com.worstcase.outpost.resources.Res
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.ExperimentalResourceApi
import java.io.ByteArrayInputStream
import java.io.File
import java.util.concurrent.TimeUnit
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Line
import javax.swing.SwingUtilities
import kotlin.concurrent.thread

/** Each playback owns its worker, subprocesses, Java Sound lines and temporary files. */
@OptIn(ExperimentalResourceApi::class)
class DesktopSoundPlayer : SoundPlayer {
    private val lock = Any()
    private val effects = linkedSetOf<Playback>()
    private var music: Playback? = null
    private var narration: Playback? = null
    private var released = false

    private class Playback {
        private val lock = Any()
        @Volatile var cancelled = false
            private set
        var worker: Thread? = null
        private val processes = mutableSetOf<Process>()
        private val lines = mutableSetOf<Line>()
        val files = mutableListOf<File>() // Only the worker creates / deletes files.

        fun process(command: List<String>): Process? = synchronized(lock) {
            if (cancelled) null else ProcessBuilder(command)
                .redirectError(ProcessBuilder.Redirect.DISCARD)
                .redirectOutput(ProcessBuilder.Redirect.DISCARD)
                .start().also { processes += it }
        }

        fun forget(process: Process) = synchronized(lock) { processes -= process }

        fun openLine(line: Line, open: () -> Unit): Boolean = synchronized(lock) {
            if (cancelled) false else {
                lines += line
                open()
                true
            }
        }

        fun closeLine(line: Line) = synchronized(lock) {
            lines -= line
            try { line.close() } catch (_: Exception) {}
        }

        fun cancel() = synchronized(lock) {
            cancelled = true
            worker?.interrupt()
            processes.forEach { try { it.destroyForcibly() } catch (_: Exception) {} }
            lines.forEach { try { it.close() } catch (_: Exception) {} }
            processes.clear()
            lines.clear()
        }

        fun cleanup() {
            // Preserve cancelled until the owner decides whether to deliver completion.
            synchronized(lock) {
                processes.forEach { try { it.destroyForcibly() } catch (_: Exception) {} }
                lines.forEach { try { it.close() } catch (_: Exception) {} }
                processes.clear()
                lines.clear()
            }
            files.forEach { it.delete() }
            files.clear()
        }
    }

    override fun playSound(effect: SoundEffect) {
        if (effect == SoundEffect.MUSIC_THEME) { startMusicLoop(); return }
        synchronized(lock) {
            if (released) return
            if (effects.size >= 8) effects.first().let { effects -= it; it.cancel() }
            val playback = Playback()
            effects += playback
            launch(playback, effect, loop = false)
        }
    }

    override fun playNarration(effect: SoundEffect, onFinished: () -> Unit) = synchronized(lock) {
        narration?.cancel()
        if (!released) {
            val playback = Playback()
            narration = playback
            launch(playback, effect, loop = false, onFinished = onFinished)
        }
    }

    override fun startMusicLoop() = synchronized(lock) {
        if (!released && music == null) {
            val playback = Playback()
            music = playback
            launch(playback, SoundEffect.MUSIC_THEME, loop = true)
        }
    }

    override fun stopMusicLoop() = synchronized(lock) { music?.cancel(); music = null }
    override fun stopNarration() = synchronized(lock) { narration?.cancel(); narration = null }
    override fun stopAll() = synchronized(lock) {
        stopMusicLoop()
        stopNarration()
        effects.forEach { it.cancel() }
        effects.clear()
    }
    override fun release() = synchronized(lock) { released = true; stopAll() }

    private fun launch(playback: Playback, effect: SoundEffect, loop: Boolean, onFinished: (() -> Unit)? = null) {
        playback.worker = thread(start = false, isDaemon = true, name = "wco-${effect.id}") {
            try {
                val file = resolveFile(playback, effect)
                val fallback = effect.fallbackWav()
                do {
                    if (playback.cancelled) break
                    var played = file != null && playExternal(playback, file)
                    if (!played && !playback.cancelled && file?.extension.equals("wav", ignoreCase = true)) {
                        played = playWav(playback, file!!.readBytes())
                    }
                    if (!played && !playback.cancelled) played = playWav(playback, fallback)
                    // Do not spin indefinitely when the machine has no audio output.
                    if (!played) break
                } while (loop && !playback.cancelled)
            } catch (_: Exception) {
                // Cancellation interrupts waits; it must never trigger another playback.
            } finally {
                playback.cleanup()
                synchronized(lock) {
                    effects -= playback
                    if (music === playback) music = null
                    if (narration === playback) narration = null
                }
                if (onFinished != null && !playback.cancelled) {
                    SwingUtilities.invokeLater { if (!playback.cancelled) onFinished() }
                }
            }
        }
        playback.worker!!.start()
    }

    private fun resolveFile(playback: Playback, effect: SoundEffect): File? {
        val name = effect.fileName ?: return null
        if (playback.cancelled) return null
        return try {
            val bytes = runBlocking { Res.readBytes("files/$name") }
            if (playback.cancelled || bytes.isEmpty()) return null
            File.createTempFile("wco_", "_$name").also { file ->
                playback.files += file
                file.writeBytes(bytes)
            }
        } catch (_: Exception) { null }
    }

    private fun playExternal(playback: Playback, file: File): Boolean {
        val os = System.getProperty("os.name", "").lowercase()
        val commands = buildList {
            if (os.contains("mac")) add(listOf("/usr/bin/afplay", file.absolutePath))
            add(listOf("ffplay", "-nodisp", "-autoexit", "-loglevel", "quiet", file.absolutePath))
            if (os.contains("linux")) add(listOf("paplay", file.absolutePath))
        }
        for (command in commands) {
            if (playback.cancelled) return false
            var process: Process? = null
            try {
                val running = playback.process(command) ?: return false
                process = running
                if (running.waitFor(180, TimeUnit.SECONDS) && running.exitValue() == 0) return true
            } catch (_: InterruptedException) {
                Thread.currentThread().interrupt()
                return false
            } catch (_: Exception) {
                // Missing player or failed decoding: try the next backend.
            } finally {
                process?.let {
                    // Await termination on this worker before deleting an open media file
                    // (especially on Windows). Preserve cancellation after the bounded wait.
                    val interrupted = Thread.interrupted()
                    try {
                        if (it.isAlive) {
                            it.destroyForcibly()
                            it.waitFor(2, TimeUnit.SECONDS)
                        }
                    } catch (_: InterruptedException) {
                        Thread.currentThread().interrupt()
                    } finally {
                        try { it.outputStream.close() } catch (_: Exception) {}
                        playback.forget(it)
                        if (interrupted) Thread.currentThread().interrupt()
                    }
                }
            }
        }
        return false
    }

    private fun playWav(playback: Playback, bytes: ByteArray): Boolean {
        if (playback.cancelled) return false
        val clip = try { AudioSystem.getClip() } catch (_: Exception) { return false }
        return try {
            AudioSystem.getAudioInputStream(ByteArrayInputStream(bytes)).use { stream ->
                if (!playback.openLine(clip) { clip.open(stream) }) return false
            }
            if (playback.cancelled) return false
            clip.start()
            val deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(180)
            while (!playback.cancelled && clip.microsecondPosition < clip.microsecondLength) {
                if (System.nanoTime() >= deadline) return false
                Thread.sleep(20)
            }
            !playback.cancelled
        } catch (_: InterruptedException) {
            Thread.currentThread().interrupt()
            false
        } catch (_: Exception) { false }
        finally { playback.closeLine(clip) }
    }
}
