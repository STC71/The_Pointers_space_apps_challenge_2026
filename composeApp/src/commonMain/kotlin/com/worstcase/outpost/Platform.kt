package com.worstcase.outpost

/**
 * Opens an HTTP(S) URL in the system browser (Desktop / Android / Wasm).
 * Used for NASA/ESA 360° panorama reference pages.
 */
expect fun openUrl(url: String)
