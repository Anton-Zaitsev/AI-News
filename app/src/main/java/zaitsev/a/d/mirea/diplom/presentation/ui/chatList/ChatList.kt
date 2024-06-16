package zaitsev.a.d.mirea.diplom.presentation.ui.chatList

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastFilter
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import zaitsev.a.d.mirea.diplom.R
import zaitsev.a.d.mirea.diplom.data.telegram.TelegramChatModelUI
import zaitsev.a.d.mirea.diplom.presentation.shimmerEffect
import zaitsev.a.d.mirea.diplom.presentation.theme.ZaitsevNewsTheme
import zaitsev.a.d.mirea.diplom.presentation.ui.mainNavigation.currentRoute
import zaitsev.a.d.mirea.telegramapi.enumData.TelegramAuthorizationState

@Composable
fun ChatList(navHostController: NavHostController, viewModel: ChatListViewModel = hiltViewModel()) {
    Column(modifier = Modifier.fillMaxSize()) {
        val listChats by viewModel.listChat.collectAsStateWithLifecycle()

        if (viewModel.authStateTelegram == TelegramAuthorizationState.READY) {

            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                item {
                    TextField(
                        value = viewModel.search,
                        onValueChange = viewModel::onSearchChanged,
                        label = { Text(stringResource(R.string.search)) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.onBackground,
                            unfocusedContainerColor = MaterialTheme.colorScheme.onBackground,
                            disabledContainerColor = MaterialTheme.colorScheme.onBackground,
                            errorContainerColor = MaterialTheme.colorScheme.onBackground,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                        ),
                        shape = RoundedCornerShape(10.dp),
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Search, contentDescription = "search")
                        },
                        trailingIcon = {
                            if (viewModel.search.isNotEmpty()){
                                IconButton(onClick = { viewModel.onSearchChanged("") }) {
                                    Icon(imageVector = Icons.Default.Clear, contentDescription = "clear")
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 15.dp, vertical = 10.dp)
                    )
                }
                items(listChats.fastFilter { chat -> if (viewModel.search.isNotEmpty()) chat.chatName.lowercase().contains(viewModel.search.lowercase()) else true }) { chat ->
                    ChatItem(telegramChat = chat, navController = navHostController)
                }
            }
        }
        else {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
                    .padding(vertical = 10.dp, horizontal = 15.dp)
                    .clip(CircleShape)
                    .shimmerEffect()

            )

            repeat(4){
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 5.dp), horizontalArrangement = spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .weight(1F)
                            .aspectRatio(1F)
                            .clip(CircleShape)
                            .shimmerEffect()
                    )

                    Column(modifier = Modifier.weight(4F), verticalArrangement = spacedBy(10.dp), horizontalAlignment = Alignment.Start) {

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(20.dp)
                                .clip(CircleShape)
                                .shimmerEffect()
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.7F)
                                .height(20.dp)
                                .clip(CircleShape)
                                .shimmerEffect()
                        )
                    }
                }
            }

        }
    }
}

@Composable
private fun ChatItem(navController: NavHostController, telegramChat: TelegramChatModelUI){
    Column(modifier = Modifier
        .fillMaxWidth()
        .clickable {
            navController.navigate(telegramChat.currentRoute)
        }) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = spacedBy(10.dp)
        ) {

            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(telegramChat.chatImgPath)
                    .crossfade(true)
                    .diskCachePolicy(CachePolicy.ENABLED)
                    .build(),
                contentDescription = stringResource(id = R.string.imageAvatar),
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f),
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
                                        .background(MaterialTheme.colorScheme.primary, CircleShape),
                                    contentAlignment = Alignment.Center
                                ){
                                    val chatIcon = telegramChat.chatName.firstOrNull()?.toString() ?: "Н"
                                    Text(
                                        text = chatIcon,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = MaterialTheme.typography.titleLarge.fontSize
                                    )
                                }
                            }
                        }
                    }
                }
            )

            Column(modifier = Modifier
                .fillMaxWidth()
                .weight(5f)
            ) {

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = telegramChat.chatName,
                        color = MaterialTheme.colorScheme.onSecondary,
                        fontWeight = FontWeight.W500,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        modifier = Modifier.weight(1f)
                    )
                    telegramChat.chatLastMessageTime?.let { time ->
                        Text(
                            text = time,
                            color = Color.Gray,
                            fontSize = MaterialTheme.typography.titleSmall.fontSize
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {

                    Text(
                        text = telegramChat.chatLastMessage.ifEmpty { "Нет сообщений" },
                        color = Color.Gray,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 2,
                        lineHeight = 17.sp,
                        modifier = Modifier.weight(1f)
                    )
                    if (telegramChat.chatUnreadMessages > 0){
                        Box(modifier = Modifier.background(Color.Gray, CircleShape), contentAlignment = Alignment.Center){
                            Text(
                                text = telegramChat.chatUnreadMessages.toString(),
                                color = MaterialTheme.colorScheme.background,
                                fontSize = MaterialTheme.typography.titleSmall.fontSize,
                                modifier = Modifier.padding(horizontal = 7.dp)
                            )
                        }
                    }
                }
            }

        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = spacedBy(20.dp)) {
            Spacer(modifier = Modifier.weight(1f))
            HorizontalDivider(modifier = Modifier.weight(5f))
        }
    }
}
@PreviewLightDark
@Composable
private fun ChatListPreview() {
    ZaitsevNewsTheme {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)) {
            ChatItem(
                telegramChat = TelegramChatModelUI(
                    chatId = 6538,
                    chatImgPath = null,
                    chatName = "Edith Rich",
                    chatLastMessage = "efficitur",
                    chatLastMessageTime = "volutpat",
                    chatUnreadMessages = 5938
                ),
                navController = rememberNavController()
            )
        }
    }
}