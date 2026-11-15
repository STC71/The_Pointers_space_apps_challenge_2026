package com.worstcase.outpost.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.worstcase.outpost.i18n.AppLanguage
import com.worstcase.outpost.resources.Res
import com.worstcase.outpost.resources.flag_br
import com.worstcase.outpost.resources.flag_de
import com.worstcase.outpost.resources.flag_es
import com.worstcase.outpost.resources.flag_fr
import com.worstcase.outpost.resources.flag_it
import com.worstcase.outpost.resources.flag_jp
import com.worstcase.outpost.resources.flag_kr
import com.worstcase.outpost.resources.flag_sa
import com.worstcase.outpost.resources.flag_ua
import com.worstcase.outpost.resources.flag_us
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

private fun flagResource(language: AppLanguage): DrawableResource =
    when (language) {
        AppLanguage.ENGLISH -> Res.drawable.flag_us
        AppLanguage.SPANISH -> Res.drawable.flag_es
        AppLanguage.FRENCH -> Res.drawable.flag_fr
        AppLanguage.PORTUGUESE -> Res.drawable.flag_br
        AppLanguage.GERMAN -> Res.drawable.flag_de
        AppLanguage.ITALIAN -> Res.drawable.flag_it
        AppLanguage.JAPANESE -> Res.drawable.flag_jp
        AppLanguage.KOREAN -> Res.drawable.flag_kr
        AppLanguage.UKRAINIAN -> Res.drawable.flag_ua
        AppLanguage.ARABIC -> Res.drawable.flag_sa
    }

@Composable
fun FlagIcon(
    language: AppLanguage,
    width: Dp = 28.dp,
    height: Dp = 20.dp,
    modifier: Modifier = Modifier
) {
    Image(
        painter = painterResource(flagResource(language)),
        contentDescription = language.displayName,
        contentScale = ContentScale.Crop,
        modifier = modifier
            .size(width = width, height = height)
            .clip(RoundedCornerShape(3.dp))
    )
}
