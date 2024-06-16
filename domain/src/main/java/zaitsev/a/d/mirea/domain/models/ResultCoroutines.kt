package zaitsev.a.d.mirea.domain.models

sealed class ResultCoroutines<out Success, out Failure>
data class Success<out Success>(val value: Success) : ResultCoroutines<Success, Nothing>()
data class Failure<out Failure>(val reason: Failure) : ResultCoroutines<Nothing, Failure>()