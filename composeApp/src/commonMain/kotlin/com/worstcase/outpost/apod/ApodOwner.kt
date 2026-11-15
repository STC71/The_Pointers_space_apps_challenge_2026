package com.worstcase.outpost.apod

import androidx.compose.runtime.*
import coil3.Image
import coil3.ImageLoader
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

/** Remember in App, outside navigation and Intro's per-page composition. */
@Composable
internal fun rememberApodFrame(visible: Boolean): ApodFrame<Image>? {
    val context = LocalPlatformContext.current
    val loader = remember(context) {
        // The bounded repository record owns persistence; no second, unbounded image cache.
        ImageLoader.Builder(context).diskCache(null).memoryCache(null).build()
    }
    val repository = remember(context, loader) {
        ApodRepository(
            storage = createApodStorage(context),
            request = ::requestApodBytes,
            decode = { bytes ->
                val result = loader.execute(ImageRequest.Builder(context).data(bytes).size(1600, 1200).build())
                val image = (result as? SuccessResult)?.image ?: error("APOD image decode failed")
                require(image.width in 1..1600 && image.height in 1..1200)
                image
            }
        )
    }
    val isVisible by rememberUpdatedState(visible)
    LaunchedEffect(repository) {
        while (true) {
            if (isVisible) {
                try {
                    withContext(Dispatchers.Default) { repository.refreshIfNeeded() }
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Throwable) {
                    println("APOD error: ${e::class.simpleName}: ${e.message}")
                }
            }
            delay(60_000)
        }
    }
    DisposableEffect(loader) { onDispose { loader.shutdown() } }
    val frame by repository.frame.collectAsState()
    return frame
}
