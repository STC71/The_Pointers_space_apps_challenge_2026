package com.worstcase.outpost.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.worstcase.outpost.theme.NasaAccent
import com.worstcase.outpost.theme.NasaBlue

/** Bounded viewport: long content scrolls, actions stay reachable, background is modal. */
@Composable
internal fun ScrollablePanelDialog(
    title: String,
    onDismiss: () -> Unit,
    titleColor: Color = NasaAccent,
    dismissOnClickOutside: Boolean = true,
    actions: @Composable ColumnScope.() -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnClickOutside = dismissOnClickOutside)
    ) {
        Box(
            modifier = Modifier.fillMaxSize().padding(vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
                    .background(NasaBlue).padding(20.dp)
            ) {
                Column(
                    Modifier.weight(1f, fill = false).fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(title, color = titleColor, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(Modifier.height(12.dp))
                    content()
                }
                Spacer(Modifier.height(16.dp))
                Column(Modifier.fillMaxWidth(), content = actions)
            }
        }
    }
}
