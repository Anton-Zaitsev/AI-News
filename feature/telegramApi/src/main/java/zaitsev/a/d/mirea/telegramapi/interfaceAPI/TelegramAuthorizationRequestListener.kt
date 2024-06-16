package zaitsev.a.d.mirea.telegramapi.interfaceAPI

import zaitsev.a.d.mirea.telegramapi.enumData.TelegramAuthorizationState

interface TelegramAuthorizationRequestListener {
    fun onTelegramAuthorizationRequestError(code: Int, message: String)
    fun onTelegramUnsupportedAuthorizationState(authorizationState: TelegramAuthorizationState)
}