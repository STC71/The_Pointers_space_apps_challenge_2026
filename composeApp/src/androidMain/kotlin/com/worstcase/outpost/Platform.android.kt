package com.worstcase.outpost

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri

/** Uses applicationContext so the link opener never retains a destroyed Activity. */
object AndroidUrlOpener {
    private var context: Context? = null

    fun initialize(context: Context) {
        this.context = context.applicationContext
    }

    fun open(url: String) {
        val uri = Uri.parse(url.trim())
        if (uri.scheme?.lowercase() !in setOf("https", "http") || uri.host.isNullOrBlank()) return
        val intent = Intent(Intent.ACTION_VIEW, uri)
            .addCategory(Intent.CATEGORY_BROWSABLE)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            context?.startActivity(intent)
        } catch (_: ActivityNotFoundException) {
            // A device without a browser must still be able to continue the mission.
        } catch (_: SecurityException) {
        }
    }
}

actual fun openUrl(url: String) {
    if (url.isNotBlank()) AndroidUrlOpener.open(url)
}
