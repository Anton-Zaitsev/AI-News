package zaitsev.a.d.mirea.diplom.constApp

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import zaitsev.a.d.mirea.diplom.R
import zaitsev.a.d.mirea.diplom.data.ChipRSSChannel
import zaitsev.a.d.mirea.diplom.data.rss.AstroBeneNews
import zaitsev.a.d.mirea.diplom.data.rss.BBCNews
import zaitsev.a.d.mirea.diplom.data.rss.BankiNews
import zaitsev.a.d.mirea.diplom.data.rss.GoogleNews
import zaitsev.a.d.mirea.diplom.data.rss.MailNews
import zaitsev.a.d.mirea.diplom.data.rss.ModelNews
import zaitsev.a.d.mirea.diplom.data.rss.NewYorkNews
import zaitsev.a.d.mirea.diplom.data.rss.TassNews
import kotlin.reflect.KClass

object Constants {

    const val ZAITSEV_NEWS_PACKAGE_NAME = "zaitsevnews"
    fun getListAllNewsRSS(): SnapshotStateList<ChipRSSChannel> {
        val map = hashMapOf<Int, ChipRSSChannel>()
        ModelNews::class.sealedSubclasses.forEach { classModel ->
            when(classModel){
                TassNews::class -> 0
                MailNews::class -> 1
                NewYorkNews::class -> 2
                BBCNews::class -> 3
                BankiNews::class -> 4
                AstroBeneNews::class -> 5
                GoogleNews::class -> 6
                else -> null
            }?.let { index ->
                map[index] = ChipRSSChannel(
                    name = classModel.getTitleByType(),
                    icon = classModel.getImageByType(),
                    type = classModel,
                    selected = classModel == TassNews::class)
            }
        }
        return map.toSortedMap().values.toMutableStateList()
    }

    fun KClass<out ModelNews>.getTitleByType(): Int{
        return when(this){
            AstroBeneNews::class -> R.string.rss_channel_astroBeneNews
            BankiNews::class -> R.string.rss_channel_bankiNews
            GoogleNews::class -> R.string.rss_channel_google
            MailNews::class -> R.string.rss_channel_mailNews
            TassNews::class -> R.string.rss_channel_tass
            BBCNews::class -> R.string.rss_channel_bbc_world
            NewYorkNews::class -> R.string.rss_channel_new_yourk_times
            else -> throw Exception("Не добавлен класс в title")
        }
    }
    fun KClass<out ModelNews>.getImageByType(): Int?{
        return when(this){
            AstroBeneNews::class -> R.drawable.astronewsrss
            BankiNews::class -> R.drawable.bankinewsrss
            GoogleNews::class -> R.drawable.googlerss
            MailNews::class -> R.drawable.mailnewsrss
            TassNews::class -> R.drawable.tassrss
            BBCNews::class -> R.drawable.bbcnewsrss
            NewYorkNews::class -> R.drawable.newyorknewsrss
            else -> null
        }
    }

}