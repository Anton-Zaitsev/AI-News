package zaitsev.a.d.mirea.diplom.di

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import zaitsev.a.d.mirea.libapp.hasNetwork
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier


private const val cacheSize = (5 * 1024 * 1024).toLong() // 5 мегабайт
@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class DefaultClient

    @Provides
    fun provideGson(): Gson {
        return GsonBuilder()
            .setLenient()
            .create()
    }

    @Provides
    @DefaultClient
    fun provideApiClient(@ApplicationContext context: Context): OkHttpClient {
        val cache = Cache(context.cacheDir, cacheSize)
        val interceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .cache(cache = cache)
            .addInterceptor(interceptor)
            .addInterceptor { chain ->
                var request = chain.request()
                request = if (hasNetwork(context))
                    request
                        .newBuilder()
                        .cacheControl(
                            CacheControl.Builder()
                                .maxAge(5, TimeUnit.MINUTES)
                                .build()
                        )
                        .build()
                else
                    request
                        .newBuilder()
                        .cacheControl(
                            CacheControl.Builder()
                                .maxStale(12, TimeUnit.HOURS)
                                .build()
                        )
                        .build()
                chain.proceed(request)
            }
            .build()
    }

}