package zaitsev.a.d.mirea.domain.models.rss

import java.net.URL
import java.util.Date

data class GoogleNewsDTO(
    val title: String,
    val linq: URL,
    val date: Date,
    val description: String,
    val source: String,
    val sourceURL: URL?
)
