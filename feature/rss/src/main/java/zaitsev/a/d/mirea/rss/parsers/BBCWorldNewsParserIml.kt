package zaitsev.a.d.mirea.rss.parsers

import android.util.Xml
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import org.xmlpull.v1.XmlPullParser
import retrofit2.Call
import retrofit2.awaitResponse
import zaitsev.a.d.mirea.rss.FailureNews
import zaitsev.a.d.mirea.rss.RSS
import zaitsev.a.d.mirea.rss.ResultServiceNews
import zaitsev.a.d.mirea.rss.RetrofitConst
import zaitsev.a.d.mirea.rss.SuccessNews
import zaitsev.a.d.mirea.rss.models.BBCWorldNewsRSS
import zaitsev.a.d.mirea.rss.readText
import zaitsev.a.d.mirea.rss.repository.BBCWorldNewsParser
import zaitsev.a.d.mirea.rss.service.BBCWorldNewsService
import java.io.InputStream
import kotlin.coroutines.cancellation.CancellationException

class BBCWorldNewsParserIml(private val bbcWorldNewsParser: BBCWorldNewsService):
    BBCWorldNewsParser {

    companion object {
        private const val IMAGE_NAME = "media:thumbnail"
        private const val URL_NAME = "url"
    }

    override suspend fun getWorldNews(): ResultServiceNews<List<BBCWorldNewsRSS>, String> {
        return getNews(call = bbcWorldNewsParser.getNewsWorldDefault())
    }

    override suspend fun getEducationNews(): ResultServiceNews<List<BBCWorldNewsRSS>, String> {
        return getNews(call = bbcWorldNewsParser.getNewsEducationDefault())
    }

    override suspend fun getPoliticsNews(): ResultServiceNews<List<BBCWorldNewsRSS>, String> {
        return getNews(call = bbcWorldNewsParser.getNewsPoliticsDefault())
    }

    override suspend fun getTechnologyNews(): ResultServiceNews<List<BBCWorldNewsRSS>, String> {
        return getNews(call = bbcWorldNewsParser.getNewsTechnologyDefault())
    }

    override suspend fun getHealthNews(): ResultServiceNews<List<BBCWorldNewsRSS>, String> {
        return getNews(call = bbcWorldNewsParser.getNewsHealthDefault())
    }
    private suspend fun getNews(call: Call<ResponseBody>): ResultServiceNews<List<BBCWorldNewsRSS>, String> {
        return withContext(Dispatchers.IO){
            try {
                val response = call.awaitResponse()
                val body = response.body()
                if (response.isSuccessful && body != null){
                    body.byteStream().buffered().use { stream ->
                        SuccessNews(parseXML(stream))
                    }
                }
                else {
                    val error = RetrofitConst.getError(response.errorBody(),response.code(), "Не удалось получить новости с GoogleNews")
                    FailureNews(error)
                }
            }
            catch (cancel: CancellationException){
                FailureNews("Получение данных было отменено")
            }
            catch (runtime: RuntimeException){
                FailureNews("Не удалось получить новости с BBC ${runtime.message}")
            }
            catch (e: Exception){
                FailureNews("Не удалось получить новости с BBC ${e.message}")
            }
        }
    }


    private fun parseXML(inputStream: InputStream): List<BBCWorldNewsRSS> {
        val parser = Xml.newPullParser().apply {
            setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            setInput(inputStream, null)
        }
        val bbcWorldNewsList = mutableListOf<BBCWorldNewsRSS>()
        var currentBBCWorldNews: BBCWorldNewsRSS? = null

        var eventType = parser.eventType
        while (eventType != XmlPullParser.END_DOCUMENT) {
            when(eventType){
                XmlPullParser.START_TAG -> {
                    when (parser.name) {
                        RSS.ITEM_NAME -> {
                            currentBBCWorldNews = BBCWorldNewsRSS()
                        }
                        RSS.TITLE_NAME -> {
                            currentBBCWorldNews?.title = parser.readText()
                        }
                        RSS.LINK_NAME -> {
                            currentBBCWorldNews?.link = parser.readText()
                        }
                        RSS.DATE_NAME -> {
                            currentBBCWorldNews?.pubDate = parser.readText()
                        }
                        RSS.DESCRIPTION_NAME -> {
                            currentBBCWorldNews?.description = parser.readText()
                        }
                        IMAGE_NAME -> {
                            currentBBCWorldNews?.imageURL = parser.getAttributeValue(parser.namespace,
                                URL_NAME
                            )
                            parser.nextTag()
                        }
                    }
                }
                XmlPullParser.END_TAG -> {
                    if (parser.name == RSS.ITEM_NAME && currentBBCWorldNews != null) {
                        bbcWorldNewsList.add(currentBBCWorldNews)
                        currentBBCWorldNews = null
                    }
                }
            }
            eventType = parser.next()
        }
        return bbcWorldNewsList
    }
}