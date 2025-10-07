package me.cameronshaw.amtraker.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Train
import androidx.compose.ui.graphics.vector.ImageVector

enum class Screen(
    val route: String,
    val icon: ImageVector,
    val isTopLevel: Boolean = true,
    val title: String? = null
) {
    Trains("train_screen", Icons.Filled.Train),
    Schedule("schedule_screen", Icons.Filled.Schedule),
    Stations("station_screen", Icons.Filled.Business),
    Settings("settings_dialog", Icons.Filled.Settings, false),
    ScheduleDetail("schedule_detail", Icons.Filled.Info, false)
}

fun String?.routeToScreen() =
    Screen.entries.find { it.route == this?.substringBefore("/") } ?: Screen.Schedule