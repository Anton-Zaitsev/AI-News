package zaitsev.a.d.mirea.libapp.passwordEncrypter

import android.util.Base64
import android.util.Log
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject


class PasswordEncryptedImp @Inject constructor() : PasswordEncrypted {

    companion object {
        private const val ALGORITHM_FACTORY = "PBKDF2WithHmacSHA1"
        private const val ALGORITHM_KEY_SPEC = "AES"
        private const val TRANSFORMATION = "AES/CBC/PKCS7Padding"
    }
    override fun decryptPassword(password: String, secretKey: SecretKey): String? {
        return try
        {
            val ivParameterSpec = IvParameterSpec(Base64.decode(secretKey.iv, Base64.DEFAULT))

            val factory = SecretKeyFactory.getInstance(ALGORITHM_FACTORY)
            val spec = PBEKeySpec(secretKey.key.toCharArray(), Base64.decode(secretKey.salt, Base64.DEFAULT), 10000, 256)
            val tmp = factory.generateSecret(spec)
            val secretKeyParsed = SecretKeySpec(tmp.encoded, ALGORITHM_KEY_SPEC)

            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.DECRYPT_MODE, secretKeyParsed, ivParameterSpec)
            String(cipher.doFinal(Base64.decode(password, Base64.DEFAULT)))
        }
        catch (e : Exception) {
            Log.d("PasswordEncryptedImp","Error while decrypting: $e", e)
            null
        }
    }

    override fun encryptPassword(password: String, secretKey: SecretKey): String? {
        return try
        {
            val ivParameterSpec = IvParameterSpec(Base64.decode(secretKey.iv, Base64.DEFAULT))
            val factory = SecretKeyFactory.getInstance(ALGORITHM_FACTORY)
            val spec = PBEKeySpec(secretKey.key.toCharArray(), Base64.decode(secretKey.salt, Base64.DEFAULT), 10000, 256)
            val tmp = factory.generateSecret(spec)
            val secretKeyParsed = SecretKeySpec(tmp.encoded, ALGORITHM_KEY_SPEC)

            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, secretKeyParsed, ivParameterSpec)
            Base64.encodeToString(cipher.doFinal(password.toByteArray(Charsets.UTF_8)), Base64.DEFAULT)
        }
        catch (e: Exception)
        {
            Log.d("PasswordEncryptedImp","Error while encrypting: $e", e)
            null
        }
    }
}