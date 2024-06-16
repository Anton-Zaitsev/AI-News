package zaitsev.a.d.mirea.diplom.di.newsModule

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import zaitsev.a.d.mirea.diplom.constApp.Constants
import zaitsev.a.d.mirea.diplom.di.NetworkModule
import zaitsev.a.d.mirea.diplom.secret.Secrets
import zaitsev.a.d.mirea.rss.service.GoogleNewsService
import javax.inject.Qualifier


@Module(includes = [NetworkModule::class])
@InstallIn(SingletonComponent::class)
class GoogleNewsModule {

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class GoogleNewsRetrofit

    @Provides
    @GoogleNewsRetrofit
    fun provideGoogleNewsRetrofit(@NetworkModule.DefaultClient googleNewsClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .client(googleNewsClient)
            .baseUrl(Secrets().getGoogleNewsUrl(Constants.ZAITSEV_NEWS_PACKAGE_NAME))
            .build()
    }

    @Provides
    fun provideRetrofitGoogleNewsService(@GoogleNewsRetrofit retrofit: Retrofit): GoogleNewsService =
        retrofit.create(GoogleNewsService::class.java)
}