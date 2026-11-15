package com.worstcase.outpost

private external fun wcoOpenExternalUrl(url: String)

actual fun openUrl(url: String) {
    // Called directly by the click handler, preserving browser user activation.
    if (url.isNotBlank()) wcoOpenExternalUrl(url.trim())
}
