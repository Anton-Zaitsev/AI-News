package zaitsev.a.d.mirea.diplom.presentation.ui.chatUser

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.Lazy
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.drinkless.td.libcore.telegram.Client
import org.drinkless.td.libcore.telegram.TdApi
import org.drinkless.td.libcore.telegram.TdApi.UserStatus
import zaitsev.a.d.mirea.diplom.data.telegram.TelegramChatModelUI
import zaitsev.a.d.mirea.diplom.data.telegram.TelegramGroupInfo
import zaitsev.a.d.mirea.diplom.data.telegram.TelegramMessageModelUI
import zaitsev.a.d.mirea.diplom.data.telegram.TypeChatTelegram
import zaitsev.a.d.mirea.diplom.data.telegram.TypeChatTelegram.DEFAULT
import zaitsev.a.d.mirea.diplom.data.telegram.TypeChatTelegram.GROUP
import zaitsev.a.d.mirea.diplom.data.telegram.telegramSending.TelegramSending
import zaitsev.a.d.mirea.diplom.data.telegram.toUI
import zaitsev.a.d.mirea.diplom.data.telegram.updateValues
import zaitsev.a.d.mirea.diplom.di.IoDispatcher
import zaitsev.a.d.mirea.diplom.presentation.ui.mainNavigation.Routing
import zaitsev.a.d.mirea.diplom.presentation.ui.profile.profileView.bottomSheet.BSProfileAvatarImp
import zaitsev.a.d.mirea.libapp.mediaContent.MediaAvatarContent
import zaitsev.a.d.mirea.libapp.pagingGallery.PagingGallery
import zaitsev.a.d.mirea.telegramapi.api.TelegramHelper
import zaitsev.a.d.mirea.telegramapi.interfaceAPI.FullInfoUpdatesListener
import javax.inject.Inject

//  кол-во сообщений, оставшееся до конца в списке загруженных, чтобы загрузить следующую порцию
private const val MESSAGE_OFFSET_TO_LOAD = 1

@HiltViewModel
class ChatUserViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val mediaAvatarContent: Lazy<MediaAvatarContent>,
    private val snackbarHostState: SnackbarHostState,
    private val telegramHelper: TelegramHelper,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    savedStateHandle: SavedStateHandle,
): ViewModel() {

    private val chatID: Long? = savedStateHandle.get<String>(Routing.CHAT_ID_USER)?.toLongOrNull()

    val typeChat = chatID?.let { id -> telegramHelper.getChat(id)?.let { chat ->
        when{
            telegramHelper.isGroup(chat) -> TypeChatTelegram.GROUP
            telegramHelper.isSuperGroup(chat) -> TypeChatTelegram.SUPER_GROUP
            telegramHelper.isChannel(chat) -> TypeChatTelegram.CHANNEL
            else -> TypeChatTelegram.DEFAULT
        }
    }} ?: DEFAULT

    private val messagesMutable: MutableStateFlow<List<TelegramMessageModelUI>> = MutableStateFlow(
        if (chatID == null) emptyList() else telegramHelper.getMessages(chatID).toUI(telegramHelper)
    )
    val messages = messagesMutable.asStateFlow()

    private val mutableStatus: MutableStateFlow<UserStatus> = MutableStateFlow(TdApi.UserStatusRecently())
    val status = mutableStatus.asStateFlow()


    private val mutableGroupInfo: MutableStateFlow<TelegramGroupInfo?> = MutableStateFlow(null)
    val groupInfo = mutableGroupInfo.asStateFlow()

    var chat: TelegramChatModelUI? by mutableStateOf(null)
        private set


    var message: TelegramSending by mutableStateOf(TelegramSending.SendingText(""))
        private set

    var visibleBSPhoto: Boolean by mutableStateOf(false)
        private set


    val interfaceBSAvatar: BSProfileAvatarImp by lazy {
        object : BSProfileAvatarImp {
            override val pagerGallery: Flow<PagingData<Uri>>
                get() = Pager(
                    PagingConfig(pageSize = 6)
                ){ PagingGallery(context = appContext, ioDispatcher = ioDispatcher) }.flow.cachedIn(viewModelScope)

            override fun takeFromGallery(uri: Uri) {
                dismiss()
                viewModelScope.launch {
                    val path = mediaAvatarContent.get().convertUriToFile(uri = uri)
                    if (path != null){
                        message = when(val mes = message){
                            is TelegramSending.SendingPhoto -> mes.copy(photoPath = path)
                            is TelegramSending.SendingText -> TelegramSending.SendingPhoto(photoPath = path, text = mes.text)
                        }
                    }
                }
            }

            override fun takePhoto(bitmap: Bitmap?) {
                dismiss()
                viewModelScope.launch {
                    bitmap?.let { bitmap ->
                        val path = mediaAvatarContent.get().convertBitmapToFile(bitmap = bitmap)
                        if (path != null){
                            message = when(val mes = message){
                                is TelegramSending.SendingPhoto -> mes.copy(photoPath = path)
                                is TelegramSending.SendingText -> TelegramSending.SendingPhoto(photoPath = path, text = mes.text)
                            }
                        }
                        return@launch
                    }
                    snackbarHostState.showSnackbar("Не удалось сделать фото.")
                }
            }

            override fun removeAvatar() {
                message = when(val mes = message){
                    is TelegramSending.SendingPhoto -> TelegramSending.SendingText(text = mes.text ?: "")
                    is TelegramSending.SendingText -> mes
                }
                dismiss()
            }

            override fun visibleRemoveAvatar(): Boolean {
                return when(val mes = message){
                    is TelegramSending.SendingPhoto -> mes.photoPath.isNotEmpty()
                    is TelegramSending.SendingText -> false
                }
            }

            override fun dismiss() {
                visibleBSPhoto = false
            }
        }
    }

    init {
        if (chatID != null){

            val chatTdApi = telegramHelper.getChat(chatID)
            chat = chatTdApi?.toUI()


            telegramHelper.downloadResultHandler = Client.ResultHandler {
                Log.d("tg_download_result", "res: $it ")
                if (it is TdApi.File) {
                    messagesMutable.update {
                        telegramHelper.getMessages(chatID).toUI(telegramHelper)
                    }
                }
            }

            telegramHelper.onMessagesChanged = {
                Log.d("tg_", "invoke onMessagesChanged")
                messagesMutable.update {
                    telegramHelper.getMessages(chatID).toUI(telegramHelper)
                }
            }

            telegramHelper.scanChatHistory(chatID)

            when(typeChat){
                DEFAULT -> {
                    viewModelScope.launch {
                        callbackFlow<UserStatus> {
                            while (viewModelScope.isActive){
                                telegramHelper.getChat(chatID)?.lastMessage?.senderId?.let {
                                    if (it !is TdApi.MessageSenderUser) return@let
                                    telegramHelper.requestUser(it.userId) { user ->
                                        trySend(user.status)
                                    }
                                }
                                delay(4000)
                            }
                            awaitClose {
                                close()
                            }
                        }.catch {
                            snackbarHostState.showSnackbar("Произошла ошибка при чтение статуса: ${it.message}")
                        }.flowOn(Dispatchers.IO).collect { userStatus ->
                            mutableStatus.update {
                                userStatus
                            }
                        }
                    }
                }
                else -> {
                    chatTdApi?.let {
                        when(val type = it.type){
                            is TdApi.ChatTypeBasicGroup -> type.basicGroupId
                            is TdApi.ChatTypeSupergroup -> type.supergroupId
                            else -> null
                        }
                    }?.let { idGroup ->
                        telegramHelper.addFullInfoUpdatesListener(object: FullInfoUpdatesListener {
                            override fun onBasicGroupFullInfoUpdated(
                                groupId: Long,
                                info: TdApi.BasicGroupFullInfo
                            ) {
                                mutableGroupInfo.update {
                                    val groupInfo = info.toUI() as TelegramGroupInfo.TelegramBasicGroupInfo
                                    when(val infoData = it){
                                        is TelegramGroupInfo.TelegramBasicGroupInfo -> {
                                            groupInfo.copy(listSubscribers = infoData.listSubscribers.updateValues(info))
                                        }
                                        else -> groupInfo
                                    }
                                }
                            }

                            override fun onSupergroupFullInfoUpdated(
                                groupId: Long,
                                info: TdApi.SupergroupFullInfo
                            ) {
                                mutableGroupInfo.update {
                                    info.toUI()
                                }
                            }
                        })

                        mutableGroupInfo.value = if (typeChat == GROUP)
                            telegramHelper.getBasicGroupFullInfo(idGroup)?.toUI()
                        else
                            telegramHelper.getSupergroupFullInfo(idGroup)?.toUI()

                        if (typeChat == GROUP){
                            viewModelScope.launch {
                                while (viewModelScope.isActive){
                                    mutableGroupInfo.value?.let { infoChat ->
                                        if (infoChat is TelegramGroupInfo.TelegramBasicGroupInfo){
                                            val listSubsWithNewStatus = infoChat.listSubscribers.mapNotNull { subs ->
                                                telegramHelper.requestUser(subs.subscriberID)?.let { user ->
                                                    subs.copy(statusOnline = user.status)
                                                }
                                            }
                                            mutableGroupInfo.update {
                                                (it as? TelegramGroupInfo.TelegramBasicGroupInfo)?.copy(listSubscribers = listSubsWithNewStatus)
                                            }
                                        }
                                    }
                                    delay(10000)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    suspend fun downloadImage(messageID: Long){
        chatID?.run {
            telegramHelper.downloadContent(chatID = this, messageID = messageID)
        }
    }
    fun readMessage(message: TelegramMessageModelUI){
        chatID?.run {
            val lastID = telegramHelper.getChat(this)?.lastReadInboxMessageId ?: 0L
            if (!message.isOutgoing && message.id > lastID) {
                telegramHelper.sendMessageInboxRead(this, message.id)
            }
        }
    }

    fun scanChat(index: Int){
        chatID?.run {
            if (messagesMutable.value.isEmpty()) return
            val lastIndexList = messagesMutable.value.lastIndex
            if (lastIndexList - MESSAGE_OFFSET_TO_LOAD < index) {
                telegramHelper.scanChatHistory(this, messagesMutable.value.last().id)
            }
        }
    }

    fun sendMessage(){
        chatID?.run chatID@{
            viewModelScope.launch {
               when(val messageType = message){
                   is TelegramSending.SendingPhoto -> {
                       val photo = messageType.photoPath
                       val text = messageType.text

                       onMessageChanged(TelegramSending.SendingText(""))
                       telegramHelper.sendPhotoMessage(
                           chatID = chatID,
                           photoLocalPath = photo, text = text
                       )
                   }
                   is TelegramSending.SendingText -> {
                       val text = messageType.text
                       onMessageChanged(TelegramSending.SendingText(""))

                       telegramHelper.sendTextMessage(
                           chatID = chatID,
                           text = text
                       )
                   }
               }
            }
        }
    }

    fun setVisiblePhotoBS(permissionAll: Boolean){
        if (permissionAll){
            visibleBSPhoto = true
        }
        else {
            viewModelScope.launch {
                snackbarHostState.showSnackbar("Вы не приняли все разрешения!")
            }
        }
    }

    fun openBSPhoto(requestPermissions: ManagedActivityResultLauncher<Array<String>, Map<String, @JvmSuppressWildcards Boolean>>){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            requestPermissions.launch(arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
            ))
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions.launch(arrayOf(Manifest.permission.READ_MEDIA_IMAGES))
        } else {
            requestPermissions.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE))
        }
    }

    fun onMessageChanged(message: TelegramSending){
        this@ChatUserViewModel.message = message
    }

    override fun onCleared() {
        when(typeChat){
            DEFAULT -> Unit
            else -> {
                telegramHelper.removeFullInfoUpdatesListener()
            }
        }
        telegramHelper.downloadResultHandler = null
        telegramHelper.onMessagesChanged = null
        super.onCleared()
    }

}