package com.worstcase.outpost.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
internal fun PreparationBackButton(label: String, onBack: () -> Unit) {
    BackButton(
        onClick = onBack,
        label = label,
        modifier = Modifier.heightIn(min = 48.dp)
    )
}

/** Back stays visible while the preparation content scrolls independently. */
@Composable
internal fun PreparationLayout(
    backLabel: String,
    onBack: () -> Unit,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        // top = 4.dp para coincidir con GlobalSettingsButton en App.kt
        Modifier.fillMaxSize().padding(start = 4.dp, end = 20.dp, top = 4.dp, bottom = 24.dp)
    ) {
        PreparationBackButton(backLabel, onBack)
        Column(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = horizontalAlignment,
            content = content
        )
    }
}
