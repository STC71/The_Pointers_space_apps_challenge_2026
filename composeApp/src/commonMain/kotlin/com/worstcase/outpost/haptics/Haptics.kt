package com.worstcase.outpost.haptics

enum class HapticType { LIGHT, SUCCESS, WARNING, ERROR }

interface HapticsPlayer {
    fun vibrate(type: HapticType)
}

object Haptics {
    var enabled: Boolean = true
    private var player: HapticsPlayer? = null

    fun initialize(player: HapticsPlayer) {
        this.player = player
    }

    fun release(player: HapticsPlayer) {
        if (this.player === player) this.player = null
    }

    fun play(type: HapticType) {
        if (enabled) player?.vibrate(type)
    }
}
