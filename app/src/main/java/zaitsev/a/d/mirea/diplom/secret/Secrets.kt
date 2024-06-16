package zaitsev.a.d.mirea.diplom.secret

class Secrets {
    companion object {
        private const val LIB_NAME = "secrets"
        init {
            System.loadLibrary(LIB_NAME)
        }
    }
    external fun getIV(packageName: String): String

    external fun getSalt(packageName: String): String

    external fun getSecretKey(packageName: String): String

    external fun getGoogleNewsUrl(packageName: String): String

    external fun getTassUrl(packageName: String): String

    external fun getMainNewsUrl(packageName: String): String

    external fun getBankiNewsUrl(packageName: String): String

    external fun getAstroBeneNewsUrl(packageName: String): String

    external fun getBBCNewsUrl(packageName: String): String

    external fun getNewYorkTimesNewsUrl(packageName: String): String
}