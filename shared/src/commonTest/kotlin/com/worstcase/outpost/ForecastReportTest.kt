package com.worstcase.outpost

import com.worstcase.outpost.audio.AudioManager
import com.worstcase.outpost.audio.SoundEffect
import com.worstcase.outpost.audio.SoundPlayer
import com.worstcase.outpost.data.MockData
import com.worstcase.outpost.domain.*
import com.worstcase.outpost.i18n.AppLanguage
import com.worstcase.outpost.presentation.GameViewModel
import com.worstcase.outpost.presentation.Screen
import kotlin.random.Random
import kotlin.test.*

/** Regression coverage for forecasts, decision reports and their side effects. */
class ForecastReportTest {
    private class CountingRandom(seed: Int) : Random() {
        private val delegate = Random(seed)
        var draws = 0
        override fun nextBits(bitCount: Int): Int { draws++; return delegate.nextBits(bitCount) }
    }

    private val audio = object : SoundPlayer { override fun playSound(effect: SoundEffect) {} }
    private val safe = CrisisOption("safe", "Hold position")
    private val neutral = GoodNews("none", "None", "None", "Fixture", sleepDelta = 0,
        stressDelta = 0, statusDelta = 0, moraleDelta = 0)
    private val rescue = GoodNews("rescue", "Resupply", "Water and recovery", "Fixture",
        waterDelta = 8, foodDelta = 2, sleepDelta = 4, stressDelta = -6, statusDelta = 7)

    @BeforeTest fun setUp() { AudioManager.initialize(audio) }
    @AfterTest fun tearDown() { AudioManager.release(audio) }

    private fun fixture(random: Random = Random(42), options: List<CrisisOption> = listOf(safe),
        news: (AppLanguage) -> GoodNews = { neutral }) = GameViewModel(random,
        crisisCatalog = { _, _ -> listOf(Crisis("exercise", "Exercise", "Fixture", options)) },
        goodNewsPicker = news)

    private fun start(vm: GameViewModel, difficulty: Difficulty = Difficulty.MID) {
        vm.finishIntro(); vm.setPlayerName("Pilot"); vm.selectDifficulty(difficulty)
        vm.selectDestination(PlanetaryTarget.MOON)
        vm.selectAnalog(MockData.getAnalogs(vm.state.value.currentLanguage).first { it.planetaryTarget == PlanetaryTarget.MOON })
        vm.confirmMissionDesign()
    }

    private fun decide(vm: GameViewModel, optionId: String = "safe") {
        val state = vm.state.value
        vm.resolveCrisis(state.activeCrisis!!.id, state.activeCrisisOccurrence!!, optionId)
    }

    private fun acknowledge(vm: GameViewModel) {
        vm.state.value.lastDecisionReport?.let { vm.dismissDecisionReport(it.forecast.crisisId, it.forecast.occurrence) }
        vm.dismissGoodNews()
    }

    @Test fun forecastSeparatesCappedDirectEffectsFromActualPeriodUse() {
        val crew = listOf(CrewMember("player", "Pilot", sleep = 40, stress = 60, status = 70))
        val before = Resources(energy = 99, water = 95, oxygen = 80, foodDays = 20, crewHealth = 70, morale = 60)
        val option = CrisisOption("recover", "Recover", energyDelta = 20, waterDelta = 24,
            foodDelta = -1, healthDelta = 5, sleepDelta = 20, stressDelta = -15)
        for (difficulty in Difficulty.entries) {
            val outcome = calculateDecision(before, crew, option, "exercise", difficulty, MissionPlan(),
                3, 21, 7, Triple(0, 0, 0))
            assertEquals(100, outcome.directResources.water) // +5 effective, not +24.
            assertEquals(100, outcome.directResources.energy)
            assertEquals(100, outcome.resources.energy) // Recharge cannot exceed the cap.
            assertEquals(19, outcome.directResources.foodDays)
            assertEquals(17, outcome.resources.foodDays)
            assertEquals(2, outcome.periodBudget.days)
            assertEquals(2, outcome.periodBudget.food)
            assertEquals(outcome.directResources.water - outcome.periodBudget.water, outcome.resources.water)
            assertEquals(60, outcome.crew.single().sleep)
            assertEquals(45, outcome.crew.single().stress)
            assertEquals(75, outcome.resources.crewHealth)
        }
    }

    @Test fun magnitudeBandsRespectIndicatorDaysAndScienceUnits() {
        assertEquals(ConsequenceMagnitude.NONE, consequenceMagnitude(0))
        assertEquals(ConsequenceMagnitude.SMALL, consequenceMagnitude(-5))
        assertEquals(ConsequenceMagnitude.MODERATE, consequenceMagnitude(6))
        assertEquals(ConsequenceMagnitude.MODERATE, consequenceMagnitude(-15))
        assertEquals(ConsequenceMagnitude.LARGE, consequenceMagnitude(16))
        assertEquals(ConsequenceMagnitude.SMALL, consequenceMagnitude(1, ConsequenceUnit.RATION_DAYS))
        assertEquals(ConsequenceMagnitude.MODERATE, consequenceMagnitude(-3, ConsequenceUnit.RATION_DAYS))
        assertEquals(ConsequenceMagnitude.LARGE, consequenceMagnitude(4, ConsequenceUnit.RATION_DAYS))
        assertEquals(ConsequenceMagnitude.SMALL, consequenceMagnitude(2, ConsequenceUnit.SCIENCE_POINTS))
        assertEquals(ConsequenceMagnitude.MODERATE, consequenceMagnitude(4, ConsequenceUnit.SCIENCE_POINTS))
        assertEquals(ConsequenceMagnitude.LARGE, consequenceMagnitude(5, ConsequenceUnit.SCIENCE_POINTS))
    }

    @Test fun houstonDoesNotSpendGameRandomnessAndCommitKeepsTheViewedSnapshot() {
        val random = CountingRandom(42)
        var newsPicks = 0
        val vm = fixture(random, news = { newsPicks++; neutral })
        start(vm, Difficulty.SENIOR); vm.triggerCrisis()
        val before = vm.state.value
        val draws = random.draws
        repeat(3) { vm.requestAiAssessment(before.activeCrisis!!.id, before.activeCrisisOccurrence!!) }
        assertEquals(draws, random.draws)
        assertEquals(0, newsPicks)
        assertEquals(before.resources, vm.state.value.resources)
        assertEquals(before.crew, vm.state.value.crew)
        assertEquals(before.missionDay, vm.state.value.missionDay)
        assertEquals(before.crisisDeck, vm.state.value.crisisDeck)
        val viewed = vm.state.value.aiForecasts.getValue("safe")
        decide(vm)
        val report = vm.state.value.lastDecisionReport!!
        assertSame(viewed, report.forecast)
        assertTrue(report.forecastConsulted)
        assertEquals(viewed.outcome.resources, report.resourcesAfter)
        assertEquals(viewed.outcome.crew, report.crewAfter)
        assertEquals(viewed.scienceAfter, report.scienceAfter)
        assertTrue(vm.state.value.aiForecasts.isEmpty())
        val committed = vm.state.value
        vm.triggerCrisis(); vm.requestEndMission()
        vm.resolveCrisis(before.activeCrisis!!.id, before.activeCrisisOccurrence!!, "safe")
        assertEquals(committed, vm.state.value)
    }

    @Test fun extraConsultationsDoNotChangeSubsequentEventsOrClock() {
        val consulted = fixture(CountingRandom(9), news = { rescue })
        val direct = fixture(CountingRandom(9), news = { rescue })
        start(consulted); start(direct)
        repeat(2) {
            consulted.triggerCrisis(); direct.triggerCrisis()
            val active = consulted.state.value
            repeat(4) { consulted.requestAiAssessment(active.activeCrisis!!.id, active.activeCrisisOccurrence!!) }
            decide(consulted); decide(direct)
            assertEquals(direct.state.value.resources, consulted.state.value.resources)
            assertEquals(direct.state.value.crew, consulted.state.value.crew)
            assertEquals(direct.state.value.activeGoodNews, consulted.state.value.activeGoodNews)
            assertEquals(direct.state.value.missionHour, consulted.state.value.missionHour)
            assertEquals(direct.state.value.missionMinute, consulted.state.value.missionMinute)
            assertFalse(direct.state.value.lastDecisionReport!!.forecastConsulted)
            acknowledge(consulted); acknowledge(direct)
        }
    }

    @Test fun rewardDifferencesAreSavedAndAcknowledgmentNeverAppliesThemAgain() {
        var picks = 0
        val drain = CrisisOption("drain", "Drain water", waterDelta = -1000)
        val random = CountingRandom(42)
        val vm = fixture(random, listOf(safe, drain), news = { picks++; rescue })
        start(vm); vm.triggerCrisis(); decide(vm); acknowledge(vm)
        vm.triggerCrisis()
        val active = vm.state.value
        vm.requestAiAssessment(active.activeCrisis!!.id, active.activeCrisisOccurrence!!)
        val forecast = vm.state.value.aiForecasts.getValue("drain")
        assertEquals(setOf(CriticalResource.WATER), forecast.criticalReserves)
        decide(vm, "drain")
        val report = vm.state.value.lastDecisionReport!!
        assertEquals(0, report.forecast.outcome.resources.water)
        assertEquals(8, report.resourcesAfter.water)
        assertEquals(rescue, report.additionalEvent)
        assertTrue(criticalDepletion(report.resourcesAfter).isEmpty())
        val waiting = vm.state.value
        vm.dismissGoodNews() // The report must be read first.
        assertEquals(waiting, vm.state.value)
        val draws = random.draws
        acknowledge(vm)
        val resources = vm.state.value.resources
        vm.showLastDecisionReport()
        assertSame(report, vm.state.value.lastDecisionReport)
        acknowledge(vm); acknowledge(vm)
        assertEquals(resources, vm.state.value.resources)
        assertEquals(1, picks)
        assertEquals(draws, random.draws)
    }

    @Test fun terminalReportsRemainReadableBeforeSuccessAndFailureScreens() {
        val success = fixture()
        start(success)
        val total = success.state.value.targetCrisisCount
        repeat(total) { index ->
            success.triggerCrisis(); decide(success)
            if (index < total - 1) acknowledge(success)
        }
        assertEquals(Screen.WELCOME_HOME, success.state.value.currentScreen)
        assertTrue(success.state.value.showDecisionReport)
        assertEquals(success.state.value.result!!.finalResources, success.state.value.lastDecisionReport!!.resourcesAfter)
        acknowledge(success)
        assertEquals(Screen.WELCOME_HOME, success.state.value.currentScreen)
        success.continueFromWelcomeHome()
        success.showLastDecisionReport()
        assertTrue(success.state.value.showDecisionReport)

        val failure = fixture(options = listOf(CrisisOption("fatal", "Lose oxygen", oxygenDelta = -1000)))
        start(failure); failure.triggerCrisis(); decide(failure, "fatal")
        assertEquals(Screen.DEBRIEF, failure.state.value.currentScreen)
        assertTrue(failure.state.value.showDecisionReport)
        val report = failure.state.value.lastDecisionReport!!
        val snapshot = failure.state.value
        failure.dismissDecisionReport("foreign", report.forecast.occurrence)
        assertEquals(snapshot, failure.state.value)
        acknowledge(failure)
        assertEquals(report.resourcesAfter, failure.state.value.result!!.finalResources)
    }

    @Test fun staleReportCallbacksCannotCloseANewerOccurrence() {
        val vm = fixture()
        start(vm); vm.triggerCrisis(); decide(vm)
        val previous = vm.state.value.lastDecisionReport!!.forecast
        acknowledge(vm)
        vm.triggerCrisis(); decide(vm)
        val latest = vm.state.value
        assertNotEquals(previous.occurrence, latest.lastDecisionReport!!.forecast.occurrence)
        vm.dismissDecisionReport(previous.crisisId, previous.occurrence)
        assertEquals(latest, vm.state.value)
    }

    @Test fun languageChangesKeepForecastMechanicsAndRestartClearsAllReports() {
        val vm = GameViewModel(Random(19))
        start(vm); vm.triggerCrisis()
        val active = vm.state.value
        vm.requestAiAssessment(active.activeCrisis!!.id, active.activeCrisisOccurrence!!)
        val forecasts = vm.state.value.aiForecasts
        vm.setLanguage(AppLanguage.SPANISH)
        assertSame(forecasts, vm.state.value.aiForecasts)
        assertEquals(active.resources, vm.state.value.resources)
        assertNotNull(vm.state.value.aiAssessment)
        decide(vm, vm.state.value.activeCrisis!!.options.first().id)
        assertTrue(vm.state.value.showDecisionReport)
        vm.restart()
        assertNull(vm.state.value.lastDecisionReport)
        assertTrue(vm.state.value.aiForecasts.isEmpty())
        assertFalse(vm.state.value.showDecisionReport)
        assertEquals(AppLanguage.SPANISH, vm.state.value.currentLanguage)
    }
}
