package zaitsev.a.d.mirea.data.repository

import zaitsev.a.d.mirea.data.toParseDataRSS
import zaitsev.a.d.mirea.domain.models.Failure
import zaitsev.a.d.mirea.domain.models.ResultCoroutines
import zaitsev.a.d.mirea.domain.models.Success
import zaitsev.a.d.mirea.domain.models.repository.rss.TassRepository
import zaitsev.a.d.mirea.domain.models.rss.TassDTO
import zaitsev.a.d.mirea.rss.FailureNews
import zaitsev.a.d.mirea.rss.SuccessNews
import zaitsev.a.d.mirea.rss.models.TassRSS
import zaitsev.a.d.mirea.rss.repository.TassParser
import java.net.URL

class TassRepositoryImp (private val tassParser: TassParser): TassRepository {

    override suspend fun getNews(): ResultCoroutines<List<TassDTO>, String> {
        return when(val result = tassParser.getNews()){
            is FailureNews -> Failure(result.reason)
            is SuccessNews -> Success(
                result.value.mapNotNull(
                    ::convertList
                ).sortedByDescending { it.date }
            )
        }
    }

    private fun convertList(news: TassRSS): TassDTO?{
        val date = news.pubDate?.toParseDataRSS()
        val checkNullData = news.title != null && news.link != null && date != null && news.description != null && !news.category.isNullOrEmpty()
        return if (checkNullData) {
            TassDTO(
                title = checkNotNull(news.title),
                date = checkNotNull(date),
                description = checkNotNull(news.description),
                linq = URL(checkNotNull(news.link)),
                imageURL = news.imageURL,
                category = checkNotNull(news.category)
            )
        } else null
    }
}