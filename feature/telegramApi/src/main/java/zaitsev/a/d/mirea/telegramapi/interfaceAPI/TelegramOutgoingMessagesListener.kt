package zaitsev.a.d.mirea.telegramapi.interfaceAPI

import org.drinkless.td.libcore.telegram.TdApi

interface TelegramOutgoingMessagesListener {
    fun onUpdateMessages(messages: List<TdApi.Message>)
    fun onDeleteMessages(chatId: Long, messages: List<Long>)
    fun onSendLiveLocationError(code: Int, message: String, messageType: Int)
}