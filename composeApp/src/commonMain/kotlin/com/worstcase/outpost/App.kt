package com.worstcase.outpost

/**
 * Root UI: navigation + global settings on every screen.
 */

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.worstcase.outpost.i18n.Strings
import com.worstcase.outpost.presentation.GameViewModel
import com.worstcase.outpost.presentation.Screen
import com.worstcase.outpost.theme.WorstCaseTheme
import com.worstcase.outpost.ui.AnalogSelectionScreen
import com.worstcase.outpost.ui.DashboardScreen
import com.worstcase.outpost.ui.DestinationScreen
import com.worstcase.outpost.ui.DebriefScreen
import com.worstcase.outpost.ui.GlobalSettingsButton
import com.worstcase.outpost.ui.HomeScreen
import com.worstcase.outpost.ui.IntroScreen
import com.worstcase.outpost.ui.MissionDesignScreen
import com.worstcase.outpost.ui.SettingsOverlay
import com.worstcase.outpost.ui.WelcomeHomeScreen
import com.worstcase.outpost.ui.DecisionReportDialog
import com.worstcase.outpost.apod.rememberApodFrame
import androidx.compose.runtime.LaunchedEffect
import com.worstcase.outpost.haptics.HapticType
import com.worstcase.outpost.haptics.Haptics

@Composable
fun App() {
    val viewModel = remember { GameViewModel() }
    val state by viewModel.state.collectAsState()
    val texts = Strings.get(state.currentLanguage)
    WorstCaseTheme(language = state.currentLanguage) {
        val backSource = state.currentScreen
        val apodFrame = rememberApodFrame(state.currentScreen == Screen.INTRO || state.currentScreen == Screen.HOME)

        PreparationBackHandler(
            enabled = !state.showSettings && state.currentScreen in setOf(
                Screen.DESTINATION, Screen.ANALOG_SELECTION, Screen.MISSION_DESIGN
            ),
            onBack = { viewModel.navigateBack(backSource) }
        )

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                when (state.currentScreen) {
                    Screen.INTRO -> {
                        IntroScreen(
                            currentLanguage = state.currentLanguage,
                            apodFrame = apodFrame,
                            onLanguageSelected = { viewModel.setLanguage(it) },
                            onFinished = { viewModel.finishIntro() }
                        )
                    }
                    Screen.HOME -> {
                        HomeScreen(
                            currentLanguage = state.currentLanguage,
                            apodFrame = apodFrame,
                            onLanguageSelected = { viewModel.setLanguage(it) },
                            onDifficultySelected = { viewModel.selectDifficulty(it) },
                            playerName = state.playerName,
                            onPlayerNameChange = { viewModel.setPlayerName(it) }
                        )
                    }
                    Screen.DESTINATION -> {
                        DestinationScreen(
                            currentLanguage = state.currentLanguage,
                            onDestinationSelected = { viewModel.selectDestination(it) },
                            onBack = { viewModel.navigateBack(Screen.DESTINATION) }
                        )
                    }
                    Screen.ANALOG_SELECTION -> {
                        AnalogSelectionScreen(
                            currentLanguage = state.currentLanguage,
                            selectedDestination = state.selectedDestination,
                            onAnalogSelected = { viewModel.selectAnalog(it) },
                            onBack = { viewModel.navigateBack(Screen.ANALOG_SELECTION) }
                        )
                    }
                    Screen.MISSION_DESIGN -> {
                        MissionDesignScreen(
                            analog = state.selectedAnalog,
                            plan = state.missionPlan,
                            currentLanguage = state.currentLanguage,
                            onPlanChanged = { viewModel.updateMissionPlan(it) },
                            onConfirm = { viewModel.confirmMissionDesign() },
                            onBack = { viewModel.navigateBack(Screen.MISSION_DESIGN) }
                        )
                    }
                    Screen.DASHBOARD -> {
                        DashboardScreen(
                            state = state,
                            currentLanguage = state.currentLanguage,
                            onTriggerCrisis = { viewModel.triggerCrisis() },
                            onResolveCrisis = { crisisId, occurrence, optionId ->
                                viewModel.resolveCrisis(crisisId, occurrence, optionId)
                            },
                            onEndMission = { viewModel.requestEndMission() },
                            onTogglePanorama = { viewModel.setPanoramaViewerVisible(it) },
                            onRequestAiCopilot = { crisisId, occurrence ->
                                viewModel.requestAiAssessment(crisisId, occurrence)
                            },
                            onDismissAiCopilot = { viewModel.dismissAiAssessment() },
                            onDismissGoodNews = { viewModel.dismissGoodNews() },
                            onShowLastDecisionReport = { viewModel.showLastDecisionReport() }
                        )
                    }
                    Screen.WELCOME_HOME -> {
                        WelcomeHomeScreen(
                            currentLanguage = state.currentLanguage,
                            onContinue = { viewModel.continueFromWelcomeHome() },
                            onShowLastDecisionReport = if (state.lastDecisionReport != null)
                                ({ viewModel.showLastDecisionReport() }) else null
                        )
                    }
                    Screen.DEBRIEF -> {
                        DebriefScreen(
                            result = state.result,
                            currentLanguage = state.currentLanguage,
                            onRestart = { viewModel.restart() },
                            onShowLastDecisionReport = if (state.lastDecisionReport != null)
                                ({ viewModel.showLastDecisionReport() }) else null
                        )
                    }
                }

                // Vibración: nueva crisis
				LaunchedEffect(state.activeCrisisOccurrence, state.showCrisisDialog) {
    				if (state.showCrisisDialog && state.activeCrisisOccurrence != null) {
        				Haptics.play(HapticType.WARNING)
    				}
				}

				// Vibración: buena noticia
				LaunchedEffect(state.activeGoodNews, state.showGoodNewsDialog) {
    				if (state.showGoodNewsDialog && state.activeGoodNews != null) {
        				Haptics.play(HapticType.SUCCESS)
    				}
				}

				LaunchedEffect(state.currentScreen) {
    				if (state.currentScreen == Screen.WELCOME_HOME) Haptics.play(HapticType.SUCCESS)
				}

				if (!state.showSettings) {
                    GlobalSettingsButton(
                        label = texts.settings,
                        onClick = { viewModel.setShowSettings(true) },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 4.dp, end = 4.dp)
                    )
                }

                if (state.showSettings) {
                    SettingsOverlay(
                        texts = texts,
                        currentLanguage = state.currentLanguage,
                        soundEnabled = state.soundEnabled,
                        onLanguageSelected = { viewModel.setLanguage(it) },
                        onToggleSound = { viewModel.setSoundEnabled(it) },
                        onClose = { viewModel.setShowSettings(false) }
                    )
                }

                if (state.showDecisionReport) {
                    state.lastDecisionReport?.let { report ->
                        DecisionReportDialog(report, state.currentLanguage) {
                            viewModel.dismissDecisionReport(report.forecast.crisisId, report.forecast.occurrence)
                        }
                    }
                }
            }
        }
    }
}
