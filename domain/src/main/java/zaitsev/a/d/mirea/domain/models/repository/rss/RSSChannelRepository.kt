package zaitsev.a.d.mirea.domain.models.repository.rss

import zaitsev.a.d.mirea.domain.models.ResultCoroutines
import zaitsev.a.d.mirea.domain.models.rss.RRSInfoDTO

interface RSSChannelRepository {
    suspend fun getInfoRSSChannel(url: String): ResultCoroutines<RRSInfoDTO, String>
}