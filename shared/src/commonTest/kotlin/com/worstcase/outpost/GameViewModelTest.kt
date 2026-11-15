package com.worstcase.outpost

import com.worstcase.outpost.audio.AudioManager
import com.worstcase.outpost.audio.SoundEffect
import com.worstcase.outpost.audio.SoundPlayer
import com.worstcase.outpost.data.MockData
import com.worstcase.outpost.domain.*
import com.worstcase.outpost.i18n.AppLanguage
import com.worstcase.outpost.i18n.Strings
import com.worstcase.outpost.i18n.description
import com.worstcase.outpost.presentation.GameUiState
import com.worstcase.outpost.presentation.GameViewModel
import com.worstcase.outpost.presentation.Screen
import com.worstcase.outpost.presentation.evaluateMissionEnd
import com.worstcase.outpost.presentation.missionLessons
import kotlin.random.Random
import kotlin.test.*

/** Written for static review only; these tests have not been executed. */
class GameViewModelTest {
    private class RecordingAudio : SoundPlayer {
        val events = mutableListOf<String>()
        override fun playSound(effect: SoundEffect) { events += effect.name }
        override fun startMusicLoop() { events += "startMusic" }
        override fun stopMusicLoop() { events += "stopMusic" }
    }

    private lateinit var audio: RecordingAudio
    private val safe = CrisisOption("safe", "Hold position")
    private val rescue = GoodNews(
        "resupply", "Resupply", "Supplies arrived", "Training fixture",
        sleepDelta = 4, stressDelta = -6, statusDelta = 7,
        moraleDelta = 6, waterDelta = 8, foodDelta = 2
    )
    private val neutralNews = rescue.copy(
        sleepDelta = 0, stressDelta = 0, statusDelta = 0,
        moraleDelta = 0, waterDelta = 0, foodDelta = 0
    )

    @BeforeTest
    fun setUp() {
        audio = RecordingAudio()
        AudioManager.initialize(audio)
        AudioManager.setSoundOn(true)
    }

    @AfterTest
    fun tearDown() {
        AudioManager.initialize(object : SoundPlayer {
            override fun playSound(effect: SoundEffect) {}
        })
        AudioManager.setSoundOn(true)
    }

    private fun fixture(
        options: List<CrisisOption> = listOf(safe),
        news: (AppLanguage) -> GoodNews = { neutralNews }
    ): GameViewModel = GameViewModel(
        random = Random(42),
        crisisCatalog = { _, _ -> listOf(Crisis("exercise", "Exercise", "Exercise", options)) },
        goodNewsPicker = news
    )

    /** Every mission, including scripted catalogs, initializes through the real design flow. */
    private fun startMission(
        vm: GameViewModel,
        difficulty: Difficulty = Difficulty.MID,
        destination: PlanetaryTarget = PlanetaryTarget.MOON,
        plan: MissionPlan = MissionPlan(),
        playerName: String = ""
    ): GameUiState {
        vm.finishIntro() // restart already returns HOME, where finishIntro is a no-op
        assertEquals(Screen.HOME, vm.state.value.currentScreen)
        vm.setPlayerName(playerName)
        vm.selectDifficulty(difficulty)
        assertEquals(Screen.DESTINATION, vm.state.value.currentScreen)
        vm.selectDestination(destination)
        assertEquals(Screen.ANALOG_SELECTION, vm.state.value.currentScreen)
        vm.selectAnalog(MockData.getAnalogs(vm.state.value.currentLanguage).first { it.planetaryTarget == destination })
        assertEquals(Screen.MISSION_DESIGN, vm.state.value.currentScreen)
        assertTrue(vm.state.value.crew.isEmpty())
        vm.updateMissionPlan(plan)
        vm.confirmMissionDesign()
        assertEquals(Screen.DASHBOARD, vm.state.value.currentScreen)
        assertTrue(vm.state.value.crew.isNotEmpty())
        return vm.state.value
    }

    private fun resolveActive(vm: GameViewModel, optionId: String = "safe") {
        val state = vm.state.value
        vm.resolveCrisis(assertNotNull(state.activeCrisis).id, assertNotNull(state.activeCrisisOccurrence), optionId)
        // Existing rule tests acknowledge the new presentation step explicitly.
        vm.state.value.lastDecisionReport?.let {
            vm.dismissDecisionReport(it.forecast.crisisId, it.forecast.occurrence)
        }
    }

    private fun turn(vm: GameViewModel, optionId: String = "safe"): GameUiState {
        vm.triggerCrisis()
        resolveActive(vm, optionId)
        return vm.state.value
    }

    private fun assertNoEffect(vm: GameViewModel, action: () -> Unit) {
        val before = vm.state.value
        val sounds = audio.events.toList()
        action()
        assertEquals(before, vm.state.value, "Rejected callbacks must leave the entire state intact")
        assertEquals(sounds, audio.events, "Rejected callbacks must not play or stop audio")
    }

    private fun assertHealth(state: GameUiState) {
        assertEquals(state.crew.sumOf { it.status } / state.crew.size, state.resources.crewHealth)
        assertTrue(state.crew.all { it.status in 0..100 && it.sleep in 0..100 && it.stress in 0..100 })
    }

    @Test
    fun initialStateAndInvalidStartupHaveNoEffects() {
        val vm = fixture()
        assertEquals(Screen.INTRO, vm.state.value.currentScreen)
        assertEquals(AppLanguage.ENGLISH, vm.state.value.currentLanguage)
        assertNull(vm.state.value.difficulty)
        assertNull(vm.state.value.selectedAnalog)
        assertNull(vm.state.value.result)
        assertEquals(0, vm.state.value.resources.crewHealth)
        assertNoEffect(vm) {
            vm.confirmMissionDesign()
            vm.triggerCrisis()
            vm.resolveCrisis("exercise", 1L, "safe")
            vm.requestEndMission()
            vm.requestAiAssessment("exercise", 1L)
        }
        vm.finishIntro()
        vm.selectDifficulty(Difficulty.MID)
        vm.selectDestination(PlanetaryTarget.MOON)
        assertNoEffect(vm) { vm.confirmMissionDesign() }
        val marsAnalog = MockData.getAnalogs(AppLanguage.ENGLISH).first { it.planetaryTarget == PlanetaryTarget.MARS }
        assertNoEffect(vm) { vm.selectAnalog(marsAnalog) }
    }

    @Test
    fun healthMeanRoundsDownAndEmptyCrewIsInvalid() {
        assertEquals(10, aggregateCrewHealth(listOf(CrewMember("a", "A", status = 10), CrewMember("b", "B", status = 11))))
        assertEquals(0, aggregateCrewHealth(emptyList()))
        assertFailsWith<IllegalArgumentException> { MockData.createInitialResources(emptyList()) }
        val vm = fixture()
        val started = startMission(vm)
        val empty = started.copy(crew = emptyList(), resources = started.resources.copy(crewHealth = 0))
        assertNull(evaluateMissionEnd(empty, withdrawalRequested = true))
    }

    @Test
    fun designInitializesResourcesAndOneHealthAggregateAtEveryDifficultyAndDestination() {
        for (difficulty in Difficulty.entries) {
            for (destination in PlanetaryTarget.entries) {
                val state = startMission(fixture(), difficulty, destination)
                assertHealth(state)
                val days = if (destination == PlanetaryTarget.MOON) 21 else 90
                val margin = when (difficulty) {
                    Difficulty.JUNIOR -> 10
                    Difficulty.MID -> 6
                    Difficulty.SENIOR -> 3
                }
                assertEquals(days + margin - 1, state.resources.foodDays) // Prepared reuse costs one ration-day.
                assertEquals(if (destination == PlanetaryTarget.MOON) 7 else 12, state.targetCrisisCount)
            }
        }
    }

    @Test
    fun habitatAndPriorityHealthBenefitsApplyOnceToEachMember() {
        fun crew(difficulty: Difficulty, priority: PriorityPlan, habitat: HabitatPlan) = MockData.createInitialCrew(
            difficulty, "Trainee", AppLanguage.ENGLISH, priority, habitat, Random(9)
        )
        for (difficulty in Difficulty.entries) {
            val baseline = crew(difficulty, PriorityPlan.SCIENCE, HabitatPlan.STANDARD)
            val crewPriority = crew(difficulty, PriorityPlan.CREW, HabitatPlan.STANDARD)
            val hardened = crew(difficulty, PriorityPlan.SCIENCE, HabitatPlan.HARDENED)
            val both = crew(difficulty, PriorityPlan.CREW, HabitatPlan.HARDENED)
            baseline.indices.forEach { i ->
                assertEquals((baseline[i].status + 5).coerceAtMost(100), crewPriority[i].status)
                assertEquals((baseline[i].status + 4).coerceAtMost(100), hardened[i].status)
                assertEquals((baseline[i].status + 9).coerceAtMost(100), both[i].status)
            }
        }
        assertTrue(aggregateCrewHealth(crew(Difficulty.MID, PriorityPlan.SCIENCE, HabitatPlan.STANDARD)) >
            aggregateCrewHealth(crew(Difficulty.SENIOR, PriorityPlan.SCIENCE, HabitatPlan.STANDARD)))
    }

    @Test
    fun realCatalogChoiceAdvancesClockAndKeepsHealthConsistent() {
        val vm = GameViewModel(Random(19))
        val before = startMission(vm, playerName = "Pilot")
        vm.triggerCrisis()
        val crisis = assertNotNull(vm.state.value.activeCrisis)
        resolveActive(vm, crisis.options.first().id)
        val after = vm.state.value
        assertEquals(listOf(crisis.id), after.crisesFaced)
        assertTrue(after.missionDay > before.missionDay)
        assertEquals(before.resources.foodDays + crisis.options.first().foodDelta -
            (after.missionDay - before.missionDay), after.resources.foodDays)
        assertFalse(after.showCrisisDialog)
        assertNull(after.activeCrisis)
        assertNull(after.activeCrisisOccurrence)
        assertHealth(after)
    }

    @Test
    fun difficultyAndTraineeModifiersApplyHealthEffectsOnlyOnce() {
        val losses = mutableListOf<Int>()
        for (difficulty in Difficulty.entries) {
            val option = CrisisOption("cost", "Cost", healthDelta = -20, energyDelta = -20)
            val vm = fixture(listOf(option))
            val before = startMission(vm, difficulty, playerName = "Pilot")
            val after = turn(vm, "cost")
            val player = after.crew.first { it.id == "player" }
            val expectedPlayerLoss = when (difficulty) {
                Difficulty.JUNIOR -> 17 // -13 * 1.35, truncated; no hidden quality penalty.
                Difficulty.MID -> 27
                Difficulty.SENIOR -> 35
            }
            assertEquals(before.crew.first { it.id == "player" }.status - expectedPlayerLoss, player.status)
            assertHealth(after)
            losses += before.resources.energy - after.resources.energy
        }
        assertTrue(losses[0] < losses[1])
        assertTrue(losses[1] < losses[2])
    }

    @Test
    fun neutralChoiceDoesNotInventRecoveryFromZeroDeltas() {
        val vm = fixture()
        val before = startMission(vm)
        val after = turn(vm)
        before.crew.zip(after.crew).forEach { (a, b) ->
            assertEquals(a.status, b.status)
            assertTrue(b.sleep < a.sleep)
            assertTrue(b.stress > a.stress)
        }
        assertHealth(after)
    }

    @Test
    fun inventedForeignDisabledAndDuplicatedOptionsHaveNoEffects() {
        val heritage = CrisisOption("heritage", "Reuse", usesAbandonedHardware = true, waterDelta = 100)
        val vm = fixture(listOf(safe, heritage))
        startMission(vm, plan = MissionPlan(planHardwareReuse = false))
        assertNoEffect(vm) { vm.resolveCrisis("exercise", 1L, "safe") } // no active crisis
        vm.triggerCrisis()
        val active = vm.state.value
        val occurrence = assertNotNull(active.activeCrisisOccurrence)
        assertEquals(listOf("safe"), assertNotNull(active.activeCrisis).options.map { it.id })
        assertNoEffect(vm) { vm.triggerCrisis() } // do not replace a pending decision
        assertNoEffect(vm) { vm.resolveCrisis("exercise", occurrence, "invented") }
        assertNoEffect(vm) { vm.resolveCrisis("other_crisis", occurrence, "safe") }
        assertNoEffect(vm) { vm.resolveCrisis("exercise", occurrence, "heritage") }
        assertNoEffect(vm) { vm.requestEndMission() } // unresolved decision
        vm.requestAiAssessment("exercise", occurrence)
        assertNotNull(vm.state.value.aiAssessment)
        resolveActive(vm)
        assertNull(vm.state.value.aiAssessment)
        assertNoEffect(vm) { vm.resolveCrisis("exercise", occurrence, "safe") }
        assertNoEffect(vm) { vm.requestAiAssessment("exercise", occurrence) }
        assertEquals(1, vm.state.value.crisesFaced.size)
    }

    @Test
    fun languageChangeCannotRestoreDisabledHardwareOrChangeOccurrence() {
        val vm = GameViewModel(Random(7))
        startMission(vm, plan = MissionPlan(planHardwareReuse = false))
        vm.triggerCrisis()
        val before = vm.state.value
        vm.setLanguage(AppLanguage.SPANISH)
        val after = vm.state.value
        assertEquals(before.activeCrisisOccurrence, after.activeCrisisOccurrence)
        assertEquals(before.activeCrisis!!.options.map { it.id }, after.activeCrisis!!.options.map { it.id })
        assertTrue(assertNotNull(after.activeCrisis).options.none { it.usesAbandonedHardware })
        assertEquals(before.resources, after.resources)
        assertEquals(before.crew, after.crew)
        assertEquals(MockData.getAnalogs(AppLanguage.SPANISH).first { it.id == before.selectedAnalog!!.id }.name,
            after.selectedAnalog!!.name)
        assertNoEffect(vm) { vm.updateMissionPlan(MissionPlan(planHardwareReuse = true)) }
    }

    @Test
    fun staleCallbacksCannotResolveRepeatedCatalogIdsEvenAfterRestart() {
        val vm = fixture()
        startMission(vm)
        vm.triggerCrisis()
        val old = assertNotNull(vm.state.value.activeCrisisOccurrence)
        resolveActive(vm)
        vm.triggerCrisis() // same crisis and option ids, different occurrence
        assertNotEquals(old, vm.state.value.activeCrisisOccurrence)
        assertNoEffect(vm) { vm.resolveCrisis("exercise", old, "safe") }
        assertNoEffect(vm) { vm.requestAiAssessment("exercise", old) }
        vm.restart()
        startMission(vm)
        vm.triggerCrisis()
        assertNotEquals(old, vm.state.value.activeCrisisOccurrence)
        assertNoEffect(vm) { vm.resolveCrisis("exercise", old, "safe") }
    }

    @Test
    fun sameOptionIdInAnotherCrisisDoesNotAuthorizeOldCallback() {
        val vm = GameViewModel(Random(1), crisisCatalog = { _, _ ->
            listOf(Crisis("one", "One", "One", listOf(safe)), Crisis("two", "Two", "Two", listOf(safe)))
        }, goodNewsPicker = { neutralNews })
        startMission(vm)
        vm.triggerCrisis()
        val first = vm.state.value
        resolveActive(vm)
        vm.triggerCrisis()
        assertNotEquals(first.activeCrisis!!.id, vm.state.value.activeCrisis!!.id)
        assertNoEffect(vm) { vm.resolveCrisis(assertNotNull(first.activeCrisis).id, first.activeCrisisOccurrence!!, "safe") }
    }

    @Test
    fun newsIsDrawnAndAppliedOnceBeforeAcknowledgmentAndBlocksOtherActions() {
        var picks = 0
        val vm = fixture(news = { picks++; rescue })
        startMission(vm)
        turn(vm)
        val before = vm.state.value
        val rewarded = turn(vm)
        assertEquals(1, picks)
        assertHealth(rewarded)
        assertTrue(rewarded.showGoodNewsDialog)
        before.crew.zip(rewarded.crew).forEach { (a, b) ->
            assertEquals((a.status + 7).coerceAtMost(100), b.status)
        }
        assertEquals(before.resources.water - 3 + 8, rewarded.resources.water)
        assertEquals(before.resources.foodDays - (rewarded.missionDay - before.missionDay) + 2, rewarded.resources.foodDays)
        assertNoEffect(vm) {
            vm.triggerCrisis()
            vm.resolveCrisis("exercise", 2L, "safe")
            vm.requestEndMission()
            vm.confirmMissionDesign()
        }
        vm.dismissGoodNews()
        assertEquals(rewarded.resources, vm.state.value.resources)
        assertEquals(rewarded.crew, vm.state.value.crew)
        assertEquals(rewarded.lastCrewDeltas, vm.state.value.lastCrewDeltas)
        assertNull(vm.state.value.activeGoodNews)
        assertNull(vm.state.value.result)
        assertNoEffect(vm) { vm.dismissGoodNews() }
        assertEquals(1, picks)
    }

    @Test
    fun defeatUsesDefinitiveIndividualHealthWithoutHiddenChoiceDamage() {
        val option = CrisisOption("health_loss", "Health loss", healthDelta = -100)
        val vm = fixture(listOf(safe, option), news = { neutralNews.copy(statusDelta = 100) })
        startMission(vm)
        turn(vm)
        turn(vm)
        vm.dismissGoodNews()
        assertTrue(vm.state.value.crew.all { it.status == 100 })
        val after = turn(vm, "health_loss") // Exactly the encoded health loss, with no hidden penalty.
        assertTrue(after.crew.all { it.status == 0 })
        assertEquals(0, after.resources.crewHealth)
        val result = assertNotNull(after.result)
        assertEquals(MissionEndReason.CriticalDepletion(setOf(CriticalResource.CREW_HEALTH)), result.endReason)
        assertEquals(after.resources, result.finalResources)
    }

    @Test
    fun depletionOfEachCriticalReserveProducesStructuredFailure() {
        val cases = listOf(
            CriticalResource.CREW_HEALTH to CrisisOption("fatal", "Fatal", healthDelta = -1000),
            CriticalResource.OXYGEN to CrisisOption("fatal", "Fatal", oxygenDelta = -1000),
            CriticalResource.WATER to CrisisOption("fatal", "Fatal", waterDelta = -1000),
            CriticalResource.FOOD to CrisisOption("fatal", "Fatal", foodDelta = -1000)
        )
        for ((resource, option) in cases) {
            val vm = fixture(listOf(option))
            startMission(vm)
            val after = turn(vm, "fatal")
            assertEquals(Screen.DEBRIEF, after.currentScreen)
            val result = assertNotNull(after.result)
            assertEquals(MissionEndReason.CriticalDepletion(setOf(resource)), result.endReason)
            assertFalse(result.isSuccess)
            assertEquals(after.resources, result.finalResources)
            assertNoEffect(vm) { vm.triggerCrisis(); vm.requestEndMission() }
        }
    }

    @Test
    fun simultaneousDepletionStoresAndTranslatesOnlyTheExhaustedReserves() {
        val vm = fixture(listOf(CrisisOption("fatal", "Fatal", waterDelta = -1000, oxygenDelta = -1000)))
        startMission(vm)
        val after = turn(vm, "fatal")
        val result = assertNotNull(after.result)
        assertEquals(MissionEndReason.CriticalDepletion(setOf(CriticalResource.WATER, CriticalResource.OXYGEN)),
            result.endReason)
        for (language in AppLanguage.entries) {
            val texts = Strings.get(language)
            val message = texts.description(result.endReason)
            assertTrue(message.contains(texts.water))
            assertTrue(message.contains(texts.oxygen))
            assertFalse(message.contains(texts.food))
            assertFalse(message.contains(texts.crewHealth))
        }
    }

    @Test
    fun energyAndMoraleAreNotNewDefeatConditions() {
        val vm = GameViewModel(Random(42), crisisCatalog = { _, _ ->
            listOf(Crisis("dust_storm_filter", "Dust", "Dust", listOf(
                CrisisOption("low", "Low", energyDelta = -1000, moraleDelta = -1000)
            )))
        }, goodNewsPicker = { neutralNews })
        startMission(vm, plan = MissionPlan(power = PowerPlan.SOLAR, habitat = HabitatPlan.HARDENED))
        val after = turn(vm, "low")
        assertEquals(0, after.resources.energy)
        assertEquals(0, after.resources.morale)
        assertNull(after.result)
        assertEquals(Screen.DASHBOARD, after.currentScreen)
    }

    @Test
    fun earlyWithdrawalIsNeitherSuccessNorCriticalDepletionInAnyLanguage() {
        val vm = fixture()
        val started = startMission(vm)
        vm.setPanoramaViewerVisible(true)
        vm.requestEndMission()
        val result = assertNotNull(vm.state.value.result)
        assertEquals(MissionEndReason.Withdrawal, result.endReason)
        assertFalse(result.isSuccess)
        assertEquals(started.resources, result.finalResources)
        assertFalse(vm.state.value.showPanoramaViewer)
        for (language in AppLanguage.entries) {
            val texts = Strings.get(language)
            assertEquals(texts.lessonWithdrawalCore, texts.description(result.endReason))
            assertEquals(texts.lessonWithdrawalCore, missionLessons(result, language).first().first)
            assertTrue(texts.missionStatusWithdrawn != texts.missionStatusFailed)
            assertFalse(result.lessons.contains(texts.lessonFailureCore))
        }
    }

    @Test
    fun completionRequiresAllCrisesPositiveReservesAndNoUnresolvedDecision() {
        val vm = fixture()
        val started = startMission(vm)
        assertNull(evaluateMissionEnd(started))
        val complete = started.copy(crisesFaced = List(started.targetCrisisCount) { "exercise" })
        assertEquals(MissionEndReason.Completed, evaluateMissionEnd(complete))
        assertNull(evaluateMissionEnd(complete.copy(activeCrisis = Crisis("pending", "Pending", "Pending", listOf(safe)))))
        assertNull(evaluateMissionEnd(complete.copy(showCrisisDialog = true)))
        assertEquals(MissionEndReason.CriticalDepletion(setOf(CriticalResource.WATER)),
            evaluateMissionEnd(complete.copy(resources = complete.resources.copy(water = 0))))
        repeat(started.targetCrisisCount) { index ->
            turn(vm)
            if (index < started.targetCrisisCount - 1) {
                assertNull(vm.state.value.result)
                vm.dismissGoodNews()
            }
        }
        assertEquals(Screen.WELCOME_HOME, vm.state.value.currentScreen)
        assertTrue(vm.state.value.result!!.isSuccess)
        assertEquals(started.targetCrisisCount, vm.state.value.result!!.crisesFaced.size)
        assertEquals(vm.state.value.resources, vm.state.value.result!!.finalResources)
        assertNoEffect(vm) { vm.triggerCrisis(); vm.requestEndMission() }
        vm.continueFromWelcomeHome()
        assertEquals(Screen.DEBRIEF, vm.state.value.currentScreen)
    }

    @Test
    fun lastTurnRewardCanRescueHealthWaterAndFoodBeforeVictoryAndIsNeverRepeated() {
        var picks = 0
        val fatal = CrisisOption("rescuable", "Rescuable", healthDelta = -1000, waterDelta = -1000, foodDelta = -1000)
        val vm = fixture(listOf(safe, fatal), news = { picks++; rescue })
        val started = startMission(vm, destination = PlanetaryTarget.MARS)
        repeat(started.targetCrisisCount - 1) { turn(vm); vm.dismissGoodNews() }
        val rewarded = turn(vm, "rescuable")
        assertEquals(6, picks)
        assertEquals(7, rewarded.resources.crewHealth)
        assertEquals(8, rewarded.resources.water)
        assertEquals(2, rewarded.resources.foodDays)
        assertHealth(rewarded)
        assertTrue(rewarded.showGoodNewsDialog)
        assertNull(rewarded.result) // allow the message to be read before the celebration
        assertNoEffect(vm) { vm.triggerCrisis(); vm.requestEndMission() }
        vm.dismissGoodNews()
        val result = assertNotNull(vm.state.value.result)
        assertEquals(MissionEndReason.Completed, result.endReason)
        assertEquals(rewarded.resources, result.finalResources)
        assertEquals(rewarded.crew, vm.state.value.crew)
        assertEquals(Screen.WELCOME_HOME, vm.state.value.currentScreen)
        assertNoEffect(vm) { vm.dismissGoodNews() }
        assertEquals(6, picks)
    }

    @Test
    fun insufficientLastTurnRewardStillFailsOnDefinitiveSnapshot() {
        val fatal = CrisisOption("oxygen_loss", "Oxygen loss", oxygenDelta = -1000)
        val vm = fixture(listOf(safe, fatal), news = { rescue })
        val started = startMission(vm, destination = PlanetaryTarget.MARS)
        repeat(started.targetCrisisCount - 1) { turn(vm); vm.dismissGoodNews() }
        val rewarded = turn(vm, "oxygen_loss")
        assertTrue(rewarded.showGoodNewsDialog)
        assertEquals(0, rewarded.resources.oxygen)
        vm.dismissGoodNews()
        val result = assertNotNull(vm.state.value.result)
        assertEquals(MissionEndReason.CriticalDepletion(setOf(CriticalResource.OXYGEN)), result.endReason)
        assertEquals(rewarded.resources, result.finalResources)
        assertEquals(Screen.DEBRIEF, vm.state.value.currentScreen)
    }

    @Test
    fun rewardCanRescueAnIncompleteMissionWithoutEndingIt() {
        val fatal = CrisisOption("water_loss", "Water loss", waterDelta = -1000)
        val vm = fixture(listOf(safe, fatal), news = { rescue })
        startMission(vm)
        turn(vm)
        val rewarded = turn(vm, "water_loss")
        assertEquals(8, rewarded.resources.water)
        vm.dismissGoodNews()
        assertNull(vm.state.value.result)
        assertEquals(Screen.DASHBOARD, vm.state.value.currentScreen)
        vm.triggerCrisis()
        assertNotNull(vm.state.value.activeCrisis)
    }

    @Test
    fun preparationBackKeepsDraftAndPreferencesThenStartsTheRevisedMission() {
        val vm = fixture()
        val plan = MissionPlan(PowerPlan.NUCLEAR, HabitatPlan.HARDENED, PriorityPlan.SCIENCE, false)
        vm.finishIntro()
        vm.setLanguage(AppLanguage.SPANISH)
        vm.setPlayerName("Pilot")
        vm.setSoundEnabled(false)
        vm.selectDifficulty(Difficulty.MID)
        vm.selectDestination(PlanetaryTarget.MOON)
        val moonSite = MockData.getAnalogs(AppLanguage.SPANISH).first { it.planetaryTarget == PlanetaryTarget.MOON }
        vm.selectAnalog(moonSite)
        vm.updateMissionPlan(plan)

        vm.navigateBack(Screen.MISSION_DESIGN)
        assertEquals(Screen.ANALOG_SELECTION, vm.state.value.currentScreen)
        assertEquals(plan, vm.state.value.missionPlan)
        assertEquals(moonSite, vm.state.value.selectedAnalog)
        assertTrue(vm.state.value.crew.isEmpty())
        assertNoEffect(vm) { vm.navigateBack(Screen.MISSION_DESIGN) } // double tap from old UI
        vm.navigateBack(Screen.ANALOG_SELECTION)
        vm.navigateBack(Screen.DESTINATION)
        assertEquals(Screen.HOME, vm.state.value.currentScreen)
        vm.selectDifficulty(Difficulty.SENIOR)
        vm.selectDestination(PlanetaryTarget.MARS)
        assertNull(vm.state.value.selectedAnalog)
        assertNoEffect(vm) { vm.confirmMissionDesign() }
        assertNoEffect(vm) { vm.selectAnalog(moonSite) }
        val marsSite = MockData.getAnalogs(AppLanguage.SPANISH).first { it.planetaryTarget == PlanetaryTarget.MARS }
        vm.selectAnalog(marsSite)
        vm.confirmMissionDesign()

        val started = vm.state.value
        assertEquals(Screen.DASHBOARD, started.currentScreen)
        assertEquals(Difficulty.SENIOR, started.difficulty)
        assertEquals(marsSite, started.selectedAnalog)
        assertEquals(plan, started.missionPlan)
        assertEquals(12, started.targetCrisisCount)
        assertEquals(90, started.totalMissionDays)
        assertEquals(AppLanguage.SPANISH, started.currentLanguage)
        assertEquals("Pilot", started.playerName)
        assertFalse(started.soundEnabled)
        assertHealth(started)
    }

    @Test
    fun reselectingTheSameDestinationKeepsTheSiteAndDesignDraft() {
        val vm = fixture()
        vm.finishIntro()
        vm.selectDifficulty(Difficulty.JUNIOR)
        vm.selectDestination(PlanetaryTarget.MOON)
        val site = MockData.getAnalogs(AppLanguage.ENGLISH).first { it.planetaryTarget == PlanetaryTarget.MOON }
        vm.selectAnalog(site)
        val plan = MissionPlan(habitat = HabitatPlan.HARDENED)
        vm.updateMissionPlan(plan)
        vm.navigateBack(Screen.MISSION_DESIGN)
        vm.navigateBack(Screen.ANALOG_SELECTION)
        vm.selectDestination(PlanetaryTarget.MOON)
        assertEquals(site, vm.state.value.selectedAnalog)
        vm.selectAnalog(site)
        assertEquals(plan, vm.state.value.missionPlan)
        vm.confirmMissionDesign()
        assertEquals(Screen.DASHBOARD, vm.state.value.currentScreen)
    }

    @Test
    fun backCannotNavigateBehindSettingsOrEscapeAnActiveOrFinishedMission() {
        val vm = fixture()
        assertNoEffect(vm) { Screen.entries.forEach { vm.navigateBack(it) } }
        vm.finishIntro()
        vm.selectDifficulty(Difficulty.MID)
        vm.setShowSettings(true)
        assertNoEffect(vm) { vm.navigateBack(Screen.DESTINATION) }
        vm.setShowSettings(false)
        vm.navigateBack(Screen.DESTINATION)
        startMission(vm)
        assertNoEffect(vm) { Screen.entries.forEach { vm.navigateBack(it) } }
        vm.triggerCrisis()
        assertNoEffect(vm) { Screen.entries.forEach { vm.navigateBack(it) } }
        resolveActive(vm)
        vm.requestEndMission()
        assertNotNull(vm.state.value.result)
        assertNoEffect(vm) { Screen.entries.forEach { vm.navigateBack(it) } }
    }

    @Test
    fun restartClearsDialogsAdviceResultsAndDeltasWhilePreservingPreferences() {
        val vm = fixture(news = { rescue })
        startMission(vm, playerName = "Pilot")
        turn(vm)
        turn(vm)
        assertNotNull(vm.state.value.activeGoodNews)
        vm.setLanguage(AppLanguage.SPANISH)
        vm.setSoundEnabled(false)
        vm.setShowSettings(true)
        vm.setPanoramaViewerVisible(true)
        vm.restart()
        assertEquals(GameUiState(currentLanguage = AppLanguage.SPANISH, playerName = "Pilot",
            soundEnabled = false, currentScreen = Screen.HOME), vm.state.value)
        startMission(vm)
        vm.triggerCrisis()
        val occurrence = assertNotNull(vm.state.value.activeCrisisOccurrence)
        vm.requestAiAssessment("exercise", occurrence)
        assertNotNull(vm.state.value.aiAssessment)
        vm.restart()
        assertNull(vm.state.value.activeCrisis)
        assertNull(vm.state.value.activeCrisisOccurrence)
        assertNull(vm.state.value.aiAssessment)
        startMission(vm)
        vm.requestEndMission()
        assertNotNull(vm.state.value.result)
        vm.restart()
        assertNull(vm.state.value.result)
        assertTrue(vm.state.value.lastCrewDeltas.isEmpty())
        assertTrue(vm.state.value.crew.isEmpty())
    }
}
