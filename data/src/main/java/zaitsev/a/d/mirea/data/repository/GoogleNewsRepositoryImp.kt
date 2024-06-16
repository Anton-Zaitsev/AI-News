package zaitsev.a.d.mirea.data.repository

import org.jsoup.Jsoup
import zaitsev.a.d.mirea.data.toParseDataRSS
import zaitsev.a.d.mirea.domain.models.Failure
import zaitsev.a.d.mirea.domain.models.ResultCoroutines
import zaitsev.a.d.mirea.domain.models.Success
import zaitsev.a.d.mirea.domain.models.repository.rss.GoogleNewsRepository
import zaitsev.a.d.mirea.domain.models.rss.GoogleNewsDTO
import zaitsev.a.d.mirea.rss.FailureNews
import zaitsev.a.d.mirea.rss.SuccessNews
import zaitsev.a.d.mirea.rss.models.GoogleNewsRSS
import zaitsev.a.d.mirea.rss.repository.GoogleNewsParser
import java.net.URL

class GoogleNewsRepositoryImp (private val googleNewsParser: GoogleNewsParser):
    GoogleNewsRepository {

    override suspend fun getNews(): ResultCoroutines<List<GoogleNewsDTO>, String> {
        return when(val result = googleNewsParser.getNews()){
            is FailureNews -> Failure(result.reason)
            is SuccessNews -> Success(
                result.value.mapNotNull(
                    ::convertList
                ).sortedByDescending { it.date }
            )
        }
    }

    override suspend fun searchNews(query: String): ResultCoroutines<List<GoogleNewsDTO>, String> {
        return when(val result = googleNewsParser.getNewsSearch(query = query)){
            is FailureNews -> Failure(result.reason)
            is SuccessNews -> Success(
                result.value.mapNotNull(
                    ::convertList
                ).sortedByDescending { it.date }
            )
        }
    }

    private fun convertList(news: GoogleNewsRSS): GoogleNewsDTO?{
        val date = news.pubDate?.toParseDataRSS(pattern = "EEE, dd MMM yyyy HH:mm:ss z")
        val checkNullData = news.title != null && news.link != null && news.description != null && date != null && news.source != null
        return if (checkNullData) {
            GoogleNewsDTO(
                title = removeHtmlTags(checkNotNull(news.title)).replace(" - ${checkNotNull(news.source)}", "", true),
                date = checkNotNull(date),
                description = removeHtmlTags(checkNotNull(news.description)),
                linq = URL(checkNotNull(news.link)),
                source = checkNotNull(news.source),
                sourceURL = news.urlSource?.let { url -> URL(url)}
            )
        } else null
    }

    private fun removeHtmlTags(input: String): String {
        return Jsoup.parse(input).text()
    }
}