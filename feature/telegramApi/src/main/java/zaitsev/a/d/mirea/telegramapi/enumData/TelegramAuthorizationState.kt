package zaitsev.a.d.mirea.telegramapi.enumData

enum class TelegramAuthorizationState {
    UNKNOWN,
    WAIT_PARAMETERS,
    WAIT_PHONE_NUMBER,
    WAIT_CODE,
    WAIT_PASSWORD,
    READY,
    LOGGING_OUT,
    CLOSING,
    CLOSED
}