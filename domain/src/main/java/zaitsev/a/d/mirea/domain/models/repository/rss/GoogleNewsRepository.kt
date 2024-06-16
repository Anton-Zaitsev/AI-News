package zaitsev.a.d.mirea.domain.models.repository.rss

import zaitsev.a.d.mirea.domain.models.ResultCoroutines
import zaitsev.a.d.mirea.domain.models.rss.GoogleNewsDTO

interface GoogleNewsRepository {

    suspend fun getNews(): ResultCoroutines<List<GoogleNewsDTO>, String>
    suspend fun searchNews(query: String): ResultCoroutines<List<GoogleNewsDTO>, String>
}