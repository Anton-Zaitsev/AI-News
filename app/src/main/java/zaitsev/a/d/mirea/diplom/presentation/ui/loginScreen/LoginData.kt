package zaitsev.a.d.mirea.diplom.presentation.ui.loginScreen

data class LoginData(
    val currentScreen: ScreenLoginEnum = ScreenLoginEnum.START,
    val isEnabledNext: Boolean = false,
    val name: String = "",
    val lastName: String = "",
    val avatar: String? = null
)
