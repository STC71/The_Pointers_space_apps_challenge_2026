/**
 * ============================================================
 *  ROOT BUILD.GRADLE.KTS
 *  Shared plugin versions for all modules.
 *  We do not put application code here.
 * ============================================================
 */

plugins {
    // These plugins are applied later inside each module
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.compose.multiplatform) apply false
    alias(libs.plugins.compose.compiler) apply false
}
