package com.example.cognitrix.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.cognitrix.R

private val tint = Color(0xFFF6F5F8)
private val white = Color(0xFFFFFFFF)
private val darkTint = Color(0xFF565E6C)
private val teal = Color(0xFF38AFA8)
private val gray = Color(0xFFB0BEC5)
private val gray2 = Color(0xFF606368)
private val gray3 = Color(0xFF3C4042)
private val black = Color(0xFF000000)
private val lightGray = Color(0xFFF3F4F6)

private val DarkColorScheme = darkColorScheme(
    primary = tint,//
    secondary = white,//
    tertiary = gray,//
    surface = teal,//
    onSurface = tint,
    outline= Color(0xFF66B3FF),

    primaryContainer = gray2,//
    secondaryContainer = lightGray,
    background = gray3
)

private val LightColorScheme = lightColorScheme(
    primary = gray3,//
    secondary = black,//
    tertiary = gray,//
    surface = teal,//
    onSurface = white,
    outline= Color(0xFF0066CC),

    primaryContainer = lightGray,//
    secondaryContainer = lightGray,
    background = white,

)

@Composable
fun CognitrixTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean =  false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    // Define the sourceSansFontFamily with individual font weights.
    val sourceSansFontFamily = FontFamily(
        Font(R.font.source_sans_regular), // Regular
        Font(R.font.source_sans_bold, FontWeight.Bold), // Bold
        Font(R.font.source_sans_extrabold, FontWeight.ExtraBold), // ExtraBold
        Font(R.font.source_sans_light, FontWeight.Light), // Light
        Font(R.font.source_sans_medium, FontWeight.Medium), // Medium
        Font(R.font.source_sans_semibold, FontWeight.SemiBold), // SemiBold
        Font(R.font.source_sans_black, FontWeight.Black), // Black
        Font(R.font.source_sans_extralight, FontWeight.ExtraLight), // ExtraLight
    )

// Updated Typography using individual font weights
    val customTypography = Typography(
        displayLarge = TextStyle(
            fontFamily = sourceSansFontFamily,
            fontWeight = FontWeight.Medium,  // Regular weight
            fontStyle = FontStyle.Normal,
            fontSize = 24.sp // Larger size for display text
        ),
        displayMedium = TextStyle(
            fontFamily = sourceSansFontFamily,
            fontWeight = FontWeight.Medium,  // Regular weight
            fontStyle = FontStyle.Normal,
            fontSize = 20.sp // Medium size for display text
        ),
        displaySmall = TextStyle(
            fontFamily = sourceSansFontFamily,
            fontWeight = FontWeight.Medium,  // Regular weight
            fontStyle = FontStyle.Normal,
            fontSize = 18.sp // Small size for display text
        ),
        headlineLarge = TextStyle(
            fontFamily = sourceSansFontFamily,
            fontWeight = FontWeight.Bold,  // Bold weight
            fontStyle = FontStyle.Normal,
            fontSize = 28.sp // Larger size for headlines
        ),
        headlineMedium = TextStyle(
            fontFamily = sourceSansFontFamily,
            fontWeight = FontWeight.Bold,  // Bold weight
            fontStyle = FontStyle.Normal,
            fontSize = 24.sp // Medium size for headlines
        ),
        headlineSmall = TextStyle(
            fontFamily = sourceSansFontFamily,
            fontWeight = FontWeight.Bold,  // Bold weight
            fontStyle = FontStyle.Normal,
            fontSize = 20.sp // Smaller size for headlines
        ),
        titleLarge = TextStyle(
            fontFamily = sourceSansFontFamily,
            fontWeight = FontWeight.SemiBold,  // SemiBold weight
            fontStyle = FontStyle.Normal,
            fontSize = 24.sp // Title text large
        ),
        titleMedium = TextStyle(
            fontFamily = sourceSansFontFamily,
            fontWeight = FontWeight.SemiBold,  // SemiBold weight
            fontStyle = FontStyle.Normal,
            fontSize = 20.sp // Title text medium
        ),
        titleSmall = TextStyle(
            fontFamily = sourceSansFontFamily,
            fontWeight = FontWeight.SemiBold,  // SemiBold weight
            fontStyle = FontStyle.Normal,
            fontSize = 16.sp // Title text small
        ),
        bodyLarge = TextStyle(
            fontFamily = sourceSansFontFamily,
            fontWeight = FontWeight.Normal,  // Regular weight
            fontStyle = FontStyle.Normal,
            fontSize = 20.sp // Body text large
        ),
        bodyMedium = TextStyle(
            fontFamily = sourceSansFontFamily,
            fontWeight = FontWeight.Normal,  // Regular weight
            fontStyle = FontStyle.Normal,
            fontSize = 18.sp // Body text medium
        ),
        bodySmall = TextStyle(
            fontFamily = sourceSansFontFamily,
            fontWeight = FontWeight.Normal,  // Regular weight
            fontStyle = FontStyle.Normal,
            fontSize = 16.sp // Body text small
        ),
        labelLarge = TextStyle(
            fontFamily = sourceSansFontFamily,
            fontWeight = FontWeight.Normal,  // Regular weight
            fontStyle = FontStyle.Normal,
            fontSize = 14.sp // Label text large
        ),
        labelMedium = TextStyle(
            fontFamily = sourceSansFontFamily,
            fontWeight = FontWeight.Normal,  // Regular weight
            fontStyle = FontStyle.Normal,
            fontSize = 12.sp // Label text medium
        ),
        labelSmall = TextStyle(
            fontFamily = sourceSansFontFamily,
            fontWeight = FontWeight.Normal,  // Regular weight
            fontStyle = FontStyle.Normal,
            fontSize = 10.sp // Label text small
        )
    )


    MaterialTheme(
        colorScheme = colorScheme,
        typography = customTypography,
        content = content
    )
}