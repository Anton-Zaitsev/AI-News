package zaitsev.a.d.mirea.diplom.presentation.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import zaitsev.a.d.mirea.diplom.BuildConfig
import zaitsev.a.d.mirea.diplom.R
import zaitsev.a.d.mirea.diplom.presentation.shimmerEffect
import zaitsev.a.d.mirea.diplom.presentation.theme.ZaitsevNewsTheme
import zaitsev.a.d.mirea.diplom.presentation.ui.mainNavigation.LOGIN_SCREEN
import zaitsev.a.d.mirea.diplom.presentation.ui.mainNavigation.Screen
import zaitsev.a.d.mirea.diplom.presentation.ui.mainNavigation.currentRoute
import java.io.File
import java.util.Locale

@Composable
fun Profile(navController: NavController, viewModel: ProfileViewModel = hiltViewModel()){
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 15.dp)
        .verticalScroll(rememberScrollState())) {

        Spacer(modifier = Modifier.height(15.dp))

        Row(modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.onBackground, RoundedCornerShape(20))
            .clip(RoundedCornerShape(20))
            .clickable {
                viewModel.user?.let { userUI ->
                    navController.navigate(userUI.currentRoute)
                }
            }
            .padding(vertical = 15.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {

            Row(horizontalArrangement = spacedBy(15.dp), verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 15.dp)) {

                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(viewModel.user?.avatarURL?.let { File(it) })
                        .crossfade(true)
                        .diskCachePolicy(CachePolicy.ENABLED)
                        .build(),
                    contentDescription = stringResource(id = R.string.imageAvatar),
                    content = {
                        val state = painter.state
                        if (state is AsyncImagePainter.State.Success){
                            Image(
                                painter = painter,
                                contentDescription = "",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.size(50.dp)
                                    .clip(CircleShape)
                            )
                        }
                        else if (state is AsyncImagePainter.State.Loading) {
                            Box(modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .shimmerEffect())
                        }
                    },
                )

                Column(verticalArrangement = spacedBy(5.dp), horizontalAlignment = Alignment.Start) {
                    if (viewModel.user != null){
                        Text(
                            text = viewModel.user?.name ?: "",
                            color = MaterialTheme.colorScheme.onSecondary,
                            fontSize = MaterialTheme.typography.titleMedium.fontSize,
                        )
                        Text(
                            text = viewModel.user?.phone?.let { viewModel.formatPhone(it) } ?: "",
                            color = Color.Gray,
                            fontSize = MaterialTheme.typography.titleSmall.fontSize
                        )
                    }
                    else {
                        Box(modifier = Modifier.fillMaxWidth(0.5f).height(15.dp).clip(CircleShape).shimmerEffect())
                        Box(modifier = Modifier.fillMaxWidth(0.7f).height(15.dp).clip(CircleShape).shimmerEffect())
                    }
                }
            }

            Image(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "",
                colorFilter = ColorFilter.tint(Color.Gray),
                modifier = Modifier.padding(end = 15.dp)
            )
        }

        Text(
            text = stringResource(R.string.notification).uppercase(Locale.getDefault()),
            color = MaterialTheme.colorScheme.onSecondary,
            fontSize = MaterialTheme.typography.labelMedium.fontSize,
            modifier = Modifier.padding(top = 10.dp, start = 15.dp, bottom = 5.dp, end = 15.dp)
        )

        Column(modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.onBackground, RoundedCornerShape(15.dp)),
            horizontalAlignment = Alignment.End
        ) {
            Row(modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(15.dp))
                .clickable {
                    navController.navigate(Screen.Notification.route)
                }
                .padding(vertical = 10.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_notification),
                        contentDescription = stringResource(R.string.notification),
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSecondary),
                        modifier = Modifier.padding(horizontal = 15.dp)
                    )

                    Text(
                        text = stringResource(R.string.notification),
                        color = MaterialTheme.colorScheme.onSecondary,
                        fontSize = MaterialTheme.typography.titleMedium.fontSize,
                    )
                }

                Image(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = stringResource(R.string.notification),
                    colorFilter = ColorFilter.tint(Color.Gray),
                    modifier = Modifier.padding(end = 15.dp)
                )
            }
            Box(modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(0.5.dp)
                .padding(end = 5.dp)
                .background(Color.Gray))

            Row(modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(15.dp))
                .clickable {

                }
                .padding(vertical = 10.dp), horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.CenterVertically) {

                Image(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_notification_off),
                    contentDescription = stringResource(R.string.notDisturbProfile),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSecondary),
                    modifier = Modifier.padding(horizontal = 15.dp)
                )

                Text(
                    text = stringResource(R.string.notDisturbProfile),
                    color = MaterialTheme.colorScheme.onSecondary,
                    fontSize = MaterialTheme.typography.titleMedium.fontSize,
                )
            }
        }

        Text(
            text = stringResource(R.string.settings).uppercase(Locale.getDefault()),
            color = MaterialTheme.colorScheme.onSecondary,
            fontSize = MaterialTheme.typography.labelMedium.fontSize,
            modifier = Modifier.padding(top = 10.dp, start = 15.dp, bottom = 5.dp, end = 15.dp)
        )

        Column(modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.onBackground, RoundedCornerShape(15.dp)),
            horizontalAlignment = Alignment.End
        ) {
            Row(modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(15.dp))
                .clickable {
                    navController.navigate(Screen.ThemeApp.route)
                }
                .padding(vertical = 10.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_palette),
                        contentDescription = stringResource(R.string.appThemeProfile),
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSecondary),
                        modifier = Modifier.padding(horizontal = 15.dp)
                    )

                    Text(
                        text = stringResource(R.string.appThemeProfile),
                        color = MaterialTheme.colorScheme.onSecondary,
                        fontSize = MaterialTheme.typography.titleMedium.fontSize,
                    )
                }

                Image(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = stringResource(R.string.appThemeProfile),
                    colorFilter = ColorFilter.tint(Color.Gray),
                    modifier = Modifier.padding(end = 15.dp)
                )
            }
            Box(modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(0.5.dp)
                .padding(end = 5.dp)
                .background(Color.Gray))

            Row(modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(15.dp))
                .clickable {
                    navController.navigate(Screen.SettingApp.route)
                }
                .padding(vertical = 10.dp), horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.CenterVertically) {

                Image(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = stringResource(R.string.appNameProfile),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSecondary),
                    modifier = Modifier.padding(horizontal = 15.dp)
                )

                Text(
                    text = stringResource(R.string.appNameProfile),
                    color = MaterialTheme.colorScheme.onSecondary,
                    fontSize = MaterialTheme.typography.titleMedium.fontSize,
                )
            }
        }

        Text(
            text = stringResource(R.string.versionAppName),
            color = MaterialTheme.colorScheme.onSecondary,
            fontSize = MaterialTheme.typography.labelMedium.fontSize,
            modifier = Modifier.padding(top = 10.dp, start = 15.dp, bottom = 5.dp, end = 15.dp)
        )


        Row(modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.onBackground, RoundedCornerShape(13.dp))
            .padding(vertical = 10.dp, horizontal = 15.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {

            Text(
                text = stringResource(id = R.string.app_name),
                color = MaterialTheme.colorScheme.onSecondary,
                fontSize = MaterialTheme.typography.titleMedium.fontSize,
            )

            Text(
                text = BuildConfig.VERSION_NAME,
                color = Color.Gray,
                fontSize = MaterialTheme.typography.titleMedium.fontSize
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.onBackground, RoundedCornerShape(13.dp))
            .clip(RoundedCornerShape(13.dp))
            .clickable {
                viewModel.exit {
                    navController.navigate(LOGIN_SCREEN)
                }
            }
            .padding(vertical = 10.dp, horizontal = 15.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {

            Text(
                text = stringResource(R.string.exit),
                color = Color.Red,
                fontSize = MaterialTheme.typography.titleMedium.fontSize
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun ProfilePreview() {
    ZaitsevNewsTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Profile(rememberNavController())
        }
    }
}