package zaitsev.a.d.mirea.rss.models

data class BankiNewsRSS(
    var title: String? = null,
    var link: String? = null,
    var pubDate: String? = null,
    var description: String? = null,
    var bankName: String? = null,
    var synopsis: String? = null
)
