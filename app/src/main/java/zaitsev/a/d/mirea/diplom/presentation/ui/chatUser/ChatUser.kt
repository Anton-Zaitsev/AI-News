package zaitsev.a.d.mirea.diplom.presentation.ui.chatUser

import android.graphics.BitmapFactory
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.DoneAll
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import org.drinkless.td.libcore.telegram.TdApi
import zaitsev.a.d.mirea.diplom.R
import zaitsev.a.d.mirea.diplom.data.telegram.TelegramContent
import zaitsev.a.d.mirea.diplom.data.telegram.TelegramGroupInfo
import zaitsev.a.d.mirea.diplom.data.telegram.TelegramMessageModelUI
import zaitsev.a.d.mirea.diplom.data.telegram.TypeChatTelegram
import zaitsev.a.d.mirea.diplom.presentation.isDarkThemeCurrent
import zaitsev.a.d.mirea.diplom.presentation.shimmerEffect
import zaitsev.a.d.mirea.diplom.presentation.theme.ZaitsevNewsTheme
import zaitsev.a.d.mirea.diplom.presentation.theme.navigationColorTextButton
import zaitsev.a.d.mirea.diplom.presentation.ui.chatUser.messageBox.PhotoMessageUI
import zaitsev.a.d.mirea.diplom.presentation.ui.chatUser.messageBox.StickerMessageUI
import zaitsev.a.d.mirea.diplom.presentation.ui.chatUser.messageBox.TextMessageUI
import zaitsev.a.d.mirea.diplom.presentation.ui.chatUser.messageBox.UnsupportedMessageUI
import zaitsev.a.d.mirea.diplom.presentation.ui.chatUser.messageBox.UserInput


private fun TdApi.UserStatus.getUserOnlineStatus(): String {
    return when(this) {
        is TdApi.UserStatusOnline -> "онлайн"
        is TdApi.UserStatusOffline -> "не в сети"
        is TdApi.UserStatusRecently -> "был(а) в сети недавно"
        is TdApi.UserStatusLastWeek -> "был(а) в сети на прошлой неделе"
        is TdApi.UserStatusLastMonth -> "был(а) в сети в прошлом месяце"
        else -> "был(а) в сети давно"
    }
}

fun TdApi.Minithumbnail.getSmallBitmap(): ImageBitmap? {
    return try {
        val bitmap = BitmapFactory.decodeByteArray(
            this.data,
            0,
            this.data.size
        )
        bitmap.asImageBitmap()
    }
    catch (_: Exception){
        null
    }
}
fun TelegramMessageModelUI.getReadingStatusIcon(): ImageVector {
    return if (sendingState == null) {
        if (isOutgoing || canGetViewers) Icons.Rounded.DoneAll
        else Icons.Rounded.Check
    } else {
        if (sendingState is TdApi.MessageSendingStatePending) Icons.Rounded.Schedule
        else Icons.Rounded.ErrorOutline
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatUser(navHostController: NavHostController, viewModel: ChatUserViewModel) {

    var textFieldFocusState by remember { mutableStateOf(false) }

    val status by viewModel.status.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 15.dp)
                    ) {

                        if (viewModel.chat != null) {

                            val chat = requireNotNull(viewModel.chat)

                            Text(
                                text = chat.chatName,
                                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = MaterialTheme.colorScheme.onSecondary,
                                lineHeight = 16.sp
                            )

                            when(viewModel.typeChat){
                                TypeChatTelegram.DEFAULT -> {
                                    Text(
                                        text = status.getUserOnlineStatus(),
                                        fontSize = MaterialTheme.typography.titleSmall.fontSize,
                                        fontWeight = FontWeight.Normal,
                                        color = when (status) {
                                            is TdApi.UserStatusOnline -> MaterialTheme.colorScheme.primary
                                            else -> Color.Gray
                                        },
                                        lineHeight = 15.sp
                                    )
                                }
                                else -> {
                                    val groupInfo by viewModel.groupInfo.collectAsStateWithLifecycle()
                                    if (groupInfo != null){
                                        val group = requireNotNull(groupInfo)

                                        val inOnline = if (group is TelegramGroupInfo.TelegramBasicGroupInfo)
                                            group.listSubscribers.count { it.statusOnline is TdApi.UserStatusOnline  }
                                        else 0

                                        Text(
                                            text = "${group.countSubscribers} участников${if (inOnline > 0) ", $inOnline в сети" else ""}",
                                            fontSize = MaterialTheme.typography.titleSmall.fontSize,
                                            fontWeight = FontWeight.Normal,
                                            color = Color.Gray,
                                            lineHeight = 15.sp
                                        )
                                    }
                                }
                            }
                        }
                        else {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(10.dp)
                                    .clip(CircleShape)
                                    .shimmerEffect()
                            )

                            Spacer(modifier = Modifier.height(5.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.7F)
                                    .height(10.dp)
                                    .clip(CircleShape)
                                    .shimmerEffect()
                            )
                        }
                    }
                },
                navigationIcon = {
                    TextButton(onClick = navHostController::popBackStack, colors = navigationColorTextButton) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                contentDescription = stringResource(R.string.back),
                            )
                            Text(
                                text = stringResource(R.string.back),
                                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                                fontWeight = FontWeight.Normal
                            )
                        }
                    }
                },
                actions = {
                    SubcomposeAsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(viewModel.chat?.chatImgPath)
                            .crossfade(true)
                            .diskCachePolicy(CachePolicy.ENABLED)
                            .build(),
                        contentDescription = stringResource(id = R.string.imageAvatar),
                        modifier = Modifier
                            .padding(end = 10.dp)
                            .size(35.dp),
                        content = {
                            val painter = painter
                            val state = painter.state

                            AnimatedContent(
                                state,
                                transitionSpec = {
                                    fadeIn(
                                        animationSpec = tween(1500)
                                    ) togetherWith fadeOut(animationSpec = tween(1500))
                                },
                                label = "Animated Content",
                                modifier = Modifier.fillMaxWidth()
                            ) { targetState ->
                                when(targetState){
                                    is AsyncImagePainter.State.Loading -> {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .clip(CircleShape)
                                                .shimmerEffect()
                                        )
                                    }
                                    is AsyncImagePainter.State.Success -> {
                                        Image(
                                            painter = painter,
                                            contentDescription = stringResource(id = R.string.imageAvatar),
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .clip(CircleShape),
                                            contentScale = ContentScale.Crop,
                                        )
                                    }
                                    else -> {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(
                                                    MaterialTheme.colorScheme.primary,
                                                    CircleShape
                                                ),
                                            contentAlignment = Alignment.Center
                                        ){
                                            val chatIcon = viewModel.chat?.chatName?.firstOrNull()?.toString() ?: "Н"
                                            Text(
                                                text = chatIcon,
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = MaterialTheme.typography.titleMedium.fontSize
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
            )
        },
        bottomBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                UserInput(
                    textFieldFocus = textFieldFocusState,
                    setTextFieldFocus = { focus ->
                        textFieldFocusState = focus
                    },
                    onMessageSent = viewModel::sendMessage,
                    modifier = Modifier.padding(horizontal = 5.dp),
                    message = viewModel.message,
                    onMessageChanged = viewModel::onMessageChanged,
                    visibleBSPhoto = viewModel.visibleBSPhoto,
                    interfaceAvatar = viewModel.interfaceBSAvatar,
                    setVisiblePhotoBS = viewModel::setVisiblePhotoBS,
                    openBSPhoto = viewModel::openBSPhoto,
                )
                Spacer(modifier = Modifier.height(15.dp))
            }
        },
        content = { paddingValues ->
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)) {

                val messages by viewModel.messages.collectAsStateWithLifecycle()
                val darkTheme = isDarkThemeCurrent()

                val context = LocalContext.current

                val keyboard = LocalSoftwareKeyboardController.current

                val listState = rememberLazyListState()


                LaunchedEffect(listState.isScrollInProgress && textFieldFocusState) {
                    keyboard?.hide()
                }

                LazyColumn(
                    state = listState,
                    reverseLayout = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 15.dp)) {

                    itemsIndexed(messages){ index, message ->

                        viewModel.readMessage(message)

                        DisposableEffect(Unit) {

                            viewModel.scanChat(index)

                            onDispose {

                            }
                        }

                        when(message.messageContent){
                            is TelegramContent.TelegramAnimatedStickerUI -> Unit

                            is TelegramContent.TelegramStickerUI -> {
                                StickerMessageUI(model = message, context = context)
                            }
                            is TelegramContent.TelegramPhotoUI -> {
                                PhotoMessageUI(model = message, darkTheme = darkTheme, context = context, downloadImage = viewModel::downloadImage)
                            }
                            is TelegramContent.TelegramTextUI -> {
                                TextMessageUI(model = message, darkTheme = darkTheme)
                            }

                            else -> {
                                UnsupportedMessageUI(model = message, darkTheme = darkTheme)
                            }
                        }
                    }
                }

            }
        }
    )
}

@PreviewLightDark
@Composable
private fun ChatUserPreview() {
    ZaitsevNewsTheme {
        ChatUser(rememberNavController(), hiltViewModel())
    }
}
