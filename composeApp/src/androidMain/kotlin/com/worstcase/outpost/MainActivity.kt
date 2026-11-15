package com.worstcase.outpost

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.worstcase.outpost.audio.AndroidSoundPlayer
import com.worstcase.outpost.audio.AudioManager

class MainActivity : ComponentActivity() {
    private lateinit var soundPlayer: AndroidSoundPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        AndroidUrlOpener.initialize(applicationContext)
        soundPlayer = AndroidSoundPlayer(applicationContext)
        AudioManager.initialize(soundPlayer)
        setContent { App() }
    }

    override fun onStart() {
        super.onStart()
        AudioManager.resume(soundPlayer)
    }

    override fun onStop() {
        AudioManager.pause(soundPlayer)
        super.onStop()
    }

    override fun onDestroy() {
        AudioManager.release(soundPlayer)
        // Also release an obsolete instance if a replacement Activity initialized audio first.
        soundPlayer.release()
        super.onDestroy()
    }
}
