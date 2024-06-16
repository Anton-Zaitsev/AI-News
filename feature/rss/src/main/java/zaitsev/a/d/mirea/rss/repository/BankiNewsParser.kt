package zaitsev.a.d.mirea.rss.repository

import zaitsev.a.d.mirea.rss.ResultServiceNews
import zaitsev.a.d.mirea.rss.models.BankiNewsRSS

interface BankiNewsParser {
    suspend fun getNews(): ResultServiceNews<List<BankiNewsRSS>, String>
}