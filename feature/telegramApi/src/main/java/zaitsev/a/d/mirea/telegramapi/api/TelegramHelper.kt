package zaitsev.a.d.mirea.telegramapi.api

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import org.drinkless.td.libcore.telegram.Client
import org.drinkless.td.libcore.telegram.TdApi
import org.drinkless.td.libcore.telegram.TdApi.TdlibParameters
import zaitsev.a.d.mirea.telegramapi.OrderedChat
import zaitsev.a.d.mirea.telegramapi.enumData.TelegramAuthorizationState
import zaitsev.a.d.mirea.telegramapi.getTelegramAuthorizationState
import zaitsev.a.d.mirea.telegramapi.interfaceAPI.FullInfoUpdatesListener
import zaitsev.a.d.mirea.telegramapi.interfaceAPI.TelegramAuthorizationRequestListener
import zaitsev.a.d.mirea.telegramapi.interfaceAPI.TelegramIncomingMessagesListener
import zaitsev.a.d.mirea.telegramapi.interfaceAPI.TelegramListener
import zaitsev.a.d.mirea.telegramapi.interfaceAPI.TelegramOutgoingMessagesListener
import zaitsev.a.d.mirea.telegramapi.interfaceAPI.TelegramSearchListener
import java.util.TreeSet
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import kotlin.math.max

class TelegramHelper(private val parameters: TdlibParameters): CoroutineScope by CoroutineScope(SupervisorJob() + Dispatchers.IO) {
    companion object {
        const val OSMAND_BOT_USERNAME = "osmand_bot"
        internal const val LIB_NAME = "tdjni"

        //		private val log = PlatformUtil.getLog(TelegramHelper::class.java)
        private const val CHATS_LIMIT = 50
        private const val NOT_FOUND_ERROR_CODE = 404
        const val IGNORED_ERROR_CODE = 406
        private const val MESSAGE_CANNOT_BE_EDITED_ERROR_CODE = 5

        private const val MAX_SEARCH_ITEMS = Int.MAX_VALUE

        // min and max values for the Telegram API
        const val MIN_LOCATION_MESSAGE_LIVE_PERIOD_SEC = 61
        const val MAX_LOCATION_MESSAGE_LIVE_PERIOD_SEC = 60 * 60 * 24 - 1 // one day

        const val MAX_LOCATION_MESSAGE_HISTORY_SCAN_SEC = 60 * 60 * 24 // one day

        const val MESSAGE_TYPE_MAP = 1
        const val MESSAGE_TYPE_TEXT = 2
        const val MESSAGE_TYPE_BOT = 3

        fun TelegramHelper.initTelegram() {

            if (!this.isInit()) {
                Log.d("tg_", "init")
                this.init()
            }
        }
    }

    var messageActiveTimeSec: Long = 0

    var lastTelegramUpdateTime: Int = 0

    private val users = ConcurrentHashMap<Long, TdApi.User>()
    private val contacts = ConcurrentHashMap<Long, TdApi.User>()
    private val basicGroups = ConcurrentHashMap<Long, TdApi.BasicGroup>()
    private val supergroups = ConcurrentHashMap<Long, TdApi.Supergroup>()

    private val chats = ConcurrentHashMap<Long, TdApi.Chat>()
    private val chatList = TreeSet<OrderedChat>()


    private val downloadChatFilesMap = ConcurrentHashMap<String, TdApi.Chat>()
    private val downloadUserFilesMap = ConcurrentHashMap<String, TdApi.User>()

    // value.content can be TdApi.MessageLocation or MessageOsmAndBotLocation
    private val usersMessages = ConcurrentHashMap<Long, TdApi.Message>()
    val allMessages = ConcurrentHashMap<Long, MutableList<TdApi.Message>>()
    var onMessagesChanged: (() -> Unit)? = null

    private val usersFullInfo = ConcurrentHashMap<Long, TdApi.UserFullInfo>()
    private val basicGroupsFullInfo = ConcurrentHashMap<Long, TdApi.BasicGroupFullInfo>()
    private val supergroupsFullInfo = ConcurrentHashMap<Long, TdApi.SupergroupFullInfo>()

    private var libraryLoaded = false
    private var telegramAuthorizationRequestHandler: TelegramAuthorizationRequestHandler? = null

    private var client: Client? = null
    private var currentUser: TdApi.User? = null
    private var osmandBot: TdApi.User? = null

    private var hasSuccessfulChatsLoad: Boolean = false
    private var needRefreshActiveLiveMessages: Boolean = true
    private var requestingActiveLiveLocationMessages: Boolean = false

    var authorizationState: TdApi.AuthorizationState? = null
        private set

    private var haveAuthorization = false

    var downloadResultHandler: Client.ResultHandler? = null

    private var updateLiveMessagesExecutor: ScheduledExecutorService? = null

    private var listener: TelegramListener? = null
    private val incomingMessagesListeners = HashSet<TelegramIncomingMessagesListener>()
    private val outgoingMessagesListeners = HashSet<TelegramOutgoingMessagesListener>()
    private val fullInfoUpdatesListeners = HashSet<FullInfoUpdatesListener>()
    private val searchListeners = HashSet<TelegramSearchListener>()


    fun setListener(listener: TelegramListener?){
        this@TelegramHelper.listener = listener
    }
    fun applyAuthParam(type: TelegramAuthorizationState, value: String?) {
        telegramAuthorizationRequestHandler?.applyAuthParam(type, value)
    }
    private fun addIncomingMessagesListener(listener: TelegramIncomingMessagesListener) {
        incomingMessagesListeners.add(listener)
    }

    fun removeIncomingMessagesListener(listener: TelegramIncomingMessagesListener) {
        incomingMessagesListeners.remove(listener)
    }

    fun addOutgoingMessagesListener(listener: TelegramOutgoingMessagesListener) {
        outgoingMessagesListeners.add(listener)
    }

    fun removeOutgoingMessagesListener(listener: TelegramOutgoingMessagesListener) {
        outgoingMessagesListeners.remove(listener)
    }

    fun addFullInfoUpdatesListener(listener: FullInfoUpdatesListener) {
        fullInfoUpdatesListeners.add(listener)
    }

    fun removeFullInfoUpdatesListener() {
        fullInfoUpdatesListeners.clear()
    }

    fun addSearchListener(listener: TelegramSearchListener) {
        searchListeners.add(listener)
    }

    fun removeSearchListener(listener: TelegramSearchListener) {
        searchListeners.remove(listener)
    }

    fun getChatList(): TreeSet<OrderedChat> {
        synchronized(chatList) {
            return TreeSet(chatList)
        }
    }

    fun getChatListIds() = getChatList().map { it.chatId }

    fun getContacts() = contacts

    fun getChatIds() = chats.keys.toList()

    fun getChat(id: Long): TdApi.Chat? = chats[id]

    fun getUser(id: Long) = if (id == getCurrentUserId()) currentUser else users[id]

    fun getOsmandBot() = osmandBot

    fun getCurrentUser() = currentUser

    fun getCurrentUserId() = currentUser?.id ?: -1

    fun getChatMessages(chatId: Long) =
        usersMessages.values.filter { it.chatId == chatId }

    fun getMessages(chatId: Long) = allMessages[chatId] ?: emptyList()

    fun getMessagesByChatIds(messageExpTime: Long): Map<Long, List<TdApi.Message>> {
        val res = mutableMapOf<Long, MutableList<TdApi.Message>>()
        for (message in usersMessages.values) {
            var messages = res[message.chatId]
            if (messages != null) {
                messages.add(message)
            } else {
                messages = mutableListOf(message)
                res[message.chatId] = messages
            }
        }
        return res
    }

    fun getBasicGroupFullInfo(id: Long): TdApi.BasicGroupFullInfo? {
        val res = basicGroupsFullInfo[id]
        if (res == null) {
            requestBasicGroupFullInfo(id)
        }
        return res
    }

    fun getSupergroupFullInfo(id: Long): TdApi.SupergroupFullInfo? {
        val res = supergroupsFullInfo[id]
        if (res == null) {
            requestSupergroupFullInfo(id)
        }
        return res
    }

    fun isGroup(chat: TdApi.Chat): Boolean {
        return chat.type is TdApi.ChatTypeBasicGroup
    }
    fun isSuperGroup(chat: TdApi.Chat): Boolean {
        return chat.type is TdApi.ChatTypeSupergroup
    }
    fun isChannel(chat: TdApi.Chat): Boolean {
        return chat.type is TdApi.ChatTypeSupergroup && (chat.type as TdApi.ChatTypeSupergroup).isChannel
    }

    inner class TelegramAuthorizationRequestHandler(val telegramAuthorizationRequestListener: TelegramAuthorizationRequestListener) {

        fun applyAuthParam(type: TelegramAuthorizationState, value: String?) {
            if (value.isNullOrEmpty()) return
            when (type) {
                TelegramAuthorizationState.WAIT_PHONE_NUMBER -> client?.send(
                    TdApi.SetAuthenticationPhoneNumber(
                        value,
                        TdApi.PhoneNumberAuthenticationSettings(false, false, false, false, null)
                    ), AuthorizationRequestHandler()
                )
                TelegramAuthorizationState.WAIT_CODE -> client?.send(TdApi.CheckAuthenticationCode(value), AuthorizationRequestHandler())
                TelegramAuthorizationState.WAIT_PASSWORD -> client?.send(TdApi.CheckAuthenticationPassword(value), AuthorizationRequestHandler())
                else -> Unit
            }
        }
    }


    fun setTelegramAuthorizationRequestHandler(telegramAuthorizationRequestListener: TelegramAuthorizationRequestListener?) {
        val handler = telegramAuthorizationRequestListener?.let { handler ->
            TelegramAuthorizationRequestHandler(handler)
        }
        this.telegramAuthorizationRequestHandler = handler
    }

    init {
        try {
            System.loadLibrary(LIB_NAME)
            Client.setLogVerbosityLevel(1)
            libraryLoaded = true
        } catch (e: Throwable) {
            Log.d("tg_", "err: ", e)
        }
    }

    fun init(): Boolean {
        return if (libraryLoaded) {
            // create client
            client = Client.create(UpdatesHandler(), null, null)

            addIncomingMessagesListener(object : TelegramIncomingMessagesListener {

                override fun onReceiveChatMessages(chatId: Long, vararg messages: TdApi.Message) {
                    if (!allMessages.containsKey(chatId)) allMessages[chatId] = mutableListOf()


                    Log.d("tg_messages", "Скачано вот столько сообщение: ${messages.size}")
                    messages.forEach { message ->
                        allMessages[chatId]?.add(0, message)
                        this@TelegramHelper.launch {
                            extraDownloadMessageRequest(chatID = chatId, messageID = message.id, messageContent = message.content)
                        }
                    }

                    onMessagesChanged?.invoke()
                }

                override fun onDeleteChatMessages(chatId: Long, messages: List<TdApi.Message>) {
                    allMessages[chatId]?.let { _ ->
                        messages.forEach { msg ->
                            allMessages[chatId]?.removeIf { it.id == msg.id }
                        }
                        listener?.onTelegramChatsChanged()

                        onMessagesChanged?.invoke()
                    }

                }

                override fun updateMessages() {
                    Log.d("tg_messages", "upd messages: $")
                }
            })

            for (chatId in allMessages.keys) {
                allMessages[chatId]?.let { messages ->

                    messages.forEach {
                        if (it.id < (chats[chatId]?.lastReadOutboxMessageId ?: 0)) {
                            it.canGetViewers = true
                        }
                        onMessagesChanged?.invoke()
                    }
                }
            }

            true
        } else {
            false
        }
    }

    fun getClientId() = currentUser?.id ?: -1L

    fun requestAuthorizationState() {
        client?.send(TdApi.GetAuthorizationState()) { obj ->
            if (obj is TdApi.AuthorizationState) {
                onAuthorizationStateUpdated(obj, true)
            }
        }
    }

    fun networkChange(networkType: TdApi.NetworkType) {
        client?.send(TdApi.SetNetworkType(networkType)) { obj ->
//			log.debug(obj)
        }
    }

    fun isInit() = client != null && haveAuthorization

    fun getUserPhotoPath(user: TdApi.User?) = when {
        user == null -> null
        hasLocalUserPhoto(user) -> user.profilePhoto?.small?.local?.path
        else -> {
            if (hasRemoteUserPhoto(user)) {
                requestUserPhoto(user)
            }
            null
        }
    }

    fun getUserIdFromChatType(type: TdApi.ChatType) = when (type) {
        is TdApi.ChatTypePrivate -> type.userId
        is TdApi.ChatTypeSecret -> type.userId
        else -> 0
    }

    fun isOsmAndBot(userId: Long) = users[userId]?.username == OSMAND_BOT_USERNAME

    fun isBot(userId: Long) = users[userId]?.type is TdApi.UserTypeBot

    fun startLiveMessagesUpdates(interval: Long) {
        stopLiveMessagesUpdates()

        val updateLiveMessagesExecutor = Executors.newSingleThreadScheduledExecutor()
        this.updateLiveMessagesExecutor = updateLiveMessagesExecutor
        updateLiveMessagesExecutor.scheduleWithFixedDelay({
            incomingMessagesListeners.forEach { it.updateMessages() }
        }, interval, interval, TimeUnit.SECONDS)
    }

    fun stopLiveMessagesUpdates() {
        updateLiveMessagesExecutor?.shutdown()
        updateLiveMessagesExecutor?.awaitTermination(1, TimeUnit.MINUTES)
    }

    private fun hasLocalUserPhoto(user: TdApi.User): Boolean {
        val localPhoto = user.profilePhoto?.small?.local
        return if (localPhoto != null) {
            localPhoto.canBeDownloaded && localPhoto.isDownloadingCompleted && localPhoto.path.isNotEmpty()
        } else {
            false
        }
    }

    private fun hasRemoteUserPhoto(user: TdApi.User): Boolean {
        val remotePhoto = user.profilePhoto?.small?.remote
        return remotePhoto?.id?.isNotEmpty() ?: false
    }

    private fun requestUserPhoto(user: TdApi.User) {
        val remotePhoto = user.profilePhoto?.small?.remote
        if (remotePhoto != null && remotePhoto.id.isNotEmpty()) {
            downloadUserFilesMap[remotePhoto.id] = user
            client?.send(TdApi.GetRemoteFile(remotePhoto.id, null)) { obj ->
                when (obj.constructor) {
                    TdApi.Error.CONSTRUCTOR -> {
                        val error = obj as TdApi.Error
                        val code = error.code
                        if (code != IGNORED_ERROR_CODE) {
                            listener?.onTelegramError(code, error.message)
                        }
                    }
                    TdApi.File.CONSTRUCTOR -> {
                        val file = obj as TdApi.File
                        client?.send(TdApi.DownloadFile(file.id, 10, 0, 0, true), downloadResultHandler)
                    }
                    else -> listener?.onTelegramError(-1, "Receive wrong response from TDLib: $obj")
                }
            }
        }
    }

    private fun requestChats(reload: Boolean = false, onComplete: (() -> Unit)?) {
        synchronized(chatList) {
            if (reload) {
                chatList.clear()
                hasSuccessfulChatsLoad = false
            }
            if (chatList.size < CHATS_LIMIT) {
                client?.send(TdApi.LoadChats(TdApi.ChatListMain(), CHATS_LIMIT)) { obj ->
                    when (obj.constructor) {
                        TdApi.Error.CONSTRUCTOR -> {
                            synchronized(chatList) {
                                val error = obj as TdApi.Error

                                Log.d("tg_messages", "hasSuccessfulChatsLoad $hasSuccessfulChatsLoad")
//								val loadedAllChats = hasSuccessfulChatsLoad && error.code == NOT_FOUND_ERROR_CODE
//								if (!loadedAllChats && error.code != IGNORED_ERROR_CODE) {
//								}
                                listener?.onTelegramError(error.code, error.message)
//								requestChats(reload, onComplete)
                            }
                        }
                        TdApi.Ok.CONSTRUCTOR -> {
                            synchronized(chatList) {
                                hasSuccessfulChatsLoad = true
                            }
                            // Some chats are received through updates, try to load more chats

                            Log.d("tg_messages", "scanChats ")
//							requestChats(false) {
                            Log.d("tg_messages", "scan history lambda")
                            scanChatsHistory()
//							}
                            onComplete?.invoke()
                        }
                        else -> {
                            listener?.onTelegramError(-1, "Receive wrong response from TDLib: $obj")
                        }
                    }
                }
                return
            }
        }
        listener?.onTelegramChatsRead()
    }

    private fun requestBasicGroupFullInfo(id: Long) {
        client?.send(TdApi.GetBasicGroupFullInfo(id)) { obj ->
            when (obj.constructor) {
                TdApi.Error.CONSTRUCTOR -> {
                    val error = obj as TdApi.Error
                    if (error.code != IGNORED_ERROR_CODE) {
                        listener?.onTelegramError(error.code, error.message)
                    }
                }
                TdApi.BasicGroupFullInfo.CONSTRUCTOR -> {
                    val info = obj as TdApi.BasicGroupFullInfo
                    basicGroupsFullInfo[id] = info
                    fullInfoUpdatesListeners.forEach { it.onBasicGroupFullInfoUpdated(id, info) }
                }
            }
        }
    }

    private fun requestSupergroupFullInfo(id: Long) {
        client?.send(TdApi.GetSupergroupFullInfo(id)) { obj ->
            when (obj.constructor) {
                TdApi.Error.CONSTRUCTOR -> {
                    val error = obj as TdApi.Error
                    if (error.code != IGNORED_ERROR_CODE) {
                        listener?.onTelegramError(error.code, error.message)
                    }
                }
                TdApi.SupergroupFullInfo.CONSTRUCTOR -> {
                    val info = obj as TdApi.SupergroupFullInfo
                    supergroupsFullInfo[id] = info
                    fullInfoUpdatesListeners.forEach { it.onSupergroupFullInfoUpdated(id, info) }
                }
            }
        }
    }

    private fun requestCurrentUser(){
        client?.send(TdApi.GetMe()) { obj ->
            when (obj.constructor) {
                TdApi.Error.CONSTRUCTOR -> {
                    val error = obj as TdApi.Error
                    if (error.code != IGNORED_ERROR_CODE) {
                        listener?.onTelegramError(error.code, error.message)
                    }
                }
                TdApi.User.CONSTRUCTOR -> {
                    val currUser = obj as TdApi.User
                    currentUser = currUser
                    if (!hasLocalUserPhoto(currUser) && hasRemoteUserPhoto(currUser)) {
                        requestUserPhoto(currUser)
                    }
                }
            }
        }
    }

    private fun requestContacts(){
        client?.send(TdApi.GetContacts()) { obj ->
            when (obj.constructor) {
                TdApi.Error.CONSTRUCTOR -> {
                    val error = obj as TdApi.Error
                    if (error.code != IGNORED_ERROR_CODE) {
                        listener?.onTelegramError(error.code, error.message)
                    }
                }
                TdApi.Users.CONSTRUCTOR -> {
                    val usersIds = obj as TdApi.Users
                    usersIds.userIds.forEach {
                        requestUser(it) {

                        }
                    }
                }
            }
        }
    }

    private fun scanChatsHistory(offset: Int = 0, limit: Int = 500) {
        Log.d("tg_messages", "scan chat history")
        chats.forEach {
            scanChatHistory(it.key, it.value.lastMessage?.id ?: 0, offset, limit)
        }
    }

    fun scanChatHistory(chatId: Long, fromMessageId: Long = 0) {
        Log.d("tg_messages", "scan chat history")
//		chats.forEach {
        if (allMessages[chatId]?.size == 0) {
            chats[chatId]?.lastMessage?.id.let {
                if (it == null) {
                    scanChatHistory(chatId, 0, 0, 500)
                } else {
                    scanChatHistory(chatId, it, 0, 500)
                }

            }
        } else {
            val fromMsg = if (fromMessageId == 0L) allMessages[chatId]?.last()?.id ?: chats[chatId]?.lastMessage?.id else fromMessageId
            fromMsg?.let {
                scanChatHistory(chatId, it, 0, 500)
            }
        }
    }

    private fun scanChatHistory(
        chatId: Long,
        fromMessageId: Long,
        offset: Int,
        limit: Int
    ) {
        client?.send(TdApi.GetChatHistory(chatId, fromMessageId, offset, limit, false)) { obj ->
            when (obj.constructor) {
                TdApi.Error.CONSTRUCTOR -> {
                    val error = obj as TdApi.Error
                    if (error.code != IGNORED_ERROR_CODE) {
                        listener?.onTelegramError(error.code, error.message)
                    }
                }
                TdApi.Messages.CONSTRUCTOR -> {
                    val messages = (obj as TdApi.Messages).messages
                    Log.d("tg_messages", "msgs from history: ${messages.size} ")

                    if (!allMessages.containsKey(chatId)) allMessages[chatId] = mutableListOf()
                    messages.forEach { message ->
                        allMessages[chatId]?.add(message)
                        if (!message.isOutgoing && message.senderId is TdApi.MessageSenderUser && !contacts.containsKey((message.senderId as TdApi.MessageSenderUser).userId)) {
                            requestUser((message.senderId as TdApi.MessageSenderUser).userId) { _ ->
                                onMessagesChanged?.invoke()
                            }
                        }
                        this@TelegramHelper.launch {
                            extraDownloadMessageRequest(chatID = chatId, messageID = message.id, messageContent = message.content)
                        }
                    }
                    if (allMessages[chatId]?.first()?.id != chats[chatId]?.lastMessage?.id) {
                        chats[chatId]?.lastMessage?.let {
                            allMessages[chatId]?.add(0, it)
                        }
                    }
                    onMessagesChanged?.invoke()
                }
            }
        }
    }

    suspend fun downloadContent(chatID: Long, messageID: Long){
        allMessages[chatID]?.firstOrNull { it.id == messageID }?.let { message ->
            val index = allMessages[chatID]!!.indexOf(message)
            if (index == -1) return@let

            if (message.content is TdApi.MessagePhoto){
                extraDownloadMessageRequest(chatID = chatID, messageID = messageID, messageContent = message.content)
            }
        }
    }

    suspend fun extraDownloadMessageRequest(chatID: Long, messageID: Long, messageContent: TdApi.MessageContent) {
        client?.run {
            val result = when(messageContent){
                is TdApi.MessagePhoto -> {
                    messageContent.download(this)
                }
                is TdApi.MessageSticker -> {
                    messageContent.download(this)
                }
                is TdApi.MessageAnimatedEmoji -> {
                    messageContent.download(this)
                }
                else -> ResultDownload.Nothing
            }
            when(result){
                is ResultDownload.Error -> {
                    listener?.onTelegramError(result.code ?: 0, result.error)
                }
                ResultDownload.Nothing -> Unit
                is ResultDownload.Success -> {
                    allMessages[chatID]?.firstOrNull { it.id == messageID }?.let { message ->
                        val messageIndex = allMessages[chatID]?.indexOf(message) ?: return@let

                        when {
                            message.content is TdApi.MessagePhoto && messageContent is TdApi.MessagePhoto -> {
                                if ((message.content as TdApi.MessagePhoto).photo?.sizes?.getOrNull(1)?.photo?.id == result.file.id){
                                    (allMessages[chatID]!![messageIndex].content as TdApi.MessagePhoto).photo?.sizes?.getOrNull(1)?.photo = result.file
                                    onMessagesChanged?.invoke()
                                }
                            }
                            message.content is TdApi.MessageSticker && messageContent is TdApi.MessageSticker -> {
                                if ((message.content as TdApi.MessageSticker).sticker.sticker.id == result.file.id){
                                    (allMessages[chatID]!![messageIndex].content as TdApi.MessageSticker).sticker.sticker = result.file
                                    onMessagesChanged?.invoke()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun getContact(userId: Long) = contacts[userId]

    suspend fun requestUser(id: Long): TdApi.User?{
        return callbackFlow {
            requestUser(id = id){ user ->
                trySend(user)
            }
            awaitClose{
                close()
            }
        }.firstOrNull()
    }

    fun requestUser(id: Long, onGetUser: ((user: TdApi.User) -> Unit)? = null) {
        client?.send(TdApi.GetUser(id)) { obj ->
            when (obj.constructor) {
                TdApi.Error.CONSTRUCTOR -> {
                    val error = obj as TdApi.Error
                    if (error.code != IGNORED_ERROR_CODE) {
                        listener?.onTelegramError(error.code, error.message)
                    }
                }
                TdApi.User.CONSTRUCTOR -> {
                    val user = obj as TdApi.User
                    contacts[user.id] = user
                    onGetUser?.invoke(user)
                    if (!hasLocalUserPhoto(user) && hasRemoteUserPhoto(user)) {
                        requestUserPhoto(user)
                    }
                }
            }
        }
    }

    fun requestChat(id: Long) {
        client?.send(TdApi.GetChat(id)) { obj ->
            when (obj.constructor) {
                TdApi.Error.CONSTRUCTOR -> {
                    val error = obj as TdApi.Error
                    if (error.code != IGNORED_ERROR_CODE) {
                        listener?.onTelegramError(error.code, error.message)
                    }
                }
                TdApi.Chat.CONSTRUCTOR -> {
                    val chat = obj as TdApi.Chat
                    chats[chat.id] = chat
                    listener?.onTelegramChatChanged(chat)
                }
            }
        }
    }

    fun disableProxy() {
        client?.send(TdApi.DisableProxy()) { obj ->
            when (obj.constructor) {
                TdApi.Error.CONSTRUCTOR -> {
                    val error = obj as TdApi.Error
                    if (error.code != IGNORED_ERROR_CODE) {
                        listener?.onTelegramError(error.code, error.message)
                    }
                }
                TdApi.Ok.CONSTRUCTOR -> {
                }
            }
        }
    }

    fun enableProxy(proxyId: Int) {
        client?.send(TdApi.EnableProxy(proxyId)) { obj ->
            when (obj.constructor) {
                TdApi.Error.CONSTRUCTOR -> {
                    val error = obj as TdApi.Error
                    if (error.code != IGNORED_ERROR_CODE) {
                        listener?.onTelegramError(error.code, error.message)
                    }
                }
                TdApi.Ok.CONSTRUCTOR -> {
                }
            }
        }
    }

    private fun requestMessage(chatId: Long, messageId: Long, onComplete: (TdApi.Message) -> Unit) {
        client?.send(TdApi.GetMessage(chatId, messageId)) { obj ->
            if (obj is TdApi.Message) {
                onComplete(obj)
            }
        }
    }

    private fun addNewMessage(message: TdApi.Message) {
        lastTelegramUpdateTime = max(lastTelegramUpdateTime, max(message.date, message.editDate))
//		if (message.isAppropriate()) {
//			val viaBot = isOsmAndBot(message.viaBotUserId)
//			if (message.isOutgoing) {
//				return
//			}
////			updateLastMessage(message)
//			if (message.isOutgoing) {
//				if (viaBot) {
//					outgoingMessagesListeners.forEach {
//						it.onUpdateMessages(listOf(message))
//					}
//				}
//			} else {
        incomingMessagesListeners.forEach {
            it.onReceiveChatMessages(message.chatId, message)
        }
//			}
//		}
    }

    fun searchChats(searchTerm: String) {
        client?.send(TdApi.SearchChats(searchTerm, MAX_SEARCH_ITEMS)) { obj ->
            checkChatsAndUsersSearch(obj)
        }
    }

    fun searchChatsOnServer(searchTerm: String) {
        client?.send(TdApi.SearchChatsOnServer(searchTerm, MAX_SEARCH_ITEMS)) { obj ->
            checkChatsAndUsersSearch(obj)
        }
    }

    fun searchPublicChats(searchTerm: String) {
        client?.send(TdApi.SearchPublicChats(searchTerm)) { obj ->
            checkChatsAndUsersSearch(obj, true)
        }
    }

    fun searchContacts(searchTerm: String) {
        client?.send(TdApi.SearchContacts(searchTerm, MAX_SEARCH_ITEMS)) { obj ->
            checkChatsAndUsersSearch(obj)
        }
    }

    private fun checkChatsAndUsersSearch(obj: TdApi.Object, publicChats: Boolean = false) {
        when (obj.constructor) {
            TdApi.Error.CONSTRUCTOR -> {
                val error = obj as TdApi.Error
                if (error.code != IGNORED_ERROR_CODE) {
                    listener?.onTelegramError(error.code, error.message)
                }
            }
            TdApi.Chats.CONSTRUCTOR -> {
                val chats = obj as TdApi.Chats
                if (publicChats) {
                    searchListeners.forEach { it.onSearchPublicChatsFinished(chats) }
                } else {
                    searchListeners.forEach { it.onSearchChatsFinished(chats) }
                }
            }
            TdApi.Users.CONSTRUCTOR -> {
                val users = obj as TdApi.Users
                searchListeners.forEach { it.onSearchContactsFinished(users) }
            }
        }
    }

    suspend fun logout(): Boolean {
        return if (libraryLoaded) {
            haveAuthorization = false
            callbackFlow {
                client?.send(TdApi.LogOut()) {
                    listener?.onTelegramStatusChanged(TelegramAuthorizationState.UNKNOWN, TelegramAuthorizationState.WAIT_PHONE_NUMBER)
                    trySend(true)
                }
                awaitClose {
                    close()
                }
            }.firstOrNull() ?: false
        } else {
            false
        }
    }

    suspend fun close(): Boolean {
        downloadResultHandler = null
        updateLiveMessagesExecutor = null
        onMessagesChanged = null
        telegramAuthorizationRequestHandler = null
        telegramAuthorizationRequestHandler = null

        currentUser = null
        osmandBot = null
        hasSuccessfulChatsLoad = false
        authorizationState = null

        users.clear()
        contacts.clear()
        basicGroups.clear()
        supergroups.clear()
        chats.clear()
        chatList.clear()
        downloadChatFilesMap.clear()
        downloadUserFilesMap.clear()
        usersMessages.clear()
        allMessages.clear()
        usersFullInfo.clear()
        basicGroupsFullInfo.clear()
        supergroupsFullInfo.clear()

        return if (libraryLoaded) {
            haveAuthorization = false
            callbackFlow {
                client?.send(TdApi.Close()) { result ->
                    if (result is TdApi.Ok){
                        trySend(true)
                    }
                    else {
                        trySend(false)
                    }
                }
                awaitClose {
                    client = null
                    close()
                }
            }.firstOrNull() ?: false
        } else {
            false
        }
    }

    fun getAllChats(): List<TdApi.Chat> {
        val list = mutableListOf<TdApi.Chat>()
        chatList.forEach { orderedChat ->
            chats[orderedChat.chatId]?.let { list.add(it) }
        }
        return list
    }

    private fun setChatPositions(chat: TdApi.Chat, positions: Array<TdApi.ChatPosition?>) {
        synchronized(chatList) {
            synchronized(chat) {
                val isChannel = isChannel(chat)
                for (position in chat.positions) {
                    if (position.list.constructor == TdApi.ChatListMain.CONSTRUCTOR) {
                        chatList.remove(OrderedChat(chat.id, position, isChannel))
                    }
                }
                chat.positions = positions
                for (position in chat.positions) {
                    if (position.list.constructor == TdApi.ChatListMain.CONSTRUCTOR) {
                        chatList.add(OrderedChat(chat.id, position, isChannel))
                    }
                }
            }
        }
    }

    suspend fun sendTextMessage(chatID: Long, text: String) {
        client?.run client@{
            val messageText = TdApi.InputMessageText(TdApi.FormattedText(text, emptyArray()), true, true)
            callbackFlow {
                this@client.send(TdApi.SendMessage(chatID, 0L, 0L, null,null, messageText)){ message ->
                    if (message is TdApi.Message){
                        trySend(message)
                    }
                    else
                        trySend(null)
                }
                awaitClose {
                    close()
                }
            }.firstOrNull()?.let { message ->
                allMessages[chatID]?.find { msg ->
                    message.id == msg.id
                }?.let { msg ->
                    msg.sendingState = null
                    onMessagesChanged?.invoke()
                }
            }
        }
    }

    suspend fun sendPhotoMessage(chatID: Long, photoLocalPath: String, text: String? = null){
        client?.run client@{
            val photo = TdApi.InputFileLocal(photoLocalPath)
            val messageText = text?.let { TdApi.FormattedText(it, emptyArray()) }
            val photoContent = TdApi.InputMessagePhoto(photo, null, IntArray(0), 100, 100, messageText, 0)

            callbackFlow {
                this@client.send(TdApi.SendMessage(chatID, 0L, 0L, null,null, photoContent)){ message ->
                    if (message is TdApi.Message){
                        trySend(message)
                    }
                    else
                        trySend(null)
                }
                awaitClose {
                    close()
                }
            }.firstOrNull()?.let { message ->
                allMessages[chatID]?.find { msg ->
                    message.id == msg.id && msg.content is TdApi.MessagePhoto
                }?.let { msg ->
                    (msg.content as TdApi.MessagePhoto).photo?.sizes?.getOrNull(1)?.photo?.local?.apply {
                        path = photoLocalPath
                        isDownloadingCompleted = true
                        canBeDeleted = false
                        isDownloadingActive = false
                    }
                    msg.sendingState = null
                    onMessagesChanged?.invoke()
                }
            }
        }
    }

    fun sendMessageInboxRead(chatId: Long, messageId: Long) {
        if (allMessages[chatId]?.firstOrNull { it.id == messageId }?.canGetViewers == false) {
            client?.send(
                TdApi.ViewMessages(chatId, 0, longArrayOf(messageId), true)
            ) {
                Log.d("tg_msg_read", "sendMessageInboxRead: $it")
            }
        }

    }

    private fun onAuthorizationStateUpdated(authorizationState: TdApi.AuthorizationState?, info: Boolean = false) {
        Log.d("tg_", "onAuthorization: ${authorizationState?.constructor == TdApi.AuthorizationStateReady.CONSTRUCTOR}")
        val prevAuthState = this.authorizationState?.getTelegramAuthorizationState() ?: TelegramAuthorizationState.UNKNOWN
        if (authorizationState != null) {
            this.authorizationState = authorizationState
        }
        when (this.authorizationState) {
            is TdApi.AuthorizationStateWaitTdlibParameters -> {
                if (!info) {
                    client?.send(TdApi.SetTdlibParameters(parameters), AuthorizationRequestHandler())
                }
                listener?.onTelegramStatusChanged(prevAuthState, TelegramAuthorizationState.WAIT_PARAMETERS)
            }
            is TdApi.AuthorizationStateWaitEncryptionKey -> {
                if (!info) {
                    client?.send(TdApi.CheckDatabaseEncryptionKey(), AuthorizationRequestHandler())
                }
                if ((this.authorizationState as? TdApi.AuthorizationStateWaitEncryptionKey)?.isEncrypted == true){
                    listener?.onTelegramStatusChanged(prevAuthState, TelegramAuthorizationState.READY)
                }
                else {
                    listener?.onTelegramStatusChanged(prevAuthState, TelegramAuthorizationState.WAIT_PHONE_NUMBER)
                }
            }
            is TdApi.AuthorizationStateReady -> {
                listener?.onTelegramStatusChanged(prevAuthState, TelegramAuthorizationState.READY)
            }
            is TdApi.AuthorizationStateWaitPhoneNumber -> {
                listener?.onTelegramStatusChanged(prevAuthState, TelegramAuthorizationState.WAIT_PHONE_NUMBER)
            }
            is TdApi.AuthorizationStateWaitCode -> {
                listener?.onTelegramStatusChanged(prevAuthState, TelegramAuthorizationState.WAIT_CODE)
            }
            is TdApi.AuthorizationStateWaitPassword -> {
                listener?.onTelegramStatusChanged(prevAuthState, TelegramAuthorizationState.WAIT_PASSWORD)
            }
            is TdApi.AuthorizationStateLoggingOut -> {
                listener?.onTelegramStatusChanged(prevAuthState, TelegramAuthorizationState.LOGGING_OUT)
            }
            is TdApi.AuthorizationStateClosing -> {
                listener?.onTelegramStatusChanged(prevAuthState, TelegramAuthorizationState.CLOSING)
            }
            is TdApi.AuthorizationStateClosed -> {
                listener?.onTelegramStatusChanged(prevAuthState, TelegramAuthorizationState.CLOSED)
            }
            else -> {
                telegramAuthorizationRequestHandler?.telegramAuthorizationRequestListener?.onTelegramUnsupportedAuthorizationState(TelegramAuthorizationState.WAIT_PHONE_NUMBER)
            }
        }
        haveAuthorization = this.authorizationState?.constructor == TdApi.AuthorizationStateReady.CONSTRUCTOR
//		if (wasAuthorized != haveAuthorization) {
//			needRefreshActiveLiveLocationMessages = true
        if (haveAuthorization) {
            requestChats(true, null)
            requestCurrentUser()
            requestContacts()
        }
//		}
        val newAuthState = this.authorizationState?.getTelegramAuthorizationState() ?: TelegramAuthorizationState.UNKNOWN

        Log.d("tg__", "change status $newAuthState ${listener == null}")
        listener?.onTelegramStatusChanged(prevAuthState, newAuthState)
    }


    private inner class UpdatesHandler : Client.ResultHandler {
        override fun onResult(obj: TdApi.Object) {
            when (obj.constructor) {
                TdApi.UpdateAuthorizationState.CONSTRUCTOR -> onAuthorizationStateUpdated((obj as TdApi.UpdateAuthorizationState).authorizationState)

                TdApi.UpdateUser.CONSTRUCTOR -> {
                    val updateUser = obj as TdApi.UpdateUser
                    val user = updateUser.user
                    users[updateUser.user.id] = user
                    if (user.isContact) {
                        contacts[user.id] = user
                    }
                    if (isOsmAndBot(user.id)) {
                        osmandBot = user
                    }
                }
                TdApi.UpdateUserStatus.CONSTRUCTOR -> {
                    val updateUserStatus = obj as TdApi.UpdateUserStatus
                    users[updateUserStatus.userId]?.let { user ->
                        synchronized(user) {
                            user.status = updateUserStatus.status
                        }
                    }
                }
                TdApi.UpdateBasicGroup.CONSTRUCTOR -> {
                    val updateBasicGroup = obj as TdApi.UpdateBasicGroup
                    basicGroups[updateBasicGroup.basicGroup.id] = updateBasicGroup.basicGroup
                }
                TdApi.UpdateSupergroup.CONSTRUCTOR -> {
                    val updateSupergroup = obj as TdApi.UpdateSupergroup
                    supergroups[updateSupergroup.supergroup.id] = updateSupergroup.supergroup
                }

                TdApi.UpdateSecretChat.CONSTRUCTOR -> Unit

                TdApi.UpdateNewChat.CONSTRUCTOR -> {
                    val updateNewChat = obj as TdApi.UpdateNewChat
                    val chat = updateNewChat.chat

                    synchronized(chat) {
                        chats[chat.id] = chat
                        val localPhoto = chat.photo?.small?.local
                        val hasLocalPhoto = if (localPhoto != null) {
                            localPhoto.canBeDownloaded && localPhoto.isDownloadingCompleted && localPhoto.path.isNotEmpty()
                        } else {
                            false
                        }
                        if (!hasLocalPhoto) {
                            val remotePhoto = chat.photo?.small?.remote
                            if (remotePhoto != null && remotePhoto.id.isNotEmpty()) {
                                downloadChatFilesMap[remotePhoto.id] = chat
                                client?.send(TdApi.GetRemoteFile(remotePhoto.id, null)) { obj ->
                                    when (obj.constructor) {
                                        TdApi.Error.CONSTRUCTOR -> {
                                            val error = obj as TdApi.Error
                                            val code = error.code
                                            Log.d("tg_download_result", "err download file ${remotePhoto.id} msg ${error.message} $code")
                                            if (code != IGNORED_ERROR_CODE) {
                                                listener?.onTelegramError(code, error.message)
                                            }
                                        }
                                        TdApi.File.CONSTRUCTOR -> {
                                            val file = obj as TdApi.File
                                            client?.send(TdApi.DownloadFile(file.id, 10, 0, 0, true), downloadResultHandler)
                                            Log.d("tg_download_result", "request download file ${file.id}")
                                        }
                                        else -> listener?.onTelegramError(-1, "Receive wrong response from TDLib: $obj")
                                    }
                                }
                            }
                        }
                        val positions = chat.positions
                        chat.positions = arrayOfNulls(0)
                        setChatPositions(chat, positions)

                        Log.d("tg_chats", "UpdateNewChat ${chats.size} ${chatList.size}")
                        listener?.onTelegramChatsChanged()
                    }
                }
                TdApi.UpdateChatTitle.CONSTRUCTOR -> {
                    val updateChat = obj as TdApi.UpdateChatTitle
                    val chat = chats[updateChat.chatId]
                    if (chat != null) {
                        synchronized(chat) {
                            chat.title = updateChat.title
                        }
                        listener?.onTelegramChatChanged(chat)
                    }
                }
                TdApi.UpdateChatPhoto.CONSTRUCTOR -> {
                    val updateChat = obj as TdApi.UpdateChatPhoto
                    val chat = chats[updateChat.chatId]
                    if (chat != null) {
                        synchronized(chat) {
                            chat.photo = updateChat.photo
                        }
                        listener?.onTelegramChatChanged(chat)
                    }
                }
                TdApi.UpdateChatLastMessage.CONSTRUCTOR -> {
                    val updateChat = obj as TdApi.UpdateChatLastMessage
                    val chat = chats[updateChat.chatId]
                    if (chat != null) {
                        synchronized(chat) {
                            chat.lastMessage = updateChat.lastMessage
                            setChatPositions(chat, updateChat.positions);
                        }
                        listener?.onTelegramChatsChanged()
                    }
                }
                TdApi.UpdateChatPosition.CONSTRUCTOR -> {
                    val updateChat = obj as TdApi.UpdateChatPosition
                    if (updateChat.position.list.constructor == TdApi.ChatListMain.CONSTRUCTOR) {
                        val chat = chats[updateChat.chatId]
                        if (chat != null) {
                            synchronized(chat) {
                                var index = 0
                                for (i in chat.positions.indices) {
                                    if (chat.positions[i].list.constructor == TdApi.ChatListMain.CONSTRUCTOR) {
                                        index = i
                                        break
                                    }
                                }
                                val length = chat.positions.size + (if (updateChat.position.order == 0L) 0 else 1) - if (index < chat.positions.size) 1 else 0
                                val newPositions = arrayOfNulls<TdApi.ChatPosition>(length)

                                var pos = 0
                                if (updateChat.position.order != 0L) {
                                    newPositions[pos++] = updateChat.position
                                }
                                for (j in chat.positions.indices) {
                                    if (j != index) {
                                        newPositions[pos++] = chat.positions[j];
                                    }
                                }
                                setChatPositions(chat, newPositions)
                            }
                        }
                        listener?.onTelegramChatsChanged()
                    }
                }
                TdApi.UpdateChatReadInbox.CONSTRUCTOR -> {
                    val updateChat = obj as TdApi.UpdateChatReadInbox
                    val chat = chats[updateChat.chatId]
                    if (chat != null) {
                        synchronized(chat) {
                            chat.lastReadInboxMessageId = updateChat.lastReadInboxMessageId
                            chat.unreadCount = updateChat.unreadCount
                        }
                    }
                }
                TdApi.UpdateChatReadOutbox.CONSTRUCTOR -> {
                    val updateChat = obj as TdApi.UpdateChatReadOutbox
                    val chat = chats[updateChat.chatId]
                    if (chat != null) {
                        synchronized(chat) {
                            chat.lastReadOutboxMessageId = updateChat.lastReadOutboxMessageId

                            allMessages[updateChat.chatId]?.let { messages ->

                                messages.forEach {
                                    if (it.id < updateChat.lastReadOutboxMessageId) {
                                        it.canGetViewers = true
                                    }
                                    onMessagesChanged?.invoke()
                                }

                            }
                        }
                    }
                }
                TdApi.UpdateChatUnreadMentionCount.CONSTRUCTOR -> {
                    val updateChat = obj as TdApi.UpdateChatUnreadMentionCount
                    val chat = chats[updateChat.chatId]
                    if (chat != null) {
                        synchronized(chat) {
                            chat.unreadMentionCount = updateChat.unreadMentionCount
                        }
                    }
                }
                TdApi.UpdateMessageEdited.CONSTRUCTOR -> {
                    Log.d("tg_helper", "UpdateMessageEdited ")
                    val updateMessageEdited = obj as TdApi.UpdateMessageEdited
                    lastTelegramUpdateTime = max(lastTelegramUpdateTime, updateMessageEdited.editDate)

                    allMessages[updateMessageEdited.chatId]?.forEachIndexed { i, msg ->
                        if (msg.id == updateMessageEdited.messageId) {
                            allMessages[updateMessageEdited.chatId]!![i].editDate = updateMessageEdited.editDate
                        }
                    }
                }
                TdApi.UpdateMessageContent.CONSTRUCTOR -> {
                    Log.d("tg_helper", "UpdateMessageContent ")
                    val updateMessageContent = obj as TdApi.UpdateMessageContent
                    val message = allMessages[updateMessageContent.chatId]?.first { it.id == updateMessageContent.messageId }
                    if (message == null) {
                        updateMessageContent.apply {
                            requestMessage(chatId, messageId, this@TelegramHelper::addNewMessage)
                        }
                    } else {
                        synchronized(message) {
                            lastTelegramUpdateTime = max(lastTelegramUpdateTime, max(message.date, message.editDate))
                            message.content = updateMessageContent.newContent
//							message.content = OsmandLocationUtils.parseMessageContent(message, this@TelegramHelper)
                        }
//						incomingMessagesListeners.forEach {
//							it.onReceiveChatMessages(message.chatId, message)
//						}
                        onMessagesChanged?.invoke()
                    }
                }
                TdApi.UpdateNewMessage.CONSTRUCTOR -> {
                    Log.d("tg_helper", "UpdateNewMessage ")
                    addNewMessage((obj as TdApi.UpdateNewMessage).message)
                }
                TdApi.UpdateMessageMentionRead.CONSTRUCTOR -> {
                    val updateChat = obj as TdApi.UpdateMessageMentionRead
                    val chat = chats[updateChat.chatId]
                    if (chat != null) {
                        synchronized(chat) {
                            chat.unreadMentionCount = updateChat.unreadMentionCount
                        }
                    }
                }
                TdApi.UpdateMessageSendFailed.CONSTRUCTOR -> {
                    needRefreshActiveLiveMessages = true
                }
                TdApi.UpdateDeleteMessages.CONSTRUCTOR -> {
                    val updateDeleteMessages = obj as TdApi.UpdateDeleteMessages
                    if (updateDeleteMessages.isPermanent) {
                        val chatId = updateDeleteMessages.chatId
                        val deletedMessages = mutableListOf<TdApi.Message>()
                        for (messageId in updateDeleteMessages.messageIds) {
                            allMessages[chatId]?.removeIf {
                                it.id == messageId
                            }
                            usersMessages.remove(messageId)
                                ?.also { deletedMessages.add(it) }
                        }
                        outgoingMessagesListeners.forEach {
                            it.onDeleteMessages(chatId, updateDeleteMessages.messageIds.toList())
                        }
                        if (deletedMessages.isNotEmpty()) {
                            incomingMessagesListeners.forEach {
                                it.onDeleteChatMessages(chatId, deletedMessages)
                            }
                        }
                        onMessagesChanged?.invoke()
//						chats[chatId]?.let { listener?.onTelegramChatChanged(it) }
                    }
                }
                TdApi.UpdateChatReplyMarkup.CONSTRUCTOR -> {
                    val updateChat = obj as TdApi.UpdateChatReplyMarkup
                    val chat = chats[updateChat.chatId]
                    if (chat != null) {
                        synchronized(chat) {
                            chat.replyMarkupMessageId = updateChat.replyMarkupMessageId
                        }
                    }
                }
                TdApi.UpdateChatDraftMessage.CONSTRUCTOR -> {
                    val updateChat = obj as TdApi.UpdateChatDraftMessage
                    val chat = chats[updateChat.chatId]
                    if (chat != null) {
                        synchronized(chat) {
                            chat.draftMessage = updateChat.draftMessage
                            setChatPositions(chat, updateChat.positions)
                        }
                        //listener?.onTelegramChatsChanged()
                    }
                }
                TdApi.UpdateChatNotificationSettings.CONSTRUCTOR -> {
                    val update = obj as TdApi.UpdateChatNotificationSettings
                    val chat = chats[update.chatId]
                    if (chat != null) {
                        synchronized(chat) {
                            chat.notificationSettings = update.notificationSettings
                        }
                    }
                }

                TdApi.UpdateFile.CONSTRUCTOR -> {
                    val updateFile = obj as TdApi.UpdateFile
                    if (updateFile.file.local.isDownloadingCompleted) {
                        val remoteId = updateFile.file.remote.id
                        val chat = downloadChatFilesMap.remove(remoteId)
                        if (chat != null) {
                            synchronized(chat) {
                                chat.photo?.small = updateFile.file
                            }
                            listener?.onTelegramChatChanged(chat)
                        }
                        val user = downloadUserFilesMap.remove(remoteId)
                        if (user != null) {
                            synchronized(user) {
                                user.profilePhoto?.small = updateFile.file
                            }
                            listener?.onTelegramUserChanged(user)
                        }
                    }
                }

                TdApi.UpdateUserFullInfo.CONSTRUCTOR -> {
                    val updateUserFullInfo = obj as TdApi.UpdateUserFullInfo
                    usersFullInfo[updateUserFullInfo.userId] = updateUserFullInfo.userFullInfo
                }
                TdApi.UpdateBasicGroupFullInfo.CONSTRUCTOR -> {
                    val updateBasicGroupFullInfo = obj as TdApi.UpdateBasicGroupFullInfo
                    val id = updateBasicGroupFullInfo.basicGroupId
                    if (basicGroupsFullInfo.containsKey(id)) {
                        val info = updateBasicGroupFullInfo.basicGroupFullInfo
                        basicGroupsFullInfo[id] = info
                        fullInfoUpdatesListeners.forEach { it.onBasicGroupFullInfoUpdated(id, info) }
                    }
                }
                TdApi.UpdateSupergroupFullInfo.CONSTRUCTOR -> {
                    val updateSupergroupFullInfo = obj as TdApi.UpdateSupergroupFullInfo
                    val id = updateSupergroupFullInfo.supergroupId
                    if (supergroupsFullInfo.containsKey(id)) {
                        val info = updateSupergroupFullInfo.supergroupFullInfo
                        supergroupsFullInfo[id] = info
                        fullInfoUpdatesListeners.forEach { it.onSupergroupFullInfoUpdated(id, info) }
                    }
                }
                TdApi.UpdateMessageSendSucceeded.CONSTRUCTOR -> {
                    val updateSucceeded = obj as TdApi.UpdateMessageSendSucceeded
                    val message = updateSucceeded.message
//					log.debug("UpdateMessageSendSucceeded: ${message.id} oldId: ${updateSucceeded.oldMessageId}")
                    outgoingMessagesListeners.forEach {
                        it.onUpdateMessages(listOf(message))
                    }
                }
            }
        }
    }

    private inner class AuthorizationRequestHandler : Client.ResultHandler {
        override fun onResult(obj: TdApi.Object) {
            when (obj.constructor) {
                TdApi.Error.CONSTRUCTOR -> {
                    val errorObj = obj as TdApi.Error
                    if (errorObj.code != IGNORED_ERROR_CODE) {
                        telegramAuthorizationRequestHandler?.telegramAuthorizationRequestListener?.onTelegramAuthorizationRequestError(errorObj.code, errorObj.message)
                        onAuthorizationStateUpdated(null) // repeat last action
                    }
                }
                TdApi.Ok.CONSTRUCTOR -> Unit
                else -> Unit
            }
        }
    }
}