package zaitsev.a.d.mirea.diplom.presentation.ui.profile.profileView.bottomSheet

import android.graphics.Bitmap
import android.net.Uri
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow

interface BSProfileAvatarImp {
    val pagerGallery: Flow<PagingData<Uri>>
    fun takeFromGallery(uri: Uri)
    fun takePhoto(bitmap: Bitmap?)
    fun removeAvatar()
    fun visibleRemoveAvatar(): Boolean
    fun dismiss()
}