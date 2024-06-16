package zaitsev.a.d.mirea.diplom.data.rss

import com.google.mlkit.nl.translate.TranslateLanguage
import java.net.URL
import java.util.Date

interface ModelNewsImp {
    val id: Long
    val isLocal: Boolean
    val title: String
    val description: String
    val linq: URL
    val date: Date
    @TranslateLanguage.Language val language: String
}