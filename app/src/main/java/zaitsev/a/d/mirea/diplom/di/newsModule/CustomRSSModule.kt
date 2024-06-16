package zaitsev.a.d.mirea.diplom.di.newsModule

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import zaitsev.a.d.mirea.diplom.di.NetworkModule
import javax.inject.Qualifier

@Module(includes = [NetworkModule::class])
@InstallIn(SingletonComponent::class)
class CustomRSSModule
{
    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class CustomRSSRetrofit

    @Provides
    @CustomRSSRetrofit
    fun provideCustomRetrofit(@NetworkModule.DefaultClient customRetrofit: OkHttpClient): Retrofit {
        return  Retrofit.Builder()
            .baseUrl("https://test.ru/api/")
            .client(customRetrofit)
            .build()
    }
}