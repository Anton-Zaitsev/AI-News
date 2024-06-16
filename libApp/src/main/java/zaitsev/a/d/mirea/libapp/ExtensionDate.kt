package zaitsev.a.d.mirea.libapp

import android.os.Build
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

fun dateNow(patternDate: String): String{
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val dateNow = LocalDateTime.now()
        val pattern = DateTimeFormatter.ofPattern(patternDate, Locale.getDefault())
        dateNow.format(pattern)
    }
    else {
        val dateNow = Calendar.getInstance().time
        val pattern = SimpleDateFormat(patternDate, Locale.getDefault())
        pattern.format(dateNow)
    }
}