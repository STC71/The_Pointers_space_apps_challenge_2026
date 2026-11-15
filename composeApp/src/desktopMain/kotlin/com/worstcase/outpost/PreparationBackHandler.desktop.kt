package com.worstcase.outpost

import androidx.compose.runtime.Composable

@Composable
actual fun PreparationBackHandler(enabled: Boolean, onBack: () -> Unit) {
    // Desktop has no Android system back dispatcher; the visible button is shared.
}
