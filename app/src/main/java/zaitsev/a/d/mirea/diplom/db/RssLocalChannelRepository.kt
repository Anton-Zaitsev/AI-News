package zaitsev.a.d.mirea.diplom.db

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import zaitsev.a.d.mirea.data.dao.RSSChannelDao
import zaitsev.a.d.mirea.data.dao.UserDao
import zaitsev.a.d.mirea.diplom.data.channelRSS.RSSInfoUI
import zaitsev.a.d.mirea.diplom.data.mappers.rssChannel.toEntity
import zaitsev.a.d.mirea.diplom.data.mappers.rssChannel.toUI

class RssLocalChannelRepository(
    private val userDao: UserDao,
    private val rssChannelDao: RSSChannelDao
) {

    suspend fun saveLocal(rss: RSSInfoUI): Boolean{
        val userID = userDao.getCurrentUserID() ?: return false
        val rssLocal = rss.toEntity(user = userID)
        return rssChannelDao.addLocalRSSChannel(rssLocal) != -1L
    }

    suspend fun removeFromLocal(rss: RSSInfoUI){
        val userID = userDao.getCurrentUserID() ?: return
        val rssLocal = rss.toEntity(user = userID)
        rssChannelDao.remove(rssLocal)
    }

    suspend fun getFlowListRSS(): Flow<List<RSSInfoUI>> {
        val userID = userDao.getCurrentUserID() ?: return flow {  }
        return rssChannelDao.getFlowListRSSChannel(user = userID).map { list -> list.map { it.toUI() } }
    }
}