package zaitsev.a.d.mirea.rss.repository

import zaitsev.a.d.mirea.rss.ResultServiceNews
import zaitsev.a.d.mirea.rss.models.AstroBeneNewsRSS

interface AstroBeneNewsParser {
    suspend fun getNews(): ResultServiceNews<List<AstroBeneNewsRSS>, String>
}