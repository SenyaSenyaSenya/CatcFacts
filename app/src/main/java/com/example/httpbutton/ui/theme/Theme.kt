package com.example.httpbutton.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

val LightColorPalette = lightColors(
    primary = BorderFacts,
    primaryVariant = BorderDDMFacts,
    secondary = TopBarFacts,
    secondaryVariant = AddText,
    background = CardBackground,
    surface = AddBackground,
    onPrimary = MenuBack,
    onSecondary = MenuButton,
    onBackground = BackgroundFacts,
    onSurface = ButtonMenuBorder,
    error = Facts,
)

val DarkColorPalette = darkColors(
    primary = BorderFacts2,
    primaryVariant = BorderDDMFacts2,
    secondary = TopBarFacts2,
    secondaryVariant = AddText2,
    background = CardBackground2,
    surface = AddBackground2,
    onPrimary = MenuBack2,
    onSecondary = MenuButton2,
    onBackground = BackgroundFacts2,
    onSurface = ButtonMenuBorder2,
    error = Facts2
)

@Composable
fun SwitchTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}