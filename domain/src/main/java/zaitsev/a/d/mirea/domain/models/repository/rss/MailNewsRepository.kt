package zaitsev.a.d.mirea.domain.models.repository.rss

import zaitsev.a.d.mirea.domain.models.ResultCoroutines
import zaitsev.a.d.mirea.domain.models.rss.MailNewsDTO

interface MailNewsRepository {
    suspend fun getNews(): ResultCoroutines<List<MailNewsDTO>, String>
}