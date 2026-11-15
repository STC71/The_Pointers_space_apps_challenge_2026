package com.worstcase.outpost.domain

/**
 * ============================================================
 *  MODELS.KT
 *  This file contains all the basic data structures (models)
 *  used by the game.
 *
 *  Think of these models as "boxes" that hold information.
 *  Every important thing in the game (resources, crew, crises…)
 *  has its own box here.
 * ============================================================
 */

/**
 * Difficulty of the game.
 * JUNIOR = Training mode with telemetry guidance and cushioned impacts
 * MID    = Standard space mission parameters
 * SENIOR = Greater decision costs and scarce supplies
 */
enum class Difficulty {
    JUNIOR,   // Easy level – good for beginners / junior astronaut trainer
    MID,      // Medium level – realistic challenges
    SENIOR    // Hard level – higher costs and crisis management
}

/**
 * Choices the player makes when designing the outpost (mission architecture).
 */
enum class PowerPlan { SOLAR, NUCLEAR, MIXED }
enum class HabitatPlan { MINIMAL, STANDARD, HARDENED }
enum class PriorityPlan { RESOURCES, CREW, SCIENCE }

data class MissionPlan(
    val power: PowerPlan = PowerPlan.MIXED,
    val habitat: HabitatPlan = HabitatPlan.STANDARD,
    val priority: PriorityPlan = PriorityPlan.CREW,
    val planHardwareReuse: Boolean = true
)


/**
 * Target planetary body for the mission.
 */
enum class PlanetaryTarget(val displayName: String, val icon: String) {
    MOON("Moon", "🌑"),
    MARS("Mars", "🔴")
}

/**
 * Represents one Earth location that looks like the Moon or Mars.
 * These places are called "analogs".
 * Scientists train equipment and people in these places before going to space.
 */
data class Analog(
    val id: String,                 // Unique identifier, example: "riotinto"
    val name: String,               // Name shown to the player, example: "Río Tinto"
    val planetaryTarget: PlanetaryTarget, // Target body (Moon or Mars)
    val targetSite: String,         // Specific celestial site (e.g. "Jezero Crater", "Shackleton Crater")
    val description: String,        // Scientific explanation why this place is an analog
    val location: String,           // Country or region, example: "Huelva, Spain"
    val environmentalModifier: String = "", // Special hazard/condition (e.g. Extreme UV, Acidic Regolith, Cold)
    val panoramaUrl: String = "",    // 360° spherical / immersive panorama visualizer URL
    val soundscapeDescription: String = "", // Ambient audio profile
    // Deep briefing for the "Explore 360" overlay (multi-paragraph, localized in MockData)
    val briefingTechnical: String = "",
    val briefingHistory: String = "",
    val briefingOps: String = ""
)

/**
 * One astronaut living in the outpost.
 * We track three simple health indicators.
 * Properties are immutable (val) for safe reactive state updates.
 */
data class CrewMember(
    val id: String,          // Unique id of the astronaut
    val name: String,        // Full name of the astronaut

    // Sleep level from 0 (exhausted) to 100 (fully rested)
    val sleep: Int = 80,

    // Stress level from 0 (calm) to 100 (very stressed)
    val stress: Int = 20,

    // Source of truth for health (0..100). Sleep/stress are separate indicators;
    // they do not automatically subtract health. Crisis rules update each explicitly.
    // General health status from 0 (critical) to 100 (excellent)
    val status: Int = 90,

    // Short historical note (for famous astronauts) or role label for the player
    val bio: String = "",

    // Mission role on this training flight (Commander, Pilot, Specialist, Trainee…)
    val role: String = ""
)

/**
 * All the resources the outpost has at any moment.
 * Health, water, oxygen and food are critical; energy and morale affect margins,
 * but are not independent defeat conditions.
 * Properties are immutable (val) for safe reactive state updates.
 */
data class Resources(
    // Electric power available (0% to 100%)
    val energy: Int = 100,

    // Clean water available (0% to 100%)
    val water: Int = 100,

    // Breathable oxygen available (0% to 100%)
    val oxygen: Int = 100,

    // How many days of food are left
    val foodDays: Int = 14,

    // Derived with aggregateCrewHealth; zero before a crew has been assigned.
    val crewHealth: Int = 0,

    // Team spirit / happiness of the crew (0 to 100)
    val morale: Int = 85
)

/**
 * One possible answer the player can choose when a crisis happens.
 * Each option changes the resources and the crew in a different way.
 */
data class CrisisOption(
    val id: String,                 // Unique id within this crisis (may recur in other crises)
    val text: String,               // Text shown on the button

    // How much each resource changes if the player chooses this option
    // Positive number = gain, Negative number = loss
    val energyDelta: Int = 0,
    val waterDelta: Int = 0,
    val oxygenDelta: Int = 0,
    val foodDelta: Int = 0,
    val healthDelta: Int = 0,
    val moraleDelta: Int = 0,

    // true = this option uses old abandoned NASA hardware
    val usesAbandonedHardware: Boolean = false,

    // Historical storytelling explaining the origin and mission of the discarded equipment
    val hardwareHistory: String? = null,

    // Explicit net crew effects of this decision, including its workload.
    // Routine work costs rest; recovery options override these defaults.
    val sleepDelta: Int = -8,
    val stressDelta: Int = 3,
    // Additional science points; the priority also supplies a base gain per crisis.
    val scienceDelta: Int = 0
)

/**
 * A critical event that forces the player to make a hard decision.
 * This is the heart of the "What is the worst that can happen?" idea.
 */
data class Crisis(
    val id: String,                     // Unique id of the crisis
    val title: String,                  // Short title shown to the player
    val description: String,            // Longer explanation of what is happening
    val options: List<CrisisOption>,    // List of possible answers (usually 2 or 3)
    val juniorHint: String? = null      // Formative guidance for the Junior Astronaut Mission Trainer
)

/**
 * AI Copilot assessment message providing tactical flight director guidance.
 */
data class GoodNews(
    val id: String,
    val title: String,
    val body: String,
    val historicalNote: String,
    val sleepDelta: Int = 4,
    val stressDelta: Int = -6,
    val statusDelta: Int = 5,
    val moraleDelta: Int = 6,
    val waterDelta: Int = 0,
    val foodDelta: Int = 0,
    val energyDelta: Int = 0,
    val oxygenDelta: Int = 0
)

data class AiFlightAssessment(
    val title: String,
    val analysis: String,
    val worstCaseScenario: String,
    val recommendedOptionId: String? = null
)

/** Mean health, rounded down once. An empty crew is invalid and has zero health. */
fun aggregateCrewHealth(crew: List<CrewMember>): Int =
    if (crew.isEmpty()) 0 else crew.sumOf { it.status.coerceIn(0, 100) } / crew.size

enum class CriticalResource { CREW_HEALTH, OXYGEN, WATER, FOOD }

/** Structured cause, independent of the UI language. */
sealed interface MissionEndReason {
    object Completed : MissionEndReason
    data class CriticalDepletion(val resources: Set<CriticalResource>) : MissionEndReason
    object Withdrawal : MissionEndReason
}

/** Final summary and the exact definitive resource snapshot used for the outcome. */
data class GameResult(
    val finalResources: Resources,
    val crisesFaced: List<String>,
    val score: Int,
    val lessons: List<String>,
    val endReason: MissionEndReason,
    val usedAbandonedHardware: Boolean = false,
    val sciencePoints: Int = 0,
    val scienceTarget: Int = 0
) {
    val isSuccess: Boolean get() = endReason == MissionEndReason.Completed
}
