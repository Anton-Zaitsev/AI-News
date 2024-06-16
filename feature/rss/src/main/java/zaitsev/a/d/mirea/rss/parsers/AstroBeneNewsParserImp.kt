package zaitsev.a.d.mirea.rss.parsers

import android.util.Xml
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.xmlpull.v1.XmlPullParser
import retrofit2.awaitResponse
import zaitsev.a.d.mirea.rss.FailureNews
import zaitsev.a.d.mirea.rss.RSS
import zaitsev.a.d.mirea.rss.ResultServiceNews
import zaitsev.a.d.mirea.rss.RetrofitConst
import zaitsev.a.d.mirea.rss.SuccessNews
import zaitsev.a.d.mirea.rss.models.AstroBeneNewsRSS
import zaitsev.a.d.mirea.rss.readText
import zaitsev.a.d.mirea.rss.repository.AstroBeneNewsParser
import zaitsev.a.d.mirea.rss.service.AstroBeneNewsService
import java.io.InputStream
import kotlin.coroutines.cancellation.CancellationException

class AstroBeneNewsParserImp(
    private val astroBeneNewsService: AstroBeneNewsService,
): AstroBeneNewsParser {

    override suspend fun getNews(): ResultServiceNews<List<AstroBeneNewsRSS>, String> {
        return withContext(Dispatchers.IO){
            try {
                val response = astroBeneNewsService.getNewsDefault().awaitResponse()
                val body = response.body()
                if (response.isSuccessful && body != null){
                    body.byteStream().buffered().use { stream ->
                        SuccessNews(parseXML(stream))
                    }
                }
                else {
                    val error = RetrofitConst.getError(response.errorBody(),response.code(), "Не удалось получить новости с AstoNews")
                    FailureNews(error)
                }
            }
            catch (cancel: CancellationException){
                FailureNews("Получение данных было отменено")
            }
            catch (runtime: RuntimeException){
                FailureNews("Не удалось получить новости с BankiNews ${runtime.message}")
            }
            catch (e: Exception){
                FailureNews("Не удалось получить новости с AstoNews ${e.message}")
            }
        }
    }

    private fun parseXML(inputStream: InputStream): List<AstroBeneNewsRSS> {
        val parser = Xml.newPullParser().apply {
            setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            setInput(inputStream, null)
        }
        val astroRSSList = mutableListOf<AstroBeneNewsRSS>()
        var astroNews: AstroBeneNewsRSS? = null

        var eventType = parser.eventType
        while (eventType != XmlPullParser.END_DOCUMENT) {
            when(eventType){
                XmlPullParser.START_TAG -> {
                    when (parser.name) {
                        RSS.ITEM_NAME -> {
                            astroNews = AstroBeneNewsRSS()
                        }
                        RSS.TITLE_NAME -> {
                            astroNews?.title = parser.readText()
                        }
                        RSS.LINK_NAME -> {
                            astroNews?.link = parser.readText()
                        }
                        RSS.DATE_NAME -> {
                            astroNews?.pubDate = parser.readText()
                        }
                        RSS.DESCRIPTION_NAME -> {
                            astroNews?.description = parser.readText()
                        }
                        RSS.CATEGORY -> {
                            parser.readText()?.let { category ->
                                if (astroNews?.category == null)
                                    astroNews?.category = mutableListOf(category)
                                else
                                    astroNews?.category?.add(category)
                            }
                        }
                    }
                }
                XmlPullParser.END_TAG -> {
                    if (parser.name == RSS.ITEM_NAME && astroNews != null) {
                        astroRSSList.add(astroNews)
                        astroNews = null
                    }
                }
            }
            eventType = parser.next()
        }
        return astroRSSList
    }
}