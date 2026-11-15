package com.worstcase.outpost

import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.worstcase.outpost.audio.AudioManager
import com.worstcase.outpost.audio.DesktopSoundPlayer
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.decodeToImageBitmap

@OptIn(ExperimentalResourceApi::class)
fun main() {
    // Construct once, outside composition; resizing cannot replace the active player.
    val soundPlayer = DesktopSoundPlayer()
    AudioManager.initialize(soundPlayer)
    try {
        application {
            val icon = remember {
                val bytes = checkNotNull(
                    Thread.currentThread().contextClassLoader.getResourceAsStream("icon-nobg.png")
                ) { "Missing desktop icon: icon-nobg.png" }.use { it.readBytes() }
                BitmapPainter(bytes.decodeToImageBitmap())
            }
            Window(
                onCloseRequest = {
                    AudioManager.release(soundPlayer)
                    exitApplication()
                },
                title = "Worst Case Outpost",
                icon = icon,
                state = rememberWindowState(width = 420.dp, height = 800.dp)
            ) { App() }
        }
    } finally {
        AudioManager.release(soundPlayer)
    }
}
