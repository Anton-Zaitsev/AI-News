package zaitsev.a.d.mirea.domain.models.rss

import java.net.URL
import java.util.Date

data class AstroBeneNewsDTO(
    val title: String,
    val description: String,
    val linq: URL,
    val date: Date,
    val category: List<String>
)
