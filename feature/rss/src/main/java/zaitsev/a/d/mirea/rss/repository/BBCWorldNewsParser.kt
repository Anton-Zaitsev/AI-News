package zaitsev.a.d.mirea.rss.repository

import zaitsev.a.d.mirea.rss.ResultServiceNews
import zaitsev.a.d.mirea.rss.models.BBCWorldNewsRSS

interface BBCWorldNewsParser {
    suspend fun getWorldNews(): ResultServiceNews<List<BBCWorldNewsRSS>, String>
    suspend fun getEducationNews(): ResultServiceNews<List<BBCWorldNewsRSS>, String>
    suspend fun getPoliticsNews(): ResultServiceNews<List<BBCWorldNewsRSS>, String>
    suspend fun getTechnologyNews(): ResultServiceNews<List<BBCWorldNewsRSS>, String>
    suspend fun getHealthNews(): ResultServiceNews<List<BBCWorldNewsRSS>, String>
}