package zaitsev.a.d.mirea.domain.models.use_cases.mailNews

import zaitsev.a.d.mirea.domain.models.Failure
import zaitsev.a.d.mirea.domain.models.Success
import zaitsev.a.d.mirea.domain.models.repository.rss.MailNewsRepository
import zaitsev.a.d.mirea.domain.models.rss.MailNewsDTO
import zaitsev.a.d.mirea.domain.models.use_cases.UseCaseResultList
import javax.inject.Inject

class GetListMailNews @Inject constructor(private val repository: MailNewsRepository) {
    suspend operator fun invoke(): UseCaseResultList<MailNewsDTO> {
        return when(val listNews = repository.getNews()){
            is Success -> UseCaseResultList.Success(listNews.value)
            is Failure -> UseCaseResultList.Failure(listNews.reason)
        }
    }
}