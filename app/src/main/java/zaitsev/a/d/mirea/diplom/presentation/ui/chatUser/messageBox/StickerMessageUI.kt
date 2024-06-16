package zaitsev.a.d.mirea.diplom.presentation.ui.chatUser.messageBox

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import zaitsev.a.d.mirea.diplom.R
import zaitsev.a.d.mirea.diplom.data.telegram.TelegramContent
import zaitsev.a.d.mirea.diplom.data.telegram.TelegramMessageModelUI
import zaitsev.a.d.mirea.diplom.presentation.shimmerEffect
import zaitsev.a.d.mirea.diplom.presentation.theme.ColorBlueLight
import zaitsev.a.d.mirea.diplom.presentation.toTimeChat
import zaitsev.a.d.mirea.diplom.presentation.ui.chatUser.chatComponent.AvatarMessage
import zaitsev.a.d.mirea.diplom.presentation.ui.chatUser.getReadingStatusIcon


@Composable
fun StickerMessageUI(model: TelegramMessageModelUI, context: Context) {
    val pathPhoto = when(val content = model.messageContent){
        is TelegramContent.TelegramAnimatedStickerUI -> content.localPath
        is TelegramContent.TelegramStickerUI -> content.localPath
        else -> null
    } ?: return

    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 4.dp),
        horizontalArrangement = if (model.isOutgoing) Arrangement.End else Arrangement.Start
    ) {

        if (model.isOutgoing){
            Spacer(modifier = Modifier.weight(1f, fill = true))
        }

        if (model.userSender != null){
            Row(modifier = Modifier.weight(1f, fill = false),
                horizontalArrangement = Arrangement.Absolute.spacedBy(10.dp),
                verticalAlignment = Alignment.Bottom
            ) {

                if (!model.isOutgoing){
                    AvatarMessage(telegramUserModelUI = model.userSender)
                }

                StickerMessageContent(
                    model = model,
                    context = context,
                    pathPhoto = pathPhoto,
                    modifier = Modifier
                )

                if (model.isOutgoing){
                    AvatarMessage(telegramUserModelUI = model.userSender)
                }
            }
        }
        else {
            StickerMessageContent(
                model = model,
                context = context,
                pathPhoto = pathPhoto,
                modifier = Modifier.weight(1f, fill = false)
            )
        }

        if (!model.isOutgoing){
            Spacer(modifier = Modifier.weight(1f, fill = true))
        }
    }
}
@Composable
private fun StickerMessageContent(model: TelegramMessageModelUI, pathPhoto: String, context: Context, modifier: Modifier){
    SubcomposeAsyncImage(
        model = ImageRequest.Builder(context)
            .data(pathPhoto)
            .crossfade(true)
            .diskCachePolicy(CachePolicy.ENABLED)
            .build(),
        contentDescription = stringResource(id = R.string.imageAvatar),
        modifier = modifier.aspectRatio(1f),
        content = {
            val painter = painter
            val state = painter.state

            when(state){
                is AsyncImagePainter.State.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(15.dp))
                            .shimmerEffect()
                    )
                }
                is AsyncImagePainter.State.Success -> {
                    Box(contentAlignment = Alignment.BottomEnd) {
                        Image(
                            painter = painter,
                            contentDescription = stringResource(id = R.string.imageAvatar),
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(shape = RoundedCornerShape(15.dp))
                                .border(
                                    if (model.isOutgoing)
                                        BorderStroke(1.dp, ColorBlueLight) else
                                        BorderStroke(0.dp, Color.Transparent),
                                    shape = RoundedCornerShape(15.dp)
                                )
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
                                fontSize = 10.sp,
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