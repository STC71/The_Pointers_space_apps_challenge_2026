package com.worstcase.outpost.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.worstcase.outpost.data.MockData
import com.worstcase.outpost.data.TrainingContent
import com.worstcase.outpost.i18n.TrainingCopy
import com.worstcase.outpost.domain.Difficulty
import com.worstcase.outpost.domain.PlanetaryTarget
import com.worstcase.outpost.i18n.AppLanguage
import com.worstcase.outpost.i18n.Strings
import com.worstcase.outpost.presentation.GameViewModel
import com.worstcase.outpost.presentation.Screen
import com.worstcase.outpost.theme.WorstCaseTheme
import org.junit.Assert.*
import org.junit.Test
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

/** Exercises the merged UI against the real mission coordinator, without network or audio. */
@OptIn(ExperimentalTestApi::class)
class MergedUiTest {
    private lateinit var rule: SkikoComposeUiTest

    private fun narrowViewport(test: SkikoComposeUiTest.() -> Unit) =
        runDesktopComposeUiTest(width = 420, height = 800) {
            rule = this
            test()
        }

    private fun preparedMission(): GameViewModel = GameViewModel().apply {
        setLanguage(AppLanguage.SPANISH)
        finishIntro()
        selectDifficulty(Difficulty.MID)
        selectDestination(PlanetaryTarget.MOON)
        selectAnalog(MockData.getAnalogs(AppLanguage.SPANISH).first { it.planetaryTarget == PlanetaryTarget.MOON })
        confirmMissionDesign()
    }

    @Test fun educationalPanelsOpenAndCloseWithoutChangingTheMission() = narrowViewport {
        val vm = preparedMission()
        val before = vm.state.value
        val texts = Strings.get(before.currentLanguage)
        rule.setContent {
            WorstCaseTheme(language = before.currentLanguage) {
                Box(Modifier.requiredSize(420.dp, 800.dp).testTag("dashboard")) {
                    DashboardScreen(before, before.currentLanguage, vm::triggerCrisis, vm::resolveCrisis, vm::requestEndMission)
                }
            }
        }
        for ((label, title, body) in listOf(
            Triple(texts.energy, texts.eduEnergyTitle, texts.eduEnergyBody),
            Triple(texts.water, texts.eduWaterTitle, texts.eduWaterBody),
            Triple(texts.oxygen, texts.eduOxygenTitle, texts.eduOxygenBody)
        )) {
            rule.onNodeWithText("$label  ·  ℹ").performScrollTo().performClick()
            rule.onNodeWithText(title).assertIsDisplayed()
            rule.onNodeWithText(body).assertExists()
            saveScreenshot(rule.onNode(isDialog()), "education-$label")
            rule.onNodeWithText(texts.close360).assertIsDisplayed().performClick()
            rule.onNodeWithText(title).assertDoesNotExist()
            assertEquals(before, vm.state.value)
        }
        saveScreenshot("dashboard", "dashboard-es")
    }

    @Test fun settingsUseImageFlagsAndKeepLanguageAndMuteCallbacks() = narrowViewport {
        var language by mutableStateOf(AppLanguage.ENGLISH)
        var enabled by mutableStateOf(true)
        var closed = false
        rule.setContent {
            WorstCaseTheme(language = language) {
                SettingsOverlay(Strings.get(language), language, enabled, { language = it }, { enabled = it }, { closed = true })
            }
        }
        // Native language names remain accessible even though the flags are images.
        rule.onNodeWithContentDescription(AppLanguage.SPANISH.displayName).performClick()
        rule.onNodeWithText(Strings.get(AppLanguage.SPANISH).settings).assertIsDisplayed()
        rule.onNode(isToggleable()).performClick()
        rule.runOnIdle { assertEquals(AppLanguage.SPANISH, language); assertFalse(enabled) }
        rule.onNodeWithText(Strings.get(AppLanguage.SPANISH).closeSettings).performClick()
        rule.runOnIdle { assertTrue(closed) }
    }

    @Test fun illustratedAnalogSelectionAndBackUseTheProtectedPreparationFlow() = narrowViewport {
        val vm = GameViewModel().apply {
            finishIntro(); selectDifficulty(Difficulty.MID); selectDestination(PlanetaryTarget.MOON)
        }
        rule.setContent {
            val state by vm.state.collectAsState()
            WorstCaseTheme(language = state.currentLanguage) {
                Box(Modifier.requiredSize(420.dp, 800.dp).testTag("analogs")) {
                    AnalogSelectionScreen(state.currentLanguage, state.selectedDestination, vm::selectAnalog) {
                        vm.navigateBack(Screen.ANALOG_SELECTION)
                    }
                }
            }
        }
        val analog = MockData.getAnalogs(AppLanguage.ENGLISH).first { it.planetaryTarget == PlanetaryTarget.MOON }
        rule.onNodeWithText(analog.name).assertIsDisplayed()
        saveScreenshot("analogs", "analogs-en")
        rule.onNodeWithText(Strings.get(AppLanguage.ENGLISH).backButton).performClick()
        rule.runOnIdle { assertEquals(Screen.DESTINATION, vm.state.value.currentScreen) }
    }

    @Test fun selectingTheIllustratedDestinationCallsTheExistingCoordinator() = narrowViewport {
        val vm = GameViewModel().apply { finishIntro(); selectDifficulty(Difficulty.MID) }
        rule.setContent {
            WorstCaseTheme {
                Box(Modifier.requiredSize(420.dp, 800.dp).testTag("destination")) {
                    DestinationScreen(AppLanguage.ENGLISH, vm::selectDestination) { vm.navigateBack(Screen.DESTINATION) }
                }
            }
        }
        saveScreenshot("destination", "destinations-en")
        rule.onNodeWithText(Strings.get(AppLanguage.ENGLISH).destinationMars).performScrollTo().performClick()
        rule.runOnIdle {
            assertEquals(PlanetaryTarget.MARS, vm.state.value.selectedDestination)
            assertEquals(Screen.ANALOG_SELECTION, vm.state.value.currentScreen)
        }
    }

    @Test fun changingScriptAppliesTypographyAndArabicDirectionWithoutHidingContent() = narrowViewport {
        var language by mutableStateOf(AppLanguage.ENGLISH)
        var direction: LayoutDirection? = null
        var family: FontFamily? = null
        rule.setContent {
            WorstCaseTheme(language = language) {
                val activeDirection = LocalLayoutDirection.current
                val activeFamily = MaterialTheme.typography.bodyLarge.fontFamily
                SideEffect { direction = activeDirection; family = activeFamily }
                Box(Modifier.requiredSize(420.dp, 800.dp).testTag("language")) {
                    HomeScreen(language, {}, {})
                }
            }
        }
        for (next in listOf(AppLanguage.JAPANESE, AppLanguage.KOREAN, AppLanguage.ARABIC, AppLanguage.ENGLISH)) {
            rule.runOnIdle { language = next }
            rule.onNodeWithText(Strings.get(next).tagline).assertIsDisplayed()
            rule.runOnIdle {
                assertEquals(if (next == AppLanguage.ARABIC) LayoutDirection.Rtl else LayoutDirection.Ltr, direction)
                if (next == AppLanguage.ENGLISH) assertEquals(FontFamily.Default, family)
                else assertNotEquals(FontFamily.Default, family)
            }
            saveScreenshot("language", "home-${next.code}")
        }
    }

    @Test fun houstonHistoryRemainsReadOnlyAndAllNotesAreReachable() = narrowViewport {
        val vm = preparedMission()
        vm.triggerCrisis()
        val crisis = vm.state.value.activeCrisis!!
        vm.requestAiAssessment(crisis.id, vm.state.value.activeCrisisOccurrence!!)
        val before = vm.state.value
        rule.setContent {
            WorstCaseTheme(language = before.currentLanguage) {
                DashboardScreen(before, before.currentLanguage, vm::triggerCrisis, vm::resolveCrisis, vm::requestEndMission)
            }
        }
        val title = TrainingCopy.HOUSTON.text(before.currentLanguage)
        rule.onNodeWithText(title).performScrollTo().performClick()
        for (note in TrainingContent.historicalNotes(before.currentLanguage)) {
            rule.onNodeWithText(note).performScrollTo().assertIsDisplayed()
        }
        saveScreenshot(rule.onNode(isDialog()), "houston-history-es")
        rule.onNodeWithText(title).performScrollTo().performClick()
        rule.onNodeWithText(TrainingContent.historicalNotes(before.currentLanguage).first()).assertDoesNotExist()
        rule.runOnIdle { assertEquals(before, vm.state.value) }
    }

    @Test fun originalDesignScenariosSupplementTheCurrentCostsWithoutChangingTheDraft() = narrowViewport {
        val vm = GameViewModel().apply {
            setLanguage(AppLanguage.SPANISH); finishIntro(); selectDifficulty(Difficulty.MID)
            selectDestination(PlanetaryTarget.MOON)
            selectAnalog(MockData.getAnalogs(AppLanguage.SPANISH).first { it.planetaryTarget == PlanetaryTarget.MOON })
        }
        val before = vm.state.value
        rule.setContent {
            WorstCaseTheme(language = before.currentLanguage) {
                Box(Modifier.requiredSize(420.dp, 800.dp).testTag("design")) {
                    MissionDesignScreen(before.selectedAnalog, before.missionPlan, before.currentLanguage,
                        vm::updateMissionPlan, vm::confirmMissionDesign) { vm.navigateBack(Screen.MISSION_DESIGN) }
                }
            }
        }
        val costs = MockData.worstCaseHints(before.missionPlan, before.currentLanguage)
        costs.forEach { rule.onNodeWithText("• $it").assertExists() }
        rule.onNodeWithText(TrainingCopy.DESIGN.text(before.currentLanguage)).performScrollTo().performClick()
        TrainingContent.designRisks(before.missionPlan, before.currentLanguage).forEach {
            rule.onNodeWithText(it).performScrollTo().assertIsDisplayed()
        }
        saveScreenshot("design", "design-training-es")
        rule.onNodeWithText(Strings.get(before.currentLanguage).confirmMissionDesign).performScrollTo().assertIsDisplayed()
        rule.runOnIdle { assertEquals(before, vm.state.value) }
    }

    @Test fun finalReflectionsAreVisibleWithoutReplacingTheMissionResult() = narrowViewport {
        val vm = preparedMission()
        vm.requestEndMission()
        val before = vm.state.value
        val result = before.result!!
        rule.setContent {
            WorstCaseTheme(language = before.currentLanguage) {
                Box(Modifier.requiredSize(420.dp, 800.dp).testTag("debrief")) {
                    DebriefScreen(result, before.currentLanguage, vm::restart)
                }
            }
        }
        TrainingContent.debriefReflections(result.finalResources, before.currentLanguage).forEach {
            rule.onNodeWithText(it).performScrollTo().assertIsDisplayed()
        }
        saveScreenshot("debrief", "debrief-training-es")
        rule.onNodeWithText(Strings.get(before.currentLanguage).playAgain).performScrollTo().assertIsDisplayed()
        rule.runOnIdle { assertEquals(before, vm.state.value) }
    }

    @Test fun restoredIntroNarrativeLeavesMissionStateAndStartActionAvailable() = narrowViewport {
        val vm = GameViewModel().apply { setLanguage(AppLanguage.SPANISH) }
        val before = vm.state.value
        val texts = Strings.get(before.currentLanguage)
        rule.setContent {
            WorstCaseTheme(language = before.currentLanguage) {
                Box(Modifier.requiredSize(420.dp, 800.dp).testTag("intro")) {
                    IntroScreen(before.currentLanguage, vm::setLanguage, vm::finishIntro)
                }
            }
        }
        repeat(2) { rule.onNodeWithText(texts.introNext).performClick() }
        rule.onNodeWithText(texts.introPage3Body).performScrollTo().assertIsDisplayed()
        rule.onNodeWithText(texts.introStart).assertIsDisplayed()
        saveScreenshot("intro", "intro-training-es")
        rule.runOnIdle { assertEquals(before, vm.state.value) }
    }

    private fun saveScreenshot(tag: String, name: String) = saveScreenshot(rule.onNodeWithTag(tag), name)

    private fun saveScreenshot(node: SemanticsNodeInteraction, name: String) {
        val pixels = node.captureToImage().toPixelMap()
        val image = BufferedImage(pixels.width, pixels.height, BufferedImage.TYPE_INT_ARGB)
        for (y in 0 until pixels.height) for (x in 0 until pixels.width) {
            val color = pixels[x, y]
            val argb = ((color.alpha * 255).toInt() shl 24) or ((color.red * 255).toInt() shl 16) or
                ((color.green * 255).toInt() shl 8) or (color.blue * 255).toInt()
            image.setRGB(x, y, argb)
        }
        val directory = File("build/reports/ui-screenshots").apply { mkdirs() }
        ImageIO.write(image, "png", File(directory, "$name.png"))
    }
}
