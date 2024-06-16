package zaitsev.a.d.mirea.diplom.presentation.ui.profile.profileView

import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import zaitsev.a.d.mirea.diplom.R
import zaitsev.a.d.mirea.diplom.presentation.shimmerEffect
import zaitsev.a.d.mirea.diplom.presentation.theme.ColorLightBlueDark
import zaitsev.a.d.mirea.diplom.presentation.theme.ColorLightBlueDarkLight
import zaitsev.a.d.mirea.diplom.presentation.theme.ColorLightBlueLight
import zaitsev.a.d.mirea.diplom.presentation.theme.ColorLightDarkDark
import java.io.File

@Composable
fun AvatarProfile(
    isDarkTheme: Boolean,
    enabledButton: Boolean,
    avatarURL: File?,
    setVisibleAvatarBS: (permissionAll: Boolean) -> Unit,
    openBSAvatar: (requestPermissions: ManagedActivityResultLauncher<Array<String>, Map<String, @JvmSuppressWildcards Boolean>>) -> Unit
){
    SubcomposeAsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(avatarURL)
            .crossfade(true)
            .diskCachePolicy(CachePolicy.ENABLED)
            .build(),
        contentDescription = stringResource(id = R.string.imageAvatar),
        modifier = Modifier.fillMaxWidth(),
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
            ){ targetState ->
                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    if (targetState is AsyncImagePainter.State.Success || targetState is AsyncImagePainter.State.Error) {

                        val requestPhotoPermissions = rememberLauncherForActivityResult(
                            ActivityResultContracts.RequestMultiplePermissions()
                        ) { results ->
                            setVisibleAvatarBS(results.entries.all { it.value })
                        }

                        if (targetState is AsyncImagePainter.State.Error) {
                            ConstraintLayout(
                                modifier = Modifier
                                    .fillMaxWidth(1f / 2.4f)
                                    .aspectRatio(1f)
                            ) {

                                val (box, image) = createRefs()

                                Box(modifier = Modifier
                                    .constrainAs(box) {
                                        linkTo(top = parent.top, bottom = parent.bottom)
                                        linkTo(start = parent.start, end = parent.end)
                                        width = Dimension.fillToConstraints
                                        height = Dimension.fillToConstraints
                                    }
                                    .background(
                                        if (!isDarkTheme) ColorLightBlueLight else ColorLightBlueDark,
                                        CircleShape
                                    )
                                    .clip(CircleShape)
                                    .clickable {
                                        openBSAvatar(requestPhotoPermissions)
                                    }
                                )

                                Icon(
                                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_camera),
                                    contentDescription = stringResource(R.string.imageAvatar),
                                    modifier = Modifier
                                        .constrainAs(image) {
                                            linkTo(
                                                start = box.start,
                                                end = box.end,
                                                startMargin = 30.dp,
                                                endMargin = 30.dp
                                            )
                                            linkTo(
                                                top = box.top,
                                                bottom = box.bottom,
                                                topMargin = 30.dp,
                                                bottomMargin = 30.dp
                                            )
                                            width = Dimension.fillToConstraints
                                            height = Dimension.fillToConstraints
                                        }
                                        .clickable {
                                            openBSAvatar(requestPhotoPermissions)
                                        },
                                    tint = if (!isDarkTheme) ColorLightBlueDarkLight else ColorLightDarkDark
                                )
                            }
                        } else {
                            Image(
                                painter = painter,
                                contentDescription = stringResource(id = R.string.imageAvatar),
                                modifier = Modifier
                                    .fillMaxWidth(1f / 2.4f)
                                    .aspectRatio(1f)
                                    .clip(CircleShape)
                                    .clickable {
                                        openBSAvatar(requestPhotoPermissions)
                                    },
                                contentScale = ContentScale.Crop,
                            )
                        }

                        Spacer(modifier = Modifier.height(5.dp))

                        TextButton(
                            enabled = enabledButton,
                            onClick = {
                                openBSAvatar(requestPhotoPermissions)
                            },
                            colors = ButtonDefaults.textButtonColors(contentColor = if (!isDarkTheme) ColorLightBlueDarkLight else ColorLightDarkDark)
                        ) {
                            Text(text = stringResource(R.string.pickPhoto))
                        }
                    }
                    else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(1f / 2.4f)
                                .aspectRatio(1f)
                                .clip(CircleShape)
                                .shimmerEffect()
                        )
                    }
                }
            }
        }
    )

}