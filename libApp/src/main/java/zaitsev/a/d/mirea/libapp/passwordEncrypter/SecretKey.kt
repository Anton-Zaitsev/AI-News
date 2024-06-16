package zaitsev.a.d.mirea.libapp.passwordEncrypter

data class SecretKey(
    val key: String,
    val salt: String,
    val iv: String
)
