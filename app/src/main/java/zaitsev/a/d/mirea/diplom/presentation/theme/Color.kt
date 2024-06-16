package zaitsev.a.d.mirea.diplom.presentation.theme

import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import zaitsev.a.d.mirea.diplom.presentation.color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

val ColorBlueLight = "#007AFF".color
val ColorBlueDark = "#0B84FF".color
val ColorTrackUncheckSwitchDark = "#39393D".color
val ColorTrackUncheckSwitchLight = "#E9E9EB".color
val ColorTrackCheckSwitch = "#31D158".color


val ColorLightBlueLight = "#D7E3EA".color
val ColorLightBlueDarkLight = "#068C94".color

val ColorLightBlueDark = "#041617".color
val ColorLightDarkDark = "#38C4EC".color

private val BGColorChatLightOut = "#CDE4FF".color
private val BGColorChatDarkOut = "#454648".color

private val BGColorChatLightInc = "#d8dae4".color
private val BGColorChatDarkInc = "#2C2D2F".color

fun getBGColorChatOut(darkTheme: Boolean) = if (darkTheme) BGColorChatDarkOut else BGColorChatLightOut
fun getBGColorChatInc(darkTheme: Boolean) = if (darkTheme) BGColorChatDarkInc else BGColorChatLightInc
fun getDateColorChat(darkTheme: Boolean) = if (darkTheme) Color.White else ColorBlueLight

val BGCardColor = "#1A202B".color

val Orange = "#FF9A02".color

val navigationColorTextButton @Composable get() = ButtonDefaults.textButtonColors(
    contentColor = MaterialTheme.colorScheme.primary,
    disabledContentColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.65f)
)

val defaultTextEditColor @Composable get() = TextFieldDefaults.colors(
    focusedContainerColor = MaterialTheme.colorScheme.primary,
    unfocusedContainerColor = MaterialTheme.colorScheme.primary,
    disabledContainerColor = MaterialTheme.colorScheme.primary,
    errorContainerColor = MaterialTheme.colorScheme.primary,
    focusedIndicatorColor = Color.Transparent,
    unfocusedIndicatorColor = Color.Transparent,
    disabledIndicatorColor = Color.Transparent,
    errorIndicatorColor = Color.Transparent,

    unfocusedTextColor = Color.White,
    focusedTextColor = Color.White,
    disabledTextColor = Color.White,
    errorTextColor = Color.White,

    focusedLabelColor = Color.White,
    unfocusedLabelColor = Color.White,
    errorLabelColor = Color.White,

    focusedSuffixColor = MaterialTheme.colorScheme.onSecondary,
    unfocusedSuffixColor = MaterialTheme.colorScheme.onSecondary,
    cursorColor = MaterialTheme.colorScheme.onSecondary
)

val defaultTextEditColorOutline @Composable get() = OutlinedTextFieldDefaults.colors(
    unfocusedTextColor = MaterialTheme.colorScheme.onSecondary,
    focusedTextColor = MaterialTheme.colorScheme.onSecondary,
    disabledTextColor = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.7f),
    errorTextColor = MaterialTheme.colorScheme.onSecondary,

    focusedBorderColor = MaterialTheme.colorScheme.primary,
    unfocusedBorderColor = MaterialTheme.colorScheme.primary,
    disabledBorderColor = MaterialTheme.colorScheme.primary,
    errorBorderColor = Color.Red,

    focusedSuffixColor = MaterialTheme.colorScheme.onSecondary,
    unfocusedSuffixColor = MaterialTheme.colorScheme.onSecondary,
    cursorColor = MaterialTheme.colorScheme.onSecondary
)