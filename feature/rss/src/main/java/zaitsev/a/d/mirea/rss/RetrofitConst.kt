package zaitsev.a.d.mirea.rss

import okhttp3.ResponseBody

internal object RetrofitConst {

    private const val DOCTYPE = "<!DOCTYPE html>"
    internal fun getError(bodyError: ResponseBody?, errorCode: Int, defaultError: String): String{
        bodyError.let { errorServer ->
            return if (errorServer != null){
                val errorResponse = errorServer.string()
                return if (errorResponse.contains(DOCTYPE,true))
                    "$defaultError, код: $errorCode"
                else
                    if (errorResponse.isNotEmpty())
                        errorResponse
                    else
                        "$defaultError, код: $errorCode"
            }
            else
                "$defaultError, код: $errorCode"
        }
    }
}