package io.homeassistant.companion.android.haverity.voice

import androidx.annotation.StringRes
import io.homeassistant.companion.android.R
import io.homeassistant.companion.android.common.assist.AssistEvent

/**
 * HA-Verity-owned classification for Assist failures.
 *
 * Upstream integration stays deliberately small; HA-Verity owns classification and wording.
 */
object HaVerityAssistDiagnostics {

    @StringRes
    fun recorderErrorStringRes(error: Throwable): Int = when (error) {
        is HaVerityVoiceException.InitializationFailed ->
            R.string.ha_verity_voice_error_microphone_initialization

        is HaVerityVoiceException.StartFailed ->
            R.string.ha_verity_voice_error_microphone_start

        is HaVerityVoiceException.ReadFailed ->
            R.string.ha_verity_voice_error_microphone_capture

        else ->
            R.string.ha_verity_voice_error_audio_upload
    }

    /**
     * Upstream reports pipeline-start failure as its generic assist_error Output.
     * Translate only that exact Output case. Real server-side Error messages pass through.
     */
    @StringRes
    fun pipelineErrorStringRes(
        event: AssistEvent.Message,
        genericAssistError: String,
    ): Int? = if (
        event is AssistEvent.Message.Output &&
        event.message.trim() == genericAssistError.trim()
    ) {
        R.string.ha_verity_voice_error_pipeline_start
    } else {
        null
    }
}
