package zaitsev.a.d.mirea.rss.models

data class TassRSS(
    var title: String? = null,
    var link: String? = null,
    var pubDate: String? = null,
    var description: String? = null,
    var imageURL: String? = null,
    var category: MutableList<String>? = null
)
