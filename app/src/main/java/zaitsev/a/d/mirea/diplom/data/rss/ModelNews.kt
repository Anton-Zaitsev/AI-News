package zaitsev.a.d.mirea.diplom.data.rss

import com.google.gson.annotations.Expose
import zaitsev.a.d.mirea.diplom.presentation.getGsonDateLong
import java.net.URL
import java.util.Date

sealed class ModelNews(
    @Expose(serialize = false, deserialize = false)
    @Transient
    override val id: Long,
    @Expose(serialize = false, deserialize = false)
    @Transient
    override val isLocal: Boolean,
    @Expose
    @Transient
    override val title: String,
    @Expose
    @Transient
    override val description: String,
    @Expose
    @Transient
    override val date: Date,
    @Expose
    @Transient
    override val linq: URL,
    @Expose
    @Transient
    override val language: String
): ModelNewsImp


val ModelNews.getGsonScheme get() = when(this){
    is AstroBeneNews -> this.toString()
    is BankiNews -> this.toString()
    is GoogleNews -> this.toString()
    is MailNews -> this.toString()
    is TassNews -> this.toString()
    is BBCNews -> this.toString()
    is NewYorkNews -> this.toString()
}
fun String.classTypeToNews(value: String): ModelNews?{
    return try {
        when(this){
            AstroBeneNews::class.simpleName  -> {
                getGsonDateLong().fromJson(value, AstroBeneNews::class.java)
            }
            BankiNews::class.simpleName -> {
                getGsonDateLong().fromJson(value, BankiNews::class.java)
            }
            GoogleNews::class.simpleName -> {
                getGsonDateLong().fromJson(value, GoogleNews::class.java)
            }
            MailNews::class.simpleName -> {
                getGsonDateLong().fromJson(value, MailNews::class.java)
            }
            TassNews::class.simpleName -> {
                getGsonDateLong().fromJson(value, TassNews::class.java)
            }
            BBCNews::class.simpleName -> {
                getGsonDateLong().fromJson(value, BBCNews::class.java)
            }
            NewYorkNews::class.simpleName -> {
                getGsonDateLong().fromJson(value, NewYorkNews::class.java)
            }
            else -> null
        }
    }catch (_: Exception){ null }
}