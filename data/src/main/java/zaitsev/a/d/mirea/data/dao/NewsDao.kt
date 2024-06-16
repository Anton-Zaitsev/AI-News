package zaitsev.a.d.mirea.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import zaitsev.a.d.mirea.data.modelSource.local.NewsLocalEntity

@Dao
interface NewsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addLocalNews(news: NewsLocalEntity): Long

    @Query("DELETE FROM news WHERE id = :newsID")
    suspend fun deleteLocalNews(newsID: Long)

    @Query("DELETE FROM news WHERE hash_code = :hashCode AND user =:userID")
    suspend fun deleteLocalNewsByHashCode(hashCode: Int, userID: Long)

    @Query("SELECT * FROM news WHERE user =:userID ORDER BY save_date DESC")
    fun getNewsFlow(userID: Long): Flow<List<NewsLocalEntity>>

    @Query("SELECT * FROM news WHERE hash_code =:hashCode AND user =:userID LIMIT 1")
    suspend fun getNewsByHashCode(hashCode: Int, userID: Long): NewsLocalEntity?

    @Query("SELECT COUNT(*) FROM news WHERE user = :userID")
    fun getCountSavedNews(userID: Long): Flow<Int>

    @Query("DELETE FROM news WHERE user =:userID")
    suspend fun deleteAllLocalNews(userID: Long)
}