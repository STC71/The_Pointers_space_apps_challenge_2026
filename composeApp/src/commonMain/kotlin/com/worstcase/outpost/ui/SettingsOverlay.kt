package com.worstcase.outpost.ui

/**
 * Global settings overlay: language + mission audio.
 * Shown from any screen via App.kt.
 */

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.worstcase.outpost.i18n.AppLanguage
import com.worstcase.outpost.i18n.StringSet
import com.worstcase.outpost.theme.NasaAccent
import com.worstcase.outpost.theme.NasaDarkBlue
import com.worstcase.outpost.theme.TextPrimary
import com.worstcase.outpost.theme.TextSecondary

@Composable
fun GlobalSettingsButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TextButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = null,
            tint = NasaAccent,
            modifier = Modifier.size(18.dp)
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text = label,
            color = NasaAccent,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun SettingsOverlay(
    texts: StringSet,
    currentLanguage: AppLanguage,
    soundEnabled: Boolean,
    onLanguageSelected: (AppLanguage) -> Unit,
    onToggleSound: (Boolean) -> Unit,
    onClose: () -> Unit
) {
    RandomAnalogBackground {
        ScrollablePanelDialog(
            title = texts.settings,
            onDismiss = onClose,
            actions = {
                Button(
                    onClick = onClose,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = NasaAccent),
                    shape = RoundedCornerShape(12.dp)
                ) { Text(texts.closeSettings) }
            }
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                FlagIcon(currentLanguage)
                Spacer(Modifier.width(8.dp))
                Text(
                    text = texts.languageLabel,
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(modifier = Modifier.height(10.dp))

            // Two rows of flags (same idea as Home)
            val languages = AppLanguage.entries.toList()
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    languages.take(5).forEach { language ->
                        LanguageChip(
                            language = language,
                            selected = language == currentLanguage,
                            onClick = { onLanguageSelected(language) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    languages.drop(5).forEach { language ->
                        LanguageChip(
                            language = language,
                            selected = language == currentLanguage,
                            onClick = { onLanguageSelected(language) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = texts.soundLabel, color = TextPrimary, fontSize = 14.sp,
                    modifier = Modifier.weight(1f).padding(end = 12.dp))
                Switch(
                    checked = soundEnabled,
                    onCheckedChange = onToggleSound,
                    colors = SwitchDefaults.colors(
                        checkedTrackColor = NasaAccent,
                        checkedThumbColor = TextPrimary
                    )
                )
            }
            Text(
                text = if (soundEnabled) texts.soundOn else texts.soundOff,
                color = TextSecondary,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun LanguageChip(
    language: AppLanguage,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (selected) NasaAccent.copy(alpha = 0.25f) else NasaDarkBlue.copy(alpha = 0.5f))
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp)
    ) {
        FlagIcon(language)
        Spacer(Modifier.height(4.dp))
        Text(
            text = language.code.uppercase(),
            color = if (selected) NasaAccent else TextSecondary,
            fontSize = 10.sp
        )
    }
}
