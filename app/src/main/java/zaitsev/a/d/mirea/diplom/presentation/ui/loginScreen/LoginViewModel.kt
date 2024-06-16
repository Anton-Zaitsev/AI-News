package zaitsev.a.d.mirea.diplom.presentation.ui.loginScreen

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.annotation.MainThread
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.Lazy
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import zaitsev.a.d.mirea.diplom.db.SignRepository
import zaitsev.a.d.mirea.diplom.db.dataUI.UserUI
import zaitsev.a.d.mirea.diplom.di.IoDispatcher
import zaitsev.a.d.mirea.diplom.presentation.ui.profile.profileView.bottomSheet.BSProfileAvatarImp
import zaitsev.a.d.mirea.libapp.mediaContent.MediaAvatarContent
import zaitsev.a.d.mirea.libapp.pagingGallery.PagingGallery
import zaitsev.a.d.mirea.libapp.phoneChecked.PhoneChecked
import zaitsev.a.d.mirea.telegramapi.api.TelegramHelper
import zaitsev.a.d.mirea.telegramapi.enumData.TelegramAuthorizationState
import zaitsev.a.d.mirea.telegramapi.getTelegramAuthorizationState
import zaitsev.a.d.mirea.telegramapi.interfaceAPI.TelegramAuthorizationRequestListener
import zaitsev.a.d.mirea.telegramapi.interfaceAPI.TelegramListener
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val mediaContent: Lazy<MediaAvatarContent>,
    private val telegramHelper: TelegramHelper,
    private val signRepository: SignRepository,
    val phoneChecked: PhoneChecked,
    private val snackbarHostState: SnackbarHostState,
): ViewModel() {


    private val loginAllScreen = hashMapOf(
        ScreenLoginEnum.START to 0,
        ScreenLoginEnum.TELEGRAM to 1,
    )

    var loginData by mutableStateOf(LoginData())
        private set

    private val mutableAuthStateTelegram: MutableStateFlow<TelegramAuthorizationState> = MutableStateFlow(telegramHelper.authorizationState?.getTelegramAuthorizationState() ?: TelegramAuthorizationState.WAIT_PHONE_NUMBER)
    val authStateTelegram = mutableAuthStateTelegram.asStateFlow()

    var authTelegram by mutableStateOf(AuthTelegram())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var visibleBSAvatar: Boolean by mutableStateOf(false)
        private set

    private val lengthMaskPhone by lazy {
        phoneChecked.maskPhone.filter { it == '#' }.length
    }


    val interfaceBSAvatar: BSProfileAvatarImp by lazy {
        object : BSProfileAvatarImp {
            override val pagerGallery: Flow<PagingData<Uri>>
                get() = Pager(
                    PagingConfig(pageSize = 6)
                ){ PagingGallery(context = appContext, ioDispatcher = ioDispatcher) }.flow.cachedIn(viewModelScope)

            override fun takeFromGallery(uri: Uri) {
                dismiss()
                viewModelScope.launch {
                    val path = mediaContent.get().convertUriToFile(uri = uri)
                    if (path != null){
                        loginData = loginData.copy(avatar = path)
                    }
                }
            }

            override fun takePhoto(bitmap: Bitmap?) {
                dismiss()
                viewModelScope.launch {
                    bitmap?.let { bitmap ->
                        val path = mediaContent.get().convertBitmapToFile(bitmap = bitmap)
                        if (path != null){
                            loginData = loginData.copy(avatar = path)
                        }
                        return@launch
                    }
                    snackbarHostState.showSnackbar("Не удалось сделать фото.")
                }
            }

            override fun removeAvatar() {
                dismiss()
                loginData = loginData.copy(avatar = null)
            }

            override fun visibleRemoveAvatar(): Boolean {
                return loginData.avatar != null
            }

            override fun dismiss() {
                visibleBSAvatar = false
            }
        }
    }

    val flowUserList get() = signRepository.getListFlowUser()
    init {
        telegramHelper.setListener(object : TelegramListener {
            override fun onTelegramStatusChanged(
                prevTelegramAuthorizationState: TelegramAuthorizationState,
                newTelegramAuthorizationState: TelegramAuthorizationState
            ) {
                if (newTelegramAuthorizationState == TelegramAuthorizationState.READY){
                    telegramHelper.setListener(null)
                    telegramHelper.setTelegramAuthorizationRequestHandler(null)
                }
                mutableAuthStateTelegram.update {
                    newTelegramAuthorizationState
                }
                isLoading = false
            }
        })
        telegramHelper.setTelegramAuthorizationRequestHandler(object : TelegramAuthorizationRequestListener {
            override fun onTelegramAuthorizationRequestError(code: Int, message: String) {
                mutableAuthStateTelegram.update {
                    TelegramAuthorizationState.CLOSING
                }
                authTelegram = authTelegram.copy(code = "", phone = "")
                viewModelScope.launch {
                    snackbarHostState.showSnackbar("Произошла ошибка при аунтефикации, код: $code, сообщение $message")
                }
                isLoading = false
            }

            override fun onTelegramUnsupportedAuthorizationState(authorizationState: TelegramAuthorizationState) {
                mutableAuthStateTelegram.update {
                    authorizationState
                }
                isLoading = false
            }
        })

        telegramHelper.requestAuthorizationState()
    }


    fun navigateNext(){
       loginAllScreen[loginData.currentScreen]?.let { index ->
           val indexNext = index + 1
           if (loginAllScreen.containsValue(indexNext)){
               loginData = loginData.copy(
                   currentScreen = loginAllScreen.entries.first { it.value == indexNext }.key,
                   isEnabledNext = false
               )
               if (authStateTelegram.value != TelegramAuthorizationState.WAIT_PHONE_NUMBER){
                   mutableAuthStateTelegram.update {
                       TelegramAuthorizationState.WAIT_PHONE_NUMBER
                   }
                   isLoading = false
               }
           }
       }
    }

    fun navigateDown(){
        isLoading = false
        if (loginData.currentScreen == ScreenLoginEnum.TELEGRAM && (authStateTelegram.value == TelegramAuthorizationState.WAIT_CODE || authStateTelegram.value == TelegramAuthorizationState.WAIT_PASSWORD)){
            mutableAuthStateTelegram.update {
                TelegramAuthorizationState.WAIT_PHONE_NUMBER
            }
            return
        }
        loginAllScreen[loginData.currentScreen]?.let { index ->
            val indexDown = index - 1
            if (loginAllScreen.containsValue(indexDown)){
                loginData = loginData.copy(
                    currentScreen = loginAllScreen.entries.first { it.value == indexDown }.key,
                    isEnabledNext = true
                )
            }
        }
    }

    fun onNameChanged(nameValue: String){
        val nameNow = nameValue.lowercase().replaceFirstChar { it.uppercase() }
        val next = checkCurrentPersonalData(personalData = nameNow) && checkCurrentPersonalData(loginData.lastName)
        loginData = loginData.copy(
            name = nameNow,
            isEnabledNext = next
        )
    }
    fun onLastNameChanged(lastNameValue: String){
        val lastNameNow = lastNameValue.lowercase().replaceFirstChar { it.uppercase() }
        val next = checkCurrentPersonalData(personalData = lastNameValue) && checkCurrentPersonalData(loginData.name)
        loginData = loginData.copy(
            lastName = lastNameNow,
            isEnabledNext = next
        )
    }

    fun checkCurrentPersonalData(personalData: String):Boolean{
        return personalData.filterNot { it.isWhitespace() && it.isDigit() }.isNotEmpty()
    }

    fun isPhoneValid() = phoneChecked.isPhoneValid(authTelegram.phone)
    fun onPhoneChanged(phone: String){
        if (phone.length <= lengthMaskPhone) {
            authTelegram = authTelegram.copy(phone = phone)
        }
    }

    fun onCodeChanged(code: String){
        if (code.length < 6){
            authTelegram = authTelegram.copy(code = code.filter { it.isDigit() })
        }
    }

    fun onPasswordChanged(password: String) {
        authTelegram = authTelegram.copy(password = password)
    }

    fun signBtnTelegram() {
        isLoading = true
        telegramHelper.applyAuthParam(
            type = authStateTelegram.value,
            value = when(authStateTelegram.value){
                TelegramAuthorizationState.WAIT_PHONE_NUMBER -> "+7${authTelegram.phone}"
                TelegramAuthorizationState.WAIT_CODE -> authTelegram.code
                TelegramAuthorizationState.WAIT_PASSWORD -> authTelegram.password
                else -> null
            }
        )
    }

    fun openBSAvatar(requestPermissions: ManagedActivityResultLauncher<Array<String>, Map<String, @JvmSuppressWildcards Boolean>>){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            requestPermissions.launch(arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
            ))
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions.launch(arrayOf(Manifest.permission.READ_MEDIA_IMAGES))
        } else {
            requestPermissions.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE))
        }
    }

    fun setVisibleAvatarBS(permissionAll: Boolean){
        if (permissionAll){
            visibleBSAvatar = true
        }
        else {
            viewModelScope.launch {
                snackbarHostState.showSnackbar("Вы не приняли все разрешения!")
            }
        }
    }

    fun saveUser(@MainThread onSuccess: () -> Unit){
        viewModelScope.launch {
            signRepository.saveUser(
                user = UserUI(
                    userID = UUID.randomUUID().toString(),
                    name = loginData.name,
                    lastName = loginData.lastName,
                    phone = authTelegram.phone,
                    password = authTelegram.password,
                    avatarURL = loginData.avatar
                )
            )
            withContext(Dispatchers.Main){
                onSuccess()
            }
        }
    }

    fun onSignWithExistUser(userUI: UserUI){
        loginData = LoginData(
            currentScreen = ScreenLoginEnum.TELEGRAM,
            isEnabledNext = false,
            name = userUI.name,
            lastName = userUI.lastName,
            avatar = userUI.avatarURL
        )
        authTelegram = AuthTelegram(
            phone = userUI.phone,
            code = "",
            password = userUI.password
        )
        mutableAuthStateTelegram.value = TelegramAuthorizationState.WAIT_PHONE_NUMBER
        signBtnTelegram()
    }

    override fun onCleared() {
        loginAllScreen.clear()
        super.onCleared()
    }

}