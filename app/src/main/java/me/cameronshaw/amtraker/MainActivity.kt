package me.cameronshaw.amtraker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dagger.hilt.android.AndroidEntryPoint
import me.cameronshaw.amtraker.ui.AmtrakerBottomBar
import me.cameronshaw.amtraker.ui.AmtrakerTopBar
import me.cameronshaw.amtraker.ui.Screen
import me.cameronshaw.amtraker.ui.dialogs.settings.SettingsDialog
import me.cameronshaw.amtraker.ui.dialogs.settings.SettingsViewModel
import me.cameronshaw.amtraker.ui.screens.schedule.ScheduleScreen
import me.cameronshaw.amtraker.ui.screens.scheduledetail.ScheduleDetailScreen
import me.cameronshaw.amtraker.ui.screens.stations.StationScreen
import me.cameronshaw.amtraker.ui.screens.trains.TrainScreen
import me.cameronshaw.amtraker.ui.theme.AmtrakerTheme

const val TRAIN_NUM_ARG = "trainNum"

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val settingsViewModel: SettingsViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val appSettings by settingsViewModel.appSettings.collectAsState()
            val useDarkTheme = when (appSettings.theme) {
                "SYSTEM" -> isSystemInDarkTheme()
                "LIGHT" -> false
                "DARK" -> true
                else -> isSystemInDarkTheme()
            }
            AmtrakerTheme(darkTheme = useDarkTheme) {
                val navController = rememberNavController()
                val snackbarHostState = remember { SnackbarHostState() }
                var showSettingsDialog by remember { mutableStateOf(false) }

                if (showSettingsDialog) {
                    SettingsDialog(
                        onDismissRequest = {
                            @Suppress("AssignedValueIsNeverRead")
                            showSettingsDialog = false
                        }
                    )
                }

                Scaffold(
                    snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                    topBar = {
                        AmtrakerTopBar(navController = navController) {
                            @Suppress("AssignedValueIsNeverRead")
                            showSettingsDialog = true
                        }
                    },
                    bottomBar = { AmtrakerBottomBar(navController = navController) }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Schedule.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(Screen.Trains.route) {
                            TrainScreen(snackbarHostState = snackbarHostState)
                        }

                        composable(Screen.Schedule.route) {
                            ScheduleScreen(
                                snackbarHostState = snackbarHostState,
                                navController = navController
                            )
                        }

                        composable(Screen.Stations.route) {
                            StationScreen(snackbarHostState = snackbarHostState)
                        }

                        composable(
                            route = "${Screen.ScheduleDetail.route}/{$TRAIN_NUM_ARG}",
                            arguments = listOf(navArgument(TRAIN_NUM_ARG) {
                                type = NavType.StringType
                            })
                        ) { _ ->
                            ScheduleDetailScreen(snackbarHostState = snackbarHostState)
                        }
                    }
                }
            }
        }
    }
}