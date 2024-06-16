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
import zaitsev.a.d.mirea.rss.service.MailService
import javax.inject.Qualifier

@Module(includes = [NetworkModule::class])
@InstallIn(SingletonComponent::class)
class MailNewsModule {

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class MailNewsRetrofit

    @Provides
    @MailNewsRetrofit
    fun provideMailNewsRetrofit(@NetworkModule.DefaultClient mailNewsClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .client(mailNewsClient)
            .baseUrl(Secrets().getMainNewsUrl(Constants.ZAITSEV_NEWS_PACKAGE_NAME))
            .build()
    }

    @Provides
    fun provideRetrofitMailNewsService(@MailNewsRetrofit retrofit: Retrofit): MailService =
        retrofit.create(MailService::class.java)
}