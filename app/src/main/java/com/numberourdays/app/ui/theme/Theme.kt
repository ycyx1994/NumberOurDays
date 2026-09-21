package com.numberourdays.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** 与旧版 XML 按钮一致：统一 8dp 圆角 */
val NodButtonShape = RoundedCornerShape(8.dp)
val NodFieldShape = RoundedCornerShape(8.dp)

val N0 = Color(0xFFFAF7F2)
val N50 = Color(0xFFF3EEE6)
val N100 = Color(0xFFE8E1D6)
val N300 = Color(0xFFC9C0B2)
val N500 = Color(0xFF8A8175)
val N700 = Color(0xFF4A453E)
val N900 = Color(0xFF1C1916)
val Accent = Color(0xFFD4553A)
val ErrorRed = Color(0xFFB33A3A)

private val LightColors = lightColorScheme(
    primary = Accent,
    onPrimary = N0,
    primaryContainer = Accent,
    onPrimaryContainer = N0,
    secondary = Accent,
    onSecondary = N0,
    background = N0,
    onBackground = N900,
    surface = N0,
    onSurface = N900,
    surfaceVariant = N50,
    onSurfaceVariant = N500,
    surfaceContainer = N50,
    surfaceContainerHigh = N100,
    outline = N300,
    outlineVariant = N100,
    error = ErrorRed,
    onError = N0,
)

private val DarkColors = darkColorScheme(
    primary = Accent,
    onPrimary = N0,
    background = N900,
    onBackground = N0,
    surface = N900,
    onSurface = N0,
    surfaceVariant = N700,
    onSurfaceVariant = N300,
    outline = N500,
    error = ErrorRed,
)

private val NodTypography = Typography(
    displayLarge = TextStyle(fontSize = 64.sp, fontWeight = FontWeight.Bold),
    titleLarge = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold),
    titleMedium = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold),
    bodyLarge = TextStyle(fontSize = 15.sp),
    bodyMedium = TextStyle(fontSize = 14.sp),
    bodySmall = TextStyle(fontSize = 12.sp),
    labelLarge = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold),
    labelMedium = TextStyle(fontSize = 13.sp),
    labelSmall = TextStyle(fontSize = 11.sp),
)

private val NodShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(8.dp),
    large = RoundedCornerShape(12.dp),
    extraLarge = RoundedCornerShape(16.dp),
)

@Composable
fun NumberOurDaysTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = NodTypography,
        shapes = NodShapes,
        content = content,
    )
}
