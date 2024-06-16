package zaitsev.a.d.mirea.data

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

internal const val patternDateRSSDefault = "EEE, d MMM yyyy HH:mm:ss Z"
internal fun String.toParseDataRSS(pattern: String = patternDateRSSDefault): Date? {
    return try {
        val formatter = DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH)
        val dateTime = ZonedDateTime.parse(this, formatter)
        Date.from(dateTime.toInstant())
    }catch (_ : Exception){
        null
    }
}