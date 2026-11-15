package com.worstcase.outpost.ui

/**
 * Crisis dialog with vertical scroll and option buttons that wrap long text.
 */

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.window.Dialog
import com.worstcase.outpost.domain.AiFlightAssessment
import com.worstcase.outpost.data.TrainingContent
import com.worstcase.outpost.i18n.TrainingCopy
import com.worstcase.outpost.domain.Crisis
import com.worstcase.outpost.domain.CrisisOption
import com.worstcase.outpost.domain.DecisionForecast
import com.worstcase.outpost.domain.Difficulty
import com.worstcase.outpost.i18n.AppLanguage
import com.worstcase.outpost.i18n.Strings
import com.worstcase.outpost.theme.*
// SuccessGreen, NasaAccent, etc. via theme.*

@Composable
fun CrisisDialog(
    crisis: Crisis,
    occurrence: Long,
    currentLanguage: AppLanguage,
    onOptionSelected: (String, Long, String) -> Unit,
    onRequestAiCopilot: (String, Long) -> Unit = { _, _ -> },
    aiAssessment: AiFlightAssessment? = null,
    forecasts: Map<String, DecisionForecast> = emptyMap(),
    difficulty: Difficulty = Difficulty.MID,
    missionDay: Int = 1,
    missionHour: Int = 8,
    missionMinute: Int = 0,
    daysToReturn: Int = 10
) {
    val texts = Strings.get(currentLanguage)
    val scroll = rememberScrollState()
    var pendingHeritageOption by remember(crisis.id, occurrence, currentLanguage) { mutableStateOf<CrisisOption?>(null) }

    Dialog(onDismissRequest = { /* must choose an action */ }) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
                .clip(RoundedCornerShape(20.dp))
                .background(NasaBlue)
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scroll),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = texts.crisisTitle,
                    color = CriticalRed,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = texts.missionDayLabel.replace("{d}", missionDay.toString()),
                    color = NasaAccent,
                    fontSize = 12.sp
                )
                Text(
                    text = texts.missionTimeLabel
                        .replace("{h}", missionHour.toString().padStart(2, '0'))
                        .replace("{m}", missionMinute.toString().padStart(2, '0')),
                    color = TextSecondary,
                    fontSize = 11.sp
                )
                Text(
                    text = texts.daysToReturnLabel.replace("{n}", daysToReturn.coerceAtLeast(0).toString()),
                    color = TextSecondary,
                    fontSize = 11.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = crisis.title,
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = crisis.description,
                    color = TextSecondary,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp,
                    modifier = Modifier.fillMaxWidth()
                )

                if (difficulty == Difficulty.JUNIOR && !crisis.juniorHint.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = crisis.juniorHint.orEmpty(),
                        color = NasaAccent,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(NasaDarkBlue)
                            .padding(10.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Always one tap: request advice (does not require a second press)
                OutlinedButton(
                    onClick = { onRequestAiCopilot(crisis.id, occurrence) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = NasaAccent)
                ) {
                    Text(
                        text = texts.askAiCopilot,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                if (aiAssessment != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(NasaDarkBlue)
                            .padding(12.dp)
                    ) {
                        Text(
                            text = aiAssessment.title,
                            color = SuccessGreen,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = aiAssessment.analysis,
                            color = TextPrimary,
                            fontSize = 14.sp,
                            lineHeight = 20.sp
                        )
                        HoustonForecasts(crisis, occurrence, forecasts, currentLanguage)
                        key(crisis.id, occurrence) {
                            val resources = forecasts.values.firstOrNull {
                                it.crisisId == crisis.id && it.occurrence == occurrence
                            }?.resourcesBefore
                            TrainingNotes(
                                title = TrainingCopy.HOUSTON.text(currentLanguage),
                                note = TrainingCopy.HOUSTON_NOTE.text(currentLanguage),
                                paragraphs = TrainingContent.houstonContext(crisis, aiAssessment, resources, currentLanguage)
                            )
                        }
                        if (aiAssessment.worstCaseScenario.isNotBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "⚠ ${aiAssessment.worstCaseScenario}",
                                color = AlertOrange,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = texts.chooseAction,
                    color = TextSecondary,
                    fontSize = 13.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    textAlign = TextAlign.Center
                )

                crisis.options.forEach { option ->
                    CrisisOptionCard(
                        option = option,
                        heritageLabel = texts.hardwareHeritage,
                        onClick = {
                            if (option.usesAbandonedHardware) {
                                pendingHeritageOption = option
                            } else {
                                onOptionSelected(crisis.id, occurrence, option.id)
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // Heritage / recycling historical context before committing the choice
        pendingHeritageOption?.let { option ->
            HeritageConfirmDialog(
                option = option,
                texts = texts,
                onAccept = {
                    pendingHeritageOption = null
                    onOptionSelected(crisis.id, occurrence, option.id)
                },
                onBack = { pendingHeritageOption = null }
            )
        }
    }
}

@Composable
private fun HeritageConfirmDialog(
    option: CrisisOption,
    texts: com.worstcase.outpost.i18n.StringSet,
    onAccept: () -> Unit,
    onBack: () -> Unit
) {
    Dialog(onDismissRequest = onBack) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .clip(RoundedCornerShape(20.dp))
                .background(NasaBlue)
                .padding(16.dp)
        ) {
            Text(
                text = texts.hardwareHeritage,
                color = NasaAccent,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = texts.heritageConfirmTitle,
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = option.text,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = texts.historicalContext,
                    color = NasaAccent,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = option.hardwareHistory?.ifBlank { texts.heritageFallbackHistory } ?: texts.heritageFallbackHistory,
                    color = TextSecondary,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onAccept,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
            ) {
                Text(texts.heritageAccept, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary)
            ) {
                Text(texts.heritageBack)
            }
        }
    }
}

@Composable
private fun CrisisOptionCard(
    option: CrisisOption,
    heritageLabel: String,
    onClick: () -> Unit
) {
    // Same NASA blue for every option — no colour bias toward heritage
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF0B3D91), // NASA identity blue — same for all three options
            contentColor = Color.White
        ),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = option.text,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                lineHeight = 17.sp,
                modifier = Modifier.fillMaxWidth()
            )
            if (option.usesAbandonedHardware) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = heritageLabel,
                    fontSize = 10.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
