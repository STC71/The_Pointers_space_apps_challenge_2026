package com.worstcase.outpost.ui

/**
 * Phase 1b – Mission Design:
 * Link Earth analog → Moon/Mars site, choose architecture, preview worst cases.
 */

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.worstcase.outpost.data.MockData
import com.worstcase.outpost.data.TrainingContent
import com.worstcase.outpost.i18n.TrainingCopy
import com.worstcase.outpost.domain.Analog
import com.worstcase.outpost.domain.HabitatPlan
import com.worstcase.outpost.domain.MissionPlan
import com.worstcase.outpost.domain.PowerPlan
import com.worstcase.outpost.domain.PriorityPlan
import com.worstcase.outpost.i18n.AppLanguage
import com.worstcase.outpost.i18n.Strings
import com.worstcase.outpost.i18n.destinationName
import com.worstcase.outpost.theme.AlertOrange
import com.worstcase.outpost.theme.NasaAccent
import com.worstcase.outpost.theme.NasaBlue
import com.worstcase.outpost.theme.TextSecondary

@Composable
fun MissionDesignScreen(
    analog: Analog?,
    plan: MissionPlan,
    currentLanguage: AppLanguage,
    onPlanChanged: (MissionPlan) -> Unit,
    onConfirm: () -> Unit,
    onBack: () -> Unit
) {
    val texts = Strings.get(currentLanguage)
    val hints = MockData.worstCaseHints(plan, currentLanguage)
    val photo = analog?.id?.let { photoForAnalogId(it) }

    RandomAnalogBackground(photo = photo) {
        PreparationLayout(
            backLabel = texts.backButton,
            onBack = onBack
        ) {
            Text(
                text = texts.missionDesignTitle,
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Text(
                text = texts.missionDesignSubtitle,
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(top = 6.dp, bottom = 16.dp)
            )

            // Analog → destination link
            Panel {
                Text(texts.missionLinkLabel, color = NasaAccent, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                Spacer(Modifier.height(8.dp))
                Text(analog?.name ?: "—", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(analog?.location ?: "", color = TextSecondary, fontSize = 12.sp)
                Spacer(Modifier.height(8.dp))
                Text(
                    text = analog?.planetaryTarget?.let { "${it.icon} ${texts.destinationName(it)}" } ?: "",
                    color = NasaAccent,
                    fontSize = 14.sp
                )
                Text(analog?.targetSite ?: "", color = Color.White.copy(alpha = 0.9f), fontSize = 13.sp)
                if (!analog?.description.isNullOrBlank()) {
                    Spacer(Modifier.height(8.dp))
                    Text(analog!!.description, color = TextSecondary, fontSize = 12.sp, lineHeight = 16.sp)
                }
            }

            Spacer(Modifier.height(14.dp))

            // Power
            SectionTitle(texts.powerLabel)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                ChoiceChip(texts.powerSolar, plan.power == PowerPlan.SOLAR, Modifier.weight(1f)) {
                    onPlanChanged(plan.copy(power = PowerPlan.SOLAR))
                }
                ChoiceChip(texts.powerNuclear, plan.power == PowerPlan.NUCLEAR, Modifier.weight(1f)) {
                    onPlanChanged(plan.copy(power = PowerPlan.NUCLEAR))
                }
                ChoiceChip(texts.powerMixed, plan.power == PowerPlan.MIXED, Modifier.weight(1f)) {
                    onPlanChanged(plan.copy(power = PowerPlan.MIXED))
                }
            }

            Spacer(Modifier.height(12.dp))
            SectionTitle(texts.habitatLabel)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                ChoiceChip(texts.habitatMinimal, plan.habitat == HabitatPlan.MINIMAL, Modifier.weight(1f)) {
                    onPlanChanged(plan.copy(habitat = HabitatPlan.MINIMAL))
                }
                ChoiceChip(texts.habitatStandard, plan.habitat == HabitatPlan.STANDARD, Modifier.weight(1f)) {
                    onPlanChanged(plan.copy(habitat = HabitatPlan.STANDARD))
                }
                ChoiceChip(texts.habitatHardened, plan.habitat == HabitatPlan.HARDENED, Modifier.weight(1f)) {
                    onPlanChanged(plan.copy(habitat = HabitatPlan.HARDENED))
                }
            }

            Spacer(Modifier.height(12.dp))
            SectionTitle(texts.priorityLabel)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                ChoiceChip(texts.priorityResources, plan.priority == PriorityPlan.RESOURCES, Modifier.weight(1f)) {
                    onPlanChanged(plan.copy(priority = PriorityPlan.RESOURCES))
                }
                ChoiceChip(texts.priorityCrew, plan.priority == PriorityPlan.CREW, Modifier.weight(1f)) {
                    onPlanChanged(plan.copy(priority = PriorityPlan.CREW))
                }
                ChoiceChip(texts.priorityScience, plan.priority == PriorityPlan.SCIENCE, Modifier.weight(1f)) {
                    onPlanChanged(plan.copy(priority = PriorityPlan.SCIENCE))
                }
            }

            Spacer(Modifier.height(12.dp))
            SectionTitle(texts.reusePlanLabel)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                ChoiceChip(texts.reusePlanYes, plan.planHardwareReuse, Modifier.weight(1f)) {
                    onPlanChanged(plan.copy(planHardwareReuse = true))
                }
                ChoiceChip(texts.reusePlanNo, !plan.planHardwareReuse, Modifier.weight(1f)) {
                    onPlanChanged(plan.copy(planHardwareReuse = false))
                }
            }

            Spacer(Modifier.height(16.dp))
            Panel {
                Text(texts.designSummary, color = NasaAccent, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                Spacer(Modifier.height(8.dp))
                hints.forEach { h ->
                    Text("• $h", color = Color.White.copy(alpha = 0.9f), fontSize = 12.sp, lineHeight = 16.sp)
                    Spacer(Modifier.height(4.dp))
                }
                Spacer(Modifier.height(8.dp))
                Text(texts.scoreWeights, color = TextSecondary, fontSize = 12.sp, lineHeight = 16.sp)
            }

            Spacer(Modifier.height(12.dp))
            TrainingNotes(
                title = TrainingCopy.DESIGN.text(currentLanguage),
                note = TrainingCopy.DESIGN_NOTE.text(currentLanguage),
                paragraphs = TrainingContent.designRisks(plan, currentLanguage)
            )
            Spacer(Modifier.height(20.dp))
            Button(
                onClick = onConfirm,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AlertOrange)
            ) {
                Text(texts.confirmMissionDesign, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        color = Color.White.copy(alpha = 0.85f),
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(bottom = 8.dp)
    )
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
private fun ChoiceChip(
    label: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(if (selected) NasaAccent.copy(alpha = 0.35f) else NasaBlue.copy(alpha = 0.45f))
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) NasaAccent else Color.White.copy(alpha = 0.2f),
                shape = RoundedCornerShape(10.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp)
    ) {
        Text(
            text = label,
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            textAlign = TextAlign.Center,
            maxLines = 2,
            lineHeight = 13.sp
        )
    }
}
