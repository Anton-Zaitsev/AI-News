package zaitsev.a.d.mirea.data.repository

import zaitsev.a.d.mirea.data.toParseDataRSS
import zaitsev.a.d.mirea.domain.models.Failure
import zaitsev.a.d.mirea.domain.models.ResultCoroutines
import zaitsev.a.d.mirea.domain.models.Success
import zaitsev.a.d.mirea.domain.models.repository.rss.MailNewsRepository
import zaitsev.a.d.mirea.domain.models.rss.MailNewsDTO
import zaitsev.a.d.mirea.rss.FailureNews
import zaitsev.a.d.mirea.rss.SuccessNews
import zaitsev.a.d.mirea.rss.models.MailNewsRSS
import zaitsev.a.d.mirea.rss.repository.MailNewsParser
import java.net.URL

class MailNewsRepositoryImp (private val mailNewsParser: MailNewsParser): MailNewsRepository {

    override suspend fun getNews(): ResultCoroutines<List<MailNewsDTO>, String> {
        return when(val result = mailNewsParser.getNews()){
            is FailureNews -> Failure(result.reason)
            is SuccessNews -> Success(
                result.value.mapNotNull(
                    ::convertList
                ).sortedByDescending { it.date }
            )
        }
    }

    private fun convertList(news: MailNewsRSS): MailNewsDTO?{
        val date = news.pubDate?.toParseDataRSS()
        val checkNullData = news.title != null && news.link != null && date != null && news.category != null && news.description != null
        return if (checkNullData) {
            MailNewsDTO(
                title = checkNotNull(news.title),
                date = checkNotNull(date),
                description = checkNotNull(news.description),
                linq = URL(checkNotNull(news.link)),
                category = checkNotNull(news.category)
            )
        } else null
    }

}