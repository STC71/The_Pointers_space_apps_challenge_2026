package com.worstcase.outpost.ui

/**
 * Main training screen: resources, crew, crisis actions, 360 briefing.
 */

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.worstcase.outpost.data.MockData
import com.worstcase.outpost.domain.CrewMember
import com.worstcase.outpost.domain.MAX_SCIENCE_PER_CRISIS
import com.worstcase.outpost.i18n.AppLanguage
import com.worstcase.outpost.i18n.Strings
import com.worstcase.outpost.i18n.ConsequenceCopy
import com.worstcase.outpost.domain.Difficulty
import com.worstcase.outpost.openUrl
import com.worstcase.outpost.presentation.GameUiState
import com.worstcase.outpost.theme.*
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun DashboardScreen(
    state: GameUiState,
    currentLanguage: AppLanguage,
    onTriggerCrisis: () -> Unit,
    onResolveCrisis: (String, Long, String) -> Unit,
    onEndMission: () -> Unit,
    onTogglePanorama: (Boolean) -> Unit = {},
    onRequestAiCopilot: (String, Long) -> Unit = { _, _ -> },
    onDismissAiCopilot: () -> Unit = {},
    onDismissGoodNews: () -> Unit = {},
    onShowLastDecisionReport: () -> Unit = {}
) {
    val texts = Strings.get(currentLanguage)
    var eduInfoKey by remember { mutableStateOf<String?>(null) }
    var showWithdrawalConfirmation by remember { mutableStateOf(false) }

    val analogPhoto = state.selectedAnalog?.id?.let { photoForAnalogId(it) }

    RandomAnalogBackground(photo = analogPhoto) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(start = 20.dp, end = 20.dp, top = 48.dp, bottom = 20.dp)
            ) {
                Text(
                    text = state.selectedAnalog?.name ?: "Outpost",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = state.selectedAnalog?.location ?: "",
                    color = NasaAccent,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                SectionTitle(texts.resourcesSection)
                if (state.lastDecisionReport != null) {
                    OutlinedButton(onClick = onShowLastDecisionReport, modifier = Modifier.fillMaxWidth()) {
                        Text(ConsequenceCopy.REOPEN.text(currentLanguage))
                    }
                    Spacer(Modifier.height(8.dp))
                }
                Panel {
                    ResourceBar(
                        label = texts.energy,
                        value = state.resources.energy,
                        onClick = { eduInfoKey = "energy" }
                    )
                    ResourceBar(
                        label = texts.water,
                        value = state.resources.water,
                        onClick = { eduInfoKey = "water" }
                    )
                    ResourceBar(
                        label = texts.oxygen,
                        value = state.resources.oxygen,
                        onClick = { eduInfoKey = "oxygen" }
                    )
                    ResourceBar(
                        label = texts.food,
                        value = (state.resources.foodDays * 5).coerceIn(0, 100),
                        valueLabel = "${state.resources.foodDays} d",
                        onClick = { eduInfoKey = "food" }
                    )
                    ResourceBar(
                        label = texts.crewHealth,
                        value = state.resources.crewHealth,
                        onClick = { eduInfoKey = "health" }
                    )
                    ResourceBar(
                        label = texts.morale,
                        value = state.resources.morale,
                        onClick = { eduInfoKey = "morale" }
                    )
                }

                val riskTax = MockData.analogRiskTax(state.selectedAnalog?.id)
                if (riskTax.first > 0 || riskTax.second > 0 || riskTax.third > 0) {
                    Spacer(modifier = Modifier.height(14.dp))
                    SectionTitle(texts.analogRiskSection)
                    Panel {
                        Text(texts.analogRiskHint, color = TextSecondary, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        if (riskTax.first > 0) {
                            Text("• ${texts.riskWater}: +${riskTax.first}", color = AlertOrange, fontSize = 13.sp)
                        }
                        if (riskTax.second > 0) {
                            Text("• ${texts.riskOxygen}: +${riskTax.second}", color = AlertOrange, fontSize = 13.sp)
                        }
                        if (riskTax.third > 0) {
                            Text("• ${texts.riskEnergy}: +${riskTax.third}", color = AlertOrange, fontSize = 13.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                SectionTitle(texts.missionClockSection)
                Panel {
                    Text(
                        texts.missionDayLabel.replace("{d}", state.missionDay.toString()),
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                    Text(
                        texts.missionTimeLabel
                            .replace("{h}", state.missionHour.toString().padStart(2, '0'))
                            .replace("{m}", state.missionMinute.toString().padStart(2, '0')),
                        color = TextSecondary,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Text(
                        texts.daysToReturnLabel.replace(
                            "{n}",
                            (state.totalMissionDays - state.missionDay).coerceAtLeast(0).toString()
                        ),
                        color = NasaAccent,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))
                SectionTitle(texts.sciencePoints)
                Panel {
                    Text("${state.sciencePoints}/${state.targetCrisisCount * MAX_SCIENCE_PER_CRISIS}",
                        color = NasaAccent, fontWeight = FontWeight.SemiBold)
                    Text(texts.scoreWeights, color = TextSecondary, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(14.dp))
                SectionTitle(texts.crewSection)
                state.crew.forEach { member ->
                    CrewCard(member, texts, state.lastCrewDeltas[member.id])
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Spacer(modifier = Modifier.height(12.dp))
                val crisisTotal = state.targetCrisisCount
                val crisisDone = state.crisesFaced.size.coerceAtMost(crisisTotal)
                Text(
                    text = "${texts.testsCompleted}: $crisisDone/$crisisTotal",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Button(
                    onClick = onTriggerCrisis,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AlertOrange)
                ) {
                    Text(texts.worstCaseButton, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = { onTogglePanorama(true) },
                    modifier = Modifier.fillMaxWidth()
                ) { Text(texts.view360, fontSize = 13.sp) }
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(
                    onClick = { showWithdrawalConfirmation = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(texts.withdrawMission, color = Color.White.copy(alpha = 0.85f))
                }
            }

            if (showWithdrawalConfirmation) {
                ScrollablePanelDialog(
                    title = texts.withdrawConfirmationTitle,
                    onDismiss = { showWithdrawalConfirmation = false },
                    dismissOnClickOutside = false,
                    actions = {
                        Button(
                            onClick = {
                                showWithdrawalConfirmation = false
                                onEndMission()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = AlertOrange)
                        ) { Text(texts.withdrawMission) }
                        TextButton(
                            onClick = { showWithdrawalConfirmation = false },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text(texts.continueMission) }
                    }
                ) {
                    Text(texts.withdrawConfirmationBody, color = TextPrimary)
                }
            }

            if (state.showCrisisDialog && state.activeCrisis != null && state.activeCrisisOccurrence != null) {
                CrisisDialog(
                    crisis = state.activeCrisis!!,
                    occurrence = state.activeCrisisOccurrence!!,
                    currentLanguage = currentLanguage,
                    onOptionSelected = onResolveCrisis,
                    onRequestAiCopilot = onRequestAiCopilot,
                    aiAssessment = state.aiAssessment,
                    forecasts = state.aiForecasts,
                    difficulty = state.difficulty ?: Difficulty.MID,
                    missionDay = state.missionDay,
                    missionHour = state.missionHour,
                    missionMinute = state.missionMinute,
                    daysToReturn = (state.totalMissionDays - state.missionDay).coerceAtLeast(0)
                )
            }

            if (state.showGoodNewsDialog && state.activeGoodNews != null && !state.showDecisionReport) {
                GoodNewsDialog(
                    news = state.activeGoodNews!!,
                    currentLanguage = currentLanguage,
                    onClose = onDismissGoodNews
                )
            }

            if (state.showPanoramaViewer) {
                val analog = state.selectedAnalog
                val body = buildString {
                    if (analog != null) {
                        append(analog.name)
                        append("\n")
                        append(analog.location)
                        append("\n\n")
                        append(analog.description)
                        if (analog.environmentalModifier.isNotBlank()) {
                            append("\n\n⚠ ")
                            append(analog.environmentalModifier)
                        }
                        val deep = MockData.analogDeepBriefing(analog.id, currentLanguage)
                        append("\n\n——\n")
                        append(texts.briefingTechnicalHeader)
                        append("\n")
                        append(deep.first)
                        append("\n\n")
                        append(texts.briefingHistoryHeader)
                        append("\n")
                        append(deep.second)
                        append("\n\n")
                        append(texts.briefingOpsHeader)
                        append("\n")
                        append(deep.third)
                        if (analog.soundscapeDescription.isNotBlank()) {
                            append("\n\n")
                            append(texts.briefingSoundscapeHeader)
                            append(": ")
                            append(analog.soundscapeDescription)
                        }
                    }
                }
                AnalogBriefingOverlay(
                    title = texts.view360,
                    body = body,
                    photo = analog?.id?.let { photoForAnalogId(it) },
                    closeLabel = texts.close360,
                    onClose = { onTogglePanorama(false) },
                    secondaryLabel = if (!analog?.panoramaUrl.isNullOrBlank()) texts.openExternalPage else null,
                    onSecondary = {
                        val url = analog?.panoramaUrl
                        if (!url.isNullOrBlank()) openUrl(url)
                    }
                )
            }

            state.aiAssessment?.let { assessment ->
                if (!state.showCrisisDialog) {
                    SimpleOverlay(
                        title = assessment.title.ifBlank { texts.askAiCopilot },
                        body = listOf(assessment.analysis, assessment.worstCaseScenario)
                            .filter { it.isNotBlank() }
                            .joinToString("\n\n"),
                        closeLabel = texts.close360,
                        onClose = onDismissAiCopilot
                    )
                }
            }

            eduInfoKey?.let { key ->
                val title = when (key) {
                    "energy" -> texts.eduEnergyTitle
                    "water" -> texts.eduWaterTitle
                    "oxygen" -> texts.eduOxygenTitle
                    "health" -> texts.eduHealthTitle
                    "food" -> texts.eduFoodTitle
                    else -> texts.eduMoraleTitle
                }
                val body = when (key) {
                    "energy" -> texts.eduEnergyBody
                    "water" -> texts.eduWaterBody
                    "oxygen" -> texts.eduOxygenBody
                    "health" -> texts.eduHealthBody
                    "food" -> texts.eduFoodBody
                    else -> texts.eduMoraleBody
                }
                SimpleOverlay(
                    title = title,
                    body = body,
                    closeLabel = texts.close360,
                    onClose = { eduInfoKey = null }
                )
            }
        }
    }
}

@Composable
private fun Panel(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(NasaBlue.copy(alpha = 0.55f))
            .padding(14.dp),
        content = content
    )
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        color = Color.White.copy(alpha = 0.9f),
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
private fun ResourceBar(label: String, value: Int, valueLabel: String? = null, onClick: (() -> Unit)? = null) {
    val clickable = onClick != null
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .clip(RoundedCornerShape(10.dp))
            .then(
                if (clickable) {
                    Modifier
                        .background(NasaAccent.copy(alpha = 0.16f))
                        .clickable { onClick.invoke() }
                } else {
                    Modifier
                }
            )
            .padding(horizontal = if (clickable) 10.dp else 0.dp, vertical = if (clickable) 8.dp else 0.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (clickable) "$label  ·  ℹ" else label,
                color = if (clickable) NasaAccent else TextSecondary,
                fontSize = 12.sp,
                fontWeight = if (clickable) FontWeight.SemiBold else FontWeight.Normal
            )
            Text(
                text = valueLabel ?: "$value%",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { (value.coerceIn(0, 100) / 100f) },
            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
            color = when {
                value < 30 -> CriticalRed
                value < 60 -> WarningYellow
                else -> SuccessGreen
            },
            trackColor = Color.White.copy(alpha = 0.15f)
        )
        if (clickable) {
            Text(
                text = "↗",
                color = NasaAccent.copy(alpha = 0.8f),
                fontSize = 10.sp,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

@Composable
private fun CrewCard(
    member: CrewMember,
    texts: com.worstcase.outpost.i18n.StringSet,
    delta: com.worstcase.outpost.presentation.CrewDelta? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(NasaBlue.copy(alpha = 0.55f))
            .padding(12.dp)
    ) {
        Text(member.name, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
        if (member.role.isNotBlank()) {
            Text(member.role, color = NasaAccent, fontSize = 12.sp)
        }
        if (member.bio.isNotBlank()) {
            Text(member.bio, color = TextSecondary, fontSize = 11.sp, lineHeight = 14.sp)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            MiniStat(texts.sleep, member.sleep, inverted = false, change = delta?.sleep ?: 0)
            MiniStat(texts.stress, member.stress, inverted = true, change = delta?.stress ?: 0)
            MiniStat(texts.status, member.status, inverted = false, change = delta?.status ?: 0)
        }
    }
}

@Composable
private fun MiniStat(
    label: String,
    value: Int,
    inverted: Boolean = false,
    change: Int = 0
) {
    // sleep/status: high = good. stress: high = bad.
    val color = if (inverted) {
        when {
            value <= 35 -> SuccessGreen
            value <= 60 -> WarningYellow
            else -> CriticalRed
        }
    } else {
        when {
            value >= 60 -> SuccessGreen
            value >= 35 -> WarningYellow
            else -> CriticalRed
        }
    }
    // Arrow: for stress, down is good; for others, up is good
    val arrow = when {
        change > 0 -> "▲"
        change < 0 -> "▼"
        else -> "·"
    }
    val arrowColor = when {
        change == 0 -> TextSecondary
        inverted && change < 0 -> SuccessGreen   // stress went down
        inverted && change > 0 -> CriticalRed    // stress went up
        !inverted && change > 0 -> SuccessGreen
        else -> CriticalRed
    }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = TextSecondary, fontSize = 11.sp)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("$value", color = color, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(arrow, color = arrowColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun SimpleOverlay(
    title: String,
    body: String,
    closeLabel: String,
    onClose: () -> Unit
) {
    ScrollablePanelDialog(
        title = title,
        onDismiss = onClose,
        actions = {
            Button(
                onClick = onClose,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = NasaAccent)
            ) { Text(closeLabel) }
        }
    ) {
        Text(body, color = TextPrimary, fontSize = 13.sp, lineHeight = 18.sp)
    }
}

@Composable
private fun AnalogBriefingOverlay(
    title: String,
    body: String,
    photo: DrawableResource?,
    closeLabel: String,
    onClose: () -> Unit,
    secondaryLabel: String? = null,
    onSecondary: (() -> Unit)? = null
) {
    Box(modifier = Modifier.fillMaxSize()) {
        if (photo != null) {
            Image(
                painter = painterResource(photo),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.45f)))
        } else {
            Box(Modifier.fillMaxSize().background(NasaDarkBlue.copy(alpha = 0.70f)))
        }
        ScrollablePanelDialog(
            title = title,
            onDismiss = onClose,
            actions = {
                if (secondaryLabel != null && onSecondary != null) {
                    Button(
                        onClick = onSecondary,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = AlertOrange)
                    ) { Text(secondaryLabel) }
                    Spacer(Modifier.height(10.dp))
                }
                Button(
                    onClick = onClose,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = NasaAccent)
                ) { Text(closeLabel) }
            }
        ) {
            Text(body, color = TextPrimary, fontSize = 13.sp, lineHeight = 18.sp)
        }
    }
}


@Composable
private fun GoodNewsDialog(
    news: com.worstcase.outpost.domain.GoodNews,
    currentLanguage: AppLanguage,
    onClose: () -> Unit
) {
    val texts = Strings.get(currentLanguage)

    ScrollablePanelDialog(
        title = texts.goodNewsTitle,
        titleColor = SuccessGreen,
        onDismiss = onClose,
        dismissOnClickOutside = false,
        actions = {
            Button(
                onClick = onClose,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
            ) { Text(texts.goodNewsClose, color = Color.White) }
        }
    ) {
        Text(news.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 17.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Text(news.body, color = TextPrimary, fontSize = 13.sp, lineHeight = 18.sp)
        Spacer(modifier = Modifier.height(10.dp))
        Text(texts.historicalContext, color = NasaAccent, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
        Text(news.historicalNote, color = TextSecondary, fontSize = 12.sp, lineHeight = 16.sp)
    }
}
