package zaitsev.a.d.mirea.speachtextcompose.tts.builder

import androidx.lifecycle.LifecycleEventObserver
import zaitsev.a.d.mirea.speachtextcompose.AnnotationIndex
import zaitsev.a.d.mirea.speachtextcompose.untils.DEF_SPEECH_AND_PITCH
import java.util.Locale

interface TextToSpeechHelper {
    fun removeSpeak()
    fun speakMessage(
        message: String,
        onHighlightListener: (AnnotationIndex) -> Unit,
        onStartListener: () -> Unit = {},
        onDoneListener: () -> Unit = {},
        onErrorListener: (error: String) -> Unit = {},
        language: Locale = Locale.forLanguageTag("ru"),
        activityDestroy: (()-> Unit)? = null,
        pitch: Float = DEF_SPEECH_AND_PITCH, speed: Float = DEF_SPEECH_AND_PITCH
    )

    val lifecycleEvent: LifecycleEventObserver
    fun resetPitchAndSpeed()
}