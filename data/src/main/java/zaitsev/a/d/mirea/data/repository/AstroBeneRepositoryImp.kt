package zaitsev.a.d.mirea.data.repository

import org.jsoup.Jsoup
import zaitsev.a.d.mirea.data.toParseDataRSS
import zaitsev.a.d.mirea.domain.models.Failure
import zaitsev.a.d.mirea.domain.models.ResultCoroutines
import zaitsev.a.d.mirea.domain.models.Success
import zaitsev.a.d.mirea.domain.models.repository.rss.AstroBeneRepository
import zaitsev.a.d.mirea.domain.models.rss.AstroBeneNewsDTO
import zaitsev.a.d.mirea.rss.FailureNews
import zaitsev.a.d.mirea.rss.SuccessNews
import zaitsev.a.d.mirea.rss.models.AstroBeneNewsRSS
import zaitsev.a.d.mirea.rss.repository.AstroBeneNewsParser
import java.net.URL

class AstroBeneRepositoryImp (private val astroBeneNewsParser: AstroBeneNewsParser):
    AstroBeneRepository {
    override suspend fun getNews(): ResultCoroutines<List<AstroBeneNewsDTO>, String> {
        return when(val result = astroBeneNewsParser.getNews()){
            is FailureNews -> Failure(result.reason)
            is SuccessNews -> Success(
                result.value.mapNotNull(
                    ::convertList
                ).sortedByDescending { it.date }
            )
        }
    }
    private fun convertList(news: AstroBeneNewsRSS): AstroBeneNewsDTO?{
        val date = news.pubDate?.toParseDataRSS()
        val checkNotNullData = news.title != null && news.link != null && date != null && news.description != null && !news.category.isNullOrEmpty()
        return if (checkNotNullData) {
            AstroBeneNewsDTO(
                title = checkNotNull(news.title),
                date = checkNotNull(date),
                description = removeHtmlTags(checkNotNull(news.description))
                    .replace("Читать далее →", "", ignoreCase = true),
                linq = URL(checkNotNull(news.link)),
                category = checkNotNull(news.category)
            )
        } else null
    }

    private fun removeHtmlTags(input: String): String {
        return Jsoup.parse(input).text()
    }
}