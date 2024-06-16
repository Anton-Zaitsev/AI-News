package zaitsev.a.d.mirea.rss.repository

import zaitsev.a.d.mirea.rss.ResultServiceNews
import zaitsev.a.d.mirea.rss.models.MailNewsRSS

interface MailNewsParser {
    suspend fun getNews(): ResultServiceNews<List<MailNewsRSS>, String>
}