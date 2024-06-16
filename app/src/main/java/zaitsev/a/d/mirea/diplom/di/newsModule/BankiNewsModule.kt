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
import zaitsev.a.d.mirea.rss.service.BankiNewsService
import javax.inject.Qualifier

@Module(includes = [NetworkModule::class])
@InstallIn(SingletonComponent::class)
class BankiNewsModule {

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class BankiNewsRetrofit

    @Provides
    @BankiNewsRetrofit
    fun provideBankiNewsRetrofit(@NetworkModule.DefaultClient bankiNewsClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .client(bankiNewsClient)
            .baseUrl(Secrets().getBankiNewsUrl(Constants.ZAITSEV_NEWS_PACKAGE_NAME))
            .build()
    }

    @Provides
    fun provideRetrofitBankiNewsService(@BankiNewsRetrofit retrofit: Retrofit): BankiNewsService =
        retrofit.create(BankiNewsService::class.java)

}