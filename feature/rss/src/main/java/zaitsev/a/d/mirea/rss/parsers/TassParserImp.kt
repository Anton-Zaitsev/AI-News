package zaitsev.a.d.mirea.rss.parsers

import android.util.Xml
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.xmlpull.v1.XmlPullParser
import retrofit2.awaitResponse
import zaitsev.a.d.mirea.rss.FailureNews
import zaitsev.a.d.mirea.rss.RSS.CATEGORY
import zaitsev.a.d.mirea.rss.RSS.DATE_NAME
import zaitsev.a.d.mirea.rss.RSS.DESCRIPTION_NAME
import zaitsev.a.d.mirea.rss.RSS.ITEM_NAME
import zaitsev.a.d.mirea.rss.RSS.LINK_NAME
import zaitsev.a.d.mirea.rss.RSS.TITLE_NAME
import zaitsev.a.d.mirea.rss.ResultServiceNews
import zaitsev.a.d.mirea.rss.RetrofitConst
import zaitsev.a.d.mirea.rss.SuccessNews
import zaitsev.a.d.mirea.rss.models.TassRSS
import zaitsev.a.d.mirea.rss.readText
import zaitsev.a.d.mirea.rss.repository.TassParser
import zaitsev.a.d.mirea.rss.service.TassService
import java.io.InputStream

class TassParserImp(
    private val tassService: TassService,
): TassParser {

    companion object {
        private const val IMAGE_NAME = "enclosure"
        private const val URL_NAME = "url"
    }
    override suspend fun getNews(): ResultServiceNews<List<TassRSS>, String> {
        return withContext(Dispatchers.IO){
            try {
                val response = tassService.getNewsDefault().awaitResponse()
                val body = response.body()
                if (response.isSuccessful && body != null){
                    body.byteStream().buffered().use { stream ->
                        SuccessNews(parseXML(stream))
                    }
                }
                else {
                    val error = RetrofitConst.getError(response.errorBody(),response.code(), "Не удалось получить новости с Tass")
                    FailureNews(error)
                }
            }
            catch (cancel: CancellationException){
                FailureNews("Получение данных было отменено")
            }
            catch (runtime: RuntimeException){
                FailureNews("Не удалось получить новости с Tass ${runtime.message}")
            }
            catch (e: Exception){
                FailureNews("Не удалось получить новости с Tass ${e.message}")
            }
        }
    }

    private fun parseXML(inputStream: InputStream): List<TassRSS> {
        val parser = Xml.newPullParser().apply {
            setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            setInput(inputStream, null)
        }
        val tassRSSList = mutableListOf<TassRSS>()
        var currentTass: TassRSS? = null

        var eventType = parser.eventType
        while (eventType != XmlPullParser.END_DOCUMENT) {
            when(eventType){
                XmlPullParser.START_TAG -> {
                    when (parser.name) {
                        ITEM_NAME -> {
                            currentTass = TassRSS()
                        }
                        TITLE_NAME -> {
                            currentTass?.title = parser.readText()
                        }
                        LINK_NAME -> {
                            currentTass?.link = parser.readText()
                        }
                        DATE_NAME -> {
                            currentTass?.pubDate = parser.readText()
                        }
                        DESCRIPTION_NAME -> {
                            currentTass?.description = parser.readText()
                        }
                        IMAGE_NAME -> {
                            currentTass?.imageURL = parser.getAttributeValue(parser.namespace, URL_NAME)
                            parser.nextTag()
                        }
                        CATEGORY -> {
                            parser.readText()?.let { category ->
                                if (currentTass?.category == null)
                                    currentTass?.category = mutableListOf(category)
                                else
                                    currentTass?.category?.add(category)
                            }
                        }
                    }
                }
                XmlPullParser.END_TAG -> {
                    if (parser.name == ITEM_NAME && currentTass != null) {
                        tassRSSList.add(currentTass)
                        currentTass = null
                    }
                }
            }
            eventType = parser.next()
        }
        return tassRSSList
    }
}