package io.homeassistant.companion.android.haverity.voice

import io.homeassistant.companion.android.R
import io.homeassistant.companion.android.common.assist.AssistEvent
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class HaVerityAssistDiagnosticsTest {

    @Test
    fun `initialization failure maps correctly`() = assertEquals(
        R.string.ha_verity_voice_error_microphone_initialization,
        HaVerityAssistDiagnostics.recorderErrorStringRes(
            HaVerityVoiceException.InitializationFailed(),
        ),
    )

    @Test
    fun `start failure maps correctly`() = assertEquals(
        R.string.ha_verity_voice_error_microphone_start,
        HaVerityAssistDiagnostics.recorderErrorStringRes(
            HaVerityVoiceException.StartFailed(),
        ),
    )

    @Test
    fun `read failure maps correctly`() = assertEquals(
        R.string.ha_verity_voice_error_microphone_capture,
        HaVerityAssistDiagnostics.recorderErrorStringRes(
            HaVerityVoiceException.ReadFailed(-1),
        ),
    )

    @Test
    fun `other recorder failure maps to upload failure`() = assertEquals(
        R.string.ha_verity_voice_error_audio_upload,
        HaVerityAssistDiagnostics.recorderErrorStringRes(
            IllegalStateException("socket failed"),
        ),
    )

    @Test
    fun `generic output maps to pipeline start failure`() = assertEquals(
        R.string.ha_verity_voice_error_pipeline_start,
        HaVerityAssistDiagnostics.pipelineErrorStringRes(
            AssistEvent.Message.Output("Oops, an error has occurred"),
            "Oops, an error has occurred",
        ),
    )

    @Test
    fun `server error is preserved`() = assertNull(
        HaVerityAssistDiagnostics.pipelineErrorStringRes(
            AssistEvent.Message.Error("STT provider unavailable"),
            "Oops, an error has occurred",
        ),
    )
}
