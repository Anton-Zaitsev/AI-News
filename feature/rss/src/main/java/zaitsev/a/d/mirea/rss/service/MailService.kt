package zaitsev.a.d.mirea.rss.service

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET

interface MailService {
    @GET("/rss")
    fun getNewsDefault(): Call<ResponseBody>
}