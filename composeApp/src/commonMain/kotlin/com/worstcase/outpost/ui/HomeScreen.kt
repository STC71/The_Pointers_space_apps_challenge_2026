package com.worstcase.outpost.ui

/**
 * Home screen:
 * - Body (logo, title, flags, name) centered
 * - Difficulty + NASA footer pinned to the bottom
 */

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.worstcase.outpost.domain.Difficulty
import com.worstcase.outpost.i18n.AppLanguage
import com.worstcase.outpost.i18n.Strings
import com.worstcase.outpost.theme.AlertOrange
import com.worstcase.outpost.theme.NasaAccent
import com.worstcase.outpost.theme.NasaBlue
import com.worstcase.outpost.theme.TextSecondary
import coil3.Image
import com.worstcase.outpost.apod.ApodFrame

@Composable
fun HomeScreen(
    currentLanguage: AppLanguage,
    onLanguageSelected: (AppLanguage) -> Unit,
    onDifficultySelected: (Difficulty) -> Unit,
    playerName: String = "",
    onPlayerNameChange: (String) -> Unit = {},
    apodFrame: ApodFrame<Image>? = null
) {
    val texts = Strings.get(currentLanguage)

    ApodBackground(apodFrame) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 24.dp, end = 24.dp, top = 48.dp, bottom = 20.dp)
        ) {
            // --- Centered body ---
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = texts.appTitle,
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = texts.tagline,
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp, bottom = 20.dp)
                )

                Text(
                    text = texts.playerNameLabel,
                    color = Color.White.copy(alpha = 0.75f),
                    fontSize = 14.sp,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = playerName,
                    onValueChange = onPlayerNameChange,
                    singleLine = true,
                    placeholder = {
                        Text(texts.playerNameHint, color = TextSecondary.copy(alpha = 0.6f))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NasaAccent,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.35f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = NasaAccent,
                        focusedContainerColor = NasaBlue.copy(alpha = 0.40f),
                        unfocusedContainerColor = NasaBlue.copy(alpha = 0.40f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            // --- Bottom: difficulty + footer ---
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = texts.selectDifficulty,
                    color = Color.White.copy(alpha = 0.75f),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    DifficultyButton(texts.junior, { onDifficultySelected(Difficulty.JUNIOR) }, Modifier.weight(1f))
                    DifficultyButton(texts.mid, { onDifficultySelected(Difficulty.MID) }, Modifier.weight(1f))
                    DifficultyButton(texts.senior, { onDifficultySelected(Difficulty.SENIOR) }, Modifier.weight(1f))
                }
                ApodCredit(apodFrame, currentLanguage)
                Text(
                    text = "NASA Space Apps Challenge 2026",
                    color = Color.White.copy(alpha = 0.55f),
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun DifficultyButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = AlertOrange,
            contentColor = MaterialTheme.colorScheme.onSecondary
        )
    ) {
        Text(text = text, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
    }
}
