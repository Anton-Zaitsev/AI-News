package zaitsev.a.d.mirea.rss.repository

import zaitsev.a.d.mirea.rss.ResultServiceNews
import zaitsev.a.d.mirea.rss.models.NewYourTimeRSS

interface NewYorkTimeNewsParser {
    suspend fun getWorldNews(): ResultServiceNews<List<NewYourTimeRSS>, String>
    suspend fun getBusinessNews(): ResultServiceNews<List<NewYourTimeRSS>, String>
    suspend fun getTechnologyNews(): ResultServiceNews<List<NewYourTimeRSS>, String>
    suspend fun getSportsNews(): ResultServiceNews<List<NewYourTimeRSS>, String>
}