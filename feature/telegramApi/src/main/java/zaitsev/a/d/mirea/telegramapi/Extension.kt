package zaitsev.a.d.mirea.telegramapi

import org.drinkless.td.libcore.telegram.TdApi
import zaitsev.a.d.mirea.telegramapi.enumData.TelegramAuthorizationState


fun TdApi.AuthorizationState.getTelegramAuthorizationState(): TelegramAuthorizationState {
    return when (this.constructor) {
        TdApi.AuthorizationStateWaitTdlibParameters.CONSTRUCTOR -> TelegramAuthorizationState.WAIT_PARAMETERS
        TdApi.AuthorizationStateWaitPhoneNumber.CONSTRUCTOR -> TelegramAuthorizationState.WAIT_PHONE_NUMBER
        TdApi.AuthorizationStateWaitCode.CONSTRUCTOR -> TelegramAuthorizationState.WAIT_CODE
        TdApi.AuthorizationStateWaitPassword.CONSTRUCTOR -> TelegramAuthorizationState.WAIT_PASSWORD
        TdApi.AuthorizationStateReady.CONSTRUCTOR -> TelegramAuthorizationState.READY
        TdApi.AuthorizationStateLoggingOut.CONSTRUCTOR -> TelegramAuthorizationState.LOGGING_OUT
        TdApi.AuthorizationStateClosing.CONSTRUCTOR -> TelegramAuthorizationState.CLOSING
        TdApi.AuthorizationStateClosed.CONSTRUCTOR -> TelegramAuthorizationState.CLOSED
        else -> TelegramAuthorizationState.UNKNOWN
    }
}