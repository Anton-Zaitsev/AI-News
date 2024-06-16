package zaitsev.a.d.mirea.diplom.presentation.ui.profile.settingNotification

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import zaitsev.a.d.mirea.diplom.di.MainDispatcher
import zaitsev.a.d.mirea.diplom.prefs.SharedPreferences
import javax.inject.Inject


@HiltViewModel
class SettingNotificationViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val sharedPreferences: SharedPreferences,
    private val snackbarHostState: SnackbarHostState,
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher,
): ViewModel() {

    var isEnableNotification by mutableStateOf(false)


    init {
        val notificationManager = appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationEnabled = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(appContext, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED && notificationManager.areNotificationsEnabled()
        } else {
            notificationManager.areNotificationsEnabled()
        }
        if (notificationEnabled){
            isEnableNotification = sharedPreferences.getBoolean(SharedPreferences.notificationUsing) ?: true
        }
    }


    fun notificationCheck(enabled: Boolean){
        sharedPreferences.saveBool(SharedPreferences.notificationUsing, value = enabled)
        isEnableNotification = enabled
    }

    fun setError(onActionPerformed: () -> Unit){
        isEnableNotification = false
        viewModelScope.launch {
            val result = snackbarHostState.showSnackbar(message = "Вы не приняли разрешение на Уведомления", actionLabel = "Перейти в Настройки", duration = SnackbarDuration.Short)
            if (result == SnackbarResult.ActionPerformed){
                withContext(mainDispatcher){
                    onActionPerformed()
                }
            }
        }
    }

}