package zaitsev.a.d.mirea.speachtextcompose.tts.builder

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import zaitsev.a.d.mirea.speachtextcompose.AnnotationIndex
import zaitsev.a.d.mirea.speachtextcompose.tts.engine.TextToSpeechEngine
import java.util.Locale

class TextToSpeechHelperIml(private val context: Context): TextToSpeechHelper{

    private var tts: TextToSpeechEngine? = null

    private var customActionForDestroy: (() -> Unit)? = null
    override fun removeSpeak(){
        tts?.destroy()
        tts = null
    }

    override fun speakMessage(
        message: String,
        onHighlightListener: (AnnotationIndex) -> Unit,
        onStartListener: () -> Unit,
        onDoneListener: () -> Unit,
        onErrorListener: (error: String) -> Unit,
        language: Locale,
        activityDestroy: (()-> Unit)?,
        pitch: Float,
        speed: Float
    ){
        removeSpeak()
        tts = TextToSpeechEngine.getInstance()
            .setOnCompletionListener(onDoneListener)
            .setOnErrorListener(onErrorListener)
            .setOnStartListener(onStartListener)
            .setLanguage(language)
            .setOnHighlightListener(onHighlightListener).apply {
                setPitchAndSpeed(pitch, speed)
                setHighlightedMessage(message)
            }

        customActionForDestroy = activityDestroy
        tts?.initTTS(context, message)
    }

    override val lifecycleEvent: LifecycleEventObserver get() = LifecycleEventObserver { _, event ->
        if (event == Lifecycle.Event.ON_DESTROY ||
            event == Lifecycle.Event.ON_STOP ||
            event == Lifecycle.Event.ON_PAUSE
        ) {
            removeSpeak()
            customActionForDestroy?.invoke()
            customActionForDestroy = null
        }
    }

    override fun resetPitchAndSpeed() {
        tts?.resetPitchAndSpeed()
    }
}