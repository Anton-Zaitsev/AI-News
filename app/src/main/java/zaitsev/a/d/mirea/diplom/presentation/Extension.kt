package zaitsev.a.d.mirea.diplom.presentation


import android.util.Patterns
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntSize
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import zaitsev.a.d.mirea.diplom.DateLongFormatTypeAdapter
import zaitsev.a.d.mirea.diplom.NewsApp
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Calendar
import java.util.Date
import java.util.Locale

val String.color
    get() = Color(android.graphics.Color.parseColor(this))

val Boolean.toggle
    get() = this.not()

fun <T: Any> List<T>.indexOfOrNull(value: T): Int?{
    val index = this.indexOf(value)
    return if (index != -1) index else null
}

inline fun <reified T: Any> SnapshotStateList<T>.updateNewValue(index: Int, newValue: T){
    removeAt(index = index)
    add(index = index, newValue)
}

inline fun <reified T: Any> SnapshotStateList<T>.updateNewValue(valueOld: T, valueNew: T){
    indexOfOrNull(valueOld)?.let { index ->
        updateNewValue(index, valueNew)
    }
}

val <T: Any?> T?.isNotNull: Boolean
    get() = this != null

val <T: Any?> T?.isNull: Boolean
    get() = this == null

fun Modifier.shimmerEffect(): Modifier = composed {
    var size by remember {
        mutableStateOf(IntSize.Zero)
    }
    val transition = rememberInfiniteTransition(label = "Загрузка")
    val startOffsetX by transition.animateFloat(
        initialValue = -2 * size.width.toFloat(),
        targetValue = 2 * size.width.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(1000)
        ), label = "Загрузка контента"
    )

    background(
        brush = Brush.linearGradient(
            colors = listOf(
                Color(0xFFB8B5B5),
                Color(0xFF8F8B8B),
                Color(0xFFB8B5B5),
            ),
            start = Offset(startOffsetX, 0f),
            end = Offset(startOffsetX + size.width.toFloat(), size.height.toFloat())
        )
    )
    .onGloballyPositioned {
        size = it.size
    }
}


@Composable
@ReadOnlyComposable
fun isDarkThemeCurrent(): Boolean {
    return (LocalContext.current.applicationContext as NewsApp).isTheme ?: isSystemInDarkTheme()
}

fun getGsonDateLong(): Gson {
   return GsonBuilder()
        .registerTypeAdapter(Date::class.java, DateLongFormatTypeAdapter())
        .create()
}

fun Date.convertToStringDateNews(locale: Locale = Locale.forLanguageTag("ru")): String {
    val pattern = SimpleDateFormat("EEEE, d MMMM", locale)
    return pattern.format(this)
}

fun Date.toTimeChat(): String {
    return SimpleDateFormat("HH:mm", Locale.getDefault()).format(this)
}

fun getDateTimeNow(): Date {
    return Date(
        LocalDateTime.now().toMillis() ?: Calendar.getInstance().timeInMillis
    )
}

private fun LocalDateTime.toMillis(zone: ZoneId = ZoneId.systemDefault()) =
    atZone(zone)?.toInstant()?.toEpochMilli()

fun String.isValidUrl(): Boolean = Patterns.WEB_URL.matcher(this).matches()