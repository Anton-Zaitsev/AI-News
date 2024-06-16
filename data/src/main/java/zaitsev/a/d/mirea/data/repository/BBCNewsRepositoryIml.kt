package zaitsev.a.d.mirea.data.repository

import zaitsev.a.d.mirea.data.toParseDataRSS
import zaitsev.a.d.mirea.domain.models.Failure
import zaitsev.a.d.mirea.domain.models.ResultCoroutines
import zaitsev.a.d.mirea.domain.models.Success
import zaitsev.a.d.mirea.domain.models.newsTypes.BBCType
import zaitsev.a.d.mirea.domain.models.newsTypes.BBCType.EDUCATION
import zaitsev.a.d.mirea.domain.models.newsTypes.BBCType.HEALTH
import zaitsev.a.d.mirea.domain.models.newsTypes.BBCType.POLITICS
import zaitsev.a.d.mirea.domain.models.newsTypes.BBCType.TECHNOLOGY
import zaitsev.a.d.mirea.domain.models.newsTypes.BBCType.WORLD
import zaitsev.a.d.mirea.domain.models.repository.rss.BBCNewsRepository
import zaitsev.a.d.mirea.domain.models.rss.BBCNewsDTO
import zaitsev.a.d.mirea.rss.FailureNews
import zaitsev.a.d.mirea.rss.SuccessNews
import zaitsev.a.d.mirea.rss.models.BBCWorldNewsRSS
import zaitsev.a.d.mirea.rss.repository.BBCWorldNewsParser
import java.net.URL

class BBCNewsRepositoryIml(private val bbcWorldNewsParser: BBCWorldNewsParser): BBCNewsRepository {

    private val patternDate = "EEE, dd MMM yyyy HH:mm:ss z"
    private fun convertList(news: BBCWorldNewsRSS): BBCNewsDTO?{
        val date = news.pubDate?.toParseDataRSS(pattern = patternDate)
        val checkNullData = news.title != null && news.link != null && date != null && news.description != null
        return if (checkNullData) {
            BBCNewsDTO(
                title = checkNotNull(news.title),
                date = checkNotNull(date),
                description = checkNotNull(news.description),
                linq = URL(checkNotNull(news.link)),
                imageURL = news.imageURL,
            )
        } else null
    }

    override suspend fun getNews(bbcType: BBCType): ResultCoroutines<List<BBCNewsDTO>, String> {
        return when(val result = when(bbcType){
            WORLD -> bbcWorldNewsParser.getWorldNews()
            EDUCATION -> bbcWorldNewsParser.getEducationNews()
            POLITICS -> bbcWorldNewsParser.getPoliticsNews()
            TECHNOLOGY -> bbcWorldNewsParser.getTechnologyNews()
            HEALTH -> bbcWorldNewsParser.getHealthNews()
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