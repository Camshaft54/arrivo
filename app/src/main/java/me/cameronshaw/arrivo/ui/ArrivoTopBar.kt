package me.cameronshaw.arrivo.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import me.cameronshaw.arrivo.R
import me.cameronshaw.arrivo.ui.util.toUiString
import java.time.OffsetDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArrivoTopBar(
    navController: NavController,
    trainsLastUpdated: OffsetDateTime,
    onSettingsClick: () -> Unit,
    onHelpClick: () -> Unit
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = navBackStackEntry?.destination?.route.routeToScreen()
    val canNavigateUp = navController.previousBackStackEntry != null
    val showLastUpdated = currentScreen.isTopLevel


    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        title = {
            Column {
                val titleText = currentScreen.title ?: stringResource(R.string.app_name)
                Text(titleText)
                if (showLastUpdated) {
                    Text(
                        text = "${stringResource(R.string.last_updated)} ${trainsLastUpdated.toUiString()}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                }
            }
        },
        navigationIcon = {
            if (canNavigateUp && !currentScreen.isTopLevel) {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        },
        actions = {
            IconButton(onClick = onHelpClick) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Help"
                )
            }
            IconButton(onClick = onSettingsClick) {
                Icon(
                    imageVector = Screen.Settings.icon,
                    contentDescription = Screen.Settings.name
                )
            }
        }
    )
}
