package com.worstcase.outpost.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.Image as CoilImage
import coil3.compose.LocalPlatformContext
import coil3.compose.asPainter
import com.worstcase.outpost.apod.ApodFrame
import com.worstcase.outpost.i18n.ApodCopy
import com.worstcase.outpost.i18n.AppLanguage
import com.worstcase.outpost.i18n.Strings
import com.worstcase.outpost.openUrl
import com.worstcase.outpost.resources.Res
import com.worstcase.outpost.resources.bg_art_001
import org.jetbrains.compose.resources.painterResource

/** Explicit opening/menu background. Analog selection and mission backgrounds stay independent. */
@Composable
internal fun ApodBackground(frame: ApodFrame<CoilImage>?, content: @Composable BoxScope.() -> Unit) {
    val context = LocalPlatformContext.current
    Box(Modifier.fillMaxSize()) {
        if (frame == null) {
            Image(painterResource(Res.drawable.bg_art_001), null,
                modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        } else {
            val painter = remember(frame.image, context) { frame.image.asPainter(context) }
            Image(painter, null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        }
        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(
            Color.Black.copy(alpha = .40f), Color.Black.copy(alpha = .58f), Color.Black.copy(alpha = .72f)
        ))))
        content()
    }
}

@Composable
internal fun ApodCredit(frame: ApodFrame<CoilImage>?, language: AppLanguage) {
    if (frame == null) return
    val entry = frame.entry
    var showDetails by remember(entry) { mutableStateOf(false) }
    TextButton(onClick = { showDetails = true }, modifier = Modifier.fillMaxWidth().semantics {
        contentDescription = "${ApodCopy.DETAILS.text(language)}: ${entry.title}, ${entry.date}"
    }) {
        // Always use the publication date, including when the picture comes from the offline cache.
        Text("NASA APOD · ${entry.date} · ⓘ", color = Color.White.copy(alpha = .85f),
            fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
    if (showDetails) ScrollablePanelDialog(
        title = ApodCopy.DETAILS.text(language),
        onDismiss = { showDetails = false },
        actions = {
            TextButton(onClick = { openUrl(entry.publicationUrl) }) {
                Text(ApodCopy.SOURCE.text(language))
            }
            TextButton(onClick = { showDetails = false }) { Text(Strings.get(language).closeSettings) }
        }
    ) {
        Text(entry.title, color = Color.White)
        Text("NASA APOD · ${entry.date}", color = Color.White.copy(alpha = .8f))
        if (entry.credit.isNotBlank()) {
            Spacer(Modifier.height(12.dp))
            Text(ApodCopy.CREDITS.text(language), color = Color.White)
            Text(entry.credit, color = Color.White.copy(alpha = .85f))
        }
        if (entry.copyright.isNotBlank() && entry.copyright != entry.credit) {
            Spacer(Modifier.height(12.dp))
            Text(ApodCopy.COPYRIGHT.text(language), color = Color.White)
            Text(entry.copyright, color = Color.White.copy(alpha = .85f))
        }
    }
}
