package zaitsev.a.d.mirea.diplom.presentation.ui.chatList

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.drinkless.td.libcore.telegram.TdApi
import zaitsev.a.d.mirea.diplom.data.telegram.TelegramChatModelUI
import zaitsev.a.d.mirea.diplom.data.telegram.toUI
import zaitsev.a.d.mirea.telegramapi.api.TelegramHelper
import zaitsev.a.d.mirea.telegramapi.enumData.TelegramAuthorizationState
import zaitsev.a.d.mirea.telegramapi.getTelegramAuthorizationState
import zaitsev.a.d.mirea.telegramapi.interfaceAPI.TelegramListener
import javax.inject.Inject

@HiltViewModel
class ChatListViewModel @Inject constructor(
    val telegramHelper: TelegramHelper
): ViewModel() {

    var authStateTelegram by mutableStateOf(telegramHelper.authorizationState?.getTelegramAuthorizationState() ?: TelegramAuthorizationState.UNKNOWN)
        private set

    private val listChatMutable: MutableStateFlow<List<TelegramChatModelUI>> = MutableStateFlow(telegramHelper.getAllChats().toUI())

    val listChat = listChatMutable.asStateFlow()


    var search by mutableStateOf("")
        private set

    init {
        telegramHelper.setListener(object : TelegramListener {
            override fun onTelegramStatusChanged(
                prevTelegramAuthorizationState: TelegramAuthorizationState,
                newTelegramAuthorizationState: TelegramAuthorizationState,
            ) {
                Log.d("tg_auth", "tg status update $prevTelegramAuthorizationState -> $newTelegramAuthorizationState")
                authStateTelegram = newTelegramAuthorizationState
            }

            override fun onTelegramChatsRead() {
                Log.d("tg_chats", "onTelegramChatsRead")
            }

            override fun onTelegramChatsChanged() {
                listChatMutable.update {
                    telegramHelper.getAllChats().toUI()
                }
                Log.d("tg_chats", "onTelegramChatsChanged {viewModel.chatListFlow.value}")
            }

            override fun onTelegramChatChanged(chat: TdApi.Chat) {
                Log.d("tg_chats", "onTelegramChatChanged")
                listChatMutable.update { list ->
                    val chatUI = chat.toUI()
                    list.toMutableList().apply {
                        val chatData = firstOrNull { it.chatId == chatUI.chatId }
                        if (chatData == null){
                            this.add(chatUI)
                        }
                        else {
                            val index = this.indexOf(chatData)
                            if (index != -1){
                                this.removeAt(index)
                                this.add(index, chatUI)
                            }
                            else {
                                this.add(chatUI)
                            }
                        }
                    }
                }
            }

            override fun onTelegramChatCreated(chat: TdApi.Chat) {
                Log.d("tg_chats", "onTelegramChatCreated")
                listChatMutable.update { list ->
                    val chatUI = chat.toUI()
                    list.toMutableList().apply {
                        this.add(chatUI)
                    }
                }
            }

            override fun onChatMessagesChanged(chatId: Long) {
                telegramHelper.getChat(chatId)?.toUI()?.let { chatUI ->
                    listChatMutable.update { list ->
                        list.toMutableList().apply {
                            val chatData = firstOrNull { it.chatId == chatUI.chatId }
                            if (chatData == null){
                                this.add(chatUI)
                            }
                            else {
                                val index = this.indexOf(chatData)
                                if (index != -1){
                                    this.removeAt(index)
                                    this.add(index, chatUI)
                                }
                                else {
                                    this.add(chatUI)
                                }
                            }
                        }
                    }
                }
            }

            override fun onTelegramUserChanged(user: TdApi.User) {
                Log.d("tg_user", "onTelegramUserChanged")
            }

            override fun onTelegramError(code: Int, message: String) {
                Log.d("tg_error", "onTelegramError $message")
            }

        })
    }

    fun onSearchChanged(value: String){
        search = value.trim()
    }

    override fun onCleared() {
        super.onCleared()
        telegramHelper.setListener(null)
    }
}