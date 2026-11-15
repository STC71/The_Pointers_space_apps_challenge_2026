package com.worstcase.outpost.ui

/**
 * Full-screen random photograph + dark overlay.
 * Maps each analog id to its drawable when available.
 *
 * File names in composeResources/drawable (jpg/jpeg) become Res.drawable.<name_without_extension>.
 */

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import com.worstcase.outpost.resources.Res
import com.worstcase.outpost.resources.analog_atacama
import com.worstcase.outpost.resources.analog_blackpoint
import com.worstcase.outpost.resources.analog_boulby
import com.worstcase.outpost.resources.analog_cinderlake
import com.worstcase.outpost.resources.analog_concordia
import com.worstcase.outpost.resources.analog_dachstein
import com.worstcase.outpost.resources.analog_dryvalleys
import com.worstcase.outpost.resources.analog_etna
import com.worstcase.outpost.resources.analog_haughton
import com.worstcase.outpost.resources.analog_hiseas
import com.worstcase.outpost.resources.analog_iceland
import com.worstcase.outpost.resources.analog_lanzarote
import com.worstcase.outpost.resources.analog_lunares
import com.worstcase.outpost.resources.analog_mdrs
import com.worstcase.outpost.resources.analog_meteorcrater
import com.worstcase.outpost.resources.analog_moseslake
import com.worstcase.outpost.resources.analog_namib
import com.worstcase.outpost.resources.analog_neemo
import com.worstcase.outpost.resources.analog_pilbara
import com.worstcase.outpost.resources.analog_riotinto
import com.worstcase.outpost.resources.analog_svalbard
import com.worstcase.outpost.resources.analog_tabernas
import com.worstcase.outpost.resources.analog_wadirum
import com.worstcase.outpost.resources.bg_art_001
import com.worstcase.outpost.resources.bg_art_002
import com.worstcase.outpost.resources.bg_artemis_splashdown
import com.worstcase.outpost.resources.bg_iss_001
import com.worstcase.outpost.resources.bg_mars_express
import com.worstcase.outpost.resources.bg_nhq_001
import com.worstcase.outpost.resources.bg_nhq_002
import com.worstcase.outpost.resources.flagstaff
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

fun allBackgroundPhotos(): List<DrawableResource> = listOf(
    Res.drawable.analog_atacama,
    Res.drawable.analog_lanzarote,
    Res.drawable.analog_riotinto,
    Res.drawable.analog_haughton,
    Res.drawable.analog_dryvalleys,
    Res.drawable.analog_hiseas,
    Res.drawable.analog_mdrs,
    Res.drawable.analog_iceland,
    Res.drawable.analog_svalbard,
    Res.drawable.analog_pilbara,
    Res.drawable.analog_etna,
    Res.drawable.analog_dachstein,
    Res.drawable.analog_concordia,
    Res.drawable.analog_blackpoint,
    Res.drawable.analog_moseslake,
    Res.drawable.flagstaff,
    Res.drawable.analog_boulby,
    Res.drawable.analog_tabernas,
    Res.drawable.analog_namib,
    Res.drawable.analog_meteorcrater,
    Res.drawable.analog_lunares,
    Res.drawable.analog_neemo,
    Res.drawable.analog_wadirum,
    Res.drawable.analog_cinderlake,
    Res.drawable.bg_art_001,
    Res.drawable.bg_art_002,
    Res.drawable.bg_iss_001,
    Res.drawable.bg_artemis_splashdown,
    Res.drawable.bg_mars_express,
    Res.drawable.bg_nhq_001,
    Res.drawable.bg_nhq_002
)

/** Photo for a specific analog site (cards, 360 overlay, dashboard). */
fun photoForAnalogId(id: String): DrawableResource? {
    return when (id) {
        "atacama" -> Res.drawable.analog_atacama
        "lanzarote" -> Res.drawable.analog_lanzarote
        "riotinto" -> Res.drawable.analog_riotinto
        "haughton" -> Res.drawable.analog_haughton
        "dryvalleys" -> Res.drawable.analog_dryvalleys
        "hiseas" -> Res.drawable.analog_hiseas
        "mdrs" -> Res.drawable.analog_mdrs
        "iceland" -> Res.drawable.analog_iceland
        "svalbard" -> Res.drawable.analog_svalbard
        "pilbara" -> Res.drawable.analog_pilbara
        "etna" -> Res.drawable.analog_etna
        "dachstein" -> Res.drawable.analog_dachstein
        // New sites — file names may omit underscores (cinderlake, wadirum, …)
        "concordia" -> Res.drawable.analog_concordia
        "blackpoint" -> Res.drawable.analog_blackpoint
        "moses_lake" -> Res.drawable.analog_moseslake
        "flagstaff" -> Res.drawable.flagstaff
        "boulby" -> Res.drawable.analog_boulby
        "tabernas" -> Res.drawable.analog_tabernas
        "namib" -> Res.drawable.analog_namib
        "meteor_crater" -> Res.drawable.analog_meteorcrater
        "lunares" -> Res.drawable.analog_lunares
        "nemo" -> Res.drawable.analog_neemo
        "wadi_rum" -> Res.drawable.analog_wadirum
        "cinder_lake" -> Res.drawable.analog_cinderlake
        else -> null
    }
}

/**
 * NASA Artemis I Orion Pacific splashdown background for the welcome-home screen.
 */
fun welcomeHomeBackground(): DrawableResource {
    return Res.drawable.bg_artemis_splashdown
}

@Composable
fun RandomAnalogBackground(
    photo: DrawableResource? = null,
    dimAlphaTop: Float = 0.40f,
    dimAlphaMid: Float = 0.58f,
    dimAlphaBottom: Float = 0.72f,
    content: @Composable BoxScope.() -> Unit
) {
    val chosen = photo ?: remember { allBackgroundPhotos().random() }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(chosen),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.Black.copy(alpha = dimAlphaTop),
                            Color.Black.copy(alpha = dimAlphaMid),
                            Color.Black.copy(alpha = dimAlphaBottom)
                        )
                    )
                )
        )
        content()
    }
}
