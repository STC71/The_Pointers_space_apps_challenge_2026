package com.worstcase.outpost

import java.awt.Desktop
import java.net.URI
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

actual fun openUrl(url: String) {
    val uri = try { URI(url.trim()) } catch (_: Exception) { return }
    if (uri.scheme?.lowercase() !in setOf("https", "http") || uri.host.isNullOrBlank()) return
    thread(isDaemon = true, name = "wco-open-link") {
        try {
            if (Desktop.isDesktopSupported()) {
                val desktop = Desktop.getDesktop()
                if (desktop.isSupported(Desktop.Action.BROWSE)) {
                    desktop.browse(uri)
                    return@thread
                }
            }
        } catch (_: Exception) {
            // Fall through to the platform launcher when AWT browsing is unavailable.
        }
        val os = System.getProperty("os.name", "").lowercase()
        val commands = when {
            os.contains("mac") -> listOf(listOf("/usr/bin/open", uri.toASCIIString()))
            os.contains("win") -> listOf(listOf("rundll32", "url.dll,FileProtocolHandler", uri.toASCIIString()))
            else -> listOf(listOf("xdg-open", uri.toASCIIString()), listOf("gio", "open", uri.toASCIIString()))
        }
        for (command in commands) {
            var process: Process? = null
            try {
                val running = ProcessBuilder(command)
                    .redirectError(ProcessBuilder.Redirect.DISCARD)
                    .redirectOutput(ProcessBuilder.Redirect.DISCARD).start()
                process = running
                if (running.waitFor(10, TimeUnit.SECONDS) && running.exitValue() == 0) return@thread
            } catch (_: Exception) {
            } finally {
                process?.let { if (it.isAlive) it.destroyForcibly() }
            }
        }
    }
}
