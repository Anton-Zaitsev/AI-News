package zaitsev.a.d.mirea.rss.service

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET

interface NewYorkTimesService {
    @GET("services/xml/rss/nyt/World.xml")
    fun getNewsWorldDefault(): Call<ResponseBody>

    @GET("services/xml/rss/nyt/Business.xml")
    fun getNewsBusinessDefault(): Call<ResponseBody>

    @GET("services/xml/rss/nyt/Technology.xml")
    fun getNewsTechnologyDefault(): Call<ResponseBody>
    @GET("services/xml/rss/nyt/Sports.xml")
    fun getNewsSportsDefault(): Call<ResponseBody>
}