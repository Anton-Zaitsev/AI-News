package zaitsev.a.d.mirea.diplom.db.dataUI

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class UserUI(
    @SerializedName("id")
    val id: Long = 0,
    @SerializedName("userID")
    val userID: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("lastName")
    val lastName: String,
    @SerializedName("phone")
    val phone: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("avatarURL")
    val avatarURL: String? = null,
): Serializable{
    override fun toString(): String {
        return Gson().toJson(this)
    }
}

