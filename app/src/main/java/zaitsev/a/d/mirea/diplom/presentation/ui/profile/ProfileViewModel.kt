package zaitsev.a.d.mirea.diplom.presentation.ui.profile

import android.util.Log
import androidx.annotation.MainThread
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import zaitsev.a.d.mirea.diplom.db.UserRepository
import zaitsev.a.d.mirea.diplom.db.dataUI.UserUI
import zaitsev.a.d.mirea.libapp.phoneChecked.PhoneChecked
import zaitsev.a.d.mirea.telegramapi.api.TelegramHelper
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val telegramHelper: TelegramHelper,
    private val userRepository: UserRepository,
    private val phoneChecked: PhoneChecked
): ViewModel() {

    var user: UserUI? by mutableStateOf(null)
        private set

    fun formatPhone(phone: String): String{
       return phoneChecked.convertToCurrentNumberPhone(phone)
    }

    init {
        viewModelScope.launch {
            userRepository.getFlowUserCurrent()
                .collect { userInDB ->
                    user = userInDB
                }
        }
    }

    fun exit(@MainThread navigationToLogin: () -> Unit){
        viewModelScope.launch {
            if (telegramHelper.isInit()){
                if (telegramHelper.logout()){
                    Log.d(ProfileViewModel::class.simpleName, "Вышел из аккаунта")
                }
            }
            user?.userID?.let { id ->
                userRepository.exitUser(userID = id)
            }
        }.invokeOnCompletion {
            navigationToLogin.invoke()
        }
    }
}