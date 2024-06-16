package zaitsevnews

class Secrets {

    // Method calls will be added by gradle task hideSecret
    // Example : external fun getWellHiddenSecret(packageName: String): String

    companion object {
        init {
            System.loadLibrary("secrets")
        }
    }

    external fun getGoogleNewsUrl(packageName: String): String

    external fun getGoogleNewsUrl(packageName: String): String

    external fun getGoogleNewsUrl(packageName: String): String

    external fun getGoogleNewsUrl(packageName: String): String

    external fun getIV(packageName: String): String

    external fun getSalt(packageName: String): String

    external fun getSecretKey(packageName: String): String

    external fun getGoogleNewsUrl(packageName: String): String

    external fun getGoogleNewsUrl(packageName: String): String

    external fun gettestDatal(packageName: String): String

    external fun getGoogleNewsUrl(packageName: String): String

    external fun getTassUrl(packageName: String): String

    external fun getMainNewsUrl(packageName: String): String

    external fun getBankiNewsUrl(packageName: String): String

    external fun getAstroBeneNewsUrl(packageName: String): String
}