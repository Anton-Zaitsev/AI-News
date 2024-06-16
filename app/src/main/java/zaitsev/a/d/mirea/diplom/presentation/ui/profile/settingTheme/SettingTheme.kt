package zaitsev.a.d.mirea.diplom.presentation.ui.profile.settingTheme

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import zaitsev.a.d.mirea.diplom.R
import zaitsev.a.d.mirea.diplom.presentation.theme.ColorBlueLight
import zaitsev.a.d.mirea.diplom.presentation.theme.ZaitsevNewsTheme
import zaitsev.a.d.mirea.diplom.presentation.ui.components.NavigationCenterBarDefault
import zaitsev.a.d.mirea.diplom.presentation.ui.components.SwitchDefault
import java.util.Locale


@Composable
fun SettingTheme(
    navHostController: NavHostController,
    useSystemTheme: Boolean,
    isDarkTheme: Boolean,
    isSystemDarkTheme: Boolean,
    setSystemTheme: (isSystem: Boolean) -> Unit,
    setTheme: (isDarkTheme: Boolean) -> Unit
){
    NavigationCenterBarDefault(navHostController = navHostController, title = stringResource(id = R.string.appThemeProfile)) { paddingValues ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 15.dp)) {

            Text(
                text = stringResource(R.string.appNameProfile).uppercase(Locale.getDefault()),
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
                        text = stringResource(id = R.string.useSystemTheme),
                        color = MaterialTheme.colorScheme.onSecondary,
                        fontSize = MaterialTheme.typography.titleMedium.fontSize,
                    )

                    SwitchDefault(isDarkTheme = isSystemDarkTheme, checked = useSystemTheme, setSystemTheme)
                }

                Text(
                    text = stringResource(R.string.textAutoSystemTheme),
                    color = Color.Gray,
                    fontSize = MaterialTheme.typography.labelMedium.fontSize,
                    lineHeight = TextUnit(18f, TextUnitType.Sp),
                    modifier = Modifier.padding(bottom = 10.dp, top = 3.dp)
                )
            }

            Spacer(modifier = Modifier.height(15.dp))

            AnimatedVisibility(
                visible = !useSystemTheme,
                enter = slideInHorizontally(animationSpec = tween(durationMillis = 200)) { fullWidth ->
                    -fullWidth / 3
                } + fadeIn(
                    animationSpec = tween(durationMillis = 200)
                ),
                exit = slideOutHorizontally(animationSpec = spring(stiffness = Spring.StiffnessHigh)) {
                    200
                } + fadeOut()
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {

                    Text(
                        text = stringResource(R.string.theme).uppercase(Locale.getDefault()),
                        color = MaterialTheme.colorScheme.onSecondary,
                        fontSize = MaterialTheme.typography.labelMedium.fontSize,
                        modifier = Modifier.padding(start = 15.dp, bottom = 5.dp, end = 15.dp)
                    )

                    Column(modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.onBackground,
                            RoundedCornerShape(15.dp)
                        ),
                        horizontalAlignment = Alignment.End
                    ) {
                        Row(modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(15.dp))
                            .clickable {
                                setTheme(false)
                            }
                            .padding(vertical = 10.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {

                            Text(
                                text = stringResource(R.string.lightTheme),
                                color = MaterialTheme.colorScheme.onSecondary,
                                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                                modifier = Modifier.padding(start = 15.dp, end = 10.dp)
                            )
                            if (!isDarkTheme) {
                                Image(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = stringResource(R.string.lightTheme),
                                    colorFilter = ColorFilter.tint(ColorBlueLight),
                                    modifier = Modifier.padding(end = 15.dp)
                                )
                            }
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
                                setTheme(true)
                            }
                            .padding(vertical = 10.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {

                            Text(
                                text = stringResource(R.string.darkTheme),
                                color = MaterialTheme.colorScheme.onSecondary,
                                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                                modifier = Modifier.padding(start = 15.dp, end = 10.dp)
                            )

                            if (isDarkTheme) {
                                Image(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = stringResource(R.string.darkTheme),
                                    colorFilter = ColorFilter.tint(ColorBlueLight),
                                    modifier = Modifier.padding(end = 15.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun SettingThemePreview() {
    ZaitsevNewsTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            SettingTheme(
                rememberNavController(),
                useSystemTheme = true,
                isDarkTheme = false,
                isSystemDarkTheme = isSystemInDarkTheme(),
                setSystemTheme = {},
                setTheme = {}
            )
        }
    }
}