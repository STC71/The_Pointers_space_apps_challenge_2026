package com.worstcase.outpost

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.worstcase.outpost.audio.AudioManager
import com.worstcase.outpost.audio.DesktopSoundPlayer

fun main() {
    // Construct once, outside composition; resizing cannot replace the active player.
    val soundPlayer = DesktopSoundPlayer()
    AudioManager.initialize(soundPlayer)
    try {
        application {
            Window(
                onCloseRequest = {
                    AudioManager.release(soundPlayer)
                    exitApplication()
                },
                title = "Worst Case Outpost",
                state = rememberWindowState(width = 420.dp, height = 800.dp)
            ) { App() }
        }
    } finally {
        AudioManager.release(soundPlayer)
    }
}
