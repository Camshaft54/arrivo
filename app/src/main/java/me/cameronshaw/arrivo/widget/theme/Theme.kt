package me.cameronshaw.arrivo.widget.theme

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.glance.GlanceTheme
import androidx.glance.color.DynamicThemeColorProviders
import androidx.glance.material3.ColorProviders
import androidx.glance.unit.ColorProvider
import me.cameronshaw.arrivo.ui.theme.darkScheme as appDarkScheme
import me.cameronshaw.arrivo.ui.theme.lightScheme as appLightScheme

@Composable
fun GlanceArrivoTheme(
    overrideSystemTheme: Boolean,
    darkTheme: Boolean,
    content: @Composable () -> Unit
) {
    GlanceTheme(
        colors = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !overrideSystemTheme) {
            DynamicThemeColorProviders
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

@SuppressLint("RestrictedApi")
fun ColorProvider.withAlpha(context: Context) = ColorProvider(this.getColor(context).copy(alpha = 0.75f))