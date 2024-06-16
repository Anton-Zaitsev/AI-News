package zaitsev.a.d.mirea.rss.parserChannel

import android.util.Xml
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.xmlpull.v1.XmlPullParser
import retrofit2.Retrofit
import retrofit2.awaitResponse
import zaitsev.a.d.mirea.rss.FailureNews
import zaitsev.a.d.mirea.rss.RSS
import zaitsev.a.d.mirea.rss.ResultServiceNews
import zaitsev.a.d.mirea.rss.RetrofitConst
import zaitsev.a.d.mirea.rss.SuccessNews
import zaitsev.a.d.mirea.rss.isURL
import zaitsev.a.d.mirea.rss.models.custom.NewsInfoRSS
import zaitsev.a.d.mirea.rss.onlyText
import zaitsev.a.d.mirea.rss.readText
import zaitsev.a.d.mirea.rss.repository.ParserRSSChannel
import zaitsev.a.d.mirea.rss.service.CustomRSSService
import java.io.InputStream
import kotlin.coroutines.cancellation.CancellationException

class ParserRSSChannelImp(
    private val retrofit: Retrofit
): ParserRSSChannel {
    override suspend fun getRSSInfo(url: String): ResultServiceNews<NewsInfoRSS, String> {
        return withContext(Dispatchers.IO){
            try {
                val service = retrofit.create(CustomRSSService::class.java)
                val response = service.getNewsInfo(url).awaitResponse()
                val body = response.body()
                if (response.isSuccessful && body != null){
                    body.byteStream().buffered().use { stream ->
                        val channel = parseXMLChannel(stream)
                        if (channel != null){
                            SuccessNews(channel)
                        }
                        else FailureNews("Не удалось получить данные о RSS канале")
                    }
                }
                else {
                    val error = RetrofitConst.getError(response.errorBody(),response.code(), "Не удалось получить данные с RSS канала")
                    FailureNews(error)
                }
            }
            catch (cancel: CancellationException){
                FailureNews("Получение данных было отменено")
            }
            catch (runtime: RuntimeException){
                FailureNews("Не удалось получить данные с RSS канала ${runtime.message}")
            }
            catch (e: Exception){
                FailureNews("Не удалось получить данные с RSS канала, проблемы с интернет-соединением ${e.message}")
            }
        }
    }

    private fun parseXMLChannel(inputStream: InputStream): NewsInfoRSS? {
        val parser = Xml.newPullParser().apply {
            setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            setInput(inputStream, null)
        }
        var eventType = parser.eventType

        var currentChannel: NewsInfoRSS? = null

        val listTag = listOf("title", "description", "link", "atom:link", "image")
        val extensionImage = listOf(".png", ".jpeg", ".jpg", ".webp")

        while (eventType != XmlPullParser.END_DOCUMENT) {
            when (eventType) {
                XmlPullParser.START_TAG -> {
                    when(parser.name){
                        RSS.ITEM_CHANNEL -> {
                            currentChannel = NewsInfoRSS()

                            var nextTag = parser.nextTag()
                            while(nextTag == XmlPullParser.START_TAG) {

                                val parserName = try {
                                    parser.name?.lowercase()
                                }catch (_: Exception){
                                    null
                                } ?: continue

                                if (listTag.contains(parserName)) {
                                    when (parserName) {
                                        "title" -> {
                                            parser.readText()?.let { title ->
                                                currentChannel = currentChannel?.copy(title = title)
                                            }
                                        }

                                        "description" -> {
                                            parser.readText()?.let { description ->
                                                currentChannel = currentChannel?.copy(description = description)
                                            }
                                        }

                                        "link", "atom:link" -> {
                                            if (currentChannel?.ling == null){
                                                if (parserName == "atom:link"){
                                                    parser.getAttributeValue(parser.namespace, "href")?.let { url ->
                                                        currentChannel = currentChannel?.copy(ling = url)
                                                    }
                                                    parser.nextTag()
                                                }
                                                else
                                                    parser.readText()?.let { ling ->
                                                        currentChannel = currentChannel?.copy(ling = ling)
                                                    }
                                            }
                                        }

                                        "image" -> {
                                            parser.readText()?.let { image ->
                                                currentChannel = currentChannel?.copy(image = image)
                                            }
                                        }
                                    }
                                }
                                else {
                                    try {
                                        val text = parser.onlyText()

                                        val isExistTagTextImage = text?.let {
                                            if (it.length < 4)
                                                false
                                            else
                                                extensionImage.any { extension ->
                                                    it.endsWith(extension)
                                                }
                                        } ?: false

                                        if (isExistTagTextImage){
                                            currentChannel = currentChannel?.copy(image = text)
                                            parser.nextTag()
                                        }
                                        else {
                                            var attribute: Int = parser.nextToken()
                                            while (attribute != XmlPullParser.END_TAG){
                                                val value: String? = parser.text
                                                if (value?.isURL() == true){
                                                    val isExist = extensionImage.any { extension ->
                                                        value.endsWith(extension)
                                                    }
                                                    if (isExist){
                                                        currentChannel = currentChannel?.copy(image = value)
                                                        break
                                                    }
                                                }
                                                attribute = parser.nextToken()
                                            }
                                        }
                                    }
                                    catch (_: Exception){}
                                }
                                try {
                                    nextTag = parser.nextTag()
                                }catch (_: Exception){
                                    parser.next()
                                    nextTag = parser.nextTag()
                                }
                            }
                            break
                        }
                        RSS.ITEM_NAME -> {
                            break
                        }
                    }
                }
            }
            eventType = parser.next()
        }
        return if (currentChannel?.title == null || currentChannel?.description == null || currentChannel?.ling == null)
            null
        else currentChannel
    }
}