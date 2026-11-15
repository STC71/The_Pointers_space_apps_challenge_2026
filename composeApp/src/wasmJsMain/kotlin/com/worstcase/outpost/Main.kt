package com.worstcase.outpost

/**
 * ============================================================
 *  MAIN.KT (WebAssembly / Wasm/JS)
 *  Entry point when running the mission simulation in a web browser.
 *  Enables judges and users worldwide to play via web without installation.
 * ============================================================
 */

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.worstcase.outpost.audio.AudioManager
import com.worstcase.outpost.audio.BrowserSoundPlayer
import kotlinx.browser.document

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val soundPlayer = BrowserSoundPlayer()
    AudioManager.initialize(soundPlayer)
    soundPlayer.attachLifecycle()
    ComposeViewport(document.body!!) {
        App()
    }
}
