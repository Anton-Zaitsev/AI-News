package zaitsev.a.d.mirea.data.repository

import org.jsoup.Jsoup
import zaitsev.a.d.mirea.data.toParseDataRSS
import zaitsev.a.d.mirea.domain.models.Failure
import zaitsev.a.d.mirea.domain.models.ResultCoroutines
import zaitsev.a.d.mirea.domain.models.Success
import zaitsev.a.d.mirea.domain.models.repository.rss.BankiNewsRepository
import zaitsev.a.d.mirea.domain.models.rss.BankiNewsDTO
import zaitsev.a.d.mirea.rss.FailureNews
import zaitsev.a.d.mirea.rss.SuccessNews
import zaitsev.a.d.mirea.rss.models.BankiNewsRSS
import zaitsev.a.d.mirea.rss.repository.BankiNewsParser
import java.net.URL

class BankiNewsRepositoryImp (private val bankiNewsParser: BankiNewsParser):
    BankiNewsRepository {

    override suspend fun getNews(): ResultCoroutines<List<BankiNewsDTO>, String> {
        return when(val result = bankiNewsParser.getNews()){
            is FailureNews -> Failure(result.reason)
            is SuccessNews -> Success(
                result.value.mapNotNull(
                    ::convertList
                ).sortedByDescending { it.date }
            )
        }
    }
    private fun convertList(news: BankiNewsRSS): BankiNewsDTO?{
        val date = news.pubDate?.toParseDataRSS()
        val checkNullData = news.title != null && news.link != null && date != null && news.description != null && news.synopsis != null
        return if (checkNullData) {
            BankiNewsDTO(
                title = checkNotNull(news.title),
                date = checkNotNull(date),
                description = removeHtmlTags(checkNotNull(news.description)),
                linq = URL(checkNotNull(news.link)),
                synopsis = checkNotNull(news.synopsis),
                bankName = news.bankName
            )
        } else null
    }

    private fun removeHtmlTags(input: String): String {
        return Jsoup.parse(input).text()
    }
}