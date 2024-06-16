package zaitsev.a.d.mirea.diplom.data.telegram

import org.drinkless.td.libcore.telegram.TdApi
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class TelegramChatModelUI(
    val chatId: Long,
    val chatImgPath: String?,
    val chatName: String,
    val chatLastMessage: String,
    val chatLastMessageTime: String?,
    val chatUnreadMessages: Int,
)

fun TdApi.Chat.toUI() = TelegramChatModelUI(
    chatId = this.id,
    chatImgPath = this.photo?.small?.local?.path,
    chatName = this.title,
    chatLastMessage = this.lastMessage?.content?.description() ?: "Сообщение",
    chatLastMessageTime = this.lastMessage?.date?.timestampToTime(),
    chatUnreadMessages = this.unreadCount
)
fun List<TdApi.Chat>.toUI() = map { chat ->
    chat.toUI()
}

private fun Int.timestampToTime(): String {
    return SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(this * 1000L))
}

private fun TdApi.MessageContent.description(): String {
    return when(this) {
        is TdApi.MessageText -> this.text.text
        is TdApi.MessagePhoto -> "Фотография"
        is TdApi.MessageVoiceNote -> "Голосовое сообщение"
        is TdApi.MessageSticker -> "Стикер"
        is TdApi.MessageAudio -> "Аудио"
        is TdApi.MessageVideo -> "Видео"
        is TdApi.MessageLocation -> "Местоположение"
        else -> {"Сообщение"}
    }
}
