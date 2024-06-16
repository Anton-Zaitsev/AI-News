package zaitsev.a.d.mirea.speachtextcompose.untils

import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener

internal const val DEF_SPEECH_AND_PITCH = 0.8f

internal fun Int.getErrorText(): String = when (this) {
    TextToSpeech.ERROR -> "ERROR"
    TextToSpeech.ERROR_INVALID_REQUEST -> "ERROR_INVALID_REQUEST"
    TextToSpeech.ERROR_NETWORK -> "ERROR_NETWORK"
    TextToSpeech.ERROR_NETWORK_TIMEOUT -> "ERROR_NETWORK_TIMEOUT"
    TextToSpeech.ERROR_SERVICE -> "ERROR_SERVICE"
    TextToSpeech.ERROR_SYNTHESIS -> "ERROR_SYNTHESIS"
    TextToSpeech.ERROR_NOT_INSTALLED_YET -> "ERROR_NOT_INSTALLED_YET"
    else -> "UNKNOWN"
}

internal inline fun TextToSpeech.setListener(
    crossinline onStart: (String?) -> Unit = {},
    crossinline onError: (String?) -> Unit = {},
    crossinline onRange: (Int, Int) -> Unit = { _, _ -> },
    crossinline onDone: (String?) -> Unit,
) = apply {
    setOnUtteranceProgressListener(object : UtteranceProgressListener() {
        override fun onStart(p0: String?) {
            onStart.invoke(p0)
        }

        override fun onDone(p0: String?) {
            onDone.invoke(p0)
        }

        @Deprecated("Deprecated in Java", ReplaceWith("onError.invoke(p0)"))
        override fun onError(p0: String?) {
            onError.invoke(p0)
        }

        override fun onRangeStart(utteranceId: String?, start: Int, end: Int, frame: Int) {
            super.onRangeStart(utteranceId, start, end, frame)
            onRange.invoke(start, end)
        }
    })
}