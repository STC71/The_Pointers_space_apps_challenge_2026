package com.worstcase.outpost.ui

/**
 * Opening presentation:
 * - Shared daily APOD background with offline fallback
 * - Main content centered
 * - Skip / Next fixed at the bottom
 */

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.worstcase.outpost.audio.AudioManager
import com.worstcase.outpost.i18n.AppLanguage
import com.worstcase.outpost.i18n.Strings
import com.worstcase.outpost.theme.NasaAccent
import com.worstcase.outpost.theme.NasaBlue
import com.worstcase.outpost.theme.TextSecondary
import coil3.Image as ApodImage
import androidx.compose.foundation.Image
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.ui.layout.ContentScale
import com.worstcase.outpost.resources.Res
import com.worstcase.outpost.resources.earth
import com.worstcase.outpost.resources.mars
import org.jetbrains.compose.resources.painterResource
import com.worstcase.outpost.apod.ApodFrame

@Composable
fun IntroScreen(
    currentLanguage: AppLanguage,
    onLanguageSelected: (AppLanguage) -> Unit,
    onFinished: () -> Unit,
    apodFrame: ApodFrame<ApodImage>? = null
) {
    val texts = Strings.get(currentLanguage)
    var page by remember { mutableStateOf(0) }

    // NASA countdown first; theme music starts when the clip ends (player callback)
    // or when the user leaves the intro (finishIntro).
    LaunchedEffect(Unit) {
        AudioManager.play(com.worstcase.outpost.audio.SoundEffect.NASA_COUNTDOWN)
    }

    val titles = listOf(texts.introPage1Title, texts.introPage2Title, texts.introPage3Title)
    val bodies = listOf(texts.introPage1Body, texts.introPage2Body, texts.introPage3Body)

    ApodBackground(apodFrame) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            // --- Main body: takes remaining space and centers content ---
            key(page) { Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                when (page) {
                    0 -> Image(
                        painter = painterResource(Res.drawable.earth),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(96.dp)
                    )
                    1 -> Image(
                        painter = painterResource(Res.drawable.mars),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(96.dp)
                    )
                    else -> Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = NasaAccent,
                        modifier = Modifier.size(96.dp)
                    )
                }
                if (page == 0) {
                    Text(
                        text = texts.introWelcome,
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(0.95f)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Worst Case Outpost",
                    color = NasaAccent,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = titles[page],
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(0.95f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.95f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(NasaBlue.copy(alpha = 0.55f))
                        .padding(20.dp)
                ) {
                    Text(
                        text = bodies[page],
                        color = TextSecondary,
                        fontSize = 15.sp,
                        lineHeight = 22.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    repeat(3) { index ->
                        Box(
                            modifier = Modifier
                                .size(if (index == page) 10.dp else 8.dp)
                                .clip(CircleShape)
                                .background(
                                    if (index == page) NasaAccent
                                    else Color.White.copy(alpha = 0.35f)
                                )
                        )
                    }
                }
            } }

            // --- Footer: Skip / Next always at the bottom ---
            ApodCredit(apodFrame, currentLanguage)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onFinished) {
                    Text(text = texts.introSkip, color = Color.White.copy(alpha = 0.85f))
                }

                Button(
                    onClick = { if (page < 2) page++ else onFinished() },
                    colors = ButtonDefaults.buttonColors(containerColor = NasaAccent),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (page < 2) texts.introNext else texts.introStart,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
