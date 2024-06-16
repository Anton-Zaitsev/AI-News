package zaitsev.a.d.mirea.diplom.presentation.ui.profile.settingTheme

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import zaitsev.a.d.mirea.diplom.NewsApp
import zaitsev.a.d.mirea.diplom.prefs.SharedPreferences
import javax.inject.Inject

@HiltViewModel
class SettingThemeViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {


    var useSystemTheme by mutableStateOf(false)
        private set

    var isDarkTheme by mutableStateOf(false)
        private set



    init {
       val theme = sharedPreferences.getBoolean(SharedPreferences.themeUsing)
       isDarkTheme = theme ?: getThemeActual()
       useSystemTheme = theme == null
    }

    fun setSystemTheme(isSystem: Boolean){
        useSystemTheme = isSystem
        if (isSystem){
            (appContext as NewsApp).isTheme = null
            isDarkTheme = getThemeActual()
            sharedPreferences.clearValue(SharedPreferences.themeUsing)
        }
    }

    fun setTheme(isDarkTheme: Boolean){
        if (this.isDarkTheme != isDarkTheme){
            useSystemTheme = false
            this.isDarkTheme = isDarkTheme
            (appContext as NewsApp).isTheme =  isDarkTheme
            sharedPreferences.saveBool(SharedPreferences.themeUsing, isDarkTheme)
        }
    }

    private fun getThemeActual(): Boolean{
        return appContext.resources.configuration.uiMode and
                android.content.res.Configuration.UI_MODE_NIGHT_MASK ==
                android.content.res.Configuration.UI_MODE_NIGHT_YES
    }
}