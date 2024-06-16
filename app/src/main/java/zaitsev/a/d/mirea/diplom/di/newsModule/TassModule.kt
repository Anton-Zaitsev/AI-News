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
import zaitsev.a.d.mirea.rss.service.TassService
import javax.inject.Qualifier

@Module(includes = [NetworkModule::class])
@InstallIn(SingletonComponent::class)
class TassModule {

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class TassRetrofit


    @Provides
    @TassRetrofit
    fun provideTassRetrofit(@NetworkModule.DefaultClient tassClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .client(tassClient)
            .baseUrl(Secrets().getTassUrl(Constants.ZAITSEV_NEWS_PACKAGE_NAME))
            .build()
    }

    @Provides
    fun provideRetrofitTassService(@TassRetrofit retrofit: Retrofit): TassService =
        retrofit.create(TassService::class.java)
}