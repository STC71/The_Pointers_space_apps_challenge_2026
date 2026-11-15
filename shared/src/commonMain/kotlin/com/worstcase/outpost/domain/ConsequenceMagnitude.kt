package com.worstcase.outpost.domain

import kotlin.math.abs

enum class ConsequenceMagnitude { NONE, SMALL, MODERATE, LARGE }
enum class ConsequenceUnit { INDICATOR, RATION_DAYS, SCIENCE_POINTS }

/** Bands: indicator 1..5 / 6..15 / 16+; days 1 / 2..3 / 4+; science 1..2 / 3..4 / 5+. */
fun consequenceMagnitude(delta: Int, unit: ConsequenceUnit = ConsequenceUnit.INDICATOR): ConsequenceMagnitude = when {
    delta == 0 -> ConsequenceMagnitude.NONE
    abs(delta) <= (when (unit) { ConsequenceUnit.INDICATOR -> 5; ConsequenceUnit.RATION_DAYS -> 1; ConsequenceUnit.SCIENCE_POINTS -> 2 }) -> ConsequenceMagnitude.SMALL
    abs(delta) <= (when (unit) { ConsequenceUnit.INDICATOR -> 15; ConsequenceUnit.RATION_DAYS -> 3; ConsequenceUnit.SCIENCE_POINTS -> 4 }) -> ConsequenceMagnitude.MODERATE
    else -> ConsequenceMagnitude.LARGE
}
