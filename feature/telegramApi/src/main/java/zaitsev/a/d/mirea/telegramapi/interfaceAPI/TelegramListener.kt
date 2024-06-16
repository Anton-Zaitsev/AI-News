package zaitsev.a.d.mirea.telegramapi.interfaceAPI

import org.drinkless.td.libcore.telegram.TdApi
import zaitsev.a.d.mirea.telegramapi.enumData.TelegramAuthorizationState

interface TelegramListener {
    fun onTelegramStatusChanged(
        prevTelegramAuthorizationState: TelegramAuthorizationState,
        newTelegramAuthorizationState: TelegramAuthorizationState,
    )
    fun onTelegramChatsRead() {}
    fun onTelegramChatsChanged() {}
    fun onTelegramChatChanged(chat: TdApi.Chat) {}
    fun onTelegramChatCreated(chat: TdApi.Chat) {}
    fun onChatMessagesChanged(chatId: Long) {}
    fun onTelegramUserChanged(user: TdApi.User) {}
    fun onTelegramError(code: Int, message: String) {}
}