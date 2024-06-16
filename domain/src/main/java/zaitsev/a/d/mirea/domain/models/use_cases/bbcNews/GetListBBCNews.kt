package zaitsev.a.d.mirea.domain.models.use_cases.bbcNews

import zaitsev.a.d.mirea.domain.models.Failure
import zaitsev.a.d.mirea.domain.models.Success
import zaitsev.a.d.mirea.domain.models.newsTypes.BBCType
import zaitsev.a.d.mirea.domain.models.repository.rss.BBCNewsRepository
import zaitsev.a.d.mirea.domain.models.rss.BBCNewsDTO
import zaitsev.a.d.mirea.domain.models.use_cases.UseCaseResultList
import javax.inject.Inject

class GetListBBCNews @Inject constructor(private val bbcNewsRepository: BBCNewsRepository) {
    suspend operator fun invoke(bbcType: BBCType): UseCaseResultList<BBCNewsDTO> {
        return when(val listNews = bbcNewsRepository.getNews(bbcType)){
            is Success -> UseCaseResultList.Success(listNews.value)
            is Failure -> UseCaseResultList.Failure(listNews.reason)
        }
    }
}