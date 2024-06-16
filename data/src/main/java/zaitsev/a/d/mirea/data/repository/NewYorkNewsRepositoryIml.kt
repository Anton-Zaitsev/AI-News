package zaitsev.a.d.mirea.data.repository

import zaitsev.a.d.mirea.data.toParseDataRSS
import zaitsev.a.d.mirea.domain.models.Failure
import zaitsev.a.d.mirea.domain.models.ResultCoroutines
import zaitsev.a.d.mirea.domain.models.Success
import zaitsev.a.d.mirea.domain.models.newsTypes.NewYorkType
import zaitsev.a.d.mirea.domain.models.repository.rss.NewYorkNewsRepository
import zaitsev.a.d.mirea.domain.models.rss.NewYourTimeDTO
import zaitsev.a.d.mirea.rss.FailureNews
import zaitsev.a.d.mirea.rss.SuccessNews
import zaitsev.a.d.mirea.rss.models.NewYourTimeRSS
import zaitsev.a.d.mirea.rss.repository.NewYorkTimeNewsParser
import java.net.URL

class NewYorkNewsRepositoryIml(private val newYorkTimeNewsParser: NewYorkTimeNewsParser): NewYorkNewsRepository {

    private val patternDate = "EEE, dd MMM yyyy HH:mm:ss Z"
    private fun convertList(news: NewYourTimeRSS): NewYourTimeDTO?{
        val date = news.pubDate?.toParseDataRSS(pattern = patternDate)
        val checkNullData = news.title != null && news.link != null && date != null && news.description != null
        return if (checkNullData) {
            NewYourTimeDTO(
                title = checkNotNull(news.title),
                date = checkNotNull(date),
                description = checkNotNull(news.description),
                linq = URL(checkNotNull(news.link)),
                imageURL = news.imageURL,
                category = news.category
            )
        } else null
    }
    override suspend fun getNews(newYorkType: NewYorkType): ResultCoroutines<List<NewYourTimeDTO>, String> {
        return when(val result = when(newYorkType){
            NewYorkType.WORLD -> newYorkTimeNewsParser.getWorldNews()
            NewYorkType.BUSINESS -> newYorkTimeNewsParser.getBusinessNews()
            NewYorkType.TECHNOLOGY -> newYorkTimeNewsParser.getTechnologyNews()
            NewYorkType.SPORT -> newYorkTimeNewsParser.getSportsNews()
        }){
            is FailureNews -> Failure(result.reason)
            is SuccessNews -> Success(
                result.value.mapNotNull(
                    ::convertList
                ).sortedByDescending { it.date }
            )
        }
    }

}