package zaitsev.a.d.mirea.diplom.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import zaitsev.a.d.mirea.data.NewsDatabase
import zaitsev.a.d.mirea.data.dao.NewsDao
import zaitsev.a.d.mirea.data.dao.RSSChannelDao
import zaitsev.a.d.mirea.data.dao.UserDao
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext appContext: Context): NewsDatabase = Room.databaseBuilder(
        appContext,
        NewsDatabase::class.java,
        NewsDatabase.DATABASE_NAME
    ).fallbackToDestructiveMigrationFrom(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23).build()

    @Provides
    fun provideUserDao(database: NewsDatabase): UserDao = database.userDao()
    @Provides
    fun provideNewsDao(database: NewsDatabase): NewsDao = database.newsDao()
    @Provides
    fun provideRSSChannelDao(database: NewsDatabase): RSSChannelDao = database.rssChannelDao()
}