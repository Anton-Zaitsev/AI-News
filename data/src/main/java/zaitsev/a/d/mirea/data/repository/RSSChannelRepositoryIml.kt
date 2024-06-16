package zaitsev.a.d.mirea.data.repository

import zaitsev.a.d.mirea.domain.models.Failure
import zaitsev.a.d.mirea.domain.models.ResultCoroutines
import zaitsev.a.d.mirea.domain.models.Success
import zaitsev.a.d.mirea.domain.models.repository.rss.RSSChannelRepository
import zaitsev.a.d.mirea.domain.models.rss.RRSInfoDTO
import zaitsev.a.d.mirea.rss.FailureNews
import zaitsev.a.d.mirea.rss.SuccessNews
import zaitsev.a.d.mirea.rss.repository.ParserRSSChannel

class RSSChannelRepositoryIml(private val parserRSSChannel: ParserRSSChannel): RSSChannelRepository {
    override suspend fun getInfoRSSChannel(url: String): ResultCoroutines<RRSInfoDTO, String> {
       return when(val result = parserRSSChannel.getRSSInfo(url = url)){
           is FailureNews -> Failure(result.reason)
           is SuccessNews -> {
               val rssInfo = result.value
               if (rssInfo.title == null || rssInfo.description == null || rssInfo.ling == null)
                   Failure("Некоторые данные в RSS канале отсутствуют")
               else Success(RRSInfoDTO(
                   title = requireNotNull(rssInfo.title),
                   description = requireNotNull(rssInfo.description),
                   ling = requireNotNull(rssInfo.ling),
                   image = rssInfo.image
               ))
           }
       }
    }
}