package com.worstcase.outpost.ui

/**
 * Analog selection cards with real site photographs from composeResources/drawable.
 */

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.worstcase.outpost.data.MockData
import com.worstcase.outpost.domain.Analog
import com.worstcase.outpost.domain.PlanetaryTarget
import com.worstcase.outpost.i18n.AppLanguage
import com.worstcase.outpost.i18n.Strings
import com.worstcase.outpost.resources.Res
import com.worstcase.outpost.resources.mars
import com.worstcase.outpost.resources.moon
import com.worstcase.outpost.theme.NasaAccent
import com.worstcase.outpost.theme.NasaBlue
import com.worstcase.outpost.theme.TextPrimary
import com.worstcase.outpost.theme.TextSecondary
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun AnalogSelectionScreen(
    currentLanguage: AppLanguage,
    selectedDestination: PlanetaryTarget? = null,
    onAnalogSelected: (Analog) -> Unit,
    onBack: () -> Unit
) {
    val texts = Strings.get(currentLanguage)
    val analogs = MockData.getAnalogs(currentLanguage).let { list ->
        if (selectedDestination != null) list.filter { it.planetaryTarget == selectedDestination }
        else list
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(start = 20.dp, end = 20.dp, top = 48.dp, bottom = 16.dp)
    ) {
        PreparationBackButton(texts.backButton, onBack)
        Text(
            text = texts.selectAnalog,
            color = TextPrimary,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 16.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.weight(1f).fillMaxWidth(),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(analogs, key = { it.id }) { analog ->
                AnalogVisualCard(
                    analog = analog,
                    texts = texts,
                    onClick = { onAnalogSelected(analog) }
                )
            }
        }
    }
}

@Composable
private fun AnalogVisualCard(
    analog: Analog,
    texts: com.worstcase.outpost.i18n.StringSet,
    onClick: () -> Unit
) {
    val photo = analogDrawable(analog.id)
    val isMars = analog.planetaryTarget == PlanetaryTarget.MARS

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(NasaBlue.copy(alpha = 0.58f))
            .border(
                width = 1.dp,
                color = NasaAccent.copy(alpha = 0.35f),
                shape = RoundedCornerShape(18.dp)
            )
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .background(analogHeaderBrush(analog))
        ) {
            if (photo != null) {
                Image(
                    painter = painterResource(photo),
                    contentDescription = analog.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                // Dark gradient for text readability
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, Color.Black.copy(alpha = 0.55f))
                            )
                        )
                )
            } else {
                Image(
                    painter = painterResource(if (isMars) Res.drawable.mars else Res.drawable.moon),
                    contentDescription = null,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .align(Alignment.Center)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp)
            ) {
                Image(
                    painter = painterResource(if (isMars) Res.drawable.mars else Res.drawable.moon),
                    contentDescription = null,
                    modifier = Modifier
                        .size(18.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isMars) texts.destinationMars else texts.destinationMoon,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = analog.name,
                color = TextPrimary,
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = analog.location,
                color = TextSecondary,
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 2.dp)
            )
            if (analog.targetSite.isNotBlank()) {
                Text(
                    text = analog.targetSite,
                    color = NasaAccent.copy(alpha = 0.9f),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = analog.description,
                color = TextSecondary,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
            if (analog.environmentalModifier.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = analog.environmentalModifier,
                    color = TextPrimary.copy(alpha = 0.85f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

private fun analogDrawable(id: String): DrawableResource? = photoForAnalogId(id)

private fun analogHeaderBrush(analog: Analog): Brush {
    return when (analog.planetaryTarget) {
        PlanetaryTarget.MARS -> Brush.verticalGradient(
            listOf(Color(0xFF8B3A1A), Color(0xFF2A1510), Color(0xFF1A2332))
        )
        PlanetaryTarget.MOON -> Brush.verticalGradient(
            listOf(Color(0xFF6B7280), Color(0xFF374151), Color(0xFF111827))
        )
    }
}
