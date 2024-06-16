package zaitsev.a.d.mirea.diplom.data.telegram

import org.drinkless.td.libcore.telegram.TdApi
import org.drinkless.td.libcore.telegram.TdApi.MessageSenderUser
import zaitsev.a.d.mirea.telegramapi.api.TelegramHelper
import java.util.Date

data class TelegramMessageModelUI(
    val id: Long,
    val isOutgoing: Boolean,
    val date: Date,
    val editDate: Date,
    val replyMessageID: Long,
    val sendingState: TdApi.MessageSendingState?,
    val messageContent: TelegramContent?,
    val isUnread: Boolean,
    val canGetViewers: Boolean,
    val isChannelPost: Boolean,
    val userSender: TelegramUserModelUI? = null
)

private fun TdApi.Message.toUserSender(telegramHelper: TelegramHelper, myUserID: Long): TelegramUserModelUI?{
    return when(val sender = this.senderId){
        is MessageSenderUser -> {
            if (myUserID == sender.userId) return null
            telegramHelper.getUser(sender.userId)?.toUI()
        }
        else -> null
    }
}
fun TdApi.Message.toUI(telegramHelper: TelegramHelper, myUserID: Long) = TelegramMessageModelUI(
    id = id,
    isOutgoing = isOutgoing,
    date = Date(date * 1000L),
    editDate = Date(editDate * 1000L),
    replyMessageID = this.replyToMessageId,
    sendingState = this.sendingState,
    messageContent = this.content.toUI(),
    isUnread = this.containsUnreadMention,
    canGetViewers = this.canGetViewers,
    isChannelPost = this.isChannelPost,
    userSender = this.toUserSender(telegramHelper = telegramHelper, myUserID = myUserID)
)

fun List<TdApi.Message>.toUI(telegramHelper: TelegramHelper): List<TelegramMessageModelUI>{
    val myUserID = telegramHelper.getCurrentUserId()
    return map { it.toUI(telegramHelper, myUserID = myUserID) }
}

fun TdApi.MessageContent.toUI(): TelegramContent? {
   return when(this){
        is TdApi.MessagePhoto -> {
            val text = this.caption.text.ifEmpty { null }
            val pathPhoto = photo.sizes.getOrNull(1)?.photo?.local?.path?.ifEmpty { null }
            TelegramContent.TelegramPhotoUI(
                localPath = pathPhoto,
                smallPhoto = photo.minithumbnail,
                text = text
            )
        }
        is TdApi.MessageSticker -> {
            val pathSticker = sticker.sticker.local.path.ifEmpty { null } ?: return null
            TelegramContent.TelegramStickerUI(pathSticker)
        }
        is TdApi.MessageText -> {
            val text = text.text.ifEmpty { null } ?: return null
            TelegramContent.TelegramTextUI(text)
        }

        is TdApi.MessageAnimatedEmoji -> {
            val textSticker = animatedEmoji.sticker.emoji.ifEmpty { null } ?: return null
            TelegramContent.TelegramTextUI(textSticker)
//           val pathStickerAnimated = animatedEmoji.sticker.sticker.local.path.ifEmpty { null } ?: return null
//           TelegramContent.TelegramAnimatedStickerUI(pathStickerAnimated)
        }
        else -> null
    }
}
sealed class TelegramContent{
    data class TelegramPhotoUI(
        val text: String?,
        val localPath: String?,
        val smallPhoto: TdApi.Minithumbnail?
    ): TelegramContent()

    data class TelegramTextUI(
        val text: String
    ): TelegramContent()

    data class TelegramStickerUI(
        val localPath: String
    ): TelegramContent()

    data class TelegramAnimatedStickerUI(
        val localPath: String
    ): TelegramContent()
}