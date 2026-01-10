package me.cameronshaw.arrivo.widget.theme

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.glance.GlanceTheme
import androidx.glance.color.ColorProvider
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

fun ColorProvider.withAlpha(context: Context, alpha: Float = 0.75f): ColorProvider {
    val lightConfig = Configuration(context.resources.configuration).apply {
        uiMode = (uiMode and Configuration.UI_MODE_NIGHT_MASK.inv()) or Configuration.UI_MODE_NIGHT_NO
    }
    val lightContext = context.createConfigurationContext(lightConfig)
    val lightColor = this.getColor(lightContext).copy(alpha = alpha)

    val darkConfig = Configuration(context.resources.configuration).apply {
        uiMode = (uiMode and Configuration.UI_MODE_NIGHT_MASK.inv()) or Configuration.UI_MODE_NIGHT_YES
    }
    val darkContext = context.createConfigurationContext(darkConfig)
    val darkColor = this.getColor(darkContext).copy(alpha = alpha)

    return ColorProvider(day = lightColor, night = darkColor)
}