package zaitsev.a.d.mirea.rss.service

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleNewsService {
    @GET("/rss")
    fun getNewsDefault(
        @Query("hl") hl: String,
        @Query("gl") gl: String,
        @Query("ceid") ceid: String
    ): Call<ResponseBody>


    @GET("/rss/search")
    fun searchNews(
        @Query("q") query: String,
        @Query("hl") hl: String,
        @Query("gl") gl: String,
        @Query("ceid") ceid: String
    ): Call<ResponseBody>
}