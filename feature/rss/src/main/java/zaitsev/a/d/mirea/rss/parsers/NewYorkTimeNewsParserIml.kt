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
import zaitsev.a.d.mirea.rss.models.NewYourTimeRSS
import zaitsev.a.d.mirea.rss.readText
import zaitsev.a.d.mirea.rss.repository.NewYorkTimeNewsParser
import zaitsev.a.d.mirea.rss.service.NewYorkTimesService
import java.io.InputStream
import kotlin.coroutines.cancellation.CancellationException

class NewYorkTimeNewsParserIml(private val newYorkTimesService: NewYorkTimesService): NewYorkTimeNewsParser {
    companion object {
        private const val IMAGE_NAME = "media:content"
        private const val URL_NAME = "url"
    }
    override suspend fun getWorldNews(): ResultServiceNews<List<NewYourTimeRSS>, String> {
        return getNews(newYorkTimesService.getNewsWorldDefault())
    }

    override suspend fun getBusinessNews(): ResultServiceNews<List<NewYourTimeRSS>, String> {
        return getNews(newYorkTimesService.getNewsWorldDefault())
    }

    override suspend fun getTechnologyNews(): ResultServiceNews<List<NewYourTimeRSS>, String> {
        return getNews(newYorkTimesService.getNewsTechnologyDefault())
    }

    override suspend fun getSportsNews(): ResultServiceNews<List<NewYourTimeRSS>, String> {
        return getNews(newYorkTimesService.getNewsSportsDefault())
    }

    private suspend fun getNews(call: Call<ResponseBody>): ResultServiceNews<List<NewYourTimeRSS>, String> {
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
                FailureNews("Не удалось получить новости с NewYour Times ${runtime.message}")
            }
            catch (e: Exception){
                FailureNews("Не удалось получить новости с NewYour Times ${e.message}")
            }
        }
    }


    private fun parseXML(inputStream: InputStream): List<NewYourTimeRSS> {
        val parser = Xml.newPullParser().apply {
            setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            setInput(inputStream, null)
        }
        val newYorkNewsList = mutableListOf<NewYourTimeRSS>()
        var currentNYNews: NewYourTimeRSS? = null

        var eventType = parser.eventType
        while (eventType != XmlPullParser.END_DOCUMENT) {
            when(eventType){
                XmlPullParser.START_TAG -> {
                    when (parser.name) {
                        RSS.ITEM_NAME -> {
                            currentNYNews = NewYourTimeRSS()
                        }
                        RSS.TITLE_NAME -> {
                            currentNYNews?.title = parser.readText()
                        }
                        RSS.LINK_NAME -> {
                            currentNYNews?.link = parser.readText()
                        }
                        RSS.DATE_NAME -> {
                            currentNYNews?.pubDate = parser.readText()
                        }
                        RSS.DESCRIPTION_NAME -> {
                            currentNYNews?.description = parser.readText()
                        }
                        RSS.CATEGORY -> {
                            parser.readText()?.let { category ->
                                if (currentNYNews?.category == null)
                                    currentNYNews?.category = mutableListOf(category)
                                else
                                    currentNYNews?.category?.add(category)
                            }
                        }
                        IMAGE_NAME -> {
                            currentNYNews?.imageURL = parser.getAttributeValue(parser.namespace,
                                URL_NAME
                            )
                            parser.nextTag()
                        }
                    }
                }
                XmlPullParser.END_TAG -> {
                    if (parser.name == RSS.ITEM_NAME && currentNYNews != null) {
                        newYorkNewsList.add(currentNYNews)
                        currentNYNews = null
                    }
                }
            }
            eventType = parser.next()
        }
        return newYorkNewsList
    }
}