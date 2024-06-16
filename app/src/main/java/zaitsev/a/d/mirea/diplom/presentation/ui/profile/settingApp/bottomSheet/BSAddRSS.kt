package zaitsev.a.d.mirea.diplom.presentation.ui.profile.settingApp.bottomSheet

import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowCircleRight
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import zaitsev.a.d.mirea.diplom.R
import zaitsev.a.d.mirea.diplom.data.channelRSS.RSSInfoUI
import zaitsev.a.d.mirea.diplom.presentation.isNull
import zaitsev.a.d.mirea.diplom.presentation.isValidUrl
import zaitsev.a.d.mirea.diplom.presentation.shimmerEffect
import zaitsev.a.d.mirea.diplom.presentation.theme.ZaitsevNewsTheme
import zaitsev.a.d.mirea.diplom.presentation.theme.defaultTextEditColorOutline

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BSAddRSS(sheetState: SheetState, bsAddRSSImp: BSAddRSSImp) {
    ModalBottomSheet(
        onDismissRequest = bsAddRSSImp::dismiss,
        sheetState = sheetState
    ) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp),verticalArrangement = Arrangement.spacedBy(10.dp)) {

            var rssChannel: RSSInfoUI? by remember {
                mutableStateOf(null)
            }

            var isLoading by remember {
                mutableStateOf(false)
            }

            var url by remember {
                mutableStateOf("")
            }
            val context = LocalContext.current
            val scope = rememberCoroutineScope()

            OutlinedTextField(
                value = url,
                onValueChange = { urlValue ->
                    url = urlValue
                },
                isError = !url.isValidUrl(),
                label = { Text(stringResource(R.string.enter_url_rss)) },
                leadingIcon = {
                      var enabledBTN by remember {
                          mutableStateOf(true)
                      }
                      IconButton(onClick = {
                          (context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager).run {
                              val itemsBuffer = primaryClip?.itemCount ?: 0
                              primaryClip?.getItemAt(itemsBuffer - 1)?.let { clip ->
                                  val text = clip.text.toString()
                                  if (text.isValidUrl()){
                                      url = text
                                  }
                                  else {
                                      enabledBTN = false
                                      scope.launch {
                                          delay(1000)
                                          enabledBTN = true
                                      }
                                  }

                              }
                          }
                      }, enabled = enabledBTN) {
                          Icon(imageVector = Icons.Default.ContentPaste, contentDescription = "Paste URL")
                      }
                },
                colors = defaultTextEditColorOutline,
                shape = RoundedCornerShape(10.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Uri,
                    imeAction = ImeAction.Done
                ),
                modifier = Modifier.fillMaxWidth()
            )


            LaunchedEffect(url) {
                rssChannel = null
                if (url.isValidUrl()){
                    delay(500)
                    isLoading = true
                    rssChannel = bsAddRSSImp.findRSSChannel(url = url)
                    isLoading = false
                }
                else {
                    isLoading = false
                }
            }

            val isLoadingData = isLoading && rssChannel.isNull


            if (!isLoading && rssChannel.isNull){
                Spacer(modifier = Modifier.height(25.dp))
            }

            AnimatedVisibility(
                visible = isLoadingData,
                enter = slideInHorizontally(animationSpec = tween(durationMillis = 200)) { fullWidth ->
                    -fullWidth / 3
                } + fadeIn(
                    animationSpec = tween(durationMillis = 200)
                ),
                exit = slideOutHorizontally(animationSpec = spring(stiffness = Spring.StiffnessHigh)) {
                    200
                } + fadeOut()
            ) {
               Box(modifier = Modifier
                   .fillMaxWidth()
                   .padding(bottom = 20.dp, top = 10.dp), contentAlignment = Alignment.Center){
                   Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth(0.8f)) {
                       Box(
                           modifier = Modifier
                               .size(40.dp)
                               .clip(CircleShape)
                               .shimmerEffect()
                       )

                       Column(modifier = Modifier
                           .weight(1f)
                           .padding(start = 10.dp), verticalArrangement = spacedBy(5.dp)) {
                           Box(
                               modifier = Modifier
                                   .fillMaxWidth(0.7f)
                                   .height(15.dp)
                                   .clip(CircleShape)
                                   .shimmerEffect()
                           )
                           Box(
                               modifier = Modifier
                                   .fillMaxWidth(0.85f)
                                   .height(15.dp)
                                   .clip(CircleShape)
                                   .shimmerEffect()
                           )
                       }

                       Box(
                           modifier = Modifier
                               .size(25.dp)
                               .clip(RoundedCornerShape(10.dp))
                               .shimmerEffect()
                       )
                   }
               }
            }

            rssChannel?.let { rss ->
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 25.dp, top = 10.dp), contentAlignment = Alignment.Center) {
                    ElevatedCard(onClick = {
                        bsAddRSSImp.addRSS(rss)
                    }, colors = CardDefaults.elevatedCardColors(contentColor = Color.White, containerColor = MaterialTheme.colorScheme.primary)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Absolute.spacedBy(10.dp), modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)) {

                            SubcomposeAsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(rss.image)
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
                                                    Text(
                                                        text = rss.title.firstOrNull()?.uppercase() ?: "К",
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
                                Text(
                                    text = rss.title,
                                    fontSize = MaterialTheme.typography.titleMedium.fontSize,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = rss.description,
                                    fontSize = MaterialTheme.typography.titleSmall.fontSize,
                                    color = Color.White,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            Icon(imageVector = Icons.Default.ArrowCircleRight, contentDescription = null)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewLightDark
@Composable
private fun BSAddRSSPreview() {
    val sheetState = SheetState(
        skipPartiallyExpanded = false,
        density = LocalDensity.current,
        initialValue = SheetValue.Expanded
    )

    ZaitsevNewsTheme {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)) {
            BSAddRSS(sheetState = sheetState, bsAddRSSImp = object : BSAddRSSImp {
                override suspend fun findRSSChannel(url: String): RSSInfoUI? {
                    return null
                }
                override fun addRSS(rss: RSSInfoUI) = Unit
                override fun dismiss() = Unit
            })
        }
    }
}