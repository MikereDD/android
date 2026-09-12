package io.homeassistant.companion.android.haverity.voice

import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HaVeritySttReadinessWatchdogTest {

    @Test
    fun `watchdog fires after timeout`() = runTest {
        var timedOut = false
        val watchdog = HaVeritySttReadinessWatchdog(this, 1.seconds)
        watchdog.arm { timedOut = true }

        advanceTimeBy(999)
        runCurrent()
        assertFalse(timedOut)

        advanceTimeBy(1)
        runCurrent()
        assertTrue(timedOut)
    }

    @Test
    fun `confirm ready prevents timeout`() = runTest {
        var timedOut = false
        val watchdog = HaVeritySttReadinessWatchdog(this, 1.seconds)
        watchdog.arm { timedOut = true }
        watchdog.confirmReady()

        advanceTimeBy(1_000)
        runCurrent()
        assertFalse(timedOut)
    }

    @Test
    fun `cancel prevents timeout`() = runTest {
        var timedOut = false
        val watchdog = HaVeritySttReadinessWatchdog(this, 1.seconds)
        watchdog.arm { timedOut = true }
        watchdog.cancel()

        advanceTimeBy(1_000)
        runCurrent()
        assertFalse(timedOut)
    }
}
