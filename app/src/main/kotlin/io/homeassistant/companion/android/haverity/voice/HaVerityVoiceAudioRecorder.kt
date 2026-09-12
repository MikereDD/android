package io.homeassistant.companion.android.haverity.voice

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder.AudioSource
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.isActive
import timber.log.Timber

private const val VOICE_SAMPLE_RATE = 16000

/**
 * Fallback sample rate in Hz. Per the Android documentation, 44100Hz is
 * the only rate guaranteed to work on all devices. Used when the device
 * does not support [VOICE_SAMPLE_RATE].
 */
private const val FALLBACK_SAMPLE_RATE = 44100

// Only format "[g]uaranteed to be supported by devices"
private const val VOICE_AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT

private const val VOICE_AUDIO_SOURCE = AudioSource.MIC
private const val VOICE_CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO

/** Duration of each read chunk in milliseconds. */
private const val READ_CHUNK_DURATION_MS = 10

/** Number of samples per read chunk at [VOICE_SAMPLE_RATE] (10ms at 16kHz). */
private const val READ_CHUNK_SIZE = VOICE_SAMPLE_RATE * READ_CHUNK_DURATION_MS / 1000

/** Number of samples per read chunk at [FALLBACK_SAMPLE_RATE] (10ms at 44.1kHz). */
private const val FALLBACK_READ_CHUNK_SIZE = FALLBACK_SAMPLE_RATE * READ_CHUNK_DURATION_MS / 1000

/** Typed capture failures delivered to the existing Assist error handler. */
sealed class HaVerityVoiceException(message: String, cause: Throwable? = null) : RuntimeException(message, cause) {
    class InitializationFailed(cause: Throwable? = null) :
        HaVerityVoiceException("Microphone could not be initialized", cause)

    class StartFailed(cause: Throwable? = null) :
        HaVerityVoiceException("Microphone recording could not start", cause)

    class ReadFailed(val errorCode: Int) :
        HaVerityVoiceException("Microphone audio capture failed with code $errorCode")
}

/** Owns capture for a normal Assist session; wake-word capture remains upstream. */
class HaVerityVoiceAudioRecorder @Inject constructor() {

    /**
     * Captures PCM mono audio at 16 kHz, retaining upstream's 44.1 kHz fallback.
     * Each collection owns its recorder and releases it on cancellation or failure.
     * Intended for the normal Assist pipeline's single collector.
     */
    fun audioData(): Flow<ShortArray> = flow {
        val recorder = try {
            createVoiceAudioRecord()
        } catch (e: IllegalArgumentException) {
            throw HaVerityVoiceException.InitializationFailed(e)
        }
        try {
            if (recorder.state != AudioRecord.STATE_INITIALIZED) {
                throw HaVerityVoiceException.InitializationFailed()
            }
            startRecording(recorder)
            val actualSampleRate = recorder.sampleRate
            val needsDownsampling = actualSampleRate != VOICE_SAMPLE_RATE
            val chunkSize = if (needsDownsampling) FALLBACK_READ_CHUNK_SIZE else READ_CHUNK_SIZE
            val buffer = ShortArray(chunkSize)
            while (currentCoroutineContext().isActive) {
                val readResult = recorder.read(buffer, 0, chunkSize)
                when {
                    readResult > 0 -> {
                        val chunk = buffer.copyOf(readResult)
                        emit(
                            if (needsDownsampling) {
                                downsample(chunk, fromRate = actualSampleRate, toRate = VOICE_SAMPLE_RATE)
                            } else {
                                chunk
                            },
                        )
                    }

                    readResult < 0 -> throw HaVerityVoiceException.ReadFailed(readResult)
                }
            }
        } finally {
            try {
                if (recorder.recordingState == AudioRecord.RECORDSTATE_RECORDING) recorder.stop()
            } catch (e: IllegalStateException) {
                Timber.e(e, "Error stopping HA-Verity AudioRecord")
            } finally {
                recorder.release()
            }
        }
    }.flowOn(Dispatchers.IO)

    private fun startRecording(recorder: AudioRecord) {
        try {
            recorder.startRecording()
        } catch (e: IllegalStateException) {
            throw HaVerityVoiceException.StartFailed(e)
        }
        if (recorder.recordingState != AudioRecord.RECORDSTATE_RECORDING) {
            throw HaVerityVoiceException.StartFailed()
        }
    }
}

@SuppressLint("MissingPermission")
private fun createVoiceAudioRecord(): AudioRecord {
    try {
        val recorder = createAudioRecord(sampleRate = VOICE_SAMPLE_RATE, chunkSize = READ_CHUNK_SIZE)
        if (recorder.state == AudioRecord.STATE_INITIALIZED) return recorder
        Timber.w(
            "AudioRecord at ${VOICE_SAMPLE_RATE}Hz failed to initialize current state ${recorder.state}, falling back to ${FALLBACK_SAMPLE_RATE}Hz",
        )
        recorder.release()
    } catch (e: IllegalArgumentException) {
        Timber.w(e, "AudioRecord does not support sample rate ${VOICE_SAMPLE_RATE}Hz")
    }

    return createAudioRecord(sampleRate = FALLBACK_SAMPLE_RATE, chunkSize = FALLBACK_READ_CHUNK_SIZE)
}

@SuppressLint("MissingPermission")
private fun createAudioRecord(sampleRate: Int, chunkSize: Int): AudioRecord {
    val minBuffer = AudioRecord.getMinBufferSize(sampleRate, VOICE_CHANNEL_CONFIG, VOICE_AUDIO_FORMAT)
    val adjustedBufferSize = maxOf(minBuffer, chunkSize * 4)
    return AudioRecord(
        VOICE_AUDIO_SOURCE,
        sampleRate,
        VOICE_CHANNEL_CONFIG,
        VOICE_AUDIO_FORMAT,
        adjustedBufferSize,
    )
}

/**
 * Downsamples a [ShortArray] of PCM audio from [fromRate] to [toRate] using
 * linear interpolation.
 *
 * For each output sample, computes the corresponding fractional position in the
 * input and linearly interpolates between the two nearest input samples. This
 * produces acceptable quality for voice audio where the source is 44.1kHz and the
 * target is 16kHz.
 */
private fun downsample(input: ShortArray, fromRate: Int, toRate: Int): ShortArray {
    check(toRate > 0 && fromRate > 0) { "Sample rates must be positive" }
    check(fromRate >= toRate) { "Downsampling requires fromRate >= toRate" }

    val ratio = fromRate.toDouble() / toRate
    val outputSize = (input.size / ratio).toInt()
    val output = ShortArray(outputSize)

    for (i in 0 until outputSize) {
        val srcPos = i * ratio
        val srcIndex = srcPos.toInt()
        val fraction = srcPos - srcIndex

        output[i] = if (srcIndex + 1 < input.size) {
            val sample = input[srcIndex] * (1.0 - fraction) + input[srcIndex + 1] * fraction
            sample.toInt().toShort()
        } else {
            input[srcIndex]
        }
    }

    return output
}
