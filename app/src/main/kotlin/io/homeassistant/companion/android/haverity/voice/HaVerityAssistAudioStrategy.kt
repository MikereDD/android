package io.homeassistant.companion.android.haverity.voice

import android.media.AudioManager
import io.homeassistant.companion.android.common.assist.AssistAudioFocus
import io.homeassistant.companion.android.common.assist.AssistAudioFocusImpl
import io.homeassistant.companion.android.common.assist.AssistAudioStrategy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

/** Routes normal Assist capture through HA-Verity with upstream audio focus. */
class HaVerityAssistAudioStrategy(
    private val recorder: HaVerityVoiceAudioRecorder,
    audioManager: AudioManager? = null,
) : AssistAudioStrategy,
    AssistAudioFocus by AssistAudioFocusImpl(audioManager) {

    override suspend fun audioData(): Flow<ShortArray> = recorder.audioData()

    override val wakeWordDetected: Flow<String> = emptyFlow()
}
