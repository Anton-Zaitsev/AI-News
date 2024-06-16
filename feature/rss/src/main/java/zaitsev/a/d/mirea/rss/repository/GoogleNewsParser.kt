package zaitsev.a.d.mirea.rss.repository

import zaitsev.a.d.mirea.rss.ResultServiceNews
import zaitsev.a.d.mirea.rss.models.GoogleNewsRSS

interface GoogleNewsParser {
    suspend fun getNews(): ResultServiceNews<List<GoogleNewsRSS>, String>
    suspend fun getNewsSearch(query: String): ResultServiceNews<List<GoogleNewsRSS>, String>
}