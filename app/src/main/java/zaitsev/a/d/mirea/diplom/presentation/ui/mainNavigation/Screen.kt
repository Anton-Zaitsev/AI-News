package zaitsev.a.d.mirea.diplom.presentation.ui.mainNavigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import zaitsev.a.d.mirea.diplom.data.rss.ModelNews
import zaitsev.a.d.mirea.diplom.data.rss.getGsonScheme
import zaitsev.a.d.mirea.diplom.data.telegram.TelegramChatModelUI
import zaitsev.a.d.mirea.diplom.db.dataUI.UserUI

sealed class Screen(val route: String) {
    data class MainScreen(
        override val resourceId: Int,
        override val imageId: Int
    ) : Screen(route = "main"), ScreenResource
    data object NewsShare: Screen(
        route = "newsShare?${Routing.NEWS_ROUTE}={${Routing.NEWS_ROUTE}}" +
                "routing?{${Routing.NEWS_ROUTE_TYPE}}={${Routing.NEWS_ROUTE_TYPE}}"
    )
    data object NewsSaved: Screen(
        route = "savedNews"
    )
    data class Chat(
        override val resourceId: Int,
        override val imageId: Int
    ) : Screen(route = "chat"), ScreenResource

    data object UserChat: Screen(route = "chatUser?${Routing.CHAT_ID_USER}={${Routing.CHAT_ID_USER}}")
    data class Profile(
        override val resourceId: Int,
        override val imageId: Int
    ): Screen(route = "profile"), ScreenResource
    data object ProfileView : Screen(
       route = "profileView?${Routing.USER_ROUTE}={${Routing.USER_ROUTE}}"
    )
    data object Notification : Screen(route = "notification")
    data object ThemeApp: Screen(route = "themeApp")
    data object SettingApp: Screen(route = "settingApp")
}
interface ScreenResource {
    @get:StringRes
    val resourceId: Int
    @get:DrawableRes
    val imageId: Int
}

val UserUI.currentRoute get() = "profileView?${Routing.USER_ROUTE}=$this"
val ModelNews.currentRoute: String
    get() {
        val typeClass = this::class.simpleName ?: ""
        return "newsShare?${Routing.NEWS_ROUTE}=${this.getGsonScheme}" +
                "routing?${Routing.NEWS_ROUTE_TYPE}=$typeClass"
    }

val TelegramChatModelUI.currentRoute: String
    get() = "chatUser?${Routing.CHAT_ID_USER}=${this.chatId}"

object Routing {
    const val USER_ROUTE = "user"
    const val NEWS_ROUTE = "news"
    const val NEWS_ROUTE_TYPE = "newsType"
    const val CHAT_ID_USER = "chatID"
}