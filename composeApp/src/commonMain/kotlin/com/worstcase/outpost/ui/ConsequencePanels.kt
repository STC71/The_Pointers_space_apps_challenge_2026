package com.worstcase.outpost.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.worstcase.outpost.data.MockData
import com.worstcase.outpost.domain.*
import com.worstcase.outpost.i18n.AppLanguage
import com.worstcase.outpost.i18n.ConsequenceCopy
import com.worstcase.outpost.i18n.Strings
import com.worstcase.outpost.theme.*

private data class Reading(
    val label: String, val before: Int, val after: Int,
    val unit: ConsequenceUnit = ConsequenceUnit.INDICATOR, val inverted: Boolean = false
)

private fun resourceReadings(before: Resources, after: Resources, language: AppLanguage): List<Reading> {
    val t = Strings.get(language)
    return listOf(
        Reading(t.energy, before.energy, after.energy), Reading(t.water, before.water, after.water),
        Reading(t.oxygen, before.oxygen, after.oxygen),
        Reading(t.food, before.foodDays, after.foodDays, ConsequenceUnit.RATION_DAYS),
        Reading(t.crewHealth, before.crewHealth, after.crewHealth), Reading(t.morale, before.morale, after.morale)
    )
}

private fun unitLabel(unit: ConsequenceUnit, language: AppLanguage): String = when (unit) {
    ConsequenceUnit.INDICATOR -> ConsequenceCopy.POINTS.text(language)
    ConsequenceUnit.RATION_DAYS -> Strings.get(language).daysUnit
    ConsequenceUnit.SCIENCE_POINTS -> ""
}

private fun signed(delta: Int): String = if (delta > 0) "+$delta" else "$delta"

private fun deltaDescription(delta: Int, forecast: Boolean, unit: ConsequenceUnit, language: AppLanguage): String {
    if (!forecast) {
        return listOf(signed(delta), unitLabel(unit, language)).filter { it.isNotEmpty() }.joinToString(" ")
    }
    val key = when (consequenceMagnitude(delta, unit)) {
        ConsequenceMagnitude.NONE -> ConsequenceCopy.NO_CHANGE
        ConsequenceMagnitude.SMALL -> if (delta > 0) ConsequenceCopy.RISE_SMALL else ConsequenceCopy.FALL_SMALL
        ConsequenceMagnitude.MODERATE -> if (delta > 0) ConsequenceCopy.RISE_MEDIUM else ConsequenceCopy.FALL_MEDIUM
        ConsequenceMagnitude.LARGE -> if (delta > 0) ConsequenceCopy.RISE_LARGE else ConsequenceCopy.FALL_LARGE
    }
    return key.text(language)
}

private fun effectColor(delta: Int, inverted: Boolean = false): Color = when {
    delta == 0 -> TextSecondary
    (delta > 0) != inverted -> SuccessGreen
    else -> AlertOrange
}

private data class EffectReading(val id: String, val label: String, val effect: String, val color: Color)

private fun forecastReadings(forecast: DecisionForecast, language: AppLanguage): List<EffectReading> {
    val t = Strings.get(language)
    val readings = resourceReadings(forecast.resourcesBefore, forecast.outcome.resources, language) +
        Reading(t.sciencePoints, forecast.scienceBefore, forecast.scienceAfter, ConsequenceUnit.SCIENCE_POINTS)
    return readings.mapIndexedNotNull { index, reading ->
        val delta = reading.after - reading.before
        if (delta == 0) null else EffectReading("resource$index", reading.label,
            deltaDescription(delta, true, reading.unit, language), effectColor(delta, reading.inverted))
    } + crewEffects(forecast.crewBefore, forecast.outcome.crew, true, language)
}

@Composable
private fun GroupedForecastEffects(readings: List<EffectReading>) {
    readings.groupBy { it.effect }.forEach { (effect, group) ->
        // A rise in science and a rise in stress can share wording, but not a
        // positive colour. Mixed benefits/costs use neutral text.
        val color = group.map { it.color }.distinct().singleOrNull() ?: TextPrimary
        Text("$effect: ${group.joinToString(", ") { it.label }}", color = color, fontSize = 13.sp,
            modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp))
    }
}

@Composable
private fun ConsequenceHeading(key: ConsequenceCopy, language: AppLanguage) {
    Spacer(Modifier.height(12.dp))
    Text(key.text(language), color = NasaAccent, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
    Spacer(Modifier.height(6.dp))
}

private fun crewEffects(
    before: List<CrewMember>, after: List<CrewMember>, forecast: Boolean, language: AppLanguage
): List<EffectReading> {
    val t = Strings.get(language)
    val previous = before.associateBy { it.id }
    val deltas = listOf(
        Triple(t.sleep, after.map { it.sleep - previous.getValue(it.id).sleep }, false),
        Triple(t.stress, after.map { it.stress - previous.getValue(it.id).stress }, true),
        Triple(t.status, after.map { it.status - previous.getValue(it.id).status }, false)
    )
    return deltas.mapIndexedNotNull { index, (label, changes, inverted) ->
        val low = changes.minOrNull() ?: 0
        val high = changes.maxOrNull() ?: 0
        if (low == 0 && high == 0) return@mapIndexedNotNull null
        val first = deltaDescription(low, forecast, ConsequenceUnit.INDICATOR, language)
        val last = deltaDescription(high, forecast, ConsequenceUnit.INDICATOR, language)
        val color = when {
            low >= 0 -> effectColor(high, inverted)
            high <= 0 -> effectColor(low, inverted)
            else -> TextSecondary
        }
        EffectReading("crew$index", label, if (first == last) first else "$first … $last", color)
    }
}

@Composable
private fun CrewRange(
    before: List<CrewMember>, after: List<CrewMember>, forecast: Boolean, language: AppLanguage,
    showNoChange: Boolean = false
) {
    val effects = crewEffects(before, after, forecast, language)
    effects.forEach { Text("${it.label}: ${it.effect}", color = it.color, fontSize = 13.sp) }
    if (effects.isEmpty() && showNoChange)
        Text(ConsequenceCopy.NO_CHANGE.text(language), color = TextSecondary, fontSize = 13.sp)
}

private data class ForecastPreview(
    val option: CrisisOption, val forecast: DecisionForecast, val readings: List<EffectReading>
)

@Composable
internal fun HoustonForecasts(
    crisis: Crisis, occurrence: Long, forecasts: Map<String, DecisionForecast>,
    language: AppLanguage
) {
    val previews = crisis.options.mapNotNull { option ->
        val forecast = forecasts[option.id] ?: return@mapNotNull null
        if (forecast.crisisId != crisis.id || forecast.occurrence != occurrence) return@mapNotNull null
        ForecastPreview(option, forecast, forecastReadings(forecast, language))
    }
    if (previews.isEmpty()) return
    val t = Strings.get(language)
    // Only say "all options" when every option has a current forecast. Compare
    // complete displayed ranges and colours, never crew averages or just labels.
    val complete = previews.size == crisis.options.size && previews.size > 1
    val common = if (complete) previews.first().readings.filter { reading ->
        previews.all { reading in it.readings }
    } else emptyList()
    val commonDays = if (complete) previews.map { it.forecast.outcome.periodBudget.days }.distinct().singleOrNull() else null
    ConsequenceHeading(ConsequenceCopy.FORECAST_TITLE, language)
    Text(ConsequenceCopy.FORECAST_NOTE.text(language), color = TextSecondary, fontSize = 12.sp)
    if (common.isNotEmpty() || commonDays != null) {
        Text(ConsequenceCopy.COMMON_EFFECTS.text(language), color = NasaAccent,
            fontWeight = FontWeight.SemiBold, fontSize = 13.sp, modifier = Modifier.padding(top = 8.dp))
        GroupedForecastEffects(common)
        commonDays?.let { Text("${ConsequenceCopy.DAYS.text(language)}: $it", color = TextSecondary, fontSize = 12.sp) }
    }
    previews.forEach { preview ->
        val forecast = preview.forecast
        val differences = preview.readings.filterNot { it in common }
        Column(Modifier.fillMaxWidth().padding(top = 8.dp).clip(RoundedCornerShape(10.dp))
            .background(NasaDarkBlue).padding(10.dp)) {
            Text(preview.option.text, color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            Spacer(Modifier.height(4.dp))
            GroupedForecastEffects(differences)
            if (differences.isEmpty()) {
                Text((if (common.isEmpty()) ConsequenceCopy.NO_CHANGE else ConsequenceCopy.ONLY_COMMON).text(language),
                    color = TextSecondary, fontSize = 13.sp)
            }
            if (commonDays == null) Text("${ConsequenceCopy.DAYS.text(language)}: ${forecast.outcome.periodBudget.days}",
                color = TextSecondary, fontSize = 12.sp)
            if (forecast.criticalReserves.isNotEmpty()) {
                val names = forecast.criticalReserves.joinToString { resource -> when (resource) {
                    CriticalResource.CREW_HEALTH -> t.crewHealth; CriticalResource.WATER -> t.water
                    CriticalResource.OXYGEN -> t.oxygen; CriticalResource.FOOD -> t.food
                } }
                Text("⚠ " + ConsequenceCopy.CRITICAL.text(language).replace("{resources}", names),
                    color = CriticalRed, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }
    }
}

@Composable
private fun ActualRow(reading: Reading, language: AppLanguage) {
    val unit = unitLabel(reading.unit, language)
    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(reading.label + (if (unit.isEmpty()) "" else " ($unit)"), modifier = Modifier.weight(1.6f),
            color = TextSecondary, fontSize = 12.sp)
        Text(reading.before.toString(), modifier = Modifier.weight(.8f), color = TextPrimary, fontSize = 13.sp)
        Text("${reading.after} (${signed(reading.after - reading.before)})", modifier = Modifier.weight(1.2f),
            color = effectColor(reading.after - reading.before, reading.inverted), fontSize = 13.sp)
    }
}

@Composable
private fun ActualHeaders(language: AppLanguage) {
    Row(Modifier.fillMaxWidth()) {
        Spacer(Modifier.weight(1.6f))
        Text(ConsequenceCopy.BEFORE.text(language), modifier = Modifier.weight(.8f), color = NasaAccent, fontSize = 11.sp)
        Text(ConsequenceCopy.ACTUAL.text(language), modifier = Modifier.weight(1.2f), color = NasaAccent, fontSize = 11.sp)
    }
}

@Composable
internal fun DecisionReportDialog(report: DecisionReport, language: AppLanguage, onClose: () -> Unit) {
    val t = Strings.get(language)
    val forecast = report.forecast
    val localized = remember(report, language) {
        MockData.getCrises(language).find { it.id == report.crisis.id } ?: report.crisis
    }
    val option = localized.options.find { it.id == forecast.optionId }
        ?: report.crisis.options.first { it.id == forecast.optionId }
    val news = remember(report.additionalEvent, language) {
        report.additionalEvent?.let { original -> MockData.goodNewsCatalog(language).find { it.id == original.id } ?: original }
    }
    ScrollablePanelDialog(
        title = ConsequenceCopy.REPORT_TITLE.text(language), onDismiss = onClose,
        dismissOnClickOutside = false,
        actions = { Button(onClick = onClose, modifier = Modifier.fillMaxWidth()) { Text(t.continueButton) } }
    ) {
        Text(localized.title, color = TextPrimary, fontWeight = FontWeight.Bold)
        Text(option.text, color = NasaAccent, fontSize = 14.sp, modifier = Modifier.padding(vertical = 8.dp))
        Text("${ConsequenceCopy.DAYS.text(language)}: ${forecast.outcome.periodBudget.days}",
            color = TextSecondary, fontSize = 13.sp, modifier = Modifier.padding(vertical = 8.dp))
        val finalRows = (resourceReadings(forecast.resourcesBefore, report.resourcesAfter, language) +
            Reading(t.sciencePoints, forecast.scienceBefore, report.scienceAfter, ConsequenceUnit.SCIENCE_POINTS))
            .filter { it.before != it.after }
        if (finalRows.isNotEmpty()) {
            ActualHeaders(language)
            finalRows.forEach { ActualRow(it, language) }
        }
        Spacer(Modifier.height(12.dp))
        Text(t.crewSection, color = NasaAccent, fontWeight = FontWeight.SemiBold)
        CrewRange(forecast.crewBefore, report.crewAfter, false, language, showNoChange = true)
        if (news != null) {
            ConsequenceHeading(ConsequenceCopy.ADDITIONAL, language)
            Text(news.title, color = SuccessGreen, fontWeight = FontWeight.SemiBold)
        }
    }
}
