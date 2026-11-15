package com.worstcase.outpost.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.worstcase.outpost.theme.NasaAccent
import com.worstcase.outpost.theme.TextPrimary
import com.worstcase.outpost.theme.TextSecondary

/** Presentation state only; the surrounding screen owns scrolling and mission actions. */
@Composable
internal fun TrainingNotes(title: String, note: String, paragraphs: List<String>) {
    var expanded by remember(title) { mutableStateOf(false) }
    OutlinedButton(onClick = { expanded = !expanded }, modifier = Modifier.fillMaxWidth()) {
        Text(title, color = NasaAccent, fontWeight = FontWeight.SemiBold)
    }
    if (expanded) {
        Text(note, color = TextSecondary, fontSize = 12.sp, lineHeight = 17.sp)
        paragraphs.forEach { paragraph ->
            Spacer(Modifier.height(10.dp))
            Text(paragraph, color = TextPrimary, fontSize = 13.sp, lineHeight = 19.sp)
        }
    }
}
