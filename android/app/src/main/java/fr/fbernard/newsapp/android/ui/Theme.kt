package fr.fbernard.newsapp.android.ui

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

/** Material 3 theme: the wallpaper colors on Android 12+, a teal scheme before. */
@Composable
fun NewsTheme(content: @Composable () -> Unit) {
    val dark = isSystemInDarkTheme()
    val context = LocalContext.current
    val colorScheme = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
            if (dark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        dark -> darkColorScheme(primary = Color(0xFF4FD8C4), secondary = Color(0xFFB1CCC6))
        else -> lightColorScheme(primary = Color(0xFF006A60), secondary = Color(0xFF4A635F))
    }
    MaterialTheme(colorScheme = colorScheme, content = content)
}
