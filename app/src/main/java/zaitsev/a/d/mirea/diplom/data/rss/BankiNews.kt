package zaitsev.a.d.mirea.diplom.data.rss

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.google.mlkit.nl.translate.TranslateLanguage
import zaitsev.a.d.mirea.diplom.presentation.getGsonDateLong
import java.io.Serializable
import java.net.URL
import java.util.Date


data class BankiNews(
    @Expose(serialize = false, deserialize = false)
    @Transient
    override val id: Long = 0,
    @Expose(serialize = false, deserialize = false)
    @Transient
    override val isLocal: Boolean = false,
    @SerializedName("title")
    override val title: String,
    @SerializedName("description")
    override val description: String,
    @SerializedName("linq")
    override val linq: URL,
    @SerializedName("date")
    override val date: Date,
    @SerializedName("synopsis")
    val synopsis: String,
    @SerializedName("bankName")
    val bankName: String?,
    @SerializedName("language")
    override val language: String = TranslateLanguage.RUSSIAN
): ModelNews(
    id = id,
    isLocal = isLocal,
    title = title,
    description = description,
    date = date,
    linq = linq,
    language = language
), Serializable {
    override fun toString(): String {
        return getGsonDateLong().toJson(this)
    }
}
