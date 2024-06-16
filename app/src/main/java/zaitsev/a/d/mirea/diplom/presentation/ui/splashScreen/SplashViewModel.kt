package zaitsev.a.d.mirea.diplom.presentation.ui.splashScreen

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import zaitsev.a.d.mirea.diplom.db.UserRepository
import zaitsev.a.d.mirea.telegramapi.api.TelegramHelper
import zaitsev.a.d.mirea.telegramapi.api.TelegramHelper.Companion.initTelegram
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    telegramHelper: TelegramHelper,
    private val userRepository: UserRepository,
): ViewModel() {

    init {
        telegramHelper.initTelegram()
    }
    suspend fun checkExistUser():Boolean {
        return userRepository.existUser()
    }
}