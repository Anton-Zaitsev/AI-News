package zaitsev.a.d.mirea.diplom.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import zaitsev.a.d.mirea.diplom.di.newsModule.AstroBeneNewsModule
import zaitsev.a.d.mirea.diplom.di.newsModule.BBCNewsModule
import zaitsev.a.d.mirea.diplom.di.newsModule.BankiNewsModule
import zaitsev.a.d.mirea.diplom.di.newsModule.CustomRSSModule
import zaitsev.a.d.mirea.diplom.di.newsModule.GoogleNewsModule
import zaitsev.a.d.mirea.diplom.di.newsModule.NewYorkNewsModule
import zaitsev.a.d.mirea.diplom.di.newsModule.TassModule
import zaitsev.a.d.mirea.rss.parserChannel.ParserRSSChannelImp
import zaitsev.a.d.mirea.rss.parsers.AstroBeneNewsParserImp
import zaitsev.a.d.mirea.rss.parsers.BBCWorldNewsParserIml
import zaitsev.a.d.mirea.rss.parsers.BankiNewsParserImp
import zaitsev.a.d.mirea.rss.parsers.GoogleNewsParserImp
import zaitsev.a.d.mirea.rss.parsers.MailNewsParserImp
import zaitsev.a.d.mirea.rss.parsers.NewYorkTimeNewsParserIml
import zaitsev.a.d.mirea.rss.parsers.TassParserImp
import zaitsev.a.d.mirea.rss.repository.AstroBeneNewsParser
import zaitsev.a.d.mirea.rss.repository.BBCWorldNewsParser
import zaitsev.a.d.mirea.rss.repository.BankiNewsParser
import zaitsev.a.d.mirea.rss.repository.GoogleNewsParser
import zaitsev.a.d.mirea.rss.repository.MailNewsParser
import zaitsev.a.d.mirea.rss.repository.NewYorkTimeNewsParser
import zaitsev.a.d.mirea.rss.repository.ParserRSSChannel
import zaitsev.a.d.mirea.rss.repository.TassParser
import zaitsev.a.d.mirea.rss.service.AstroBeneNewsService
import zaitsev.a.d.mirea.rss.service.BBCWorldNewsService
import zaitsev.a.d.mirea.rss.service.BankiNewsService
import zaitsev.a.d.mirea.rss.service.GoogleNewsService
import zaitsev.a.d.mirea.rss.service.MailService
import zaitsev.a.d.mirea.rss.service.NewYorkTimesService
import zaitsev.a.d.mirea.rss.service.TassService


@Module(includes = [
    DispatcherModule::class,
    GoogleNewsModule::class,
    TassModule::class,
    BankiNewsModule::class,
    AstroBeneNewsModule::class,
    BBCNewsModule::class,
    NewYorkNewsModule::class,
    CustomRSSModule::class
])
@InstallIn(SingletonComponent::class)
class DomainModule {
    @Provides
    fun provideGoogleNewsParser(googleNewsService: GoogleNewsService): GoogleNewsParser =
        GoogleNewsParserImp(googleNewsService = googleNewsService)
    @Provides
    fun provideTassParser(tassService: TassService): TassParser =
        TassParserImp(tassService = tassService,)
    @Provides
    fun provideMailNewsParser(mailService: MailService): MailNewsParser =
        MailNewsParserImp(mailNewsService = mailService)
    @Provides
    fun provideBankiNewsParser(bankiNewsService: BankiNewsService): BankiNewsParser =
        BankiNewsParserImp(bankiNewsService = bankiNewsService)
    @Provides
    fun provideAstroBeneNewsParser(astroBeneNewsService: AstroBeneNewsService): AstroBeneNewsParser =
        AstroBeneNewsParserImp(astroBeneNewsService = astroBeneNewsService)

    @Provides
    fun provideBBCNewsParser(bbcNewsService: BBCWorldNewsService): BBCWorldNewsParser =
        BBCWorldNewsParserIml(bbcWorldNewsParser = bbcNewsService)

    @Provides
    fun provideNewYorkNewsParser(newYorkService: NewYorkTimesService): NewYorkTimeNewsParser =
        NewYorkTimeNewsParserIml(newYorkTimesService = newYorkService)

    @Provides
    fun provideCustomRSSChannelParser(@CustomRSSModule.CustomRSSRetrofit retrofit: Retrofit): ParserRSSChannel =
        ParserRSSChannelImp(retrofit = retrofit)

}