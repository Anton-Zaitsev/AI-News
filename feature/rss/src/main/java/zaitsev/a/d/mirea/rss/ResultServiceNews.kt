package zaitsev.a.d.mirea.rss

sealed class ResultServiceNews<out Success, out Failure>
data class SuccessNews<out Success>(val value: Success) : ResultServiceNews<Success, Nothing>()
data class FailureNews<out Failure>(val reason: Failure) : ResultServiceNews<Nothing, Failure>()