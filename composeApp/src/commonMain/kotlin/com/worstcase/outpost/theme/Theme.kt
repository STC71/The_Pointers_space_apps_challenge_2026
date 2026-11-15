package com.worstcase.outpost.theme

/**
 * ============================================================
 *  THEME.KT
 *  This file defines the visual style of the whole application.
 *
 *  We use a dark theme inspired by NASA control rooms:
 *  - Very dark blue background
 *  - Bright cyan for important elements
 *  - Orange for warnings and critical buttons
 *  - Red for danger
 * ============================================================
 */

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.LayoutDirection
import com.worstcase.outpost.i18n.AppLanguage
import com.worstcase.outpost.resources.Res
import com.worstcase.outpost.resources.noto_sans_arabic
import com.worstcase.outpost.resources.noto_sans_jp
import com.worstcase.outpost.resources.noto_sans_kr
import org.jetbrains.compose.resources.Font
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ----------------------------------------------------------
//  Color definitions
//  Each color has a clear name so anyone can understand it.
// ----------------------------------------------------------

/** Almost black blue – used as the main background */
val NasaDarkBlue = Color(0xFF0B0F19)

/** Slightly lighter blue – used for cards and surfaces */
val NasaBlue = Color(0xFF1B2838)

/** Bright cyan – used for primary buttons and highlights */
val NasaAccent = Color(0xFF00A8E8)

/** Strong orange – used for the “worst case” button and warnings */
val AlertOrange = Color(0xFFFF6B35)

/** Bright amber for "attention" states (distinct from red). */
val WarningYellow = Color(0xFFFFD60A)

/** Strong red – used when something is critical */
val CriticalRed = Color(0xFFE63946)

/** Soft green – used for positive values */
val SuccessGreen = Color(0xFF2A9D8F)

/** Almost white – main text color */
val TextPrimary = Color(0xFFE8EEF4)

/** Gray-blue – secondary text color */
val TextSecondary = Color(0xFFA8B2C1)

// ----------------------------------------------------------
//  Material 3 color scheme
//  We only define the dark scheme because the game is always dark.
// ----------------------------------------------------------
private val DarkColorScheme = darkColorScheme(
    primary = NasaAccent,          // Main interactive color
    onPrimary = Color.Black,       // Text on top of primary color
    secondary = AlertOrange,       // Secondary interactive color
    onSecondary = Color.Black,     // Text on top of secondary color
    background = NasaDarkBlue,     // Screen background
    onBackground = TextPrimary,    // Text on background
    surface = NasaBlue,            // Cards, panels, dialogs
    onSurface = TextPrimary,       // Text on surfaces
    error = CriticalRed,           // Error / danger color
    onError = Color.White          // Text on error color
)

/**
 * This is the main theme function.
 * Every screen of the app must be wrapped inside WorstCaseTheme { ... }
 * so that all colors and styles are applied correctly.
 */
@Composable
fun WorstCaseTheme(
    language: AppLanguage = AppLanguage.ENGLISH,
    content: @Composable () -> Unit
) {
    // Loading one selected script leaves the first frame and navigation available.
    val family = when (language) {
        AppLanguage.JAPANESE -> FontFamily(Font(Res.font.noto_sans_jp))
        AppLanguage.KOREAN -> FontFamily(Font(Res.font.noto_sans_kr))
        AppLanguage.ARABIC -> FontFamily(Font(Res.font.noto_sans_arabic))
        else -> FontFamily.Default
    }
    val typography = remember(family) { missionTypography(family) }
    CompositionLocalProvider(
        LocalLayoutDirection provides if (language == AppLanguage.ARABIC) LayoutDirection.Rtl else LayoutDirection.Ltr
    ) {
        MaterialTheme(colorScheme = DarkColorScheme, typography = typography, content = content)
    }
}

private fun missionTypography(family: FontFamily): Typography = Typography().let { base ->
    base.copy(
        displayLarge = base.displayLarge.copy(fontFamily = family),
        displayMedium = base.displayMedium.copy(fontFamily = family),
        displaySmall = base.displaySmall.copy(fontFamily = family),
        headlineLarge = base.headlineLarge.copy(fontFamily = family),
        headlineMedium = base.headlineMedium.copy(fontFamily = family),
        headlineSmall = base.headlineSmall.copy(fontFamily = family),
        titleLarge = base.titleLarge.copy(fontFamily = family),
        titleMedium = base.titleMedium.copy(fontFamily = family),
        titleSmall = base.titleSmall.copy(fontFamily = family),
        bodyLarge = base.bodyLarge.copy(fontFamily = family),
        bodyMedium = base.bodyMedium.copy(fontFamily = family),
        bodySmall = base.bodySmall.copy(fontFamily = family),
        labelLarge = base.labelLarge.copy(fontFamily = family),
        labelMedium = base.labelMedium.copy(fontFamily = family),
        labelSmall = base.labelSmall.copy(fontFamily = family)
    )
}
