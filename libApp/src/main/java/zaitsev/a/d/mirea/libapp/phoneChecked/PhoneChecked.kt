package zaitsev.a.d.mirea.libapp.phoneChecked

interface PhoneChecked {
    val maskPhone: String
    fun convertToCurrentNumberPhone(numberPhone: String): String
    fun isPhoneValid(numberPhone: String): Boolean
}