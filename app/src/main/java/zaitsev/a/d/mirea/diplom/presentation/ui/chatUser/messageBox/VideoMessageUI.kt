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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem.fromUri
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerView
import org.drinkless.td.libcore.telegram.TdApi
import zaitsev.a.d.mirea.diplom.data.telegram.TelegramMessageModelUI
import zaitsev.a.d.mirea.diplom.presentation.theme.ColorBlueLight
import zaitsev.a.d.mirea.diplom.presentation.toTimeChat
import zaitsev.a.d.mirea.diplom.presentation.ui.chatUser.getReadingStatusIcon
import zaitsev.a.d.mirea.diplom.presentation.ui.chatUser.getSmallBitmap

@Composable
fun VideoMessageUI(model: TelegramMessageModelUI, context: Context, lifecycleOwner: LifecycleOwner){

    val content = (model.messageContent as TdApi.MessageVideo)
    val localPath = content.video.video.local.path.ifEmpty { null }

    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 4.dp),
        horizontalArrangement = if (model.isOutgoing) Arrangement.End else Arrangement.Start
    ) {

        if (model.isOutgoing){
            Spacer(modifier = Modifier.weight(1f, fill = true))
        }

        if (localPath != null) {

            val exoPlayer = remember(context) {
                ExoPlayer.Builder(context)
                    .build()
                    .also { exoPlayer ->
                        exoPlayer.setMediaItem(fromUri(localPath))
                        exoPlayer.volume = 0F
                        exoPlayer.playWhenReady = true
                        exoPlayer.repeatMode = Player.REPEAT_MODE_ONE
                        exoPlayer.prepare()
                    }
            }

            AndroidView(
                modifier = Modifier.clip(RoundedCornerShape(20.dp)),
                factory = { contextView ->
                    PlayerView(contextView).apply {
                        player = exoPlayer
                        useController = false
                    }
                })

            DisposableEffect(lifecycleOwner) {
                // Create an observer that triggers our remembered callbacks
                // for sending analytics events

                val observer = LifecycleEventObserver { _, event ->
                    if (event == Lifecycle.Event.ON_RESUME) {
                        exoPlayer.play()
                    } else if (event == Lifecycle.Event.ON_PAUSE) {
                        exoPlayer.stop()
                    }
                }

                // Add the observer to the lifecycle
                lifecycleOwner.lifecycle.addObserver(observer)

                // When the effect leaves the Composition, remove the observer
                onDispose {
                    exoPlayer.release()
                    lifecycleOwner.lifecycle.removeObserver(observer)
                }
            }
        }
        else {
            Box(contentAlignment = Alignment.BottomEnd, modifier = Modifier
                .weight(1f, fill = true)
                .aspectRatio(1f)
            ) {

                val bitmap = content.video.minithumbnail?.getSmallBitmap()

                if (bitmap != null){
                    Image(
                        bitmap = bitmap,
                        contentDescription = "",
                        contentScale = ContentScale.Crop,
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
                }
                else {
                    Image(
                        imageVector = Icons.Default.Image,
                        contentDescription = "",
                        contentScale = ContentScale.Crop,
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

        if (!model.isOutgoing){
            Spacer(modifier = Modifier.weight(1f, fill = true))
        }

    }
}