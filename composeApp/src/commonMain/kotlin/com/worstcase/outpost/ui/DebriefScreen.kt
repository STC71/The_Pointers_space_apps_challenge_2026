package com.worstcase.outpost.ui

/**
 * Mission debrief with photo background and localized lessons.
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.worstcase.outpost.domain.GameResult
import com.worstcase.outpost.data.TrainingContent
import com.worstcase.outpost.i18n.TrainingCopy
import com.worstcase.outpost.domain.MissionEndReason
import com.worstcase.outpost.i18n.description
import com.worstcase.outpost.presentation.missionLessons
import com.worstcase.outpost.i18n.AppLanguage
import com.worstcase.outpost.i18n.Strings
import com.worstcase.outpost.i18n.ConsequenceCopy
import com.worstcase.outpost.theme.AlertOrange
import com.worstcase.outpost.theme.CriticalRed
import com.worstcase.outpost.theme.NasaAccent
import com.worstcase.outpost.theme.NasaBlue
import com.worstcase.outpost.theme.SuccessGreen
import com.worstcase.outpost.theme.TextPrimary
import com.worstcase.outpost.theme.TextSecondary

@Composable
fun DebriefScreen(
    result: GameResult?,
    currentLanguage: AppLanguage,
    onRestart: () -> Unit,
    onShowLastDecisionReport: (() -> Unit)? = null
) {
    val texts = Strings.get(currentLanguage)
    val localizedLessons = result?.let { missionLessons(it, currentLanguage) } ?: emptyList()

    RandomAnalogBackground {
        if (result == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No mission data available", color = Color.White)
            }
            return@RandomAnalogBackground
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(start = 20.dp, end = 20.dp, top = 48.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = texts.debriefTitle,
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = when (result.endReason) {
                    MissionEndReason.Completed -> texts.missionStatusSuccess
                    is MissionEndReason.CriticalDepletion -> texts.missionStatusFailed
                    MissionEndReason.Withdrawal -> texts.missionStatusWithdrawn
                },
                color = when (result.endReason) {
                    MissionEndReason.Completed -> SuccessGreen
                    is MissionEndReason.CriticalDepletion -> CriticalRed
                    MissionEndReason.Withdrawal -> AlertOrange
                },
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
            )

            Spacer(Modifier.height(8.dp))
            Text(texts.description(result.endReason), color = TextSecondary, textAlign = TextAlign.Center)
            Spacer(Modifier.height(8.dp))
            Text(
                text = "${texts.finalScore}: ${result.score}/100",
                color = NasaAccent,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(16.dp))

            onShowLastDecisionReport?.let { showReport ->
                OutlinedButton(onClick = showReport, modifier = Modifier.fillMaxWidth()) {
                    Text(ConsequenceCopy.REOPEN.text(currentLanguage))
                }
                Spacer(Modifier.height(12.dp))
            }

            Panel {
                Text("${texts.sciencePoints}: ${result.sciencePoints}/${result.scienceTarget}", color = NasaAccent)
                Spacer(Modifier.height(8.dp))
                Text(texts.scoreWeights, color = TextSecondary, fontSize = 12.sp, lineHeight = 16.sp)
            }

            Spacer(Modifier.height(12.dp))

            Panel {
                Text(texts.finalResources, color = TextPrimary, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))
                val r = result.finalResources
                Text("${texts.energy}: ${r.energy}%", color = TextSecondary, fontSize = 13.sp)
                Text("${texts.water}: ${r.water}%", color = TextSecondary, fontSize = 13.sp)
                Text("${texts.oxygen}: ${r.oxygen}%", color = TextSecondary, fontSize = 13.sp)
                Text("${texts.crewHealth}: ${r.crewHealth}%", color = TextSecondary, fontSize = 13.sp)
                Text("${texts.morale}: ${r.morale}%", color = TextSecondary, fontSize = 13.sp)
                Text("${texts.food}: ${r.foodDays} ${texts.daysUnit}", color = TextSecondary, fontSize = 13.sp)
            }

            Spacer(Modifier.height(12.dp))
            Panel {
                Text(
                    "${texts.crisesFaced}: ${result.crisesFaced.size}",
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(Modifier.height(12.dp))
            Panel {
                Text(texts.lessonsLearned, color = TextPrimary, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))
                localizedLessons.forEach { (lesson, positive) ->
                    Row(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                        Text(
                            if (positive) "▲ " else "▼ ",
                            color = if (positive) SuccessGreen else CriticalRed,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            lesson,
                            color = if (positive) SuccessGreen.copy(alpha = 0.92f) else CriticalRed.copy(alpha = 0.9f),
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
            Panel {
                Text(TrainingCopy.REFLECTIONS.text(currentLanguage), color = NasaAccent,
                    fontWeight = FontWeight.SemiBold)
                TrainingContent.debriefReflections(result.finalResources, currentLanguage).forEach { reflection ->
                    Spacer(Modifier.height(10.dp))
                    Text(reflection, color = TextSecondary, fontSize = 13.sp, lineHeight = 19.sp)
                }
            }
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = onRestart,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AlertOrange)
            ) {
                Text(texts.playAgain, fontWeight = FontWeight.Bold)
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
            .padding(16.dp),
        content = content
    )
}
