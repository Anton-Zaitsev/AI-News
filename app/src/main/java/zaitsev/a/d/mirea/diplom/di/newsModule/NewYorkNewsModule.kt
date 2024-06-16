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
import zaitsev.a.d.mirea.rss.service.NewYorkTimesService
import javax.inject.Qualifier

@Module(includes = [NetworkModule::class])
@InstallIn(SingletonComponent::class)
class NewYorkNewsModule {
    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class NewYorkNewsRetrofit

    @Provides
    @NewYorkNewsRetrofit
    fun provideBBCRetrofit(@NetworkModule.DefaultClient newYorkTimesClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .client(newYorkTimesClient)
            .baseUrl(Secrets().getNewYorkTimesNewsUrl(Constants.ZAITSEV_NEWS_PACKAGE_NAME))
            .build()
    }

    @Provides
    fun provideRetrofitNYService(@NewYorkNewsRetrofit retrofit: Retrofit): NewYorkTimesService =
        retrofit.create(NewYorkTimesService::class.java)
}