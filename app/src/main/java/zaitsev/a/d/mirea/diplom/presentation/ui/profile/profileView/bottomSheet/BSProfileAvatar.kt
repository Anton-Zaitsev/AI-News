package zaitsev.a.d.mirea.diplom.presentation.ui.profile.profileView.bottomSheet

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import zaitsev.a.d.mirea.diplom.R
import zaitsev.a.d.mirea.diplom.presentation.theme.ZaitsevNewsTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BSProfileAvatar(sheetState: SheetState, bsProfileAvatarImp: BSProfileAvatarImp, buttonNameDelete: String = stringResource(R.string.removeAvatar)){
    ModalBottomSheet(
        onDismissRequest = bsProfileAvatarImp::dismiss,
        sheetState = sheetState
    ) {
        val scope = rememberCoroutineScope()
        Column(modifier = Modifier.fillMaxWidth()) {

            val launcherGallery = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri ->
                uri?.let { bsProfileAvatarImp.takeFromGallery(it) }
            }
            val launcherTakePhoto = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
                bsProfileAvatarImp.takePhoto(bitmap)
            }

            val widthScreen = (LocalConfiguration.current.screenWidthDp.dp / 5) + (5.dp * 4)
            val context = LocalContext.current

            val lazyPagingItems = bsProfileAvatarImp.pagerGallery.collectAsLazyPagingItems()
            LazyRow(modifier = Modifier.padding(vertical = 10.dp)){
                items(
                    lazyPagingItems.itemCount,
                    key = lazyPagingItems.itemKey { it }
                ) { index ->
                    lazyPagingItems[index]?.let{ url ->

                        Box(modifier = Modifier
                            .size(widthScreen)
                            .clip(RoundedCornerShape(10))
                            .clickable {
                                bsProfileAvatarImp.takeFromGallery(url)
                            }
                            .padding(5.dp)
                        ){
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(url)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = stringResource(R.string.imageFromGallery),
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(10))
                            )
                        }
                    }
                }
            }

            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 15.dp),verticalArrangement = spacedBy(5.dp)) {
                OutlinedButton(onClick = {
                    launcherGallery.launch("image/*")
                }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                    Text(text = stringResource(R.string.openGallery))
                }

                OutlinedButton(onClick = {
                    launcherTakePhoto.launch()
                }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                    Text(text = stringResource(R.string.takePhoto))
                }


                if (bsProfileAvatarImp.visibleRemoveAvatar()){
                    OutlinedButton(
                        onClick = bsProfileAvatarImp::removeAvatar,
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)) {
                        Text(buttonNameDelete)
                    }
                }

                Button(onClick = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) {
                            bsProfileAvatarImp.dismiss()
                        }
                    }
                }, modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 15.dp)) {
                    Text(text = stringResource(id = R.string.back))
                }
            }
        }
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@PreviewLightDark
@Composable
private fun ProfileViewPreview() {
    val sheetState = SheetState(
        skipPartiallyExpanded = false,
        density = LocalDensity.current,
        initialValue = SheetValue.Expanded
    )
    ZaitsevNewsTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            BSProfileAvatar(sheetState = sheetState, bsProfileAvatarImp = object : BSProfileAvatarImp{
                override val pagerGallery: Flow<PagingData<Uri>>
                    get() = flow {  }
                override fun takeFromGallery(uri: Uri) = Unit
                override fun takePhoto(bitmap: Bitmap?) = Unit
                override fun removeAvatar() = Unit
                override fun visibleRemoveAvatar(): Boolean {
                   return true
                }
                override fun dismiss() = Unit

            })
        }
    }
}