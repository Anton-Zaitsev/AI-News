package zaitsev.a.d.mirea.diplom.presentation.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.SnackbarHostState
import dagger.hilt.android.AndroidEntryPoint
import zaitsev.a.d.mirea.diplom.NewsApp
import zaitsev.a.d.mirea.diplom.presentation.theme.ZaitsevNewsTheme
import zaitsev.a.d.mirea.diplom.presentation.ui.mainNavigation.NavigationGraph
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var snackbar: SnackbarHostState

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onBackPressedDispatcher.addCallback(this, enabled = true){}
        setContent {
            val theme = (applicationContext as NewsApp).isTheme ?: isSystemInDarkTheme()
            ZaitsevNewsTheme(theme) {
                NavigationGraph(snackbarHostState = snackbar)
            }
        }
    }

    companion object {
        const val NOTIFICATION_ID = 101
        const val NOTIFICATION_CHANNEL_ID = "telegramService"
        const val CHANNEL_ID = "channelID"
    }
}
