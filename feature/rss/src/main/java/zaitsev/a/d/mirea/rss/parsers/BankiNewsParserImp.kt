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
import zaitsev.a.d.mirea.rss.models.BankiNewsRSS
import zaitsev.a.d.mirea.rss.readText
import zaitsev.a.d.mirea.rss.repository.BankiNewsParser
import zaitsev.a.d.mirea.rss.service.BankiNewsService
import java.io.InputStream
import kotlin.coroutines.cancellation.CancellationException

class BankiNewsParserImp(
    private val bankiNewsService: BankiNewsService,
): BankiNewsParser {
    companion object {
        private const val SYNOPSIS = "synopsis"
        private const val BANK_NAME = "bank_name"
    }

    override suspend fun getNews(): ResultServiceNews<List<BankiNewsRSS>, String> {
        return withContext(Dispatchers.IO){
            try {
                val response = bankiNewsService.getNewsDefault().awaitResponse()
                val body = response.body()
                if (response.isSuccessful && body != null){
                    body.byteStream().buffered().use { stream ->
                        SuccessNews(parseXML(stream))
                    }
                }
                else {
                    val error = RetrofitConst.getError(response.errorBody(),response.code(), "Не удалось получить новости с BankiNews")
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
                FailureNews("Не удалось получить новости с BankiNews ${e.message}")
            }
        }
    }
    private fun parseXML(inputStream: InputStream): List<BankiNewsRSS> {
        val parser = Xml.newPullParser().apply {
            setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            setInput(inputStream, null)
        }
        val bankiRSSList = mutableListOf<BankiNewsRSS>()
        var currentBanki: BankiNewsRSS? = null

        var eventType = parser.eventType
        while (eventType != XmlPullParser.END_DOCUMENT) {
            when(eventType){
                XmlPullParser.START_TAG -> {
                    when (parser.name) {
                        RSS.ITEM_NAME -> {
                            currentBanki = BankiNewsRSS()
                        }
                        RSS.TITLE_NAME -> {
                            currentBanki?.title = parser.readText()
                        }
                        RSS.LINK_NAME -> {
                            currentBanki?.link = parser.readText()
                        }
                        RSS.DATE_NAME -> {
                            currentBanki?.pubDate = parser.readText()
                        }
                        RSS.DESCRIPTION_NAME -> {
                            currentBanki?.description = parser.readText()
                        }
                        SYNOPSIS -> {
                            currentBanki?.synopsis = parser.readText()
                        }
                        BANK_NAME -> {
                            currentBanki?.bankName = parser.readText()
                        }
                    }
                }
                XmlPullParser.END_TAG -> {
                    if (parser.name == RSS.ITEM_NAME && currentBanki != null) {
                        bankiRSSList.add(currentBanki)
                        currentBanki = null
                    }
                }
            }
            eventType = parser.next()
        }
        return bankiRSSList
    }
}