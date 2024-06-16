package zaitsev.a.d.mirea.rss.service

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

internal interface CustomRSSService {
    @GET
    fun getNewsInfo(@Url url: String): Call<ResponseBody>
    @GET
    fun getNewsDefault(@Url url: String): Call<ResponseBody>
}