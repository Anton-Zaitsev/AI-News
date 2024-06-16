package zaitsev.a.d.mirea.rsa

import java.nio.charset.StandardCharsets
import java.security.Key
import java.security.KeyFactory
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.Base64
import javax.crypto.Cipher
import javax.inject.Inject

class RSAImp @Inject constructor(): RSA {

    companion object{
        private const val RSA_ALGORITHM = "RSA"
        private const val CIPHER_TYPE_FOR_RSA = "RSA/ECB/PKCS1Padding"
        private const val SIZE_RSA_ALGORITHM = 2048
    }

    private val cipher = Cipher.getInstance(CIPHER_TYPE_FOR_RSA)
    private val keyFactory: KeyFactory = KeyFactory.getInstance(RSA_ALGORITHM)
    private val keyPairGenerator: KeyPairGenerator = KeyPairGenerator.getInstance(RSA_ALGORITHM)

    init {
        keyPairGenerator.initialize(SIZE_RSA_ALGORITHM)
    }
    override fun encrypt(text: String, publicKeyUser: String): String? {
        val publicKey = generatePublicKey(publicKeyUser) ?: return null
        return try {
            cipher.init(Cipher.ENCRYPT_MODE, publicKey)
            val encryptedBytes = cipher.doFinal(text.toByteArray(StandardCharsets.UTF_8))
            return String(Base64.getEncoder().encode(encryptedBytes))
        }catch (e: Exception){
            //Log.d("RSA", "При шифровании возникло исключение ${e.message}")
            null
        }
    }

    override fun decrypt(encryptedText: String, myPrivateKey: String): String? {
        val privateKey = generatePrivateKey(myPrivateKey) ?: return null
        return try {
            cipher.init(Cipher.DECRYPT_MODE, privateKey)
            val decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedText))
            String(decryptedBytes)
        }catch (e: Exception){
            //Log.d("RSA", "При расшифровании возникло исключение ${e.message}")
            null
        }
    }

    override fun generateKeyPair(): KeyPair {
        val keyPair = keyPairGenerator.generateKeyPair()
        val publicKey = keyToEncodedString(keyPair.public)
        val privateKey = keyToEncodedString(keyPair.private)
        return KeyPair(
            privateKey = privateKey,
            publicKey = publicKey
        )
    }

    private fun generatePrivateKey(privateStr: String): PrivateKey? {
        return try {
            val privateKeySpec = PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateStr))
            keyFactory.generatePrivate(privateKeySpec)
        }catch (e: Exception){
            //Log.d("RSA", "При генерации приватного ключа возникло исключение ${e.message}")
            null
        }
    }

    private fun generatePublicKey(publicStr: String): PublicKey? {
        return try {
            val pubKeySpec = X509EncodedKeySpec(Base64.getDecoder().decode(publicStr))
            keyFactory.generatePublic(pubKeySpec)
        }catch (e: Exception){
            //Log.d("RSA", "При генерации публичного ключа возникло исключение ${e.message}")
            null
        }
    }

    private fun keyToEncodedString(key: Key): String {
        return String(Base64.getEncoder().encode(key.encoded))
    }

}