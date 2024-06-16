package zaitsev.a.d.mirea.diplom.data.telegram.telegramSending

sealed class TelegramSending{
    data class SendingText(val text: String): TelegramSending()
    data class SendingPhoto(val photoPath: String, val text: String?): TelegramSending()
}