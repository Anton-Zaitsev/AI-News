package zaitsev.a.d.mirea.diplom.presentation.ui.profile.profileView

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import zaitsev.a.d.mirea.diplom.R
import zaitsev.a.d.mirea.diplom.presentation.theme.ZaitsevNewsTheme
import zaitsev.a.d.mirea.diplom.presentation.ui.components.MaskVisualTransformation
import zaitsev.a.d.mirea.diplom.presentation.ui.profile.profileView.bottomSheet.BSProfileAvatar
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileView(navHostController: NavHostController, isDarkTheme: Boolean = isSystemInDarkTheme(), viewModel: ProfileViewViewModel){
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.profileData),
                        color = MaterialTheme.colorScheme.onSecondary,
                        fontSize = MaterialTheme.typography.titleMedium.fontSize,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    TextButton(onClick = {navHostController.popBackStack()}) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                contentDescription = stringResource(R.string.back),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = stringResource(R.string.back),
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                                fontWeight = FontWeight.Normal
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.background),
                actions = {
                    TextButton(
                        onClick = {
                             viewModel.safeUser{
                                 navHostController.popBackStack()
                             }
                        },
                        enabled = viewModel.mutableUser != null && !viewModel.loadedPhoto
                    ) {
                        Text(
                            text = stringResource(R.string.ready),
                            color = Color.Gray,
                            fontSize = MaterialTheme.typography.titleMedium.fontSize,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            )
        }, content = { paddingValues ->
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)
                .padding(horizontal = 15.dp)
                .verticalScroll(rememberScrollState())) {

                AvatarProfile(
                    isDarkTheme = isDarkTheme,
                    enabledButton = viewModel.mutableUser != null && !viewModel.loadedPhoto,
                    avatarURL = viewModel.mutableUser?.avatarURL?.let { File(it) },
                    setVisibleAvatarBS = viewModel::setVisibleAvatarBS,
                    openBSAvatar =  viewModel::openBSAvatar
                )


                Spacer(modifier = Modifier.height(15.dp))

                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 5.dp)
                    .background(MaterialTheme.colorScheme.onBackground, RoundedCornerShape(15.dp)),
                    horizontalAlignment = Alignment.End
                ) {
                    TextField(
                        enabled = viewModel.mutableUser != null && !viewModel.loadedPhoto,
                        value = viewModel.mutableUser?.name ?: "",
                        onValueChange = viewModel::setName,
                        placeholder = {
                            Text(text = stringResource(R.string.name))
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Gray,
                            focusedIndicatorColor = Color.Gray
                        ),
                        shape = RoundedCornerShape(15.dp)
                    )

                    TextField(
                        enabled =viewModel.mutableUser != null && !viewModel.loadedPhoto,
                        value = viewModel.mutableUser?.lastName ?: "",
                        onValueChange = viewModel::setFamily,
                        placeholder = {
                            Text(text = stringResource(R.string.family))
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Gray,
                            focusedIndicatorColor = Color.Gray
                        )
                    )

                    TextField(
                        enabled = viewModel.mutableUser != null && !viewModel.loadedPhoto,
                        value = viewModel.mutableUser?.phone ?: "",
                        onValueChange = viewModel::setPhone,
                        placeholder = {
                            Text(text = stringResource(R.string.numberPhone))
                        },
                        isError = !viewModel.isPhoneValid() && viewModel.mutableUser?.phone.isNullOrEmpty(),
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Gray,
                            focusedIndicatorColor = Color.Gray
                        ),
                        readOnly = true,
                        singleLine = true,
                        visualTransformation = MaskVisualTransformation(viewModel.maskPhone),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Phone,
                            autoCorrect = false
                        )
                    )

                    Text(
                        text = stringResource(R.string.placeholderProfileView),
                        color = Color.Gray,
                        fontSize = MaterialTheme.typography.labelMedium.fontSize,
                        lineHeight = TextUnit(18f, TextUnitType.Sp),
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }

            val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            if (viewModel.visibleBSAvatar){
                BSProfileAvatar(
                    sheetState = sheetState,
                    bsProfileAvatarImp = viewModel.interfaceBSAvatar
                )
            }
        }
    )
}

@PreviewLightDark
@Composable
private fun ProfileViewPreview() {
    ZaitsevNewsTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val viewModel = hiltViewModel<ProfileViewViewModel>()
            ProfileView(rememberNavController(), viewModel = viewModel)
        }
    }
}