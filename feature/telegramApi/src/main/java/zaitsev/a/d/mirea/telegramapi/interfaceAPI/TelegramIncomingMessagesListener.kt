package zaitsev.a.d.mirea.telegramapi.interfaceAPI

import org.drinkless.td.libcore.telegram.TdApi

interface TelegramIncomingMessagesListener {
    fun onReceiveChatMessages(chatId: Long, vararg messages: TdApi.Message)
    fun onDeleteChatMessages(chatId: Long, messages: List<TdApi.Message>)
    fun updateMessages()
}