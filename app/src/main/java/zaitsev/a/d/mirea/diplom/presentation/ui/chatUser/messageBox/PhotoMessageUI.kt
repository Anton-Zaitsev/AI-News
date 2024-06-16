package zaitsev.a.d.mirea.diplom.presentation.ui.chatUser.messageBox

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Downloading
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import kotlinx.coroutines.launch
import zaitsev.a.d.mirea.diplom.R
import zaitsev.a.d.mirea.diplom.data.telegram.TelegramContent
import zaitsev.a.d.mirea.diplom.data.telegram.TelegramMessageModelUI
import zaitsev.a.d.mirea.diplom.presentation.shimmerEffect
import zaitsev.a.d.mirea.diplom.presentation.theme.getBGColorChatInc
import zaitsev.a.d.mirea.diplom.presentation.theme.getBGColorChatOut
import zaitsev.a.d.mirea.diplom.presentation.toTimeChat
import zaitsev.a.d.mirea.diplom.presentation.ui.chatUser.chatComponent.AvatarMessage
import zaitsev.a.d.mirea.diplom.presentation.ui.chatUser.getReadingStatusIcon
import zaitsev.a.d.mirea.diplom.presentation.ui.chatUser.getSmallBitmap


@Composable
fun PhotoMessageUI(model: TelegramMessageModelUI, context: Context, darkTheme: Boolean, downloadImage: suspend (messageID: Long) -> Unit,) {
    val content = (model.messageContent as TelegramContent.TelegramPhotoUI)

    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 4.dp),
        horizontalArrangement = if (model.isOutgoing) Arrangement.End else Arrangement.Start
    ) {

        if (model.isOutgoing){
            Spacer(modifier = Modifier.weight(1f, fill = true))
        }

        if (model.userSender != null){
            Row(
                modifier = Modifier.weight(2.5f, fill = false),
                horizontalArrangement = Arrangement.Absolute.spacedBy(10.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                if (!model.isOutgoing){
                    AvatarMessage(telegramUserModelUI = model.userSender)
                }

                PhotoMessageContent(model = model, content = content, modifier = Modifier, darkTheme = darkTheme, context = context, downloadImage = downloadImage)

                if (model.isOutgoing){
                    AvatarMessage(telegramUserModelUI = model.userSender)
                }
            }
        }
        else {
            PhotoMessageContent(model = model, content = content, modifier = Modifier.weight(2f, fill = false), darkTheme = darkTheme, context = context, downloadImage = downloadImage)
        }

        if (!model.isOutgoing){
            Spacer(modifier = Modifier.weight(1f, fill = true))
        }
    }
}

@Composable
private fun PhotoMessageContent(
    model: TelegramMessageModelUI,
    content: TelegramContent.TelegramPhotoUI,
    darkTheme: Boolean,
    context: Context,
    modifier: Modifier,
    downloadImage: suspend (messageID: Long) -> Unit,
){

    val pathPhoto = content.localPath

    val color = if (!model.isOutgoing) getBGColorChatInc(darkTheme) else getBGColorChatOut(darkTheme)

    if (pathPhoto != null){
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(context)
                .data(pathPhoto)
                .crossfade(true)
                .diskCachePolicy(CachePolicy.ENABLED)
                .build(),
            contentDescription = stringResource(id = R.string.imageAvatar),
            modifier = modifier,
            content = {
                val painter = painter
                val state = painter.state

                when(state){
                    is AsyncImagePainter.State.Loading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(15.dp))
                                .shimmerEffect()
                        )
                    }
                    is AsyncImagePainter.State.Success -> {
                        Column(modifier = Modifier
                            .background(
                                color,
                                RoundedCornerShape(15.dp)
                            )
                            .clip(RoundedCornerShape(15.dp))
                            .border(
                                BorderStroke(1.dp, color),
                                shape = RoundedCornerShape(15.dp)
                            )
                        ) {

                            Box(contentAlignment = Alignment.BottomEnd, modifier = Modifier.fillMaxWidth()) {
                                Image(
                                    painter = painter,
                                    contentDescription = stringResource(id = R.string.imageAvatar),
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Row(modifier = Modifier
                                    .padding(
                                        start = 8.dp,
                                        end = 8.dp,
                                        bottom = 5.dp,
                                        top = 5.dp
                                    )
                                    .background(Color(0, 0, 0, 170), CircleShape)
                                    .padding(horizontal = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {

                                    Text(
                                        text = model.date.toTimeChat(),
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        lineHeight = 20.sp
                                    )

                                    if (!(model.isChannelPost || !model.isOutgoing)){
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Icon(
                                            imageVector = model.getReadingStatusIcon(),
                                            contentDescription = null,
                                            modifier = Modifier.size(15.dp),
                                            tint = Color.White
                                        )
                                    }
                                }
                            }

                            if (content.text != null){
                                Text(
                                    text = content.text,
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.onSecondary,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }
                    else -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(15.dp))
                                .shimmerEffect()
                        )
                    }
                }
            }
        )
    }
    else {

        Column(modifier = Modifier
            .background(
                color,
                RoundedCornerShape(15.dp)
            )
            .clip(RoundedCornerShape(15.dp))
            .border(
                BorderStroke(1.dp, color),
                shape = RoundedCornerShape(15.dp)
            )
        ) {

            val scope = rememberCoroutineScope()

            var isLoading by remember {
                mutableStateOf(false)
            }

            Box(contentAlignment = Alignment.TopEnd, modifier = Modifier.aspectRatio(1f)){

                Box(contentAlignment = Alignment.BottomEnd, modifier = Modifier.fillMaxSize()) {

                    val bitmap = content.smallPhoto?.getSmallBitmap()

                    if (bitmap != null){
                        Image(
                            bitmap = bitmap,
                            contentDescription = "",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                        )
                    }
                    else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .shimmerEffect()
                        )
                    }

                    Row(modifier = Modifier
                        .padding(
                            start = 8.dp,
                            end = 8.dp,
                            bottom = 5.dp,
                            top = 5.dp
                        )
                        .background(Color(0, 0, 0, 170), CircleShape)
                        .padding(horizontal = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Text(
                            text = model.date.toTimeChat(),
                            color = Color.White,
                            fontSize = 10.sp,
                            lineHeight = 18.sp
                        )

                        if (!(model.isChannelPost || !model.isOutgoing)){
                            Spacer(modifier = Modifier.width(3.dp))
                            Icon(
                                imageVector = model.getReadingStatusIcon(),
                                contentDescription = null,
                                modifier = Modifier.size(15.dp),
                                tint = Color.White
                            )
                        }
                    }
                }

                if (!isLoading){
                    IconButton(onClick = {
                        isLoading = true
                        scope.launch {
                            downloadImage.invoke(model.id)
                            isLoading = false
                        }
                    }, colors = IconButtonDefaults.iconButtonColors(contentColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.padding(5.dp)) {
                        Icon(imageVector = Icons.Default.Downloading, contentDescription = null)
                    }
                }
                else {
                    CircularProgressIndicator(
                        color = Color.White,
                        trackColor = MaterialTheme.colorScheme.primary,
                        strokeCap = StrokeCap.Round,
                        modifier = Modifier.padding(10.dp).size(35.dp)
                    )
                }
            }

            if (content.text != null){
                Text(
                    text = content.text,
                    color = MaterialTheme.colorScheme.onSecondary,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                )
            }

        }
    }
}