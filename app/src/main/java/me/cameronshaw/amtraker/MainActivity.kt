package me.cameronshaw.amtraker

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dagger.hilt.android.AndroidEntryPoint
import me.cameronshaw.amtraker.ui.Screen
import me.cameronshaw.amtraker.ui.dialogs.settings.SettingsDialog
import me.cameronshaw.amtraker.ui.dialogs.settings.SettingsViewModel
import me.cameronshaw.amtraker.ui.routeToScreen
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
                        onDismissRequest = { showSettingsDialog = false }
                    )
                }

                Scaffold(
                    snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                    topBar = {
                        val navBackStackEntry by navController.currentBackStackEntryAsState()
                        val currentDestination =
                            navBackStackEntry?.destination?.route.routeToScreen()

                        val canNavigateUp = navController.previousBackStackEntry != null

                        TopAppBar(
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            title = {
                                val titleText = currentDestination.title
                                    ?: stringResource(R.string.app_name)
                                Text(titleText)
                            },
                            navigationIcon = {
                                if (canNavigateUp && currentDestination.isTopLevel) {
                                    IconButton(onClick = { navController.navigateUp() }) {
                                        Icon(
                                            Icons.AutoMirrored.Filled.ArrowBack,
                                            contentDescription = "Back"
                                        )
                                    }
                                }
                            },
                            actions = {
                                IconButton(onClick = { showSettingsDialog = true }) {
                                    Icon(
                                        imageVector = Screen.Settings.icon,
                                        contentDescription = Screen.Settings.name
                                    )
                                }
                            }
                        )
                    },
                    bottomBar = {
                        NavigationBar {
                            val navBackStackEntry by navController.currentBackStackEntryAsState()
                            val currentDestination = navBackStackEntry?.destination

                            Screen.entries.filter { it.isTopLevel }.forEach { screen ->
                                NavigationBarItem(
                                    icon = { Icon(screen.icon, contentDescription = screen.name) },
                                    label = { Text(screen.name) },
                                    selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                                    onClick = {
                                        // Is the current screen's graph root the same as the tab we're clicking?
                                        val currentTabRootRoute = currentDestination?.parent?.startDestinationRoute ?: navController.graph.findStartDestination().route
                                        val isSameTab = currentDestination?.hierarchy?.any { it.route == screen.route || (it as? NavGraph)?.startDestinationRoute == screen.route } == true
                                        // TODO: Gemini is trash at figuring out this navigation stuff, I really need to understand it myself and rewrite it :)

                                        if (isSameTab) {
                                            // We are in the same tab hierarchy
                                            if (currentDestination.route != screen.route) {
                                                // And not on the root screen of this tab, so pop to it
                                                Log.d("BottomNav", "Popping to ${screen.route} from ${currentDestination?.route}")
                                                navController.popBackStack(screen.route, inclusive = false, saveState = false)
                                            } else {
                                                // Already on the root of this tab. Do nothing or scroll to top.
                                                Log.d("BottomNav", "Already on root ${screen.route}")
                                            }
                                        } else {
                                            // Different tab, standard navigation
                                            Log.d("BottomNav", "Navigating to different tab ${screen.route}")
                                            navController.navigate(screen.route) {
                                                popUpTo(navController.graph.findStartDestination().id) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        }
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                        selectedTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                        indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
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
                        ) { backStackEntry ->
                            ScheduleDetailScreen(snackbarHostState = snackbarHostState)
                        }
                    }
                }
            }
        }
    }
}