package zaitsev.a.d.mirea.domain.models.use_cases.googleNews

import zaitsev.a.d.mirea.domain.models.Failure
import zaitsev.a.d.mirea.domain.models.Success
import zaitsev.a.d.mirea.domain.models.repository.rss.GoogleNewsRepository
import zaitsev.a.d.mirea.domain.models.rss.GoogleNewsDTO
import zaitsev.a.d.mirea.domain.models.use_cases.UseCaseResultList
import javax.inject.Inject

class GetListGoogleNews @Inject constructor(private val repository: GoogleNewsRepository) {
    suspend operator fun invoke(): UseCaseResultList<GoogleNewsDTO> {
        return when(val listNews = repository.getNews()){
            is Success -> UseCaseResultList.Success(listNews.value)
            is Failure -> UseCaseResultList.Failure(listNews.reason)
        }
    }
}