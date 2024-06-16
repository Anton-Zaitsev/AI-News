package zaitsev.a.d.mirea.diplom.presentation.ui.mainNavigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.neverEqualPolicy
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import zaitsev.a.d.mirea.diplom.R
import zaitsev.a.d.mirea.diplom.presentation.isDarkThemeCurrent
import zaitsev.a.d.mirea.diplom.presentation.theme.ZaitsevNewsTheme
import zaitsev.a.d.mirea.diplom.presentation.ui.chatList.ChatList
import zaitsev.a.d.mirea.diplom.presentation.ui.chatUser.ChatUser
import zaitsev.a.d.mirea.diplom.presentation.ui.chatUser.ChatUserViewModel
import zaitsev.a.d.mirea.diplom.presentation.ui.loginScreen.LoginScreen
import zaitsev.a.d.mirea.diplom.presentation.ui.loginScreen.LoginViewModel
import zaitsev.a.d.mirea.diplom.presentation.ui.main.newsSaved.SavedNews
import zaitsev.a.d.mirea.diplom.presentation.ui.main.newsShare.NewsShare
import zaitsev.a.d.mirea.diplom.presentation.ui.main.newsShare.NewsShareViewModel
import zaitsev.a.d.mirea.diplom.presentation.ui.main.newsToday.NewsToday
import zaitsev.a.d.mirea.diplom.presentation.ui.profile.Profile
import zaitsev.a.d.mirea.diplom.presentation.ui.profile.profileView.ProfileView
import zaitsev.a.d.mirea.diplom.presentation.ui.profile.profileView.ProfileViewViewModel
import zaitsev.a.d.mirea.diplom.presentation.ui.profile.settingApp.SettingApp
import zaitsev.a.d.mirea.diplom.presentation.ui.profile.settingNotification.SettingNotification
import zaitsev.a.d.mirea.diplom.presentation.ui.profile.settingTheme.SettingTheme
import zaitsev.a.d.mirea.diplom.presentation.ui.profile.settingTheme.SettingThemeViewModel
import zaitsev.a.d.mirea.diplom.presentation.ui.splashScreen.SplashScreen


private val navigationColorButton @Composable get() = NavigationBarItemDefaults.colors(
    selectedIconColor = MaterialTheme.colorScheme.primary,
    selectedTextColor = MaterialTheme.colorScheme.onSecondary,
    indicatorColor = MaterialTheme.colorScheme.onBackground,
    unselectedIconColor = MaterialTheme.colorScheme.primary,
    unselectedTextColor = MaterialTheme.colorScheme.onSecondary,
)
private const val MAIN_ROUTE_NAV = "main_nav"
private const val CHAT_ROUTE_NAV = "chat_nav"
private const val PROFILE_ROUTE_NAV = "profile_nav"
private const val SPLASH_SCREEN = "splash_screen"
const val LOGIN_SCREEN = "login_screen"

@Composable
fun NavigationGraph(snackbarHostState: SnackbarHostState) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val arrayNav by  remember {
        mutableStateOf(
            value = listOf(
                Screen.MainScreen(R.string.main_nav, R.drawable.ic_newspaper),
                Screen.Chat(R.string.chat_nav, R.drawable.ic_chat_round),
                Screen.Profile(R.string.profile_nav, R.drawable.ic_profile)
            ),
            policy = neverEqualPolicy()
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Scaffold(
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
            bottomBar = {
                val visible = currentDestination?.route != SPLASH_SCREEN &&
                              currentDestination?.route != LOGIN_SCREEN &&
                              currentDestination?.route != Screen.UserChat.route

                AnimatedContent(
                    targetState = visible,
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
                    }, label = "AnimatedNavigationBar"
                ) { targetVisibility ->
                    if (targetVisibility){
                        NavigationBar(containerColor = MaterialTheme.colorScheme.background) {
                            arrayNav.forEach { screenRoute ->
                                NavigationBarItem(
                                    colors = navigationColorButton,
                                    selected = currentDestination?.route == screenRoute.route,
                                    onClick = { navController.navigate(screenRoute.route) },
                                    icon = {
                                        Icon(imageVector = ImageVector.vectorResource(id = screenRoute.imageId), contentDescription = screenRoute.route)
                                    },
                                    label = {
                                        Text(
                                            text = stringResource(id = screenRoute.resourceId),
                                            fontSize = MaterialTheme.typography.labelSmall.fontSize
                                        )
                                    },
                                    alwaysShowLabel = true
                                )
                            }
                        }
                    }
                }
            }
        ) { innerPadding ->

            val routeMain = arrayNav[0].route
            val routeChat = arrayNav[1].route
            val routeProfile = arrayNav[2].route

            val isSystemTheme = isDarkThemeCurrent()


            NavHost(navController, startDestination = SPLASH_SCREEN, Modifier.padding(innerPadding)) {

                composable(route = SPLASH_SCREEN){
                    SplashScreen(
                        navController = navController,
                        mainScreen = MAIN_ROUTE_NAV,
                        splashScreen = SPLASH_SCREEN,
                        loginScreen = LOGIN_SCREEN
                    )
                }

                composable(route = LOGIN_SCREEN) {
                    val viewModel = hiltViewModel<LoginViewModel>()

                    val authState by viewModel.authStateTelegram.collectAsStateWithLifecycle()

                    LoginScreen(
                        navController = navController,
                        name = viewModel.loginData.name,
                        lastName = viewModel.loginData.lastName,
                        isCheckedName = viewModel.checkCurrentPersonalData(viewModel.loginData.name),
                        isCheckedLastName = viewModel.checkCurrentPersonalData(viewModel.loginData.lastName),
                        onNameChanged = viewModel::onNameChanged,
                        onLastNameChanged = viewModel::onLastNameChanged,

                        onPhoneChanged = viewModel::onPhoneChanged,
                        onCodeChanged = viewModel::onCodeChanged,
                        onPasswordChanged = viewModel::onPasswordChanged,
                        isPhoneValid = viewModel::isPhoneValid,
                        maskPhone = viewModel.phoneChecked.maskPhone,
                        signBtnTelegram = viewModel::signBtnTelegram,
                        authState = authState,
                        authTelegram = viewModel.authTelegram,

                        currentScreen = viewModel.loginData.currentScreen,
                        isEnabledNext = viewModel.loginData.isEnabledNext,
                        navigateNext = viewModel::navigateNext,
                        navigateDown = viewModel::navigateDown,

                        avatarUrl = viewModel.loginData.avatar,
                        visibleBSAvatar = viewModel.visibleBSAvatar,
                        interfaceAvatar = viewModel.interfaceBSAvatar,
                        setVisibleAvatarBS = viewModel::setVisibleAvatarBS,
                        openBSAvatar = viewModel::openBSAvatar,
                        sign = viewModel::saveUser,

                        loginScreen = LOGIN_SCREEN,
                        mainScreen = MAIN_ROUTE_NAV,

                        flowUser = viewModel.flowUserList,
                        onSignExistUser = viewModel::onSignWithExistUser,

                        isLoading = viewModel.isLoading
                    )
                }


                navigation(
                    startDestination = routeMain,
                    route = MAIN_ROUTE_NAV
                ){
                    composable(route = routeMain) {
                        NewsToday(navController = navController)
                    }
                    composable(route = Screen.NewsShare.route){ navBackStackEntry ->
                        val viewModel = hiltViewModel<NewsShareViewModel>(navBackStackEntry)
                        NewsShare(navController = navController, viewModel = viewModel)
                    }
                    composable(route = Screen.NewsSaved.route){
                        SavedNews(navHostController = navController)
                    }
                }

                navigation(
                    startDestination = routeChat,
                    route = CHAT_ROUTE_NAV
                ) {
                    composable(route = routeChat){
                        ChatList(navHostController = navController)
                    }
                    composable(route = Screen.UserChat.route) { navBackStackEntry ->
                        val viewModel = hiltViewModel<ChatUserViewModel>(navBackStackEntry)
                        ChatUser(navHostController = navController, viewModel = viewModel)
                    }
                }

                navigation(
                    startDestination = routeProfile,
                    route = PROFILE_ROUTE_NAV
                ) {
                    composable(route = routeProfile) {
                        Profile(navController = navController)
                    }
                    composable(route = Screen.ProfileView.route) { navBackStackEntry ->
                        val viewModel = hiltViewModel<ProfileViewViewModel>(navBackStackEntry)
                        ProfileView(navHostController = navController, isDarkTheme = isSystemTheme, viewModel = viewModel)
                    }
                    composable(route = Screen.Notification.route){
                        SettingNotification(navHostController = navController, isDarkTheme = isSystemTheme)
                    }
                    composable(route = Screen.ThemeApp.route){
                        val viewModel = hiltViewModel<SettingThemeViewModel>()
                        SettingTheme(
                            navHostController = navController,
                            useSystemTheme = viewModel.useSystemTheme,
                            isSystemDarkTheme = isSystemTheme,
                            isDarkTheme = viewModel.isDarkTheme,
                            setSystemTheme = viewModel::setSystemTheme,
                            setTheme = viewModel::setTheme
                        )
                    }
                    composable(route = Screen.SettingApp.route){
                        SettingApp(navHostController = navController)
                    }
                }
            }
        }
    }
}


@PreviewLightDark
@Composable
private fun NavigationGraphPreview() {
    ZaitsevNewsTheme {
        NavigationGraph(SnackbarHostState())
    }
}