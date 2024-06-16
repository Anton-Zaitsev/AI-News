package zaitsev.a.d.mirea.libapp.phoneChecked

import javax.inject.Inject

class PhoneCheckedRussianIml @Inject constructor(): PhoneChecked {
    override val maskPhone: String = "+7 (###)-###-##-##"

    private val specialSymbolsPhone by lazy {
        maskPhone.indices.filter { maskPhone[it] != '#' }
    }

    private val regexPhone = Regex("^9[0-9]{9}$")

    override fun convertToCurrentNumberPhone(numberPhone: String): String {
        var out = ""
        var maskIndex = 0
        val a = specialSymbolsPhone
        numberPhone.forEach { char ->
            while (a.contains(maskIndex)) {
                out += maskPhone[maskIndex]
                maskIndex++
            }
            out += char
            maskIndex++
        }
        return out
    }

    override fun isPhoneValid(numberPhone: String): Boolean {
        val currentNumber = numberPhone.filter { it.isDigit() }
        return regexPhone.matches(currentNumber)
    }
}