package me.cameronshaw.amtraker.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Train
import androidx.compose.ui.graphics.vector.ImageVector

enum class Screen(val route: String, val icon: ImageVector) {
    Trains("train_screen", Icons.Filled.Train),
    Schedule("schedule_screen", Icons.Filled.Schedule),
    Stations("station_screen", Icons.Filled.Business),
    SETTINGS("settings_dialog", Icons.Filled.Settings)
}