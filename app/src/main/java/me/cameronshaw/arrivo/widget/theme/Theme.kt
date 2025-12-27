package me.cameronshaw.arrivo.widget.theme

import android.os.Build
import androidx.compose.runtime.Composable
import androidx.glance.GlanceTheme
import androidx.glance.material3.ColorProviders
import androidx.compose.material3.darkColorScheme as m3DarkColorScheme
import androidx.compose.material3.lightColorScheme as m3LightColorScheme
import me.cameronshaw.arrivo.ui.theme.darkScheme as appDarkScheme
import me.cameronshaw.arrivo.ui.theme.lightScheme as appLightScheme

@Composable
fun GlanceArrivoTheme(
    overrideSystemTheme: Boolean,
    darkTheme: Boolean,
    content: @Composable () -> Unit
) {
    GlanceTheme(
        colors = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ColorProviders(
                light = if (darkTheme && overrideSystemTheme) m3DarkColorScheme() else m3LightColorScheme(),
                dark = if (darkTheme || !overrideSystemTheme) m3DarkColorScheme() else m3LightColorScheme()
            )
        } else {
            ColorProviders(
                light = if (darkTheme && overrideSystemTheme) appDarkScheme else appLightScheme,
                dark = if (darkTheme || !overrideSystemTheme) appDarkScheme else appLightScheme
            )
        }
    ) {
        content()
    }
}