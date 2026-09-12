package io.homeassistant.companion.android.haverity.voice

import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HaVeritySttReadinessWatchdog(
    private val scope: CoroutineScope,
    private val timeout: Duration = DEFAULT_TIMEOUT,
) {
    private var job: Job? = null

    fun arm(onTimeout: () -> Unit) {
        cancel()
        job = scope.launch {
            delay(timeout)
            onTimeout()
        }
    }

    fun confirmReady() = cancel()

    fun cancel() {
        job?.cancel()
        job = null
    }

    companion object {
        val DEFAULT_TIMEOUT: Duration = 10.seconds
    }
}
