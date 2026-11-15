package com.worstcase.outpost

import com.worstcase.outpost.data.MockData
import com.worstcase.outpost.domain.*
import com.worstcase.outpost.i18n.AppLanguage
import com.worstcase.outpost.presentation.GameViewModel
import com.worstcase.outpost.presentation.Screen
import kotlin.random.Random
import kotlin.test.*

/** Authored for this balance revision; not executed as part of the change. */
class MissionRulesTest {
    private val crew = listOf(
        CrewMember("astro1", "Crew", sleep = 40, stress = 60, status = 70),
        CrewMember("player", "Trainee", sleep = 40, stress = 60, status = 70)
    )
    private val reserves = Resources(energy = 60, water = 80, oxygen = 80, foodDays = 30, crewHealth = 70, morale = 60)

    private fun resolve(
        option: CrisisOption,
        difficulty: Difficulty = Difficulty.MID,
        plan: MissionPlan = MissionPlan(),
        crisisId: String = "medical_fatigue",
        resources: Resources = reserves
    ) = calculateDecision(resources, crew, option, crisisId, difficulty, plan, 3, 21, 7, Triple(0, 0, 0))

    @Test fun sleepShiftRestoresEveryCrewMemberAtEveryDifficultyAndPaysItsCosts() {
        val crisis = MockData.getCrises(AppLanguage.ENGLISH).single { it.id == "medical_fatigue" }
        val sleep = crisis.options.single { it.id == "sleep_shift" }
        val share = crisis.options.single { it.id == "share_load" }
        for (difficulty in Difficulty.entries) {
            val outcome = resolve(sleep, difficulty)
            outcome.crew.forEach {
                assertEquals(60, it.sleep)
                assertEquals(45, it.stress)
                assertEquals(74, it.status)
            }
            assertEquals(27, outcome.resources.foodDays) // Two calendar days and one extra ration-day.
            assertEquals(0, outcome.scienceGain)
            val redistributed = resolve(share, difficulty)
            assertEquals(48, redistributed.crew.first().sleep)
            assertEquals(52, redistributed.crew.first().stress)
            assertEquals(2, redistributed.scienceGain)
            assertTrue(redistributed.resources.foodDays > outcome.resources.foodDays)
        }
    }

    @Test fun resourceGainsAndHeritageTagsDoNotInventCrewRecovery() {
        val neutral = CrisisOption("neutral", "Neutral", sleepDelta = 0, stressDelta = 0)
        val recovered = neutral.copy(waterDelta = 20, oxygenDelta = 10, usesAbandonedHardware = true)
        assertEquals(crew, resolve(neutral).crew)
        assertEquals(crew, resolve(recovered).crew)
        assertEquals(resolve(recovered).crew, resolve(recovered.copy(usesAbandonedHardware = false)).crew)
    }

    @Test fun harmfulChangesScaleOnceWhileBenefitsAndLimitsStayExplicit() {
        assertEquals(0, decisionDelta(-1, Difficulty.JUNIOR))
        assertEquals(-13, decisionDelta(-20, Difficulty.JUNIOR))
        assertEquals(-35, decisionDelta(-20, Difficulty.SENIOR, trainee = true))
        assertEquals(12, decisionDelta(12, Difficulty.SENIOR))
        assertEquals(-15, decisionDelta(-15, Difficulty.SENIOR, stress = true, trainee = true))
        assertEquals(15, decisionDelta(12, Difficulty.SENIOR, stress = true))
        val capped = resolve(CrisisOption("cap", "Cap", sleepDelta = 1000, stressDelta = -1000, healthDelta = 1000))
        assertTrue(capped.crew.all { it.sleep == 100 && it.stress == 0 && it.status == 100 })
        val exhausted = resolve(CrisisOption("loss", "Loss", sleepDelta = -1000, stressDelta = 1000, healthDelta = -1000))
        assertTrue(exhausted.crew.all { it.sleep == 0 && it.stress == 100 && it.status == 0 })
    }

    @Test fun powerAndHabitatHaveDistinctOperatingCostsAndWeatherResponse() {
        val option = CrisisOption("work", "Work")
        val solar = resolve(option, plan = MissionPlan(power = PowerPlan.SOLAR))
        val storm = resolve(option, plan = MissionPlan(power = PowerPlan.SOLAR), crisisId = "dust_storm_filter")
        val nuclear = resolve(option, plan = MissionPlan(power = PowerPlan.NUCLEAR))
        val nuclearStorm = resolve(option, plan = MissionPlan(power = PowerPlan.NUCLEAR), crisisId = "solar_storm")
        assertEquals(66, solar.resources.energy)
        assertEquals(62, storm.resources.energy)
        assertEquals(nuclear.resources.energy, nuclearStorm.resources.energy)
        assertEquals(solar.resources.water - 1, nuclear.resources.water)
        val standard = resolve(option)
        val hardened = resolve(option, plan = MissionPlan(habitat = HabitatPlan.HARDENED))
        val minimal = resolve(option, plan = MissionPlan(habitat = HabitatPlan.MINIMAL))
        assertEquals(standard.resources.water + 1, hardened.resources.water)
        assertEquals(standard.resources.oxygen + 1, hardened.resources.oxygen)
        assertEquals(standard.resources.energy - 2, hardened.resources.energy)
        assertEquals(standard.resources.water - 1, minimal.resources.water)
    }

    @Test fun initialBenefitsHaveRealReserveCostsAndAreClampedAfterSummation() {
        fun initial(plan: MissionPlan) = MockData.createInitialResources(crew, Difficulty.MID, plan, 21)
        val standard = initial(MissionPlan(planHardwareReuse = false))
        val hardened = initial(MissionPlan(habitat = HabitatPlan.HARDENED, planHardwareReuse = false))
        val minimal = initial(MissionPlan(habitat = HabitatPlan.MINIMAL, planHardwareReuse = false))
        assertTrue(hardened.oxygen > standard.oxygen)
        assertTrue(hardened.energy < standard.energy)
        assertTrue(hardened.foodDays < standard.foodDays)
        assertTrue(minimal.foodDays > standard.foodDays)
        assertTrue(minimal.oxygen < standard.oxygen)
        val reuse = initial(MissionPlan())
        assertEquals(standard.energy - 4, reuse.energy)
        assertEquals(standard.foodDays - 1, reuse.foodDays)
        val junior = MockData.createInitialResources(crew, Difficulty.JUNIOR,
            MissionPlan(PowerPlan.NUCLEAR, HabitatPlan.HARDENED, PriorityPlan.SCIENCE, true))
        assertEquals(92, junior.energy) // 100 + 10 - 10 - 4 - 4, one final cap.
    }

    @Test fun scienceHasAnObservableBenefitAndFinalScoresStayWithinOneHundred() {
        val option = CrisisOption("work", "Work")
        val ordinary = resolve(option)
        val science = resolve(option, plan = MissionPlan(priority = PriorityPlan.SCIENCE))
        assertEquals(2, ordinary.scienceGain)
        assertEquals(6, science.scienceGain)
        assertTrue(missionScore(science.resources, 42, 42, true) > missionScore(ordinary.resources, 14, 42, true))
        val perfect = Resources(energy = 100, water = 100, oxygen = 100, crewHealth = 100, morale = 100)
        assertEquals(100, missionScore(perfect, 42, 42, true))
        assertEquals(50, missionScore(perfect, 42, 42, false))
        assertEquals(85, missionScore(perfect, 0, 42, true))
    }

    @Test fun shelterAndOrdinaryRepairsHaveExplicitBenefitsAndCosts() {
        val crisis = MockData.getCrises(AppLanguage.ENGLISH).single { it.id == "solar_storm" }
        val shelter = resolve(crisis.options.single { it.id == "shelter" }, crisisId = crisis.id)
        val salvage = resolve(crisis.options.single { it.id == "abandoned_opportunity" }, crisisId = crisis.id)
        assertEquals(70, shelter.resources.crewHealth)
        assertTrue(salvage.resources.crewHealth < shelter.resources.crewHealth)
        assertTrue(salvage.resources.energy > shelter.resources.energy)
        assertTrue(shelter.scienceGain < salvage.scienceGain)
        val waterCrisis = MockData.getCrises(AppLanguage.ENGLISH).single { it.id == "water_recycling" }
        val repair = resolve(waterCrisis.options.single { it.id == "repair" }, crisisId = waterCrisis.id)
        val heritage = resolve(waterCrisis.options.single { it.id == "abandoned_surveyor" }, crisisId = waterCrisis.id)
        assertEquals(93, repair.resources.water)
        assertEquals(51, repair.resources.energy)
        assertEquals(70, repair.resources.crewHealth)
        assertTrue(heritage.resources.water > repair.resources.water)
        assertTrue(heritage.resources.energy < repair.resources.energy)
        val scrubber = MockData.getCrises(AppLanguage.ENGLISH).single { it.id == "system_failure" }
        val standard = resolve(scrubber.options.single { it.id == "standard_repair" }, crisisId = scrubber.id)
        assertTrue(standard.resources.oxygen > reserves.oxygen)
        assertTrue(standard.resources.energy < reserves.energy)
    }

    @Test fun englishAndSpanishCatalogsHaveIdenticalMechanicalEffects() {
        val spanish = MockData.getCrises(AppLanguage.SPANISH).associateBy { it.id }
        for (english in MockData.getCrises(AppLanguage.ENGLISH)) {
            val translated = spanish.getValue(english.id).options.associateBy { it.id }
            for (option in english.options) {
                assertEquals(option.copy(text = "", hardwareHistory = null),
                    translated.getValue(option.id).copy(text = "", hardwareHistory = null))
            }
        }
    }

    @Test fun committedSleepDecisionMatchesPureOutcomeAndAdvisorUsesTheSameEffects() {
        val medical = MockData.getCrises(AppLanguage.ENGLISH).single { it.id == "medical_fatigue" }
        val vm = GameViewModel(Random(7), crisisCatalog = { _, _ -> listOf(medical) })
        vm.finishIntro(); vm.selectDifficulty(Difficulty.SENIOR); vm.selectDestination(PlanetaryTarget.MOON)
        vm.selectAnalog(MockData.getAnalogs(AppLanguage.ENGLISH).first { it.planetaryTarget == PlanetaryTarget.MOON })
        vm.confirmMissionDesign(); vm.triggerCrisis()
        val before = vm.state.value
        val option = before.activeCrisis!!.options.single { it.id == "sleep_shift" }
        val predicted = calculateDecision(before.resources, before.crew, option, medical.id,
            before.difficulty!!, before.missionPlan, before.missionDay, before.totalMissionDays,
            before.targetCrisisCount, MockData.analogRiskTax(before.selectedAnalog!!.id))
        vm.requestAiAssessment(medical.id, before.activeCrisisOccurrence!!)
        val firstAdvice = vm.state.value.aiAssessment
        vm.requestAiAssessment(medical.id, before.activeCrisisOccurrence!!)
        assertEquals(firstAdvice, vm.state.value.aiAssessment)
        assertEquals(before.resources, vm.state.value.resources)
        assertEquals(before.crew, vm.state.value.crew)
        vm.resolveCrisis(medical.id, before.activeCrisisOccurrence!!, option.id)
        assertEquals(predicted.resources, vm.state.value.resources)
        assertEquals(predicted.crew, vm.state.value.crew)
        assertEquals(predicted.scienceGain, vm.state.value.sciencePoints)
        val lowCrew = reserves.copy(crewHealth = 70)
        val outcomes = medical.options.associate { it.id to resolve(it, resources = lowCrew) }
        assertEquals("sleep_shift", MockData.evaluateAiCopilot(medical, lowCrew, AppLanguage.ENGLISH, outcomes).recommendedOptionId)
    }

    @Test fun scriptedRealCatalogRouteCoversEachDestinationAndDifficulty() {
        val choices = mapOf(
            "water_recycling" to "abandoned_surveyor", "solar_storm" to "shelter",
            "system_failure" to "cannibalize_apollo13", "micrometeorite_breach" to "abandoned_lrv",
            "telemetry_blackout" to "abandoned_viking", "dust_storm_filter" to "heritage_filter",
            "battery_thermal" to "isolate_string", "suit_leak" to "abort_eva",
            "crop_failure" to "reroute_power", "nav_glitch" to "radio_beacon",
            "medical_fatigue" to "sleep_shift", "fire_alarm" to "vent_module"
        )
        for (destination in PlanetaryTarget.entries) for (difficulty in Difficulty.entries) {
            val vm = GameViewModel(Random(19), goodNewsPicker = {
                GoodNews("none", "None", "None", "Fixture", sleepDelta = 0, stressDelta = 0,
                    statusDelta = 0, moraleDelta = 0)
            })
            vm.finishIntro(); vm.selectDifficulty(difficulty); vm.selectDestination(destination)
            vm.selectAnalog(MockData.getAnalogs(AppLanguage.ENGLISH).first { it.planetaryTarget == destination })
            vm.updateMissionPlan(MissionPlan(PowerPlan.SOLAR, HabitatPlan.HARDENED, PriorityPlan.RESOURCES, true))
            vm.confirmMissionDesign()
            val total = vm.state.value.targetCrisisCount
            repeat(total) {
                assertEquals(Screen.DASHBOARD, vm.state.value.currentScreen)
                vm.triggerCrisis()
                val state = vm.state.value
                val crisis = assertNotNull(state.activeCrisis)
                vm.resolveCrisis(crisis.id, state.activeCrisisOccurrence!!, choices.getValue(crisis.id))
                vm.state.value.lastDecisionReport?.let {
                    vm.dismissDecisionReport(it.forecast.crisisId, it.forecast.occurrence)
                }
                vm.dismissGoodNews()
            }
            val result = assertNotNull(vm.state.value.result)
            assertTrue(result.isSuccess, "$destination / $difficulty")
            assertEquals(total, result.crisesFaced.size)
            assertTrue(result.score in 0..100)
        }
    }
}
