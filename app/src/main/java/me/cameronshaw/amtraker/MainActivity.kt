package me.cameronshaw.amtraker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import me.cameronshaw.amtraker.ui.Screen
import me.cameronshaw.amtraker.ui.dialogs.settings.SettingsDialog
import me.cameronshaw.amtraker.ui.screens.schedule.ScheduleScreen
import me.cameronshaw.amtraker.ui.screens.stations.StationScreen
import me.cameronshaw.amtraker.ui.screens.stations.StationViewModel
import me.cameronshaw.amtraker.ui.screens.trains.TrainScreen
import me.cameronshaw.amtraker.ui.theme.AmtrakerTheme
import me.cameronshaw.amtraker.widget.RefreshWidgetWorker

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AmtrakerTheme {
                val navController = rememberNavController()
                val snackbarHostState = remember { SnackbarHostState() }
                val scope = rememberCoroutineScope()
                var showSettingsDialog by remember { mutableStateOf(false) }

                val viewModel: StationViewModel = hiltViewModel()

                LaunchedEffect(key1 = true) {
                    viewModel.eventFlow.collect { message ->
                        scope.launch {
                            snackbarHostState.showSnackbar(message = message)
                        }
                    }
                }

                if (showSettingsDialog) {
                    SettingsDialog(
                        onDismissRequest = { showSettingsDialog = false }
                    )
                }

                Scaffold(
                    snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                    topBar = {
                        TopAppBar(
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                titleContentColor = MaterialTheme.colorScheme.primary,
                            ),
                            title = { Text("Amtraker") },
                            actions = {
                                IconButton(onClick = { showSettingsDialog = true }) {
                                    Icon(
                                        imageVector = Screen.SETTINGS.icon,
                                        contentDescription = Screen.SETTINGS.name
                                    )
                                }
                            }
                        )
                    },
                    bottomBar = {
                        NavigationBar {
                            val navBackStackEntry by navController.currentBackStackEntryAsState()
                            val currentDestination = navBackStackEntry?.destination

                            Screen.entries.filter { it != Screen.SETTINGS }.forEach { screen ->
                                NavigationBarItem(
                                    icon = { Icon(screen.icon, contentDescription = screen.name) },
                                    label = { Text(screen.name) },
                                    selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                                    onClick = {
                                        navController.navigate(screen.route) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Schedule.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(Screen.Trains.route) {
                            TrainScreen()
                        }

                        composable(Screen.Schedule.route) {
                            ScheduleScreen()
                        }

                        composable(Screen.Stations.route) {
                            StationScreen()
                        }
                    }
                }
            }
        }
    }
}