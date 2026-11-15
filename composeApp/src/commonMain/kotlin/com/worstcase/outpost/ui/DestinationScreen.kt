package com.worstcase.outpost.ui

/**
 * Choose mission destination: Moon (7 tests) or Mars (12 tests).
 */

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.worstcase.outpost.domain.PlanetaryTarget
import com.worstcase.outpost.resources.Res
import com.worstcase.outpost.resources.moon
import com.worstcase.outpost.resources.mars
import androidx.compose.foundation.Image
import org.jetbrains.compose.resources.painterResource
import com.worstcase.outpost.i18n.AppLanguage
import com.worstcase.outpost.i18n.Strings
import com.worstcase.outpost.theme.CriticalRed
import com.worstcase.outpost.theme.NasaAccent
import com.worstcase.outpost.theme.NasaBlue
import com.worstcase.outpost.theme.SuccessGreen
import com.worstcase.outpost.theme.TextSecondary

@Composable
fun DestinationScreen(
    currentLanguage: AppLanguage,
    onDestinationSelected: (PlanetaryTarget) -> Unit,
    onBack: () -> Unit
) {
    val texts = Strings.get(currentLanguage)

    RandomAnalogBackground {
        PreparationLayout(
            backLabel = texts.backButton,
            onBack = onBack,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = texts.destinationTitle,
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                text = texts.destinationSubtitle,
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp, bottom = 20.dp)
            )

            DestinationCard(
                icon = {
                    Image(
                        painter = painterResource(Res.drawable.moon),
                        contentDescription = texts.destinationMoon,
                        modifier = Modifier.size(48.dp)
                    )
                },
                title = texts.destinationMoon,
                body = texts.destinationMoonInfo,
                pros = texts.destinationMoonPros,
                cons = texts.destinationMoonCons,
                testsLabel = texts.destinationTestsLabel.replace("{n}", "7"),
                onClick = { onDestinationSelected(PlanetaryTarget.MOON) }
            )
            Spacer(modifier = Modifier.height(14.dp))
            DestinationCard(
                icon = {
                    Image(
                        painter = painterResource(Res.drawable.mars),
                        contentDescription = texts.destinationMars,
                        modifier = Modifier.size(48.dp)
                    )
                },
                title = texts.destinationMars,
                body = texts.destinationMarsInfo,
                pros = texts.destinationMarsPros,
                cons = texts.destinationMarsCons,
                testsLabel = texts.destinationTestsLabel.replace("{n}", "12"),
                onClick = { onDestinationSelected(PlanetaryTarget.MARS) }
            )
        }
    }
}

@Composable
private fun DestinationCard(
    icon: @Composable () -> Unit, // Image resource
    title: String,
    body: String,
    pros: String,
    cons: String,
    testsLabel: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(NasaBlue.copy(alpha = 0.55f))
            .border(1.dp, NasaAccent.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        icon()
        Spacer(modifier = Modifier.height(8.dp))
        Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(6.dp))
        Text(body, color = TextSecondary, fontSize = 13.sp, lineHeight = 18.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Text(pros, color = SuccessGreen, fontSize = 12.sp, lineHeight = 16.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(cons, color = CriticalRed.copy(alpha = 0.9f), fontSize = 12.sp, lineHeight = 16.sp)
        Spacer(modifier = Modifier.height(10.dp))
        Text(testsLabel, color = NasaAccent, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
    }
}
