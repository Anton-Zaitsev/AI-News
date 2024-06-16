package zaitsev.a.d.mirea.rss.service

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET

interface TassService {
    @GET("/rss/v2.xml")
    fun getNewsDefault(): Call<ResponseBody>
}