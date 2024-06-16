package zaitsev.a.d.mirea.rss.repository

import zaitsev.a.d.mirea.rss.ResultServiceNews
import zaitsev.a.d.mirea.rss.models.custom.NewsInfoRSS

interface ParserRSSChannel {
    suspend fun getRSSInfo(url: String): ResultServiceNews<NewsInfoRSS, String>
}