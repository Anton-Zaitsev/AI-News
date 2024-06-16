package zaitsev.a.d.mirea.libapp.passwordEncrypter

interface PasswordEncrypted {
    fun decryptPassword(password: String, secretKey: SecretKey): String?
    fun encryptPassword(password: String, secretKey: SecretKey): String?
}