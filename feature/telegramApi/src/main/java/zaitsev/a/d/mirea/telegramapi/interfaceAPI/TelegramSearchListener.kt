package zaitsev.a.d.mirea.telegramapi.interfaceAPI

import org.drinkless.td.libcore.telegram.TdApi

interface TelegramSearchListener {
    fun onSearchChatsFinished(obj: TdApi.Chats)
    fun onSearchPublicChatsFinished(obj: TdApi.Chats)
    fun onSearchContactsFinished(obj: TdApi.Users)
}