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
import zaitsev.a.d.mirea.rss.service.BBCWorldNewsService
import javax.inject.Qualifier

@Module(includes = [NetworkModule::class])
@InstallIn(SingletonComponent::class)
class BBCNewsModule {
    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class BBCNewsRetrofit

    @Provides
    @BBCNewsRetrofit
    fun provideBBCRetrofit(@NetworkModule.DefaultClient bbcClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .client(bbcClient)
            .baseUrl(Secrets().getBBCNewsUrl(Constants.ZAITSEV_NEWS_PACKAGE_NAME))
            .build()
    }

    @Provides
    fun provideRetrofitBBCService(@BBCNewsRetrofit retrofit: Retrofit): BBCWorldNewsService =
        retrofit.create(BBCWorldNewsService::class.java)
}