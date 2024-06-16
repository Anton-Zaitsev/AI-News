package zaitsev.a.d.mirea.rss.parsers

import android.util.Xml
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.xmlpull.v1.XmlPullParser
import retrofit2.awaitResponse
import zaitsev.a.d.mirea.rss.FailureNews
import zaitsev.a.d.mirea.rss.RSS
import zaitsev.a.d.mirea.rss.ResultServiceNews
import zaitsev.a.d.mirea.rss.RetrofitConst
import zaitsev.a.d.mirea.rss.SuccessNews
import zaitsev.a.d.mirea.rss.models.MailNewsRSS
import zaitsev.a.d.mirea.rss.readText
import zaitsev.a.d.mirea.rss.repository.MailNewsParser
import zaitsev.a.d.mirea.rss.service.MailService
import java.io.InputStream

class MailNewsParserImp(
    private val mailNewsService: MailService,
): MailNewsParser {

    override suspend fun getNews(): ResultServiceNews<List<MailNewsRSS>, String> {
        return withContext(Dispatchers.IO){
            try {
                val response = mailNewsService.getNewsDefault().awaitResponse()
                val body = response.body()
                if (response.isSuccessful && body != null){
                    body.byteStream().buffered().use { stream ->
                        SuccessNews(parseXML(stream))
                    }
                }
                else {
                    val error = RetrofitConst.getError(response.errorBody(),response.code(), "Не удалось получить новости с MailNews")
                    FailureNews(error)
                }
            }
            catch (cancel: CancellationException){
                FailureNews("Получение данных было отменено")
            }
            catch (runtime: RuntimeException){
                FailureNews("Не удалось получить новости с MailNews ${runtime.message}")
            }
            catch (e: Exception){
                FailureNews("Не удалось получить новости с MailNews ${e.message}")
            }
        }
    }

    private fun parseXML(inputStream: InputStream): List<MailNewsRSS> {
        val parser = Xml.newPullParser().apply {
            setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            setInput(inputStream, null)
        }
        val mailRSSList = mutableListOf<MailNewsRSS>()
        var currentMail: MailNewsRSS? = null

        var eventType = parser.eventType
        while (eventType != XmlPullParser.END_DOCUMENT) {
            when(eventType){
                XmlPullParser.START_TAG -> {
                    when (parser.name) {
                        RSS.ITEM_NAME -> {
                            currentMail = MailNewsRSS()
                        }
                        RSS.TITLE_NAME -> {
                            currentMail?.title = parser.readText()
                        }
                        RSS.LINK_NAME -> {
                            currentMail?.link = parser.readText()
                        }
                        RSS.DATE_NAME -> {
                            currentMail?.pubDate = parser.readText()
                        }
                        RSS.DESCRIPTION_NAME -> {
                            currentMail?.description = parser.readText()
                        }
                        RSS.CATEGORY -> {
                            currentMail?.category = parser.readText()
                        }
                    }
                }
                XmlPullParser.END_TAG -> {
                    if (parser.name == RSS.ITEM_NAME && currentMail != null) {
                        mailRSSList.add(currentMail)
                        currentMail = null
                    }
                }
            }
            eventType = parser.next()
        }
        return mailRSSList
    }
}