package zaitsev.a.d.mirea.speachtextcompose.tts.engine

import android.content.Context
import android.speech.tts.TextToSpeech
import zaitsev.a.d.mirea.speachtextcompose.AnnotationIndex
import zaitsev.a.d.mirea.speachtextcompose.untils.DEF_SPEECH_AND_PITCH
import zaitsev.a.d.mirea.speachtextcompose.untils.getErrorText
import zaitsev.a.d.mirea.speachtextcompose.untils.setListener
import java.util.Locale

internal class TextToSpeechEngine private constructor() {
    private var tts: TextToSpeech? = null

    private var defaultPitch = 1f
    private var defaultSpeed = 1f
    private var defLanguage: Locale? = Locale.ENGLISH
    private var _onStartListener: (() -> Unit)? = null
    private var _onDoneListener: (() -> Unit)? = null
    private var _onErrorListener: ((String) -> Unit)? = null
    private var _onHighlightListener: ((AnnotationIndex) -> Unit)? = null
    private var _message: String? = null


    internal companion object {
        private var instance: TextToSpeechEngine? = null
        internal fun getInstance(): TextToSpeechEngine {
            if (instance == null) {
                instance = TextToSpeechEngine()
            }
            return requireNotNull(instance)
        }
    }

    internal fun initTTS(context: Context, message: String) {
        tts = TextToSpeech(context) {
            if (it == TextToSpeech.SUCCESS) {
                tts?.language = defLanguage ?:  Locale.ENGLISH
                tts?.setPitch(defaultPitch)
                tts?.setSpeechRate(defaultSpeed)
                tts?.setListener(
                    onStart = {
                        _onStartListener?.invoke()
                    },
                    onError = { e ->
                        e?.let { error ->
                            _onErrorListener?.invoke(error)
                        }
                    },
                    onRange = { start, end ->
                        if (_message != null)
                            _onHighlightListener?.invoke(AnnotationIndex(start = start, end = end))
                    },
                    onDone = {
                        _onDoneListener?.invoke()
                    }
                )
                speak(message)
            } else {
                _onErrorListener?.invoke(it.getErrorText())
            }
        }
    }

    private fun speak(message: String): TextToSpeechEngine {
        tts?.speak(
            message,
            TextToSpeech.QUEUE_FLUSH,
            null,
            TextToSpeech.ACTION_TTS_QUEUE_PROCESSING_COMPLETED
        )
        return this
    }

    internal fun setPitchAndSpeed(pitch: Float, speed: Float) {
        defaultPitch = pitch
        defaultSpeed = speed
    }

    internal fun resetPitchAndSpeed() {
        defaultPitch = DEF_SPEECH_AND_PITCH
        defaultSpeed = DEF_SPEECH_AND_PITCH
    }

    internal fun setLanguage(local: Locale): TextToSpeechEngine {
        defLanguage = local
        return this
    }

    internal fun setHighlightedMessage(message: String) {
        _message = message
    }

    internal fun setOnStartListener(onStartListener: (() -> Unit)): TextToSpeechEngine {
        _onStartListener = onStartListener
        return this
    }

    internal fun setOnCompletionListener(onDoneListener: () -> Unit): TextToSpeechEngine {
        _onDoneListener = onDoneListener
        return this
    }

    internal fun setOnErrorListener(onErrorListener: (String) -> Unit): TextToSpeechEngine {
        _onErrorListener = onErrorListener
        return this
    }

    internal fun setOnHighlightListener(onHighlightListener: (AnnotationIndex) -> Unit): TextToSpeechEngine {
        _onHighlightListener = onHighlightListener
        return this
    }


    internal fun destroy() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        defLanguage = null
        _onStartListener = null
        _onDoneListener = null
        _onErrorListener = null
        _onHighlightListener = null
        _message = null
        instance = null
    }

}