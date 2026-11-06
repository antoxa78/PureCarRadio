package com.toxa.pureradio.ui

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.app.UiModeManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

/**
 * Inline speech recognition for the search field.
 *
 * Uses [SpeechRecognizer] instead of the [RecognizerIntent] activity so partial
 * results can be written straight into the field while the user is speaking and
 * no system dialog takes over the screen.
 */
class VoiceSearchController internal constructor(private val context: Context) {

    var isListening by mutableStateOf(false)
        private set

    var isAvailable by mutableStateOf(SpeechRecognizer.isRecognitionAvailable(context))
        private set

    var onPartialResult: (String) -> Unit = {}
    var onFinalResult: (String) -> Unit = {}
    var onError: (Int) -> Unit = {}

    private var recognizer: SpeechRecognizer? = null

    fun start() {
        if (isListening) return

        isAvailable = SpeechRecognizer.isRecognitionAvailable(context)
        if (!isAvailable) {
            onError(SpeechRecognizer.ERROR_CLIENT)
            return
        }

        recognizer?.destroy()
        val newRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
        recognizer = newRecognizer
        newRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                isListening = true
            }

            override fun onBeginningOfSpeech() = Unit
            override fun onRmsChanged(rmsdB: Float) = Unit
            override fun onBufferReceived(buffer: ByteArray?) = Unit
            override fun onEndOfSpeech() = Unit

            override fun onError(error: Int) {
                isListening = false
                onError(error)
            }

            override fun onResults(results: Bundle?) {
                isListening = false
                results
                    ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    ?.firstOrNull()
                    ?.trim()
                    ?.takeIf { it.isNotEmpty() }
                    ?.let(onFinalResult)
            }

            override fun onPartialResults(partialResults: Bundle?) {
                partialResults
                    ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    ?.firstOrNull()
                    ?.trim()
                    ?.takeIf { it.isNotEmpty() }
                    ?.let(onPartialResult)
            }

            override fun onEvent(eventType: Int, params: Bundle?) = Unit
        })

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, recognitionLanguage())
            putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.packageName)
        }

        try {
            newRecognizer.startListening(intent)
            // Some recognition services never call onReadyForSpeech, so flip the
            // state here as well and let the final/error callback reset it.
            isListening = true
        } catch (_: Exception) {
            isListening = false
            newRecognizer.destroy()
            recognizer = null
            onError(SpeechRecognizer.ERROR_CLIENT)
        }
    }

    fun cancel() {
        try {
            recognizer?.cancel()
        } catch (_: Exception) {
        }
        isListening = false
    }

    fun destroy() {
        try {
            recognizer?.destroy()
        } catch (_: Exception) {
        }
        recognizer = null
        isListening = false
    }

    private fun recognitionLanguage(): String {
        val tag = Locale.getDefault().toLanguageTag()
        // Android still reports Hebrew as "iw" in some places; recognisers
        // expect the modern "he" form.
        return if (tag.startsWith("iw")) "he-IL" else tag
    }
}

/**
 * Creates and remembers a [VoiceSearchController] and keeps its callbacks up
 * to date across recompositions. The recogniser is always released when the
 * caller leaves the composition.
 */
@Composable
fun rememberVoiceSearchController(
    onPartialResult: (String) -> Unit,
    onFinalResult: (String) -> Unit,
    onError: (Int) -> Unit
): VoiceSearchController {
    val context = LocalContext.current
    val latestPartial by rememberUpdatedState(onPartialResult)
    val latestFinal by rememberUpdatedState(onFinalResult)
    val latestError by rememberUpdatedState(onError)

    val controller = remember(context) {
        VoiceSearchController(context.applicationContext).apply {
            this.onPartialResult = { latestPartial(it) }
            this.onFinalResult = { latestFinal(it) }
            this.onError = { latestError(it) }
        }
    }

    DisposableEffect(controller) {
        onDispose { controller.destroy() }
    }

    return controller
}

/** True on Android Automotive and head units that run the full app in car mode. */
fun isCarUi(context: Context): Boolean {
    val uiModeManager = context.getSystemService(Context.UI_MODE_SERVICE) as? UiModeManager
    if (uiModeManager?.currentModeType == Configuration.UI_MODE_TYPE_CAR) return true
    return context.packageManager.hasSystemFeature(PackageManager.FEATURE_AUTOMOTIVE)
}
