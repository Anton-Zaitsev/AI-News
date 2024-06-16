package zaitsev.a.d.mirea.diplom.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import zaitsev.a.d.mirea.libapp.passwordEncrypter.PasswordEncrypted
import zaitsev.a.d.mirea.libapp.passwordEncrypter.PasswordEncryptedImp
import zaitsev.a.d.mirea.libapp.phoneChecked.PhoneChecked
import zaitsev.a.d.mirea.libapp.phoneChecked.PhoneCheckedRussianIml
import zaitsev.a.d.mirea.rsa.RSA
import zaitsev.a.d.mirea.rsa.RSAImp
import zaitsev.a.d.mirea.translateml.ml.LanguageML
import zaitsev.a.d.mirea.translateml.ml.LanguageMLImp

@Module
@InstallIn(ViewModelComponent::class)
interface BindModule {
    @Binds
    fun bindLanguageML(languageMLImp: LanguageMLImp): LanguageML
    @Binds
    fun bindPasswordEncrypted(passwordEncryptedImp: PasswordEncryptedImp): PasswordEncrypted
    @Binds
    fun bindPhoneChecked(phoneCheckedRussianIml: PhoneCheckedRussianIml): PhoneChecked
    @Binds
    fun bindRSA(rsaImp: RSAImp): RSA
}