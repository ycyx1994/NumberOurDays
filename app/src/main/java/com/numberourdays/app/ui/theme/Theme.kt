package com.numberourdays.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.numberourdays.app.R

/** Controls: inputs, buttons — 8dp */
val NodButtonShape = RoundedCornerShape(8.dp)
val NodFieldShape = RoundedCornerShape(8.dp)

/** Cards: home hero, detail hero, drag-lift — 12dp */
val NodCardShape = RoundedCornerShape(12.dp)

/** FAB locked shape (do not rely on ROM defaults) */
val NodFabShape = RoundedCornerShape(16.dp)

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

/**
 * Brand-display number style shared by home carousel + detail hero.
 * Barlow Condensed Bold (OFL) bundled in res/font.
 */
val NodDisplayNumber = TextStyle(
    fontFamily = FontFamily(Font(R.font.barlow_condensed_bold)),
    fontSize = 64.sp,
    fontWeight = FontWeight.Bold,
    letterSpacing = 0.sp,
)

private val NodTypography = Typography(
    displayLarge = NodDisplayNumber,
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
    large = NodCardShape,
    extraLarge = RoundedCornerShape(16.dp),
)

/**
 * v1 locks the paper light theme. Brand DESIGN.md has no dark system;
 * a half-applied M3 dark scheme loses the paper identity.
 */
@Composable
fun NumberOurDaysTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = NodTypography,
        shapes = NodShapes,
        content = content,
    )
}
