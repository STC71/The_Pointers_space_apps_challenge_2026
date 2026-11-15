package com.worstcase.outpost.domain

/** Signed initial changes and explicit operating costs, all in game units. */
data class DesignEffects(
    val energy: Int = 0,
    val water: Int = 0,
    val oxygen: Int = 0,
    val food: Int = 0,
    val morale: Int = 0,
    val health: Int = 0,
    val sleep: Int = 0,
    val stress: Int = 0,
    val waterPerCrisis: Int = 0,
    val oxygenPerCrisis: Int = 0,
    val energyPerCrisis: Int = 0
) {
    operator fun plus(other: DesignEffects) = DesignEffects(
        energy + other.energy, water + other.water, oxygen + other.oxygen,
        food + other.food, morale + other.morale, health + other.health,
        sleep + other.sleep, stress + other.stress,
        waterPerCrisis + other.waterPerCrisis,
        oxygenPerCrisis + other.oxygenPerCrisis,
        energyPerCrisis + other.energyPerCrisis
    )
}

fun powerEffects(power: PowerPlan): DesignEffects = when (power) {
    PowerPlan.SOLAR -> DesignEffects(energy = -8, water = 6)
    PowerPlan.NUCLEAR -> DesignEffects(energy = 10, water = -6, waterPerCrisis = 1)
    PowerPlan.MIXED -> DesignEffects(energy = 3)
}

fun habitatEffects(habitat: HabitatPlan): DesignEffects = when (habitat) {
    HabitatPlan.MINIMAL -> DesignEffects(energy = 8, food = 4, oxygen = -8, morale = -6,
        waterPerCrisis = 1, oxygenPerCrisis = 1)
    HabitatPlan.STANDARD -> DesignEffects()
    HabitatPlan.HARDENED -> DesignEffects(energy = -10, food = -2, oxygen = 5, health = 4,
        waterPerCrisis = -1, oxygenPerCrisis = -1, energyPerCrisis = 2)
}

fun priorityEffects(priority: PriorityPlan): DesignEffects = when (priority) {
    PriorityPlan.RESOURCES -> DesignEffects(water = 8, food = 2, morale = -4, sleep = -3)
    PriorityPlan.CREW -> DesignEffects(water = -4, morale = 8, health = 5, sleep = 5, stress = -5)
    PriorityPlan.SCIENCE -> DesignEffects(energy = -4, food = -1, stress = 5)
}

fun reuseEffects(enabled: Boolean): DesignEffects =
    if (enabled) DesignEffects(energy = -4, food = -1) else DesignEffects()

fun MissionPlan.designEffects(): DesignEffects = powerEffects(power) + habitatEffects(habitat) +
    priorityEffects(priority) + reuseEffects(planHardwareReuse)

/** Deterministic recharge per decision period, reduced by the relevant crisis. */
fun powerGeneration(power: PowerPlan, crisisId: String): Int {
    val disrupted = crisisId in setOf("solar_storm", "dust_storm_filter")
    return when (power) {
        PowerPlan.SOLAR -> if (disrupted) 2 else 6
        PowerPlan.NUCLEAR -> 7
        PowerPlan.MIXED -> if (disrupted) 3 else 5
    }
}

const val MAX_SCIENCE_PER_CRISIS = 6
fun sciencePerCrisis(priority: PriorityPlan): Int = if (priority == PriorityPlan.SCIENCE) MAX_SCIENCE_PER_CRISIS else 2

/** Only harmful changes are scaled. Rest and stress relief retain their stated benefit. */
fun decisionDelta(delta: Int, difficulty: Difficulty, stress: Boolean = false, trainee: Boolean = false): Int {
    val harmful = if (stress) delta > 0 else delta < 0
    if (!harmful) return delta
    val multiplier = when (difficulty) {
        Difficulty.JUNIOR -> 0.65f
        Difficulty.MID -> 1f
        Difficulty.SENIOR -> 1.30f
    }
    val scaled = (delta * multiplier).toInt()
    return if (trainee) (scaled * 1.35f).toInt() else scaled
}

data class DecisionOutcome(
    val resources: Resources,
    val crew: List<CrewMember>,
    val missionDay: Int,
    val scienceGain: Int,
    val averageSleepChange: Int,
    val averageStressChange: Int,
    val directResources: Resources,
    val periodBudget: PeriodBudget
)

/** Planned use/recharge; effective changes may be smaller at the resource limits. */
data class PeriodBudget(
    val days: Int,
    val water: Int,
    val oxygen: Int,
    val food: Int,
    val energyGenerated: Int,
    val energyUpkeep: Int
)

/** One immutable pre-decision snapshot, excluding any subsequent random reward. */
data class DecisionForecast(
    val crisisId: String,
    val occurrence: Long,
    val optionId: String,
    val resourcesBefore: Resources,
    val crewBefore: List<CrewMember>,
    val dayBefore: Int,
    val scienceBefore: Int,
    val scienceAfter: Int,
    val outcome: DecisionOutcome
) {
    val criticalReserves: Set<CriticalResource> get() = criticalDepletion(outcome.resources)
}

/** The saved forecast and the actual committed snapshot, including an optional reward. */
data class DecisionReport(
    val crisis: Crisis,
    val forecast: DecisionForecast,
    val forecastConsulted: Boolean,
    val resourcesAfter: Resources,
    val crewAfter: List<CrewMember>,
    val scienceAfter: Int,
    val additionalEvent: GoodNews?
)

/** Shared survival thresholds for previews and definitive mission outcomes. */
fun criticalDepletion(resources: Resources): Set<CriticalResource> = buildSet {
    if (resources.crewHealth <= 0) add(CriticalResource.CREW_HEALTH)
    if (resources.oxygen <= 0) add(CriticalResource.OXYGEN)
    if (resources.water <= 0) add(CriticalResource.WATER)
    if (resources.foodDays <= 0) add(CriticalResource.FOOD)
}

/** Pure, shared calculation for the committed decision and the local advisor.
 * Rewards are deliberately excluded and are applied exactly once by the turn coordinator.
 * Resource changes are percentage points; food is ration-days; science is points. */
fun calculateDecision(
    resources: Resources,
    crew: List<CrewMember>,
    option: CrisisOption,
    crisisId: String,
    difficulty: Difficulty,
    plan: MissionPlan,
    missionDay: Int,
    totalMissionDays: Int,
    remainingCrises: Int,
    siteCost: Triple<Int, Int, Int>
): DecisionOutcome {
    require(crew.isNotEmpty())
    val step = ((totalMissionDays - missionDay).coerceAtLeast(0) / remainingCrises.coerceAtLeast(1)).coerceAtLeast(1)
    val newDay = (missionDay + step).coerceAtMost(totalMissionDays)
    fun delta(value: Int) = decisionDelta(value, difficulty)
    val updatedCrew = crew.map { member ->
        val trainee = member.id == "player"
        member.copy(
            sleep = (member.sleep + decisionDelta(option.sleepDelta, difficulty, trainee = trainee)).coerceIn(0, 100),
            stress = (member.stress + decisionDelta(option.stressDelta, difficulty, stress = true, trainee = trainee)).coerceIn(0, 100),
            status = (member.status + decisionDelta(option.healthDelta, difficulty, trainee = trainee)).coerceIn(0, 100)
        )
    }
    val design = plan.designEffects()
    val supportCost = when (difficulty) {
        Difficulty.JUNIOR -> 2
        Difficulty.MID -> 3
        Difficulty.SENIOR -> 4
    }
    val waterCost = (supportCost + design.waterPerCrisis).coerceAtLeast(1) + siteCost.first
    val oxygenCost = (supportCost + design.oxygenPerCrisis).coerceAtLeast(1) + siteCost.second
    val direct = resources.copy(
        energy = (resources.energy + delta(option.energyDelta)).coerceIn(0, 100),
        water = (resources.water + delta(option.waterDelta)).coerceIn(0, 100),
        oxygen = (resources.oxygen + delta(option.oxygenDelta)).coerceIn(0, 100),
        foodDays = (resources.foodDays + option.foodDelta).coerceAtLeast(0),
        morale = (resources.morale + delta(option.moraleDelta)).coerceIn(0, 100),
        crewHealth = aggregateCrewHealth(updatedCrew)
    )
    return DecisionOutcome(
        resources = direct.copy(
            energy = (direct.energy + powerGeneration(plan.power, crisisId) - design.energyPerCrisis - siteCost.third).coerceIn(0, 100),
            water = (direct.water - waterCost).coerceIn(0, 100),
            oxygen = (direct.oxygen - oxygenCost).coerceIn(0, 100),
            foodDays = (direct.foodDays - (newDay - missionDay).coerceAtLeast(0)).coerceAtLeast(0),
            crewHealth = aggregateCrewHealth(updatedCrew)
        ),
        crew = updatedCrew,
        missionDay = newDay,
        scienceGain = (sciencePerCrisis(plan.priority) + option.scienceDelta).coerceIn(0, MAX_SCIENCE_PER_CRISIS),
        averageSleepChange = updatedCrew.zip(crew).sumOf { (after, before) -> after.sleep - before.sleep } / crew.size,
        averageStressChange = updatedCrew.zip(crew).sumOf { (after, before) -> after.stress - before.stress } / crew.size,
        directResources = direct,
        periodBudget = PeriodBudget(
            days = (newDay - missionDay).coerceAtLeast(0),
            water = waterCost, oxygen = oxygenCost,
            food = (newDay - missionDay).coerceAtLeast(0),
            energyGenerated = powerGeneration(plan.power, crisisId),
            energyUpkeep = design.energyPerCrisis + siteCost.third
        )
    )
}

/** Score is 0..100: 85% survival margins, 15% science; unsuccessful runs are halved. */
fun missionScore(resources: Resources, sciencePoints: Int, scienceTarget: Int, success: Boolean): Int {
    val survival = (resources.crewHealth * 2 + resources.morale + resources.energy + resources.water + resources.oxygen) / 6
    val science = if (scienceTarget > 0) (sciencePoints * 100 / scienceTarget).coerceIn(0, 100) else 0
    val score = ((survival.coerceIn(0, 100) * 85 + science * 15) / 100).coerceIn(0, 100)
    return if (success) score else score / 2
}
