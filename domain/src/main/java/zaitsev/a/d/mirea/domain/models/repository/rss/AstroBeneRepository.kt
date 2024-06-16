package zaitsev.a.d.mirea.domain.models.repository.rss

import zaitsev.a.d.mirea.domain.models.ResultCoroutines
import zaitsev.a.d.mirea.domain.models.rss.AstroBeneNewsDTO

interface AstroBeneRepository {
    suspend fun getNews(): ResultCoroutines<List<AstroBeneNewsDTO>, String>
}