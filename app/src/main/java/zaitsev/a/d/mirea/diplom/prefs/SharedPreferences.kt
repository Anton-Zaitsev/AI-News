package zaitsev.a.d.mirea.diplom.prefs

import android.content.Context
import android.content.SharedPreferences

class SharedPreferences(context: Context) {
    companion object {
        private const val PREFS_NAME = "prefs_zaitsev_news"
        const val themeUsing = "themeUsing"
        const val notificationUsing = "notificationUsing"
    }
    private val sharedPref: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveBool(name: String, value: Boolean){
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putBoolean(name, value)
        editor.apply()
    }

    fun getBoolean(name: String): Boolean?{
        return if (contains(name = name))
            sharedPref.getBoolean(name, false)
        else null
    }

    fun contains(name: String): Boolean {
        return sharedPref.contains(name)
    }

    fun clearValue(name: String){
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.remove(name)
        editor.apply()
    }
}