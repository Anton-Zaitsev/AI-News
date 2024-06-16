package zaitsev.a.d.mirea.diplom

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dagger.hilt.android.HiltAndroidApp
import zaitsev.a.d.mirea.diplom.prefs.SharedPreferences

@HiltAndroidApp
class NewsApp: Application(){

    var isTheme: Boolean? by mutableStateOf(null)

    override fun onCreate() {
        super.onCreate()
        isTheme = SharedPreferences(baseContext).getBoolean(SharedPreferences.themeUsing)
    }
}