package zaitsev.a.d.mirea.diplom.di

import android.content.Context
import androidx.compose.material3.SnackbarHostState
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.drinkless.td.libcore.telegram.TdApi
import zaitsev.a.d.mirea.diplom.prefs.SharedPreferences
import zaitsev.a.d.mirea.speachtextcompose.tts.builder.TextToSpeechHelper
import zaitsev.a.d.mirea.speachtextcompose.tts.builder.TextToSpeechHelperIml
import zaitsev.a.d.mirea.telegramapi.api.TelegramHelper
import java.io.File
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideSnackbar(): SnackbarHostState = SnackbarHostState()

    @Provides
    @Singleton
    fun provideTelegramApi(@ApplicationContext appContext: Context): TelegramHelper = TelegramHelper(parameters = TdApi.TdlibParameters().apply {
        databaseDirectory = File(appContext.getExternalFilesDir(null), "tdlib").absolutePath
        useMessageDatabase = true
        useSecretChats = false
        apiId = throw Exception("enter apiID TDLib")
        apiHash = throw Exception("enter api Hash TDLib")
        systemLanguageCode = "en"
        deviceModel = "Android"
        systemVersion = throw Exception("enter systemVersion TDLib")
        applicationVersion = throw Exception("enter applicationVersion TDLib")
        enableStorageOptimizer = true
    })

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext appContext: Context): SharedPreferences =
        SharedPreferences(context = appContext)

    @Provides
    fun provideTextToSpeech(@ApplicationContext appContext: Context) : TextToSpeechHelper = TextToSpeechHelperIml(context = appContext)
}