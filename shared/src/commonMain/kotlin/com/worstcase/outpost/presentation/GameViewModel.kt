package com.worstcase.outpost.presentation

/**
 * ============================================================
 *  GAMEVIEWMODEL.KT
 *  This file is the "brain" of the game.
 *
 *  It holds the current state of the game (resources, crew,
 *  which screen is visible, active crisis, etc.) and
 *  provides functions that change that state when the player
 *  does something (choose difficulty, select analog, answer
 *  a crisis, end the mission…).
 *
 *  The screens (UI) only read the state and call these functions.
 *  They never change the data directly.
 * ============================================================
 */

import com.worstcase.outpost.data.MockData
import com.worstcase.outpost.domain.*
import com.worstcase.outpost.i18n.AppLanguage
import com.worstcase.outpost.i18n.Strings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.random.Random

/**
 * The different screens the player can see.
 * We use an enum so we cannot accidentally write a wrong name.
 */
/** Per-astronaut change from the last crisis decision (positive = improved). */
data class CrewDelta(
    val sleep: Int = 0,
    val stress: Int = 0,  // for stress, negative delta = less stress = better
    val status: Int = 0
)

enum class Screen {
    INTRO,              // Opening presentation (value proposition)
    HOME,               // Title + language + difficulty
    DESTINATION,        // Choose Moon or Mars mission destination
    ANALOG_SELECTION,   // Choose Earth analog (phase 1)
    MISSION_DESIGN,     // Design outpost architecture (phase 1b)
    DASHBOARD,          // Training in the outpost (phase 2)
    WELCOME_HOME,       // Success celebration before debrief
    DEBRIEF             // Final summary screen
}

/**
 * This class holds ALL the information that is currently true
 * in the game (the "state").
 * When any value changes, the UI automatically updates.
 */
data class GameUiState(
    // Which language is currently selected (default = English)
    val currentLanguage: AppLanguage = AppLanguage.ENGLISH,

    // Which difficulty the player chose (null = not chosen yet)
    val difficulty: Difficulty? = null,

    // Moon or Mars mission destination (null = not chosen yet)
    val selectedDestination: com.worstcase.outpost.domain.PlanetaryTarget? = null,

    // How many crisis tests must be completed for this destination
    val targetCrisisCount: Int = 5,

    // Last change in sleep/stress/status per crew id (for UI arrows)
    val lastCrewDeltas: Map<String, CrewDelta> = emptyMap(),

    // Mission timeline (ISO-like display uses these fields)
    val missionDay: Int = 1,
    val missionHour: Int = 8,
    val missionMinute: Int = 0,
    val totalMissionDays: Int = 14,

    // Good-news popup (max ~1 per 2 crises)
    val activeGoodNews: com.worstcase.outpost.domain.GoodNews? = null,
    val showGoodNewsDialog: Boolean = false,
    val crisesSinceLastGoodNews: Int = 0,

    // Which analog location the player selected (null = not chosen yet)
    val selectedAnalog: Analog? = null,

    // Current resources of the outpost
    val resources: Resources = Resources(),

    // Astronauts currently living in the outpost
    val crew: List<CrewMember> = emptyList(),

    // The crisis that is happening right now (null = no active crisis)
    val activeCrisis: Crisis? = null,
    // Monotonic occurrence identity; catalog ids can recur after a deck refill.
    val activeCrisisOccurrence: Long? = null,

    // true = the crisis dialog must be shown on screen
    val showCrisisDialog: Boolean = false,

    // List of crisis ids that have already happened in this mission
    val crisesFaced: List<String> = emptyList(),
    // Shuffled crisis pool for this mission (reduces same-order repetition)
    val crisisDeck: List<com.worstcase.outpost.domain.Crisis> = emptyList(),

    // true = the player has used abandoned hardware at least once
    val usedAbandonedHardware: Boolean = false,

    val sciencePoints: Int = 0,

    // Which screen is currently visible
    val currentScreen: Screen = Screen.INTRO,

    // Final result of the mission (only filled when the mission ends)
    val result: GameResult? = null,

    // AI Copilot active assessment
    val aiAssessment: AiFlightAssessment? = null,
    val aiForecasts: Map<String, DecisionForecast> = emptyMap(),
    val lastDecisionReport: DecisionReport? = null,
    val showDecisionReport: Boolean = false,

    // true = show 360° VR panoramic visualizer of the selected Earth analog
    val showPanoramaViewer: Boolean = false,

    // Master switch for mission audio (Quindar, alarms, ambient)
    val soundEnabled: Boolean = true,

    // true = settings panel is visible
    val showSettings: Boolean = false,

    // Optional name of the human player (added to the training crew)
    val playerName: String = "",

    // Outpost architecture chosen in Mission Design
    val missionPlan: com.worstcase.outpost.domain.MissionPlan = com.worstcase.outpost.domain.MissionPlan()
)

/**
 * The ViewModel is the class that owns the state and the functions
 * that change it. Screens talk to this class.
 */
class GameViewModel internal constructor(
    private val random: Random,
    // Small content seams for deterministic turn-boundary tests; production uses MockData.
    private val crisisCatalog: (AppLanguage, PlanetaryTarget?) -> List<Crisis> = { language, target ->
        MockData.getCrises(language, target)
    },
    private val goodNewsPicker: (AppLanguage) -> GoodNews = { MockData.randomGoodNews(it, random) }
) {
    constructor() : this(Random.Default)

    // Intentionally survives restart, so callbacks from an earlier mission stay invalid.
    private var nextCrisisOccurrence = 0L

    private fun canAct(state: GameUiState): Boolean =
        state.currentScreen == Screen.DASHBOARD && state.result == null &&
            state.crew.isNotEmpty() && state.activeGoodNews == null && !state.showGoodNewsDialog && !state.showDecisionReport

    // ----------------------------------------------------------------
    //  Private mutable state (only this class can change it)
    // ----------------------------------------------------------------
    private val _state = MutableStateFlow(GameUiState())

    // ----------------------------------------------------------------
    //  Public read-only state (screens can only read it)
    // ----------------------------------------------------------------
    val state: StateFlow<GameUiState> = _state.asStateFlow()

    // ----------------------------------------------------------------
    //  FUNCTIONS THAT THE SCREENS CAN CALL
    // ----------------------------------------------------------------

    /**
     * Changes the language of the whole application.
     * The UI and active data will automatically reflect the localized texts.
     */
    fun setSoundEnabled(enabled: Boolean) {
        com.worstcase.outpost.audio.AudioManager.setSoundOn(enabled)
        _state.update { it.copy(soundEnabled = enabled) }
    }

    fun setShowSettings(visible: Boolean) {
        _state.update { it.copy(showSettings = visible) }
    }

    fun setPlayerName(name: String) {
        _state.update { it.copy(playerName = name) }
    }

    fun finishIntro() {
        if (_state.value.currentScreen != Screen.INTRO) return
        com.worstcase.outpost.audio.AudioManager.startBackgroundMusic()
        _state.update { it.copy(currentScreen = Screen.HOME) }
    }

    fun setLanguage(language: AppLanguage) {
        _state.update { currentState ->
            // Re-localize active analog and crisis if already selected
            val updatedAnalog = currentState.selectedAnalog?.let { current ->
                MockData.getAnalogs(language).find { it.id == current.id } ?: current
            }
            val updatedCrisis = currentState.activeCrisis?.let { current ->
                // Preserve the allowed ids even when translated catalog entries have more options.
                val translated = crisisCatalog(language, currentState.selectedDestination)
                    .find { it.id == current.id } ?: current
                // Translation only changes copy, never effects or eligibility of an active decision.
                val localized = current.copy(
                    title = translated.title,
                    description = translated.description,
                    juniorHint = translated.juniorHint,
                    options = current.options.map { option ->
                        val text = translated.options.find { it.id == option.id }
                        option.copy(text = text?.text ?: option.text,
                            hardwareHistory = text?.hardwareHistory ?: option.hardwareHistory)
                    }
                )
                enabledCrisis(MockData.localizeCrisis(localized, language), currentState.missionPlan)
            }

            currentState.copy(
                currentLanguage = language,
                selectedAnalog = updatedAnalog,
                activeCrisis = updatedCrisis,
                // Forecasts contain mechanical snapshots, so changing copy cannot invalidate them.
                aiAssessment = if (currentState.aiAssessment != null && updatedCrisis != null)
                    MockData.evaluateAiCopilot(updatedCrisis, currentState.resources, language,
                        currentState.aiForecasts.mapValues { it.value.outcome }) else null
            )
        }
    }

    /** Selects difficulty before choosing a destination. */
    fun selectDifficulty(difficulty: Difficulty) {
        if (_state.value.currentScreen != Screen.HOME) return
        _state.update { currentState ->
            currentState.copy(
                difficulty = difficulty,
                currentScreen = Screen.DESTINATION
            )
        }
    }

    /** Preparation only. Preserve the draft; reject a callback from a previous screen. */
    fun navigateBack(from: Screen) {
        _state.update { current ->
            if (current.currentScreen != from || current.showSettings || current.result != null) return@update current
            val previous = when (from) {
                Screen.DESTINATION -> Screen.HOME
                Screen.ANALOG_SELECTION -> Screen.DESTINATION
                Screen.MISSION_DESIGN -> Screen.ANALOG_SELECTION
                else -> return@update current
            }
            current.copy(currentScreen = previous)
        }
    }

    /**
     * Moon = 7 crisis tests, Mars = 12 crisis tests.
     */
    fun selectDestination(destination: com.worstcase.outpost.domain.PlanetaryTarget) {
        if (_state.value.currentScreen != Screen.DESTINATION) return
        val count = when (destination) {
            com.worstcase.outpost.domain.PlanetaryTarget.MOON -> 7
            com.worstcase.outpost.domain.PlanetaryTarget.MARS -> 12
        }
        // Planned surface stay / return window used for the mission clock
        val days = when (destination) {
            com.worstcase.outpost.domain.PlanetaryTarget.MOON -> 21
            com.worstcase.outpost.domain.PlanetaryTarget.MARS -> 90
        }
        com.worstcase.outpost.audio.AudioManager.play(com.worstcase.outpost.audio.SoundEffect.QUINDAR_BEEP)
        _state.update {
            it.copy(
                selectedDestination = destination,
                // Revisiting a destination preserves its draft; changing body invalidates the site.
                selectedAnalog = it.selectedAnalog?.takeIf { analog -> analog.planetaryTarget == destination },
                targetCrisisCount = count,
                totalMissionDays = days,
                missionDay = 1,
                missionHour = listOf(6, 7, 8, 9, 14, 16, 18).random(random),
                missionMinute = listOf(0, 5, 10, 15, 20, 30, 45).random(random),
                crisesSinceLastGoodNews = 0,
                currentScreen = Screen.ANALOG_SELECTION
            )
        }
    }

    /**
     * Called when the player chooses an Earth analog matching the destination.
     */
    fun selectAnalog(analog: Analog) {
        val state = _state.value
        if (state.currentScreen != Screen.ANALOG_SELECTION || analog.planetaryTarget != state.selectedDestination) return
        com.worstcase.outpost.audio.AudioManager.play(com.worstcase.outpost.audio.SoundEffect.QUINDAR_BEEP)
        _state.update { currentState ->
            currentState.copy(
                selectedAnalog = analog,
                currentScreen = Screen.MISSION_DESIGN
            )
        }
    }

    fun updateMissionPlan(plan: com.worstcase.outpost.domain.MissionPlan) {
        if (_state.value.currentScreen != Screen.MISSION_DESIGN) return
        _state.update { it.copy(missionPlan = plan) }
    }

    /**
     * Locks the mission architecture and starts outpost training (phase 2).
     */
    fun confirmMissionDesign() {
        val current = _state.value
        if (current.currentScreen != Screen.MISSION_DESIGN || current.result != null) return
        val diff = current.difficulty ?: return
        val destination = current.selectedDestination ?: return
        if (current.selectedAnalog?.planetaryTarget != destination) return
        val plan = current.missionPlan
        val crew = MockData.createInitialCrew(
            difficulty = diff, playerName = current.playerName, language = current.currentLanguage,
            priority = plan.priority, habitat = plan.habitat, random = random
        )
        if (crew.isEmpty()) return
        _state.value = current.copy(
            resources = MockData.createInitialResources(crew, diff, plan, current.totalMissionDays),
            crew = crew,
            crisesFaced = emptyList(),
            crisisDeck = crisisCatalog(current.currentLanguage, destination).shuffled(random),
            usedAbandonedHardware = false,
            sciencePoints = 0,
            activeCrisis = null,
            activeCrisisOccurrence = null,
            showCrisisDialog = false,
            aiAssessment = null,
            aiForecasts = emptyMap(),
            lastDecisionReport = null,
            showDecisionReport = false,
            activeGoodNews = null,
            showGoodNewsDialog = false,
            crisesSinceLastGoodNews = 0,
            lastCrewDeltas = emptyMap(),
            showPanoramaViewer = false,
            missionDay = if (destination == PlanetaryTarget.MOON) 3 else 10,
            currentScreen = Screen.DASHBOARD
        )
        com.worstcase.outpost.audio.AudioManager.play(com.worstcase.outpost.audio.SoundEffect.QUINDAR_BEEP)
        com.worstcase.outpost.audio.AudioManager.play(com.worstcase.outpost.audio.SoundEffect.HABITAT_AMBIENT)
    }

    /**
     * Called when the player presses the big button
     * "What is the worst that can happen?".
     *
     * Draws one occurrence from the mission deck (refilled when exhausted).
     * The shared outcome rule checks completion before opening another decision.
     */
    fun triggerCrisis() {
        val currentState = _state.value
        if (!canAct(currentState) || currentState.activeCrisis != null || currentState.showCrisisDialog) return
        evaluateMissionEnd(currentState)?.let { finishMission(it); return }
        val deck = currentState.crisisDeck.ifEmpty {
            crisisCatalog(currentState.currentLanguage, currentState.selectedDestination).shuffled(random)
        }
        if (deck.isEmpty()) return
        val crisis = enabledCrisis(MockData.localizeCrisis(deck.first(), currentState.currentLanguage), currentState.missionPlan)
        if (crisis.options.isEmpty()) return
        _state.value = currentState.copy(
            activeCrisis = crisis,
            activeCrisisOccurrence = ++nextCrisisOccurrence,
            crisisDeck = deck.drop(1),
            showCrisisDialog = true,
            aiForecasts = emptyMap(),
            aiAssessment = null
        )
        com.worstcase.outpost.audio.AudioManager.play(com.worstcase.outpost.audio.SoundEffect.CRITICAL_ALARM)
    }

    private fun enabledCrisis(crisis: Crisis, plan: MissionPlan): Crisis = crisis.copy(
        options = crisis.options.filter { plan.planHardwareReuse || !it.usesAbandonedHardware }
    )

    /**
     * Called when the player chooses one of the options of a crisis.
     * Applies consequences (scaling penalties by difficulty) and advances game time.
     */
    fun resolveCrisis(crisisId: String, occurrence: Long, optionId: String) {
        val currentState = _state.value
        if (!canAct(currentState) || !currentState.showCrisisDialog) return
        val crisis = currentState.activeCrisis ?: return
        if (crisis.id != crisisId || currentState.activeCrisisOccurrence != occurrence) return
        val option = crisis.options.singleOrNull { it.id == optionId } ?: return
        if (option.usesAbandonedHardware && !currentState.missionPlan.planHardwareReuse) return
        if (currentState.difficulty == null) return

        val consulted = currentState.aiForecasts[option.id]?.takeIf {
            it.crisisId == crisis.id && it.occurrence == occurrence &&
                it.resourcesBefore == currentState.resources && it.crewBefore == currentState.crew &&
                it.dayBefore == currentState.missionDay && it.scienceBefore == currentState.sciencePoints
        }
        val forecast = consulted ?: forecastOption(currentState, crisis, occurrence, option)
        val effects = forecast.outcome
        val updatedCrisesFaced = currentState.crisesFaced + crisis.id
        val newHour = listOf(5, 6, 7, 8, 9, 10, 13, 14, 15, 16, 17, 18, 19, 21).random(random)
        val newMinute = listOf(0, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50).random(random)

        val crisesSince = currentState.crisesSinceLastGoodNews + 1
        // At least one good-news event every two "worst case" resolutions
        val maybeGoodNews = if (crisesSince >= 2) {
            goodNewsPicker(currentState.currentLanguage)
        } else null

        // The turn is committed as one snapshot: effects -> time/burn -> one reward ->
        // health aggregate -> outcome. A reward may rescue a depleted reserve this turn.
        var resolved = currentState.copy(
            resources = effects.resources,
            crew = effects.crew,
            aiForecasts = emptyMap(),
            showDecisionReport = true,
            sciencePoints = (currentState.sciencePoints + effects.scienceGain)
                .coerceIn(0, currentState.targetCrisisCount * MAX_SCIENCE_PER_CRISIS),
            crisesFaced = updatedCrisesFaced,
            activeCrisis = null,
            activeCrisisOccurrence = null,
            showCrisisDialog = false,
            aiAssessment = null,
            usedAbandonedHardware = currentState.usedAbandonedHardware || option.usesAbandonedHardware,
            missionDay = effects.missionDay,
            missionHour = newHour,
            missionMinute = newMinute,
            crisesSinceLastGoodNews = if (maybeGoodNews != null) 0 else crisesSince,
            activeGoodNews = maybeGoodNews,
            showGoodNewsDialog = maybeGoodNews != null
        )
        if (maybeGoodNews != null) resolved = applyGoodNews(resolved, maybeGoodNews)
        resolved = resolved.copy(
            resources = resolved.resources.copy(crewHealth = aggregateCrewHealth(resolved.crew)),
            lastCrewDeltas = resolved.crew.associate { member ->
                val before = currentState.crew.first { it.id == member.id }
                member.id to CrewDelta(member.sleep - before.sleep, member.stress - before.stress, member.status - before.status)
            }
        )
        resolved = resolved.copy(lastDecisionReport = DecisionReport(
            crisis = crisis, forecast = forecast, forecastConsulted = consulted != null,
            resourcesAfter = resolved.resources, crewAfter = resolved.crew,
            scienceAfter = resolved.sciencePoints, additionalEvent = maybeGoodNews
        ))
        val outcome = evaluateMissionEnd(resolved)
        _state.value = resolved
        com.worstcase.outpost.audio.AudioManager.play(com.worstcase.outpost.audio.SoundEffect.DECISION_CONFIRMED)
        // Rewards are already applied. Only presentation waits for acknowledgment.
        if (maybeGoodNews == null && outcome != null) finishMission(outcome)
    }

    private fun calculateOption(state: GameUiState, crisis: Crisis, option: CrisisOption): DecisionOutcome =
        calculateDecision(
            resources = state.resources, crew = state.crew, option = option, crisisId = crisis.id,
            difficulty = requireNotNull(state.difficulty), plan = state.missionPlan,
            missionDay = state.missionDay, totalMissionDays = state.totalMissionDays,
            remainingCrises = state.targetCrisisCount - state.crisesFaced.size,
            siteCost = MockData.analogRiskTax(state.selectedAnalog?.id)
        )

    private fun forecastOption(state: GameUiState, crisis: Crisis, occurrence: Long, option: CrisisOption): DecisionForecast {
        val outcome = calculateOption(state, crisis, option)
        return DecisionForecast(
            crisisId = crisis.id, occurrence = occurrence, optionId = option.id,
            resourcesBefore = state.resources, crewBefore = state.crew, dayBefore = state.missionDay,
            scienceBefore = state.sciencePoints,
            scienceAfter = (state.sciencePoints + outcome.scienceGain).coerceIn(0, state.targetCrisisCount * MAX_SCIENCE_PER_CRISIS),
            outcome = outcome
        )
    }

    /** Presentation only: never redraw a reward or reapply a consequence. */
    fun dismissDecisionReport(crisisId: String, occurrence: Long) {
        val current = _state.value
        val report = current.lastDecisionReport ?: return
        if (!current.showDecisionReport || report.forecast.crisisId != crisisId ||
            report.forecast.occurrence != occurrence) return
        _state.update { it.copy(showDecisionReport = false) }
    }

    fun showLastDecisionReport() {
        val current = _state.value
        if (current.lastDecisionReport == null || current.activeCrisis != null || current.showCrisisDialog ||
            current.showGoodNewsDialog || current.showSettings ||
            current.currentScreen !in setOf(Screen.DASHBOARD, Screen.WELCOME_HOME, Screen.DEBRIEF)) return
        _state.update { it.copy(showDecisionReport = true) }
    }

    private fun applyGoodNews(state: GameUiState, news: GoodNews): GameUiState = state.copy(
        resources = state.resources.copy(
            energy = (state.resources.energy + news.energyDelta).coerceIn(0, 100),
            oxygen = (state.resources.oxygen + news.oxygenDelta).coerceIn(0, 100),
            morale = (state.resources.morale + news.moraleDelta).coerceIn(0, 100),
            water = (state.resources.water + news.waterDelta).coerceIn(0, 100),
            foodDays = (state.resources.foodDays + news.foodDelta).coerceAtLeast(0)
        ),
        crew = state.crew.map { member ->
            member.copy(
                sleep = (member.sleep + news.sleepDelta).coerceIn(0, 100),
                stress = (member.stress + news.stressDelta).coerceIn(0, 100),
                status = (member.status + news.statusDelta).coerceIn(0, 100)
            )
        }
    )

    fun dismissGoodNews() {
        val current = _state.value
        if (current.currentScreen != Screen.DASHBOARD || current.result != null ||
            current.activeGoodNews == null || !current.showGoodNewsDialog || current.showDecisionReport) return
        val after = current.copy(activeGoodNews = null, showGoodNewsDialog = false)
        _state.value = after
        // Acknowledgment has no random draw and no resource effects.
        evaluateMissionEnd(after)?.let { finishMission(it) }
        com.worstcase.outpost.audio.AudioManager.play(com.worstcase.outpost.audio.SoundEffect.QUINDAR_BEEP)
    }

    /** The UI requests withdrawal; only the shared rule can choose the outcome. */
    fun requestEndMission() {
        val current = _state.value
        if (!canAct(current) || current.activeCrisis != null || current.showCrisisDialog) return
        evaluateMissionEnd(current, withdrawalRequested = true)?.let { finishMission(it) }
    }

    private fun finishMission(reason: MissionEndReason) {
        val snapshot = _state.value
        if (snapshot.result != null) return
        val currentResources = snapshot.resources
        val isSuccess = reason == MissionEndReason.Completed

        val scienceTarget = snapshot.targetCrisisCount * MAX_SCIENCE_PER_CRISIS
        val score = missionScore(currentResources, snapshot.sciencePoints, scienceTarget, isSuccess)

        val lessons = buildMissionLessons(currentResources, reason, snapshot.usedAbandonedHardware,
            snapshot.currentLanguage).map { it.first }

        _state.value = snapshot.copy(
            currentScreen = if (isSuccess) Screen.WELCOME_HOME else Screen.DEBRIEF,
            showCrisisDialog = false,
            activeCrisis = null,
            activeCrisisOccurrence = null,
            aiAssessment = null,
            showPanoramaViewer = false,
            showSettings = false,
            showGoodNewsDialog = false,
            activeGoodNews = null,
            result = GameResult(
                finalResources = currentResources,
                crisesFaced = snapshot.crisesFaced,
                score = score,
                lessons = lessons,
                endReason = reason,
                usedAbandonedHardware = snapshot.usedAbandonedHardware,
                sciencePoints = snapshot.sciencePoints,
                scienceTarget = scienceTarget
            )
        )
        if (isSuccess) {
            com.worstcase.outpost.audio.AudioManager.play(com.worstcase.outpost.audio.SoundEffect.DECISION_CONFIRMED)
        }
    }

    /** Player finished the welcome-home celebration → show debrief. */
    fun continueFromWelcomeHome() {
        if (_state.value.currentScreen != Screen.WELCOME_HOME) return
        _state.update { it.copy(currentScreen = Screen.DEBRIEF) }
    }

    /**
     * Toggles or opens the 360° spherical view modal of the current analog site.
     */
    fun setPanoramaViewerVisible(visible: Boolean) {
        if (_state.value.currentScreen != Screen.DASHBOARD) return
        _state.update { it.copy(showPanoramaViewer = visible) }
    }

    /**
     * Requests an AI Flight Copilot evaluation for the current crisis.
     */
    fun requestAiAssessment(crisisId: String, occurrence: Long) {
        val current = _state.value
        if (!canAct(current) || !current.showCrisisDialog || current.activeCrisisOccurrence != occurrence) return
        val currentCrisis = current.activeCrisis ?: return
        if (currentCrisis.id != crisisId) return
        val currentResources = _state.value.resources
        val lang = _state.value.currentLanguage

        // The audio dispatcher suspends the theme until the narration completes.
        com.worstcase.outpost.audio.AudioManager.play(
            com.worstcase.outpost.audio.SoundEffect.HOUSTON_PROBLEM
        )

        val forecasts = currentCrisis.options.associate { it.id to forecastOption(current, currentCrisis, occurrence, it) }
        val assessment = MockData.evaluateAiCopilot(currentCrisis, currentResources, lang, forecasts.mapValues { it.value.outcome })
        _state.update { it.copy(aiAssessment = assessment, aiForecasts = forecasts) }
    }

    /**
     * Dismisses the active AI assessment.
     */
    fun dismissAiAssessment() {
        _state.update { it.copy(aiAssessment = null) }
    }

    /**
     * Resets everything so the player can start a new mission.
     */
    fun restart() {
        // Keep language + player name; skip intro and return to Home (difficulty select)
        _state.value = GameUiState(
            currentLanguage = _state.value.currentLanguage,
            playerName = _state.value.playerName,
            soundEnabled = _state.value.soundEnabled,
            currentScreen = Screen.HOME
        )
        if (_state.value.soundEnabled) {
            com.worstcase.outpost.audio.AudioManager.startBackgroundMusic()
        }
    }
}

/** Single outcome rule. Message presentation is not an unresolved decision: rewards
 * have already been committed before this rule sees the snapshot. Empty crews are
 * invalid; callers must not start or advance such a mission. */
internal fun evaluateMissionEnd(state: GameUiState, withdrawalRequested: Boolean = false): MissionEndReason? {
    if (state.currentScreen != Screen.DASHBOARD || state.result != null || state.crew.isEmpty() ||
        state.activeCrisis != null || state.showCrisisDialog) return null
    val r = state.resources
    val depleted = criticalDepletion(r)
    return when {
        depleted.isNotEmpty() -> MissionEndReason.CriticalDepletion(depleted)
        state.crisesFaced.size >= state.targetCrisisCount -> MissionEndReason.Completed
        withdrawalRequested -> MissionEndReason.Withdrawal
        else -> null
    }
}

/** Shared lesson rules, also used when the report is translated after finishing. */
fun missionLessons(result: GameResult, language: AppLanguage): List<Pair<String, Boolean>> =
    buildMissionLessons(result.finalResources, result.endReason, result.usedAbandonedHardware, language)

private fun buildMissionLessons(
    resources: Resources, reason: MissionEndReason, usedHardware: Boolean, language: AppLanguage
): List<Pair<String, Boolean>> {
    val texts = Strings.get(language)
    return buildList {
        add(when (reason) {
            MissionEndReason.Completed -> texts.lessonSuccessCore to true
            is MissionEndReason.CriticalDepletion -> texts.lessonFailureCore to false
            MissionEndReason.Withdrawal -> texts.lessonWithdrawalCore to false
        })
        add(if (usedHardware) texts.lessonHardwareUsed to true else texts.lessonHardwareUnused to false)
        add(if (resources.crewHealth < 50 || resources.morale < 50) texts.lessonCrewStrain to false
            else texts.lessonPsychology to true)
        add(if (resources.energy < 40 || resources.water < 40 || resources.oxygen < 40) texts.lessonResourceRisk to false
            else texts.lessonResources to true)
    }.distinctBy { it.first }.take(4)
}
