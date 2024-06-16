package zaitsev.a.d.mirea.diplom.presentation.ui.components

import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import zaitsev.a.d.mirea.diplom.presentation.theme.ColorTrackCheckSwitch
import zaitsev.a.d.mirea.diplom.presentation.theme.ColorTrackUncheckSwitchDark
import zaitsev.a.d.mirea.diplom.presentation.theme.ColorTrackUncheckSwitchLight

@Composable
fun SwitchDefault(isDarkTheme: Boolean, checked: Boolean, onCheckedChange: ((Boolean) -> Unit)?){
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        colors = SwitchDefaults.colors(
            checkedThumbColor = Color.White,
            checkedTrackColor = ColorTrackCheckSwitch,
            checkedBorderColor = Color.Transparent,
            uncheckedBorderColor = Color.Transparent,
            uncheckedThumbColor = Color.White,
            uncheckedTrackColor = if (isDarkTheme) ColorTrackUncheckSwitchDark else ColorTrackUncheckSwitchLight
        )
    )
}