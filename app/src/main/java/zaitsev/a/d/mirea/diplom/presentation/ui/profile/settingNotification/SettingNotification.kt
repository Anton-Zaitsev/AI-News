package zaitsev.a.d.mirea.diplom.presentation.ui.profile.settingNotification

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import zaitsev.a.d.mirea.diplom.R
import zaitsev.a.d.mirea.diplom.presentation.theme.ZaitsevNewsTheme
import zaitsev.a.d.mirea.diplom.presentation.ui.components.NavigationCenterBarDefault
import zaitsev.a.d.mirea.diplom.presentation.ui.components.SwitchDefault

@Composable
fun SettingNotification(
    navHostController: NavHostController,
    isDarkTheme: Boolean,
    viewModel: SettingNotificationViewModel = hiltViewModel<SettingNotificationViewModel>()
){
    NavigationCenterBarDefault(navHostController = navHostController, title = stringResource(id = R.string.notification)) { paddingValues ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 15.dp)) {

            val activity = LocalContext.current as Activity
            val permissionLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) { permissionGranted ->
                if (permissionGranted){
                    viewModel.notificationCheck(viewModel.isEnableNotification)
                }
                else {
                    viewModel.setError{
                        val intent = Intent().apply {
                            action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                            putExtra(Settings.EXTRA_APP_PACKAGE, activity.packageName)
                        }
                        activity.startActivity(intent)
                    }
                }
            }

            Text(
                text = stringResource(R.string.notificationApp),
                color = MaterialTheme.colorScheme.onSecondary,
                fontSize = MaterialTheme.typography.labelMedium.fontSize,
                modifier = Modifier.padding(top = 10.dp, start = 15.dp, bottom = 5.dp, end = 15.dp)
            )


            Column(modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.onBackground, RoundedCornerShape(13.dp))
                .padding(horizontal = 15.dp)) {

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {

                    Text(
                        text = stringResource(id = if (!viewModel.isEnableNotification) R.string.disableNotification else R.string.enableNotification),
                        color = MaterialTheme.colorScheme.onSecondary,
                        fontSize = MaterialTheme.typography.titleMedium.fontSize,
                    )

                    SwitchDefault(isDarkTheme = isDarkTheme, checked = viewModel.isEnableNotification){ value ->
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            viewModel.isEnableNotification = value
                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                        else {
                            viewModel.notificationCheck(value)
                        }
                    }
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun SettingNotificationPreview() {
    val navController = rememberNavController()
    ZaitsevNewsTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            SettingNotification(navController, isDarkTheme = true)
        }
    }
}