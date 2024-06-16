package zaitsev.a.d.mirea.diplom.data

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import zaitsev.a.d.mirea.diplom.data.rss.ModelNews
import kotlin.reflect.KClass

data class ChipRSSChannel(
    @StringRes val name: Int,
    @DrawableRes val icon: Int?,
    val selected: Boolean = false,
    val type: KClass<out ModelNews>
)
