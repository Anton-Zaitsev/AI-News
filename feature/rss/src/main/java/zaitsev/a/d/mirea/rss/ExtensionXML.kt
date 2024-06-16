package zaitsev.a.d.mirea.rss

import android.webkit.URLUtil
import org.xmlpull.v1.XmlPullParser

internal fun XmlPullParser.readText(): String?{
    return if (this.next() == XmlPullParser.TEXT) {
        val text = this.text
        this.nextTag()
        text
    }
    else null
}

internal fun XmlPullParser.onlyText(): String? {
    return try {
        if (this.next() == XmlPullParser.TEXT) {
            this.text
        } else null
    }
    catch (_: Exception) {
        null
    }
}

internal fun String.isURL(): Boolean {
    return URLUtil.isValidUrl(this)
}