package zaitsev.a.d.mirea.data

import androidx.room.RoomDatabase
import zaitsev.a.d.mirea.data.dao.NewsDao
import zaitsev.a.d.mirea.data.dao.RSSChannelDao
import zaitsev.a.d.mirea.data.dao.UserDao
import zaitsev.a.d.mirea.data.modelSource.local.NewsLocalEntity
import zaitsev.a.d.mirea.data.modelSource.local.RssChannelEntity
import zaitsev.a.d.mirea.data.modelSource.local.UserEntity

@androidx.room.Database(
entities = [
    UserEntity::class,
    NewsLocalEntity::class,
    RssChannelEntity::class
],
version = 1,
exportSchema = false
)
abstract class NewsDatabase : RoomDatabase(){
    abstract fun userDao(): UserDao
    abstract fun newsDao(): NewsDao
    abstract fun rssChannelDao(): RSSChannelDao
    companion object {
        const val DATABASE_NAME = "news_database"
    }
}