package zaitsev.a.d.mirea.rss.models

data class GoogleNewsRSS(
    var title: String? = null,
    var link: String? = null,
    var pubDate: String? = null,
    var description: String? = null,
    var urlSource: String? = null,
    var source: String? = null
)
