package zaitsev.a.d.mirea.diplom.presentation.ui.loginScreen

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import zaitsev.a.d.mirea.diplom.R
import zaitsev.a.d.mirea.diplom.db.dataUI.UserUI
import zaitsev.a.d.mirea.diplom.presentation.theme.ZaitsevNewsTheme
import zaitsev.a.d.mirea.diplom.presentation.theme.defaultTextEditColorOutline
import zaitsev.a.d.mirea.diplom.presentation.theme.navigationColorTextButton
import zaitsev.a.d.mirea.diplom.presentation.toggle
import zaitsev.a.d.mirea.diplom.presentation.ui.components.MaskVisualTransformation
import zaitsev.a.d.mirea.diplom.presentation.ui.loginScreen.ScreenLoginEnum.START
import zaitsev.a.d.mirea.diplom.presentation.ui.loginScreen.ScreenLoginEnum.TELEGRAM
import zaitsev.a.d.mirea.diplom.presentation.ui.profile.profileView.AvatarProfile
import zaitsev.a.d.mirea.diplom.presentation.ui.profile.profileView.bottomSheet.BSProfileAvatar
import zaitsev.a.d.mirea.diplom.presentation.ui.profile.profileView.bottomSheet.BSProfileAvatarImp
import zaitsev.a.d.mirea.telegramapi.enumData.TelegramAuthorizationState
import java.io.File


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavHostController,
    name: String,
    lastName: String,
    avatarUrl: String?,
    visibleBSAvatar: Boolean,
    interfaceAvatar: BSProfileAvatarImp,
    setVisibleAvatarBS: (permission: Boolean) -> Unit,
    openBSAvatar: (requestPermissions: ManagedActivityResultLauncher<Array<String>, Map<String, @JvmSuppressWildcards Boolean>>) -> Unit,

    isLoading: Boolean,
    isCheckedName: Boolean,
    isCheckedLastName: Boolean,
    onNameChanged: (name: String) -> Unit,
    onLastNameChanged: (lastName: String) -> Unit,

    authTelegram: AuthTelegram,
    onPhoneChanged: (phone: String) -> Unit,
    isPhoneValid: () -> Boolean,
    maskPhone: String,

    authState: TelegramAuthorizationState,
    onCodeChanged: (code: String) -> Unit,
    onPasswordChanged: (password: String) -> Unit,

    signBtnTelegram: () -> Unit,
    sign: (onSuccess: () -> Unit) -> Unit,

    currentScreen: ScreenLoginEnum,
    isEnabledNext: Boolean,
    navigateNext: () -> Unit,
    navigateDown: () -> Unit,

    loginScreen: String,
    mainScreen: String,

    flowUser: Flow<List<UserUI>>,
    onSignExistUser: (user: UserUI) -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    AnimatedContent(targetState = currentScreen, label = ""){ screen ->
                        Text(
                            text = when(screen){
                                START -> "Личные данные"
                                TELEGRAM -> if (authState == TelegramAuthorizationState.READY) "Выберите аватар" else "Вход в Telegram"
                            },
                            color = MaterialTheme.colorScheme.onSecondary,
                            fontSize = MaterialTheme.typography.titleMedium.fontSize,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                navigationIcon = {
                    if (currentScreen != START && authState != TelegramAuthorizationState.READY){
                        TextButton(onClick = navigateDown, colors = navigationColorTextButton) {
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
                    }
                },
                actions = {
                    if (currentScreen != TELEGRAM){
                        TextButton(onClick = navigateNext, enabled = isEnabledNext, colors = navigationColorTextButton) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = stringResource(R.string.next),
                                    fontSize = MaterialTheme.typography.titleMedium.fontSize,
                                    fontWeight = FontWeight.Normal
                                )
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                    contentDescription = stringResource(R.string.next)
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.background),
                scrollBehavior = scrollBehavior
            )
        },
        content = { paddingValues ->
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(25.dp))

                Crossfade(targetState = currentScreen, label = "screenLogin") { screen ->
                    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = spacedBy(15.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        when (screen) {
                            START -> {
                                OutlinedTextField(
                                    value = name,
                                    onValueChange = onNameChanged,
                                    label = { Text("Ваше имя:") },
                                    colors = defaultTextEditColorOutline,
                                    shape = RoundedCornerShape(10.dp),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Text,
                                        imeAction = ImeAction.Next
                                    ),
                                    supportingText = {
                                        if (!isCheckedName) {
                                            Text(text = "Необходимо указать корректное имя.")
                                        }
                                    },
                                    isError = !isCheckedName,
                                    modifier = Modifier.fillMaxWidth()
                                )


                                OutlinedTextField(
                                    value = lastName,
                                    onValueChange = onLastNameChanged,
                                    label = { Text("Ваша фамилия:") },
                                    colors = defaultTextEditColorOutline,
                                    shape = RoundedCornerShape(10.dp),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Text,
                                        imeAction = ImeAction.Done
                                    ),
                                    supportingText = {
                                        if (!isCheckedLastName) {
                                            Text(text = "Необходимо указать корректную фамилию.")
                                        }
                                    },
                                    isError = !isCheckedLastName,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            TELEGRAM -> {
                                Crossfade(targetState = authState, label = "animation") { state ->
                                    when(state){
                                        TelegramAuthorizationState.WAIT_CODE -> {
                                            OutlinedTextField(
                                                value = authTelegram.code,
                                                onValueChange = onCodeChanged,
                                                label = { Text(stringResource(R.string.codeTelegram)) },
                                                colors = defaultTextEditColorOutline,
                                                shape = RoundedCornerShape(10.dp),
                                                singleLine = true,
                                                isError = authTelegram.code.none { it.isDigit() },
                                                keyboardOptions = KeyboardOptions(
                                                    keyboardType = KeyboardType.Number,
                                                    imeAction = ImeAction.Done
                                                ),
                                                supportingText = {
                                                    if (authTelegram.code.none { it.isDigit() }) {
                                                        Text(text = "Необходимо указать корректный код присланный вам на номер телефона.")
                                                    }
                                                },
                                                enabled = !isLoading,
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                        }
                                        TelegramAuthorizationState.WAIT_PASSWORD -> {

                                            var isVisiblePassword by rememberSaveable {
                                                mutableStateOf(false)
                                            }

                                            OutlinedTextField(
                                                value = authTelegram.password,
                                                onValueChange = onPasswordChanged,
                                                label = { Text(stringResource(R.string.passwordTelegram)) },
                                                colors = defaultTextEditColorOutline,
                                                shape = RoundedCornerShape(10.dp),
                                                singleLine = true,
                                                isError = authTelegram.password.filterNot { it.isWhitespace() }.isEmpty(),
                                                keyboardOptions = KeyboardOptions(
                                                    keyboardType = KeyboardType.Password,
                                                    imeAction = ImeAction.Done
                                                ),
                                                visualTransformation = if (isVisiblePassword) VisualTransformation.None else PasswordVisualTransformation(),
                                                supportingText = {
                                                    if (authTelegram.password.filterNot { it.isWhitespace() }.isEmpty()) {
                                                        Text(text = "Необходимо указать ваш пароль от аккаунта Telegram")
                                                    }
                                                },
                                                trailingIcon = {
                                                     IconButton(onClick = { isVisiblePassword = isVisiblePassword.toggle }) {
                                                         Icon(
                                                             imageVector = ImageVector.vectorResource(
                                                                 id = if (isVisiblePassword) R.drawable.eye_open else R.drawable.eye_close
                                                             ),
                                                             contentDescription = "visible password"
                                                         )
                                                     }
                                                },
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                        }
                                        TelegramAuthorizationState.READY -> {
                                            AvatarProfile(
                                                isDarkTheme = isSystemInDarkTheme(),
                                                enabledButton = true,
                                                avatarURL = avatarUrl?.let { url -> File(url) },
                                                setVisibleAvatarBS = setVisibleAvatarBS,
                                                openBSAvatar = openBSAvatar
                                            )
                                        }
                                        else -> {
                                            OutlinedTextField(
                                                value = authTelegram.phone,
                                                onValueChange = onPhoneChanged,
                                                label = { Text(stringResource(R.string.numberPhone)) },
                                                colors = defaultTextEditColorOutline,
                                                shape = RoundedCornerShape(10.dp),
                                                singleLine = true,
                                                isError = !isPhoneValid(),
                                                visualTransformation = MaskVisualTransformation(maskPhone),
                                                keyboardOptions = KeyboardOptions(
                                                    keyboardType = KeyboardType.Phone,
                                                    imeAction = ImeAction.Done
                                                ),
                                                supportingText = {
                                                    if (!isPhoneValid()) {
                                                        Text(text = "Необходимо указать корректный номер телефона.")
                                                    }
                                                },
                                                enabled = !isLoading,
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                        }
                                    }
                                }

                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = if (authState == TelegramAuthorizationState.READY) Alignment.Center else Alignment.CenterEnd
                                ) {
                                    Button(onClick = {
                                        when(authState){
                                            TelegramAuthorizationState.READY -> {
                                                sign.invoke {
                                                    navController.navigate(mainScreen){
                                                        popUpTo(loginScreen) {
                                                            inclusive = true
                                                        }
                                                    }
                                                }
                                            }
                                            else -> {
                                                signBtnTelegram()
                                            }
                                        }
                                    }, enabled = when(authState){
                                        TelegramAuthorizationState.WAIT_CODE -> authTelegram.code.any { it.isDigit() }
                                        TelegramAuthorizationState.WAIT_PASSWORD, TelegramAuthorizationState.READY -> true
                                        else -> isPhoneValid()
                                    } && !isLoading, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = Color.White)) {
                                        Text(text = when(authState){
                                            TelegramAuthorizationState.WAIT_PASSWORD -> "Войти в аккаунт"
                                            TelegramAuthorizationState.READY -> "Перейти к новостям"
                                            else -> "Далее"
                                        })
                                    }
                                }
                            }
                        }
                    }
                }

                val usersList by flowUser.collectAsStateWithLifecycle(initialValue = emptyList())

                if (currentScreen == START && usersList.isNotEmpty()){
                    Spacer(modifier = Modifier.weight(1f))
                    if (usersList.size == 1){
                        Box(modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp, start = 15.dp, end = 15.dp), contentAlignment = Alignment.Center) {
                            ProfileSavedLoginUI(userUI = usersList[0]) {
                                onSignExistUser(usersList[0])
                            }
                        }
                    }
                    else {
                        LazyRow(horizontalArrangement = spacedBy(15.dp), modifier = Modifier.padding(bottom = 20.dp)) {
                            items(usersList){ user ->
                                ProfileSavedLoginUI(userUI = user) {
                                    onSignExistUser(user)
                                }
                            }
                        }
                    }
                }

                val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
                if (visibleBSAvatar){
                    BSProfileAvatar(
                        sheetState = sheetState,
                        bsProfileAvatarImp = interfaceAvatar
                    )
                }
            }
        }
    )
}

@PreviewLightDark
@Composable
private fun LoginScreenStartPreview() {
    ZaitsevNewsTheme {
        val navController = rememberNavController()
        Box(modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)){
            LoginScreen(navController,
                name = "Leigh Harvey",
                lastName = "Jaime Martinez",
                isCheckedName = true,
                isCheckedLastName = true,
                onNameChanged = {},
                onLastNameChanged = {},
                currentScreen = TELEGRAM,
                isEnabledNext = false,
                navigateNext = {},
                navigateDown = {},
                onPhoneChanged = {

                },
                authTelegram = AuthTelegram(
                    phone = "+7(999)999-99-99",
                    code = "neque",
                    password = "pellentesque"
                ),
                signBtnTelegram = {},
                isPhoneValid = { true },
                maskPhone = "",
                authState = TelegramAuthorizationState.READY,
                onCodeChanged = {},
                onPasswordChanged = {},
                avatarUrl = null,
                visibleBSAvatar = false,
                interfaceAvatar = object : BSProfileAvatarImp{
                    override val pagerGallery: Flow<PagingData<Uri>>
                        get() = flow {  }
                    override fun takeFromGallery(uri: Uri) = Unit
                    override fun takePhoto(bitmap: Bitmap?) = Unit
                    override fun removeAvatar() = Unit
                    override fun visibleRemoveAvatar(): Boolean = true
                    override fun dismiss() = Unit
                },
                openBSAvatar = {

                },
                setVisibleAvatarBS = {

                },
                sign = {

                },
                mainScreen = "",
                loginScreen = "",
                flowUser = flow {
                    emit(
                        listOf(
                            UserUI(
                                id = 5296,
                                userID = "aliquid",
                                name = "Tina",
                                lastName = "Rocco",
                                phone = "(903) 647-5647",
                                password = "suas",
                                avatarURL = null
                            )
                        )
                    )
                },
                onSignExistUser = {

                },
                isLoading = false
            )
        }
    }
}