package zaitsev.a.d.mirea.domain.models.repository.rss

import zaitsev.a.d.mirea.domain.models.ResultCoroutines
import zaitsev.a.d.mirea.domain.models.rss.TassDTO

interface TassRepository {
    suspend fun getNews(): ResultCoroutines<List<TassDTO>, String>
}