package com.worstcase.outpost.ui

/**
 * Celebration screen after a successful mission, before the formal debrief.
 * Lightweight confetti animation (no external library).
 */

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.worstcase.outpost.i18n.AppLanguage
import com.worstcase.outpost.i18n.Strings
import com.worstcase.outpost.i18n.ConsequenceCopy
import com.worstcase.outpost.theme.NasaAccent
import com.worstcase.outpost.theme.NasaBlue
import com.worstcase.outpost.theme.SuccessGreen
import kotlin.random.Random

private data class ConfettiPiece(
    val xFrac: Float,
    val speed: Float,
    val size: Float,
    val color: Color,
    val spin: Float
)

@Composable
fun WelcomeHomeScreen(
    currentLanguage: AppLanguage,
    onContinue: () -> Unit,
    onShowLastDecisionReport: (() -> Unit)? = null
) {
    val texts = Strings.get(currentLanguage)
    val pieces = remember {
        val colors = listOf(
            Color(0xFFFFD60A),
            Color(0xFFFF6B35),
            Color(0xFF00A8E8),
            Color(0xFF2A9D8F),
            Color(0xFFE63946),
            Color(0xFFFFFFFF)
        )
        List(42) {
            ConfettiPiece(
                xFrac = Random.nextFloat(),
                speed = 0.35f + Random.nextFloat() * 0.9f,
                size = 6f + Random.nextFloat() * 10f,
                color = colors.random(),
                spin = Random.nextFloat() * 360f
            )
        }
    }

    val transition = rememberInfiniteTransition(label = "confetti")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "confettiPhase"
    )

    RandomAnalogBackground(
        photo = welcomeHomeBackground(),
        dimAlphaTop = 0.22f,
        dimAlphaMid = 0.35f,
        dimAlphaBottom = 0.48f
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                pieces.forEachIndexed { index, piece ->
                    val fall = ((phase * piece.speed) + index * 0.02f) % 1.2f
                    val y = fall * (h + 40f) - 20f
                    val x = piece.xFrac * w + kotlin.math.sin((phase + index) * 6.28f) * 18f
                    rotate(degrees = piece.spin + phase * 180f, pivot = Offset(x, y)) {
                        drawRect(
                            color = piece.color,
                            topLeft = Offset(x, y),
                            size = Size(piece.size, piece.size * 0.45f)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 24.dp, top = 48.dp, bottom = 24.dp)
                    .background(NasaBlue.copy(alpha = 0.48f), RoundedCornerShape(20.dp))
                    .padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    Modifier.weight(1f, fill = false).fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = texts.welcomeHomeTitle,
                        color = SuccessGreen,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = texts.welcomeHomeBody,
                        color = Color.White.copy(alpha = 0.92f),
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(Modifier.height(22.dp))
                onShowLastDecisionReport?.let { showReport ->
                    TextButton(onClick = showReport, modifier = Modifier.fillMaxWidth()) {
                        Text(ConsequenceCopy.REOPEN.text(currentLanguage))
                    }
                }
                Button(
                    onClick = onContinue,
                    modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NasaAccent)
                ) {
                    Text(texts.welcomeHomeContinue, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
