package zaitsev.a.d.mirea.rss.parsers

import android.util.Xml
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.xmlpull.v1.XmlPullParser
import retrofit2.awaitResponse
import zaitsev.a.d.mirea.rss.FailureNews
import zaitsev.a.d.mirea.rss.RSS.DATE_NAME
import zaitsev.a.d.mirea.rss.RSS.DESCRIPTION_NAME
import zaitsev.a.d.mirea.rss.RSS.ITEM_NAME
import zaitsev.a.d.mirea.rss.RSS.LINK_NAME
import zaitsev.a.d.mirea.rss.RSS.TITLE_NAME
import zaitsev.a.d.mirea.rss.ResultServiceNews
import zaitsev.a.d.mirea.rss.RetrofitConst
import zaitsev.a.d.mirea.rss.SuccessNews
import zaitsev.a.d.mirea.rss.models.GoogleNewsRSS
import zaitsev.a.d.mirea.rss.readText
import zaitsev.a.d.mirea.rss.repository.GoogleNewsParser
import zaitsev.a.d.mirea.rss.service.GoogleNewsService
import java.io.InputStream
import kotlin.coroutines.cancellation.CancellationException

class GoogleNewsParserImp(
    private val googleNewsService: GoogleNewsService,
): GoogleNewsParser {
    companion object {
        private const val SOURCE_NAME = "source"
        private const val URL_NAME = "url"
    }
    override suspend fun getNews(): ResultServiceNews<List<GoogleNewsRSS>, String> {
        return withContext(Dispatchers.IO){
            try {
                val response = googleNewsService.getNewsDefault(hl = "ru", gl = "RU", ceid = "RU:ru").awaitResponse()
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
                FailureNews("Не удалось получить новости с GoogleNews ${runtime.message}")
            }
            catch (e: Exception){
                FailureNews("Не удалось получить новости с GoogleNews ${e.message}")
            }
        }
    }

    override suspend fun getNewsSearch(query: String): ResultServiceNews<List<GoogleNewsRSS>, String> {
        return withContext(Dispatchers.IO){
            try {
                val response = googleNewsService.searchNews(query = query.lowercase(), hl = "ru", gl = "RU", ceid = "RU:ru").awaitResponse()
                val body = response.body()
                if (response.isSuccessful && body != null){
                    body.byteStream().use { stream ->
                        SuccessNews(parseXML(stream))
                    }
                }
                else {
                    val errorBDD = RetrofitConst.getError(response.errorBody(),response.code(), "Не удалось получить новости с GoogleNews")
                    FailureNews(errorBDD)
                }
            }
            catch (e: Exception){
                FailureNews("Не удалось получить новости с GoogleNews ${e.message}")
            }
        }
    }

    private fun parseXML(inputStream: InputStream): List<GoogleNewsRSS>{
        val parser = Xml.newPullParser().apply {
            setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            setInput(inputStream, null)
        }
        val googleNewsRSSList = mutableListOf<GoogleNewsRSS>()
        var currentNewsGoogle: GoogleNewsRSS? = null

        var eventType = parser.eventType
        while (eventType != XmlPullParser.END_DOCUMENT) {
            when(eventType){
                XmlPullParser.START_TAG -> {
                    when (parser.name) {
                        ITEM_NAME -> {
                            currentNewsGoogle = GoogleNewsRSS()
                        }
                        TITLE_NAME -> {
                            currentNewsGoogle?.title = parser.readText()
                        }
                        LINK_NAME -> {
                            currentNewsGoogle?.link = parser.readText()
                        }
                        DATE_NAME -> {
                            currentNewsGoogle?.pubDate = parser.readText()
                        }
                        DESCRIPTION_NAME -> {
                            currentNewsGoogle?.description = parser.readText()
                        }
                        SOURCE_NAME -> {
                            currentNewsGoogle?.urlSource = parser.getAttributeValue(parser.namespace, URL_NAME)
                            currentNewsGoogle?.source = parser.readText()
                        }
                    }
                }
                XmlPullParser.END_TAG -> {
                    if (parser.name == ITEM_NAME && currentNewsGoogle != null) {
                        googleNewsRSSList.add(currentNewsGoogle)
                        currentNewsGoogle = null
                    }
                }
            }
            eventType = parser.next()
        }
        return googleNewsRSSList
    }
}