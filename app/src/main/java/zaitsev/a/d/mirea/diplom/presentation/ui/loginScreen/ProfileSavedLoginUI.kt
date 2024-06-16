package zaitsev.a.d.mirea.diplom.presentation.ui.loginScreen

import android.content.Context
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowCircleRight
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import zaitsev.a.d.mirea.diplom.R
import zaitsev.a.d.mirea.diplom.db.dataUI.UserUI
import zaitsev.a.d.mirea.diplom.presentation.shimmerEffect
import zaitsev.a.d.mirea.diplom.presentation.theme.ZaitsevNewsTheme
import java.io.File

@Composable
fun ProfileSavedLoginUI(
    userUI: UserUI,
    context : Context = LocalContext.current,
    onClick: () -> Unit
) {

    ElevatedCard(onClick = onClick, colors = CardDefaults.elevatedCardColors(contentColor = Color.White, containerColor = MaterialTheme.colorScheme.primary)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Absolute.spacedBy(10.dp), modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)) {

            SubcomposeAsyncImage(
                model = ImageRequest.Builder(context)
                    .data(userUI.avatarURL?.let { File(it) })
                    .crossfade(true)
                    .diskCachePolicy(CachePolicy.ENABLED)
                    .build(),
                contentDescription = stringResource(id = R.string.imageAvatar),
                modifier = Modifier.size(40.dp),
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
                        modifier = Modifier.fillMaxSize()
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
                                            Color.White,
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ){
                                    val chatIcon = userUI.name.firstOrNull()?.toString()?.uppercase() ?: "Н"
                                    Text(
                                        text = chatIcon,
                                        color = Color.Black,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = MaterialTheme.typography.titleMedium.fontSize
                                    )
                                }
                            }
                        }
                    }
                }
            )

            Column(modifier = Modifier.weight(1f, fill = false)) {
                Text(text = "${userUI.name} ${userUI.lastName}", fontSize = MaterialTheme.typography.titleMedium.fontSize, fontWeight = FontWeight.Bold)

                val lastPhoneNumber = try {
                    if (userUI.phone.length >= 2){
                        userUI.phone.takeLast(2)
                    } else "**"
                }catch (_: Exception){ "**"}

                Text(text = "+7 *** *** ** $lastPhoneNumber", fontSize = MaterialTheme.typography.titleSmall.fontSize)
            }

            Icon(imageVector = Icons.Default.ArrowCircleRight, contentDescription = null)
        }
    }
}

@Preview
@Composable
private fun ProfileSavedLoginUIPreview() {
    ZaitsevNewsTheme {
        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(10.dp)){
            LazyRow {
                item {
                    ProfileSavedLoginUI(
                        userUI = UserUI(
                            id = 3164,
                            userID = "auctor",
                            name = "Jean",
                            lastName = "Patty",
                            phone = "9567658945",
                            password = "audire",
                            avatarURL = null
                        ), context = LocalContext.current,
                        onClick = {

                        }
                    )
                }
            }
        }
    }
}