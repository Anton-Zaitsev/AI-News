package zaitsev.a.d.mirea.diplom.db

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import zaitsev.a.d.mirea.data.dao.NewsDao
import zaitsev.a.d.mirea.data.dao.UserDao
import zaitsev.a.d.mirea.diplom.data.mappers.mapperLocalNews.toEntity
import zaitsev.a.d.mirea.diplom.data.mappers.mapperLocalNews.toUI
import zaitsev.a.d.mirea.diplom.data.rss.AstroBeneNews
import zaitsev.a.d.mirea.diplom.data.rss.BBCNews
import zaitsev.a.d.mirea.diplom.data.rss.BankiNews
import zaitsev.a.d.mirea.diplom.data.rss.GoogleNews
import zaitsev.a.d.mirea.diplom.data.rss.MailNews
import zaitsev.a.d.mirea.diplom.data.rss.ModelNews
import zaitsev.a.d.mirea.diplom.data.rss.NewYorkNews
import zaitsev.a.d.mirea.diplom.data.rss.TassNews
import zaitsev.a.d.mirea.diplom.data.rss.classTypeToNews

class NewsLocalRepository(
    private val userDao: UserDao,
    private val newsDao: NewsDao
) {

    suspend fun saveLocal(modelNews: ModelNews): Boolean{
        val userID = userDao.getCurrentUserID() ?: return false
        val newsLocal = modelNews.toEntity(userID = userID) ?: return false
        return newsDao.addLocalNews(newsLocal) != -1L
    }

    suspend fun getLocalNewsByHashCode(hashCode: Int): ModelNews?{
        return userDao.getCurrentUserID()?.let { userID ->
            newsDao.getNewsByHashCode(hashCode = hashCode, userID = userID)?.let { news ->
                news.type.classTypeToNews(value = news.content)?.let { modelNews ->
                    when(modelNews){
                        is AstroBeneNews -> modelNews.copy(id = news.id, isLocal = true)
                        is BBCNews -> modelNews.copy(id = news.id, isLocal = true)
                        is BankiNews -> modelNews.copy(id = news.id, isLocal = true)
                        is GoogleNews -> modelNews.copy(id = news.id, isLocal = true)
                        is MailNews -> modelNews.copy(id = news.id, isLocal = true)
                        is NewYorkNews -> modelNews.copy(id = news.id, isLocal = true)
                        is TassNews -> modelNews.copy(id = news.id, isLocal = true)
                    }
                }
            }
        }
    }

    suspend fun removeLocalNewsByHashCode(hashCode: Int){
        userDao.getCurrentUserID()?.let { userID ->
            newsDao.deleteLocalNewsByHashCode(hashCode = hashCode, userID = userID)
        }
    }

    suspend fun getFlowCountSavedNews(): Flow<Int>? {
        return userDao.getCurrentUserID()?.let { userID ->
            newsDao.getCountSavedNews(userID = userID)
        }
    }

    suspend fun getFlowListLocalNews(): Flow<List<ModelNews>>? {
        return userDao.getCurrentUserID()?.let { userID ->
            newsDao.getNewsFlow(userID = userID).map { list -> list.mapNotNull { news -> news.toUI() } }
        }
    }

    suspend fun deleteAllLocalNews() {
        userDao.getCurrentUserID()?.let { userID ->
            newsDao.deleteAllLocalNews(userID = userID)
        }
    }

}