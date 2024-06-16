package zaitsev.a.d.mirea.rss.service

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET

interface BBCWorldNewsService {
    @GET("news/world/rss.xml")
    fun getNewsWorldDefault(): Call<ResponseBody>

    @GET("news/education/rss.xml")
    fun getNewsEducationDefault(): Call<ResponseBody>

    @GET("news/technology/rss.xml")
    fun getNewsTechnologyDefault(): Call<ResponseBody>

    @GET("news/politics/rss.xml")
    fun getNewsPoliticsDefault(): Call<ResponseBody>

    @GET("news/health/rss.xml")
    fun getNewsHealthDefault(): Call<ResponseBody>
}