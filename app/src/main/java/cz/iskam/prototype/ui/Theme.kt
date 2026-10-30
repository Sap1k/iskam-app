package cz.iskam.prototype.ui

import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Same green M3 palette as the HTML prototype's :root tokens
private val LightColors = lightColorScheme(
    primary = Color(0xFF2C6A3D), onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFB0F1BA), onPrimaryContainer = Color(0xFF00210C),
    secondary = Color(0xFF4F6352), onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFD2E8D3), onSecondaryContainer = Color(0xFF0E1F12),
    tertiary = Color(0xFF6B5383), onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFF0DBFF), onTertiaryContainer = Color(0xFF25103B),
    error = Color(0xFFBA1A1A), onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6), onErrorContainer = Color(0xFF410002),
    background = Color(0xFFF7FAF1), onBackground = Color(0xFF181D17),
    surface = Color(0xFFF7FAF1), onSurface = Color(0xFF181D17),
    surfaceVariant = Color(0xFFDDE5DA), onSurfaceVariant = Color(0xFF414940),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFF1F5EB), surfaceContainer = Color(0xFFEBEFE5),
    surfaceContainerHigh = Color(0xFFE5EADF), surfaceContainerHighest = Color(0xFFDFE4D9),
    outline = Color(0xFF717970), outlineVariant = Color(0xFFC1C9BE),
    inverseSurface = Color(0xFF2D322C), inverseOnSurface = Color(0xFFEEF2E8),
    inversePrimary = Color(0xFF95D5A0), surfaceTint = Color(0xFF2C6A3D),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF95D5A0), onPrimary = Color(0xFF003919),
    primaryContainer = Color(0xFF105227), onPrimaryContainer = Color(0xFFB0F1BA),
    secondary = Color(0xFFB6CCB8), onSecondary = Color(0xFF223526),
    secondaryContainer = Color(0xFF384B3A), onSecondaryContainer = Color(0xFFD2E8D3),
    tertiary = Color(0xFFD7BAF0), onTertiary = Color(0xFF3B2551),
    tertiaryContainer = Color(0xFF533B69), onTertiaryContainer = Color(0xFFF0DBFF),
    error = Color(0xFFFFB4AB), onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A), onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF10140F), onBackground = Color(0xFFDFE4D9),
    surface = Color(0xFF10140F), onSurface = Color(0xFFDFE4D9),
    surfaceVariant = Color(0xFF414940), onSurfaceVariant = Color(0xFFC1C9BE),
    surfaceContainerLowest = Color(0xFF0B0F0A),
    surfaceContainerLow = Color(0xFF181D17), surfaceContainer = Color(0xFF1C211B),
    surfaceContainerHigh = Color(0xFF272B25), surfaceContainerHighest = Color(0xFF313630),
    outline = Color(0xFF8B9389), outlineVariant = Color(0xFF414940),
    inverseSurface = Color(0xFFDFE4D9), inverseOnSurface = Color(0xFF2D322C),
    inversePrimary = Color(0xFF2C6A3D), surfaceTint = Color(0xFF95D5A0),
)

/** Colours the M3 scheme has no slot for (the prototype's --warn-c). */
data class ExtraColors(val warnC: Color, val onWarnC: Color)

private val LightExtra = ExtraColors(Color(0xFFFFE08C), Color(0xFF241A00))
private val DarkExtra = ExtraColors(Color(0xFF574500), Color(0xFFFFE08C))

val LocalExtraColors = staticCompositionLocalOf { LightExtra }

@Composable
fun IskamTheme(dark: Boolean, content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalExtraColors provides if (dark) DarkExtra else LightExtra) {
        MaterialExpressiveTheme(
            colorScheme = if (dark) DarkColors else LightColors,
            motionScheme = MotionScheme.expressive(),
            content = content,
        )
    }
}

/** Type scale from the prototype (.h-xl, .h-l, .t-title, .t-body, .t-label, .t-over). */
object T {
    val hxl = TextStyle(fontSize = 34.sp, lineHeight = 36.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = (-0.6).sp)
    val hl = TextStyle(fontSize = 28.sp, lineHeight = 31.sp, fontWeight = FontWeight.Bold, letterSpacing = (-0.4).sp)
    val title = TextStyle(fontSize = 16.5.sp, lineHeight = 22.sp, fontWeight = FontWeight.SemiBold)
    val body = TextStyle(fontSize = 15.sp, lineHeight = 22.sp)
    val label = TextStyle(fontSize = 13.sp, lineHeight = 18.sp)
    val over = TextStyle(fontSize = 12.5.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.9.sp)
}
