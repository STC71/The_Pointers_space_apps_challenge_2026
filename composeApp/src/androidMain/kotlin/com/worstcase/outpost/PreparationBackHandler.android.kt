package com.worstcase.outpost

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable

@Composable
actual fun PreparationBackHandler(enabled: Boolean, onBack: () -> Unit) {
    BackHandler(enabled = enabled, onBack = onBack)
}
