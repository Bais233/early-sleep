package com.earlysleep.app.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bedtime
import androidx.compose.material.icons.rounded.Insights
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.earlysleep.app.ui.components.NightBackground
import com.earlysleep.app.ui.screens.AboutScreen
import com.earlysleep.app.ui.screens.HomeScreen
import com.earlysleep.app.ui.screens.RecordsScreen
import com.earlysleep.app.ui.screens.SettingsScreen

private const val ABOUT_ROUTE = "about"

enum class AppDestination(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    Home("home", "今晚", Icons.Rounded.Bedtime),
    Records("records", "记录", Icons.Rounded.Insights),
    Settings("settings", "设置", Icons.Rounded.Settings)
}

@Composable
fun EarlySleepRoot(viewModel: SleepViewModel) {
    val context = LocalContext.current
    val remindersEnabled by viewModel.remindersEnabled.collectAsStateWithLifecycle()
    val checkIns by viewModel.checkInDates.collectAsStateWithLifecycle()
    val scheme = MaterialTheme.colorScheme

    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        NightBackground()

        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                if (currentRoute != ABOUT_ROUTE) {
                    NavigationBar(
                        containerColor = scheme.surface.copy(alpha = 0.96f),
                        tonalElevation = 0.dp
                    ) {
                        AppDestination.entries.forEach { destination ->
                            NavigationBarItem(
                                selected = currentRoute == destination.route,
                                onClick = {
                                    navController.navigate(destination.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = {
                                    Icon(
                                        imageVector = destination.icon,
                                        contentDescription = destination.label,
                                        modifier = Modifier.size(23.dp)
                                    )
                                },
                                label = {
                                    Text(text = destination.label, fontSize = 11.sp)
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = scheme.secondary,
                                    selectedTextColor = scheme.secondary,
                                    indicatorColor = scheme.secondary.copy(alpha = 0.12f),
                                    unselectedIconColor = scheme.onSurfaceVariant,
                                    unselectedTextColor = scheme.onSurfaceVariant
                                )
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = AppDestination.Home.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(AppDestination.Home.route) {
                    HomeScreen(
                        remindersEnabled = remindersEnabled,
                        checkIns = checkIns,
                        onToggleReminder = viewModel::setRemindersEnabled,
                        onCheckIn = viewModel::checkInTonight
                    )
                }
                composable(AppDestination.Records.route) {
                    RecordsScreen(checkIns = checkIns)
                }
                composable(AppDestination.Settings.route) {
                    SettingsScreen(
                        remindersEnabled = remindersEnabled,
                        checkInCount = checkIns.size,
                        onToggleReminder = viewModel::setRemindersEnabled,
                        onClearCheckIns = viewModel::clearCheckIns,
                        onOpenAbout = { navController.navigate(ABOUT_ROUTE) }
                    )
                }
                composable(ABOUT_ROUTE) {
                    AboutScreen(onBack = { navController.popBackStack() })
                }
            }
        }
    }
}
