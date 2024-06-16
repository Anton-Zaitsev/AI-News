package zaitsev.a.d.mirea.domain.models.use_cases


sealed class UseCaseResultList<T> {
    data class Success<T>(val list: List<T>) : UseCaseResultList<T>()
    data class Failure<T>(val error: String) : UseCaseResultList<T>()
}
