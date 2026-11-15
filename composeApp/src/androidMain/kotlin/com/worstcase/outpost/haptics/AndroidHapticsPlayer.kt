package com.worstcase.outpost.haptics

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

class AndroidHapticsPlayer(
    context: Context,
) : HapticsPlayer {
    private val vibrator: Vibrator =
        if (Build.VERSION.SDK_INT >= 31) {
            (context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

    override fun vibrate(type: HapticType) {
        if (!vibrator.hasVibrator()) return

        // pausa, vibra, pausa, vibra... (milisegundos)
        val pattern =
            when (type) {
                HapticType.LIGHT -> longArrayOf(0, 30)
                HapticType.SUCCESS -> longArrayOf(0, 40, 60, 40)
                HapticType.WARNING -> longArrayOf(0, 120, 80, 120)
                HapticType.ERROR -> longArrayOf(0, 400)
            }
        if (Build.VERSION.SDK_INT >= 26) {
            vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(pattern, -1)
        }
    }
}
