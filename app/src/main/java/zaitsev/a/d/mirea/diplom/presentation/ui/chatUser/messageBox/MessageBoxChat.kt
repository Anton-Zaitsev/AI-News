package zaitsev.a.d.mirea.diplom.presentation.ui.chatUser.messageBox

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Mood
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusTarget
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.EmojiSupportMatch
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.paging.PagingData
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import zaitsev.a.d.mirea.diplom.R
import zaitsev.a.d.mirea.diplom.data.telegram.telegramSending.TelegramSending
import zaitsev.a.d.mirea.diplom.presentation.shimmerEffect
import zaitsev.a.d.mirea.diplom.presentation.theme.ZaitsevNewsTheme
import zaitsev.a.d.mirea.diplom.presentation.toggle
import zaitsev.a.d.mirea.diplom.presentation.ui.profile.profileView.bottomSheet.BSProfileAvatar
import zaitsev.a.d.mirea.diplom.presentation.ui.profile.profileView.bottomSheet.BSProfileAvatarImp
import java.io.File


@PreviewLightDark
@Composable
private fun UserInputPreview() {
    ZaitsevNewsTheme {
        var textFieldFocusState by remember { mutableStateOf(false) }

        UserInput(onMessageSent = {}, textFieldFocus = textFieldFocusState, setTextFieldFocus = {
            textFieldFocusState = it
        }, visibleBSPhoto = false, setVisiblePhotoBS = { }, interfaceAvatar = object : BSProfileAvatarImp{
            override val pagerGallery: Flow<PagingData<Uri>>
                get() = flow {  }

            override fun takeFromGallery(uri: Uri){}

            override fun takePhoto(bitmap: Bitmap?){}

            override fun removeAvatar(){}

            override fun visibleRemoveAvatar(): Boolean {
                return true
            }

            override fun dismiss() {}
        }, openBSPhoto = {

        }, message = TelegramSending.SendingPhoto("", ""), onMessageChanged = {}, modifier = Modifier, resetScroll = {})
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserInput(
    message: TelegramSending,
    onMessageChanged: (TelegramSending) -> Unit,
    onMessageSent: () -> Unit,

    textFieldFocus: Boolean,
    setTextFieldFocus: (value: Boolean) -> Unit,
    modifier: Modifier = Modifier,
    resetScroll: () -> Unit = {},

    visibleBSPhoto: Boolean,
    interfaceAvatar: BSProfileAvatarImp,
    setVisiblePhotoBS: (permission: Boolean) -> Unit,
    openBSPhoto: (requestPermissions: ManagedActivityResultLauncher<Array<String>, Map<String, @JvmSuppressWildcards Boolean>>) -> Unit,
) {

    val requestPhotoPermissions = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        setVisiblePhotoBS(results.entries.all { it.value })
    }

    var isShowEmoji by remember {
        mutableStateOf(false)
    }
    val dismissKeyboard = { isShowEmoji = false}


    if (isShowEmoji) {
        BackHandler(onBack = dismissKeyboard)
    }

    val textState by rememberSaveable(message, stateSaver = TextFieldValue.Saver) {
        val myMessage = when(message){
            is TelegramSending.SendingPhoto -> message.text ?: ""
            is TelegramSending.SendingText -> message.text
        }
        mutableStateOf(TextFieldValue(text = myMessage, selection = TextRange(myMessage.length)))
    }


    Surface(color = MaterialTheme.colorScheme.background) {
        Column(modifier = modifier) {


            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
                verticalAlignment = Alignment.Bottom
            ) {


                IconButton(
                    onClick = {
                        openBSPhoto(requestPhotoPermissions)
                    },
                    colors = IconButtonDefaults.iconButtonColors(contentColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(
                        imageVector = Icons.Default.AddCircleOutline,
                        contentDescription = "Media",
                        modifier = Modifier.size(28.dp)
                    )
                }


                MessageBoxChat(modifier = Modifier
                    .weight(1f)
                    .padding(end = 5.dp)
                    .semantics {
                        keyboardShownProperty =
                            isShowEmoji == false && textFieldFocus
                    },
                    textFieldValue = textState,
                    onTextChanged = { textField ->
                        val messageSend = when(message){
                            is TelegramSending.SendingPhoto -> message.copy(text = textField.text)
                            is TelegramSending.SendingText -> message.copy(text = textField.text)
                        }
                        onMessageChanged(messageSend)
                    },
                    onTextFieldFocused = { focused ->
                        if (focused) {
                            isShowEmoji = false
                            resetScroll()
                        }
                        setTextFieldFocus(focused)
                    },
                    focusState = textFieldFocus,
                    keyboardType = KeyboardType.Text,
                    onSend = {
                        onMessageSent()
                        resetScroll()
                        dismissKeyboard()
                    },
                    onShowEmoji = {
                        isShowEmoji = isShowEmoji.toggle
                    },
                    photoAttached = when(message){
                        is TelegramSending.SendingPhoto -> message.photoPath
                        is TelegramSending.SendingText -> null
                    },
                    removePhoto = interfaceAvatar::removeAvatar
                )

                AnimatedVisibility(
                    visible = (message is TelegramSending.SendingPhoto) || textState.text.filterNot { it.isWhitespace() }.isNotEmpty(),
                    enter = expandHorizontally { 20 },
                    exit = shrinkHorizontally(
                        animationSpec = tween(),
                        shrinkTowards = Alignment.End,
                    ) { fullWidth ->
                        fullWidth / 4
                    }
                ) {
                    IconButton(
                        onClick = {
                            onMessageSent()
                            resetScroll()
                            dismissKeyboard()
                        },
                        colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = Color.White),
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowUpward,
                            contentDescription = "Media",
                        )
                    }
                }
            }

            AnimatedContent(
                targetState = isShowEmoji,
                transitionSpec = {
                    if (targetState > initialState) {
                        (slideInVertically { height -> height } + fadeIn()).togetherWith(
                            slideOutVertically { height -> -height } + fadeOut())
                    } else {
                        (slideInVertically { height -> -height } + fadeIn()).togetherWith(
                            slideOutVertically { height -> height } + fadeOut())
                    }.using(
                        SizeTransform(clip = false)
                    )
                }, label = "AnimatedEmoji"
            ) { isShowEmojiValue ->
                if (isShowEmojiValue){

                    val focusRequester = FocusRequester()
                    SideEffect {
                        focusRequester.requestFocus()
                    }

                    Surface(color = MaterialTheme.colorScheme.background) {
                        EmojiSelector(
                            onTextAdded = { emoji ->
                                val messageNew = when(message){
                                    is TelegramSending.SendingPhoto -> message.copy(text = message.text + emoji)
                                    is TelegramSending.SendingText -> message.copy(text = message.text + emoji)
                                }
                                onMessageChanged(messageNew)
                            },
                            focusRequester = focusRequester
                        )
                    }
                }
            }
        }

        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        if (visibleBSPhoto){
            BSProfileAvatar(
                sheetState = sheetState,
                bsProfileAvatarImp = interfaceAvatar,
                buttonNameDelete = stringResource(R.string.remove_photo)
            )
        }

    }
}
private val KeyboardShownKey = SemanticsPropertyKey<Boolean>("KeyboardShownKey")
private var SemanticsPropertyReceiver.keyboardShownProperty by KeyboardShownKey

@Composable
private fun MessageBoxChat(
    photoAttached: String?,
    removePhoto: () -> Unit,
    modifier: Modifier,
    onSend: () -> Unit,
    onShowEmoji: () -> Unit,
    textFieldValue: TextFieldValue,
    onTextChanged: (TextFieldValue) -> Unit,
    onTextFieldFocused: (Boolean) -> Unit,
    keyboardType: KeyboardType,
    focusState: Boolean,
) {

    var lastFocusState by remember { mutableStateOf(false) }

    Column(modifier = modifier.background(MaterialTheme.colorScheme.onBackground, RoundedCornerShape(20.dp)), verticalArrangement = spacedBy(10.dp), horizontalAlignment = Alignment.Start) {

        if (photoAttached != null){
            Column(modifier = Modifier.fillMaxWidth()) {
                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(File(photoAttached))
                        .crossfade(true)
                        .diskCachePolicy(CachePolicy.ENABLED)
                        .build(),
                    contentDescription = stringResource(id = R.string.imageAvatar),
                    modifier = Modifier
                        .padding(10.dp)
                        .size(80.dp),
                    content = {
                        val painter = painter
                        when(val state = painter.state){
                            is AsyncImagePainter.State.Loading -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(10.dp))
                                        .shimmerEffect()
                                )
                            }
                            else -> {
                                Button(modifier = Modifier.fillMaxSize(),
                                    onClick = removePhoto,
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopEnd){
                                        if (state is AsyncImagePainter.State.Success){
                                            Image(
                                                painter = painter,
                                                contentDescription = stringResource(id = R.string.imageAvatar),
                                                modifier = Modifier.fillMaxSize(),
                                                contentScale = ContentScale.Crop,
                                            )
                                        }
                                        else {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .shimmerEffect()
                                            )
                                        }

                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = null, tint = Color.Red,
                                            modifier = Modifier.padding(4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                )
                HorizontalDivider()
            }
        }

        BasicTextField(modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { state ->
                if (lastFocusState != state.isFocused) {
                    onTextFieldFocused(state.isFocused)
                }
                lastFocusState = state.isFocused
            },
            value = textFieldValue,
            onValueChange = onTextChanged,
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            textStyle = LocalTextStyle.current.copy(
                color = MaterialTheme.colorScheme.onSecondary,
                platformStyle = PlatformTextStyle(
                    emojiSupportMatch = EmojiSupportMatch.None
                )
            ),
            maxLines = 4,
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType,
                imeAction = ImeAction.Send
            ),
            keyboardActions = KeyboardActions(onSend = { onSend() }),
            decorationBox = { innerTextField ->

                val valueIsEmpty = textFieldValue.text.isEmpty()

                ConstraintLayout(modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 15.dp, end = 5.dp)
                ) {
                    val (box, button) = createRefs()

                    Box(modifier = Modifier.constrainAs(box){
                        height = Dimension.preferredWrapContent
                        width = Dimension.fillToConstraints
                        linkTo(start = parent.start, end = button.start)
                        linkTo(top = parent.top, topMargin = 2.dp, bottom = parent.bottom, bottomMargin = 2.dp)
                    }) {

                        if (valueIsEmpty && !focusState){
                            Text(
                                text = stringResource(R.string.message),
                                color = Color.Gray
                            )
                        }
                        innerTextField()
                    }

                    IconButton(
                        onClick = onShowEmoji,
                        colors = IconButtonDefaults.iconButtonColors(contentColor = Color.Gray),
                        modifier = Modifier.constrainAs(button){
                            height = Dimension.wrapContent
                            width = Dimension.wrapContent
                            end.linkTo(parent.end)
                            linkTo(top = parent.top, bottom = parent.bottom, bias = 1f)
                        }
                    ) {
                        Icon(imageVector = Icons.Outlined.Mood, contentDescription = "emoji")
                    }
                }
            }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalFoundationApi::class)
@Composable
private fun EmojiSelector(
    onTextAdded: (String) -> Unit,
    focusRequester: FocusRequester
){

    Column(
        modifier = Modifier
            .focusRequester(focusRequester)
            .focusTarget()
            .semantics { contentDescription = "Выбор Эмоджи" }
    ) {
        HorizontalDivider()


        val pagerState = rememberPagerState(pageCount = {
            (emojis.size + SIZE_IN_PAGE_EMOJI - 1) / SIZE_IN_PAGE_EMOJI
        })

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth(),
            userScrollEnabled = true,
            reverseLayout = false,
            pageSize = PageSize.Fill
        ) { page ->

            val startIndex = page * SIZE_IN_PAGE_EMOJI
            val endIndex = minOf(startIndex + SIZE_IN_PAGE_EMOJI, emojis.size)
            val emojisForPage = emojis.subList(startIndex, endIndex)


            FlowRow{
                emojisForPage.forEach { emoji ->
                    TextButton(
                        onClick = { onTextAdded(emoji) },
                        modifier = Modifier
                            .sizeIn(minWidth = 42.dp, minHeight = 42.dp)
                    ) {
                        Text(
                            text = emoji,
                            style = LocalTextStyle.current.copy(
                                fontSize = 18.sp
                            ),
                        )
                    }
                }
            }
        }

        val widthBox = (LocalConfiguration.current.screenWidthDp / pagerState.pageCount) / 2 - 20
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(pagerState.pageCount) { iteration ->
                val color = if (pagerState.currentPage == iteration) Color.DarkGray else Color.LightGray
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .clip(CircleShape)
                        .background(color)
                        .width(widthBox.dp)
                        .height(4.dp)
                )
            }
        }
    }
}

private const val SIZE_IN_PAGE_EMOJI = 32

private val emojis = listOf(
    "\ud83d\ude00", // Grinning Face
    "\ud83d\ude01", // Grinning Face With Smiling Eyes
    "\ud83d\ude02", // Face With Tears of Joy
    "\ud83d\ude03", // Smiling Face With Open Mouth
    "\ud83d\ude04", // Smiling Face With Open Mouth and Smiling Eyes
    "\ud83d\ude05", // Smiling Face With Open Mouth and Cold Sweat
    "\ud83d\ude06", // Smiling Face With Open Mouth and Tightly-Closed Eyes
    "\ud83d\ude09", // Winking Face
    "\ud83d\ude0a", // Smiling Face With Smiling Eyes
    "\ud83d\ude0b", // Face Savouring Delicious Food
    "\ud83d\ude0e", // Smiling Face With Sunglasses
    "\ud83d\ude0d", // Smiling Face With Heart-Shaped Eyes
    "\ud83d\ude18", // Face Throwing a Kiss
    "\ud83d\ude17", // Kissing Face
    "\ud83d\ude19", // Kissing Face With Smiling Eyes
    "\ud83d\ude1a", // Kissing Face With Closed Eyes
    "\u263a", // White Smiling Face
    "\ud83d\ude42", // Slightly Smiling Face
    "\ud83e\udd17", // Hugging Face
    "\ud83d\ude07", // Smiling Face With Halo
    "\ud83e\udd13", // Nerd Face
    "\ud83e\udd14", // Thinking Face
    "\ud83d\ude10", // Neutral Face
    "\ud83d\ude11", // Expressionless Face
    "\ud83d\ude36", // Face Without Mouth
    "\ud83d\ude44", // Face With Rolling Eyes
    "\ud83d\ude0f", // Smirking Face
    "\ud83d\ude23", // Persevering Face
    "\ud83d\ude25", // Disappointed but Relieved Face
    "\ud83d\ude2e", // Face With Open Mouth
    "\ud83e\udd10", // Zipper-Mouth Face
    "\ud83d\ude2f", // Hushed Face
    "\ud83d\ude2a", // Sleepy Face
    "\ud83d\ude2b", // Tired Face
    "\ud83d\ude34", // Sleeping Face
    "\ud83d\ude0c", // Relieved Face
    "\ud83d\ude1b", // Face With Stuck-Out Tongue
    "\ud83d\ude1c", // Face With Stuck-Out Tongue and Winking Eye
    "\ud83d\ude1d", // Face With Stuck-Out Tongue and Tightly-Closed Eyes
    "\ud83d\ude12", // Unamused Face
    "\ud83d\ude13", // Face With Cold Sweat
    "\ud83d\ude14", // Pensive Face
    "\ud83d\ude15", // Confused Face
    "\ud83d\ude43", // Upside-Down Face
    "\ud83e\udd11", // Money-Mouth Face
    "\ud83d\ude32", // Astonished Face
    "\ud83d\ude37", // Face With Medical Mask
    "\ud83e\udd12", // Face With Thermometer
    "\ud83e\udd15", // Face With Head-Bandage
    "\u2639", // White Frowning Face
    "\ud83d\ude41", // Slightly Frowning Face
    "\ud83d\ude16", // Confounded Face
    "\ud83d\ude1e", // Disappointed Face
    "\ud83d\ude1f", // Worried Face
    "\ud83d\ude24", // Face With Look of Triumph
    "\ud83d\ude22", // Crying Face
    "\ud83d\ude2d", // Loudly Crying Face
    "\ud83d\ude26", // Frowning Face With Open Mouth
    "\ud83d\ude27", // Anguished Face
    "\ud83d\ude28", // Fearful Face
    "\ud83d\ude29", // Weary Face
    "\ud83d\ude2c", // Grimacing Face
    "\ud83d\ude30", // Face With Open Mouth and Cold Sweat
    "\ud83d\ude31", // Face Screaming in Fear
    "\ud83d\ude33", // Flushed Face
    "\ud83d\ude35", // Dizzy Face
    "\ud83d\ude21", // Pouting Face
    "\ud83d\ude20", // Angry Face
    "\ud83d\ude08", // Smiling Face With Horns
    "\ud83d\udc7f", // Imp
    "\ud83d\udc79", // Japanese Ogre
    "\ud83d\udc7a", // Japanese Goblin
    "\ud83d\udc80", // Skull
    "\ud83d\udc7b", // Ghost
    "\ud83d\udc7d", // Extraterrestrial Alien
    "\ud83e\udd16", // Robot Face
    "\ud83d\udca9", // Pile of Poo
    "\ud83d\ude3a", // Smiling Cat Face With Open Mouth
    "\ud83d\ude38", // Grinning Cat Face With Smiling Eyes
    "\ud83d\ude39", // Cat Face With Tears of Joy
    "\ud83d\ude3b", // Smiling Cat Face With Heart-Shaped Eyes
    "\ud83d\ude3c", // Cat Face With Wry Smile
    "\ud83d\ude3d", // Kissing Cat Face With Closed Eyes
    "\ud83d\ude40", // Weary Cat Face
    "\ud83d\ude3f", // Crying Cat Face
    "\ud83d\ude3e", // Pouting Cat Face
    "\ud83d\udc66", // Boy
    "\ud83d\udc67", // Girl
    "\ud83d\udc68", // Man
    "\ud83d\udc69", // Woman
    "\ud83d\udc74", // Older Man
    "\ud83d\udc75", // Older Woman
    "\ud83d\udc76", // Baby
    "\ud83d\udc71", // Person With Blond Hair
    "\ud83d\udc6e", // Police Officer
    "\ud83d\udc72", // Man With Gua Pi Mao
    "\ud83d\udc73", // Man With Turban
    "\ud83d\udc77", // Construction Worker
    "\u26d1", // Helmet With White Cross
    "\ud83d\udc78", // Princess
    "\ud83d\udc82", // Guardsman
    "\ud83d\udd75", // Sleuth or Spy
    "\ud83c\udf85", // Father Christmas
    "\ud83d\udc70", // Bride With Veil
    "\ud83d\udc7c", // Baby Angel
    "\ud83d\udc86", // Face Massage
    "\ud83d\udc87", // Haircut
    "\ud83d\ude4d", // Person Frowning
    "\ud83d\ude4e", // Person With Pouting Face
    "\ud83d\ude45", // Face With No Good Gesture
    "\ud83d\ude46", // Face With OK Gesture
    "\ud83d\udc81", // Information Desk Person
    "\ud83d\ude4b", // Happy Person Raising One Hand
    "\ud83d\ude47", // Person Bowing Deeply
    "\ud83d\ude4c", // Person Raising Both Hands in Celebration
    "\ud83d\ude4f", // Person With Folded Hands
    "\ud83d\udde3", // Speaking Head in Silhouette
    "\ud83d\udc64", // Bust in Silhouette
    "\ud83d\udc65", // Busts in Silhouette
    "\ud83d\udeb6", // Pedestrian
    "\ud83c\udfc3", // Runner
    "\ud83d\udc6f", // Woman With Bunny Ears
    "\ud83d\udc83", // Dancer
    "\ud83d\udd74", // Man in Business Suit Levitating
    "\ud83d\udc6b", // Man and Woman Holding Hands
    "\ud83d\udc6c", // Two Men Holding Hands
    "\ud83d\udc6d", // Two Women Holding Hands
    "\ud83d\udc8f" // Kiss
)