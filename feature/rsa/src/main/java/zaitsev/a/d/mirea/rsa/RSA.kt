package zaitsev.a.d.mirea.rsa

interface RSA {
    fun encrypt(text: String, publicKeyUser: String): String?
    fun decrypt(encryptedText: String, myPrivateKey: String): String?
    fun generateKeyPair(): KeyPair
}