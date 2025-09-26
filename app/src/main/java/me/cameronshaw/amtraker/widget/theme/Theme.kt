package me.cameronshaw.amtraker.widget.theme

import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.glance.GlanceTheme
import androidx.glance.material3.ColorProviders

@Composable
fun GlanceAmtrakerTheme(
    content: @Composable () -> Unit
) {
    GlanceTheme(
        colors = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ColorProviders(
                light = lightColorScheme(),
                dark = darkColorScheme()
            )
        } else {
            // Define fallback colors for older Android versions
            // These are inspired by your app's Theme.kt
            ColorProviders(
                light = me.cameronshaw.amtraker.ui.theme.LightColorScheme,
                dark = me.cameronshaw.amtraker.ui.theme.DarkColorScheme
            )
        }
    ) {
        content()
    }
}