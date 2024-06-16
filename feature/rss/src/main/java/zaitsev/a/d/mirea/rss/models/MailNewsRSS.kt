package zaitsev.a.d.mirea.rss.models

data class MailNewsRSS(
    var title: String? = null,
    var link: String? = null,
    var pubDate: String? = null,
    var description: String? = null,
    var category: String? = null
)
