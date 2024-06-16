package zaitsev.a.d.mirea.diplom.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import zaitsev.a.d.mirea.data.dao.NewsDao
import zaitsev.a.d.mirea.data.dao.RSSChannelDao
import zaitsev.a.d.mirea.data.dao.UserDao
import zaitsev.a.d.mirea.data.repository.AstroBeneRepositoryImp
import zaitsev.a.d.mirea.data.repository.BBCNewsRepositoryIml
import zaitsev.a.d.mirea.data.repository.BankiNewsRepositoryImp
import zaitsev.a.d.mirea.data.repository.GoogleNewsRepositoryImp
import zaitsev.a.d.mirea.data.repository.MailNewsRepositoryImp
import zaitsev.a.d.mirea.data.repository.NewYorkNewsRepositoryIml
import zaitsev.a.d.mirea.data.repository.RSSChannelRepositoryIml
import zaitsev.a.d.mirea.data.repository.TassRepositoryImp
import zaitsev.a.d.mirea.diplom.db.NewsLocalRepository
import zaitsev.a.d.mirea.diplom.db.RssLocalChannelRepository
import zaitsev.a.d.mirea.diplom.db.SignRepository
import zaitsev.a.d.mirea.diplom.db.UserRepository
import zaitsev.a.d.mirea.domain.models.repository.rss.AstroBeneRepository
import zaitsev.a.d.mirea.domain.models.repository.rss.BBCNewsRepository
import zaitsev.a.d.mirea.domain.models.repository.rss.BankiNewsRepository
import zaitsev.a.d.mirea.domain.models.repository.rss.GoogleNewsRepository
import zaitsev.a.d.mirea.domain.models.repository.rss.MailNewsRepository
import zaitsev.a.d.mirea.domain.models.repository.rss.NewYorkNewsRepository
import zaitsev.a.d.mirea.domain.models.repository.rss.RSSChannelRepository
import zaitsev.a.d.mirea.domain.models.repository.rss.TassRepository
import zaitsev.a.d.mirea.libapp.mediaContent.MediaAvatarContent
import zaitsev.a.d.mirea.libapp.passwordEncrypter.PasswordEncrypted
import zaitsev.a.d.mirea.rss.repository.AstroBeneNewsParser
import zaitsev.a.d.mirea.rss.repository.BBCWorldNewsParser
import zaitsev.a.d.mirea.rss.repository.BankiNewsParser
import zaitsev.a.d.mirea.rss.repository.GoogleNewsParser
import zaitsev.a.d.mirea.rss.repository.MailNewsParser
import zaitsev.a.d.mirea.rss.repository.NewYorkTimeNewsParser
import zaitsev.a.d.mirea.rss.repository.ParserRSSChannel
import zaitsev.a.d.mirea.rss.repository.TassParser

@Module(includes = [BindModule::class])
@InstallIn(ViewModelComponent::class)
class DataModule {

    @Provides
    fun provideMediaPhoto(@ApplicationContext appContext: Context, @IoDispatcher ioDispatcher: CoroutineDispatcher) =
        MediaAvatarContent(context = appContext, ioDispatcher = ioDispatcher)

    @Provides
    fun provideSignRepository(userDao: UserDao, passwordEncrypted: PasswordEncrypted): SignRepository = SignRepository(userDao, passwordEncrypted)
    @Provides
    fun provideUserRepository(userDao: UserDao, passwordEncrypted: PasswordEncrypted): UserRepository = UserRepository(userDao) { passwordEncrypted }
    @Provides
    fun provideNewsLocalRepository(userDao: UserDao, newsDao: NewsDao) = NewsLocalRepository(userDao = userDao, newsDao = newsDao)
    @Provides
    fun provideGoogleNewsRepository(googleNewsParser: GoogleNewsParser): GoogleNewsRepository = GoogleNewsRepositoryImp(googleNewsParser)
    @Provides
    fun provideTassRepository(tassParser: TassParser): TassRepository = TassRepositoryImp(tassParser)
    @Provides
    fun provideMailNewsRepository(mailNewsParser: MailNewsParser): MailNewsRepository = MailNewsRepositoryImp(mailNewsParser)
    @Provides
    fun provideBankiNewsRepository(bankiNewsParser: BankiNewsParser): BankiNewsRepository = BankiNewsRepositoryImp(bankiNewsParser)
    @Provides
    fun provideAstroBeneNewsRepository(astroBeneNewsParser: AstroBeneNewsParser): AstroBeneRepository = AstroBeneRepositoryImp(astroBeneNewsParser)
    @Provides
    fun provideBBCNewsRepository(bbcNewsParser: BBCWorldNewsParser): BBCNewsRepository = BBCNewsRepositoryIml(bbcNewsParser)
    @Provides
    fun provideNewYorkNewsRepository(newYorkTimeNewsParser: NewYorkTimeNewsParser): NewYorkNewsRepository = NewYorkNewsRepositoryIml(newYorkTimeNewsParser)

    @Provides
    fun provideRssLocalChannelRepository(userDao: UserDao, rssChannelDao: RSSChannelDao): RssLocalChannelRepository = RssLocalChannelRepository(
        userDao = userDao,
        rssChannelDao = rssChannelDao
    )
    @Provides
    fun provideRssChannelRepository(parser: ParserRSSChannel): RSSChannelRepository = RSSChannelRepositoryIml(parserRSSChannel = parser)
}