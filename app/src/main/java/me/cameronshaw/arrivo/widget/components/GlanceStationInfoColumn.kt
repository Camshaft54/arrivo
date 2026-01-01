package me.cameronshaw.arrivo.widget.components

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.size
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import me.cameronshaw.arrivo.R
import me.cameronshaw.arrivo.data.model.Status
import me.cameronshaw.arrivo.ui.theme.StatusBlue
import me.cameronshaw.arrivo.ui.theme.StatusGreen
import me.cameronshaw.arrivo.ui.theme.StatusRed
import me.cameronshaw.arrivo.ui.util.toUiString
import java.time.OffsetDateTime

@SuppressLint("RestrictedApi")
@Composable
fun GlanceStationInfoColumn(
    stationCode: String, description: String, status: Status, time: OffsetDateTime?
) {
    val statusColor = when (status) {
        Status.EARLY -> ColorProvider(StatusBlue)
        Status.ON_TIME -> ColorProvider(StatusGreen)
        Status.LATE -> ColorProvider(StatusRed)
        Status.UNKNOWN -> GlanceTheme.colors.onSurfaceVariant
    }

    Column(
        horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
        verticalAlignment = Alignment.Vertical.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.Vertical.CenterVertically) {
            Text(
                text = stationCode, style = TextStyle(
                    fontWeight = FontWeight.Bold, color = GlanceTheme.colors.onSurfaceVariant
                )
            )
            Spacer(GlanceModifier.size(4.dp))
            Image(
                provider = ImageProvider(R.drawable.ic_circle),
                contentDescription = status.description,
                colorFilter = androidx.glance.ColorFilter.tint(statusColor),
                modifier = GlanceModifier.size(14.dp)
            )
        }
        Text(
            text = description,
            style = TextStyle(fontSize = 12.sp, color = GlanceTheme.colors.onSurfaceVariant)
        )
        Text(
            text = time?.toUiString() ?: "--:--", style = TextStyle(
                fontWeight = FontWeight.Bold, color = GlanceTheme.colors.onSurfaceVariant
            )
        )
    }
}