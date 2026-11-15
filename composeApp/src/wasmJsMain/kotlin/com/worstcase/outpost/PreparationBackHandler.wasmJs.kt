package com.worstcase.outpost

import androidx.compose.runtime.Composable

@Composable
actual fun PreparationBackHandler(enabled: Boolean, onBack: () -> Unit) {
    // Preparation navigation is local to the app, independent of browser history.
}
