package zaitsev.a.d.mirea.diplom.presentation.ui.profile.profileView

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
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.google.gson.Gson
import dagger.Lazy
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import zaitsev.a.d.mirea.diplom.db.UserRepository
import zaitsev.a.d.mirea.diplom.db.dataUI.UserUI
import zaitsev.a.d.mirea.diplom.di.IoDispatcher
import zaitsev.a.d.mirea.diplom.presentation.ui.mainNavigation.Routing
import zaitsev.a.d.mirea.diplom.presentation.ui.profile.profileView.bottomSheet.BSProfileAvatarImp
import zaitsev.a.d.mirea.libapp.mediaContent.MediaAvatarContent
import zaitsev.a.d.mirea.libapp.pagingGallery.PagingGallery
import zaitsev.a.d.mirea.libapp.phoneChecked.PhoneChecked
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ProfileViewViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val mediaContent: Lazy<MediaAvatarContent>,
    private val userRepository: UserRepository,
    savedStateHandle: SavedStateHandle,
    private val snackbarHostState: SnackbarHostState,
    private val phoneChecked: PhoneChecked,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
): ViewModel() {


    val interfaceBSAvatar: BSProfileAvatarImp by lazy {
        object : BSProfileAvatarImp{
            override val pagerGallery: Flow<PagingData<Uri>>
                get() = Pager(
                    PagingConfig(pageSize = 6)
                ){ PagingGallery(context = appContext, ioDispatcher = ioDispatcher) }.flow.cachedIn(viewModelScope)

            override fun takeFromGallery(uri: Uri) {
                dismiss()
                viewModelScope.launch {
                    loadedPhoto = true
                    val path = mediaContent.get().convertUriToFile(uri = uri)
                    if (path != null){
                        mutableUser = mutableUser?.copy(avatarURL = path)
                    }
                    loadedPhoto = false
                }
            }

            override fun takePhoto(bitmap: Bitmap?) {
                dismiss()
                viewModelScope.launch {
                    bitmap?.let { bitmap ->
                        loadedPhoto = true
                        val path = mediaContent.get().convertBitmapToFile(bitmap = bitmap)
                        if (path != null){
                            mutableUser = mutableUser?.copy(avatarURL = path)
                        }
                        loadedPhoto = false
                        return@launch
                    }
                    snackbarHostState.showSnackbar("Не удалось сделать фото.")
                }
            }

            override fun removeAvatar() {
                dismiss()
                mutableUser = mutableUser?.copy(avatarURL = null)
            }

            override fun visibleRemoveAvatar(): Boolean {
               return mutableUser?.avatarURL != null
            }

            override fun dismiss() {
                visibleBSAvatar = false
            }
        }
    }

    val maskPhone = phoneChecked.maskPhone

    private val lengthMaskPhone by lazy {
        maskPhone.filter { it == '#' }.length
    }

    var mutableUser: UserUI? by mutableStateOf(null)
        private set

    var loadedPhoto: Boolean by mutableStateOf(false)
        private set

    var visibleBSAvatar: Boolean by mutableStateOf(false)
        private set


    init {
        savedStateHandle.get<String>(Routing.USER_ROUTE)?.let { user ->
            val userUI = try {
                val data = Gson().fromJson(user, UserUI::class.java)
                data.copy(phone = data.phone.filter { it.isDigit() })
            }catch (_: Exception){
                null
            }
            mutableUser = userUI
            savedStateHandle.remove<String>(Routing.USER_ROUTE)
        }
    }
    fun setName(name: String){
        mutableUser = mutableUser?.copy(name = if (name.length > 1) name.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(
                Locale.getDefault()
            ) else it.toString()
        } else name)
    }
    fun setFamily(family: String){
        mutableUser = mutableUser?.copy(lastName = if (family.length > 1) family.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(
                Locale.getDefault()
            ) else it.toString()
        } else family)
    }

    fun setPhone(phone: String){
        if (phone.length <= lengthMaskPhone) {
            mutableUser = mutableUser?.copy(phone = phone)
        }
    }

    fun isPhoneValid(): Boolean {
        return mutableUser?.phone?.let { phone -> phoneChecked.isPhoneValid(phone) } ?: false
    }


    fun safeUser(@MainThread isSaved: () -> Unit){
        mutableUser?.let { user ->
            if (!isPhoneValid()) return
            if (user.name.filterNot { it.isWhitespace() }.isEmpty()) return
            if (user.lastName.filterNot { it.isWhitespace() }.isEmpty()) return
            loadedPhoto = true
            viewModelScope.launch {
                userRepository.safeEditUser(userUI = user)
            }.invokeOnCompletion {
                loadedPhoto = false
                isSaved()
            }
        }
    }

    fun openBSAvatar(requestPermissions: ManagedActivityResultLauncher<Array<String>, Map<String, @JvmSuppressWildcards Boolean>>){
        if (mutableUser != null && !loadedPhoto) {
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
}