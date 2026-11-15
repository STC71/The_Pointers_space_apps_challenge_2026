package com.worstcase.outpost

import com.worstcase.outpost.data.MockData
import com.worstcase.outpost.data.TrainingContent
import com.worstcase.outpost.domain.*
import com.worstcase.outpost.i18n.AppLanguage
import com.worstcase.outpost.i18n.Strings
import com.worstcase.outpost.i18n.TrainingCopy
import com.worstcase.outpost.presentation.GameViewModel
import kotlin.random.Random
import kotlin.test.*

class TrainingContentTest {
    @Test fun allFourHistoricalNotesRemainAvailableInBothNarrativeLanguages() {
        for (language in listOf(AppLanguage.ENGLISH, AppLanguage.SPANISH)) {
            val notes = TrainingContent.historicalNotes(language)
            assertEquals(4, notes.size)
            assertTrue(notes[0].contains(if (language == AppLanguage.SPANISH) "Apolo 13" else "Apollo 13"))
            assertTrue(notes[1].contains("ECLSS"))
            assertTrue(notes[2].contains("Opportunity 2018"))
            assertTrue(notes[3].contains("Gemini/"))
            assertEquals(notes, TrainingContent.historicalNotes(language))
        }
        for (language in AppLanguage.entries) for (label in TrainingCopy.entries) {
            assertTrue(label.text(language).isNotBlank(), "$language / $label")
        }
    }

    @Test fun trainingGuidanceFollowsTheExistingRecommendationAndRetainsHeritage() {
        val ordinary = CrisisOption("repair", "Repair")
        val heritage = ordinary.copy(id = "heritage", usesAbandonedHardware = true, hardwareHistory = "Historical equipment")
        val crisis = Crisis("exercise", "Exercise", "Training", listOf(ordinary, heritage))
        for (lang in listOf(AppLanguage.ENGLISH, AppLanguage.SPANISH)) {
            val spanish = lang == AppLanguage.SPANISH
            val advice = AiFlightAssessment("Houston", "Current advice", "Current warning", ordinary.id)
            val lowCrew = TrainingContent.houstonContext(crisis, advice, Resources(crewHealth = 40), lang)
            assertTrue(lowCrew.first().contains(if (spanish) "rendimiento de tripulación" else "crew performance"))
            val lowWater = TrainingContent.houstonContext(crisis, advice, Resources(crewHealth = 80, water = 20), lang)
            assertTrue(lowWater.first().contains(if (spanish) "consumibles" else "consumables"))
            val reuse = TrainingContent.houstonContext(crisis, advice.copy(recommendedOptionId = heritage.id), Resources(), lang)
            assertTrue(reuse.first().contains(if (spanish) "tiempo y herramientas" else "time and tools"))
            assertTrue(reuse.last().contains("Historical equipment"))
            assertEquals("Current advice", advice.analysis)
            assertEquals(ordinary.id, advice.recommendedOptionId)
        }
    }

    @Test fun everyDesignCombinationKeepsFourEducationalAxesAlongsideCurrentCosts() {
        for (language in listOf(AppLanguage.ENGLISH, AppLanguage.SPANISH)) {
            for (power in PowerPlan.entries) for (habitat in HabitatPlan.entries) {
                for (priority in PriorityPlan.entries) for (reuse in listOf(false, true)) {
                    val plan = MissionPlan(power, habitat, priority, reuse)
                    val costs = MockData.worstCaseHints(plan, language)
                    val context = TrainingContent.designRisks(plan, language)
                    assertEquals(4, context.size)
                    assertEquals(4, context.distinct().size)
                    assertTrue(context.all { it.isNotBlank() })
                    assertEquals(costs, MockData.worstCaseHints(plan, language))
                }
            }
        }
        val nuclear = MissionPlan(power = PowerPlan.NUCLEAR)
        assertTrue(TrainingContent.designRisks(nuclear, AppLanguage.SPANISH).first().contains("refrigeración"))
    }

    @Test fun nextMissionReflectionsAreAvailableWithoutReplacingTheSavedOutcome() {
        for (language in listOf(AppLanguage.ENGLISH, AppLanguage.SPANISH)) {
            val texts = Strings.get(language)
            val low = Resources(water = 40, oxygen = 40)
            assertEquals(listOf(texts.lessonImprovePace, texts.lessonImproveComm),
                TrainingContent.debriefReflections(low, language))
            assertEquals(listOf(texts.lessonImprovePace, texts.lessonImproveComm, texts.lessonImproveStock),
                TrainingContent.debriefReflections(low.copy(water = 80), language))
        }
    }

    @Test fun repeatedReadingPreservesMissionSnapshotsAdviceAndSubsequentRandomEvents() {
        fun start() = GameViewModel(Random(42)).apply {
            finishIntro(); selectDifficulty(Difficulty.MID); selectDestination(PlanetaryTarget.MOON)
            selectAnalog(MockData.getAnalogs(AppLanguage.ENGLISH).first { it.planetaryTarget == PlanetaryTarget.MOON })
            confirmMissionDesign()
        }
        val reading = start()
        val control = start()
        repeat(2) {
            reading.triggerCrisis(); control.triggerCrisis()
            val crisis = assertNotNull(reading.state.value.activeCrisis)
            val occurrence = assertNotNull(reading.state.value.activeCrisisOccurrence)
            reading.requestAiAssessment(crisis.id, occurrence)
            control.requestAiAssessment(crisis.id, occurrence)
            val before = reading.state.value
            repeat(10) {
                TrainingContent.houstonContext(crisis, assertNotNull(before.aiAssessment), before.resources, before.currentLanguage)
                TrainingContent.designRisks(before.missionPlan, before.currentLanguage)
                TrainingContent.debriefReflections(before.resources, before.currentLanguage)
            }
            assertEquals(before, reading.state.value)
            assertEquals(control.state.value, reading.state.value)
            val option = crisis.options.first { !it.usesAbandonedHardware }
            for (vm in listOf(reading, control)) {
                vm.resolveCrisis(crisis.id, occurrence, option.id)
                val report = assertNotNull(vm.state.value.lastDecisionReport)
                vm.dismissDecisionReport(report.forecast.crisisId, report.forecast.occurrence)
                vm.dismissGoodNews()
            }
            assertEquals(control.state.value, reading.state.value)
        }
    }
}
