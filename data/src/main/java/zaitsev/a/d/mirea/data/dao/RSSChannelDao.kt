package zaitsev.a.d.mirea.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import zaitsev.a.d.mirea.data.modelSource.local.RssChannelEntity

@Dao
interface RSSChannelDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addLocalRSSChannel(rssLocal: RssChannelEntity): Long
    @Delete
    suspend fun remove(rssLocal: RssChannelEntity)

    @Query("SELECT * FROM rss_channel WHERE user =:user")
    fun getFlowListRSSChannel(user: Long): Flow<List<RssChannelEntity>>
}