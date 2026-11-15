package com.worstcase.outpost

import androidx.compose.runtime.Composable

/** Android system back. Other targets use the visible preparation back buttons. */
@Composable
expect fun PreparationBackHandler(enabled: Boolean, onBack: () -> Unit)
