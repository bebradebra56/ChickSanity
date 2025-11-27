package com.chickisa.sofskil.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.chickisa.sofskil.ui.screens.*
import com.chickisa.sofskil.ui.viewmodel.*

@Composable
fun AppNavigation(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Main.route
    ) {
        composable(
            route = Screen.Main.route
        ) {
            // ViewModel will be recreated when navigating back after Clear All Data
            val viewModel: MainViewModel = viewModel(
                key = "main_${navController.currentBackStackEntry?.id}"
            )
            MainScreen(
                viewModel = viewModel,
                onNavigateToAddZone = {
                    navController.navigate(Screen.AddEditZone.createRoute())
                },
                onNavigateToZoneDetail = { zoneId ->
                    navController.navigate(Screen.ZoneDetail.createRoute(zoneId))
                },
                onNavigateToStatistics = {
                    navController.navigate(Screen.Statistics.route)
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }
        
        composable(
            route = "add_edit_zone?zoneId={zoneId}",
            arguments = listOf(
                navArgument("zoneId") {
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) { backStackEntry ->
            val viewModel: AddEditZoneViewModel = viewModel()
            val zoneIdString = backStackEntry.arguments?.getString("zoneId")
            val zoneId = zoneIdString?.toLongOrNull()
            
            AddEditZoneScreen(
                viewModel = viewModel,
                zoneId = zoneId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(
            route = Screen.ZoneDetail.route,
            arguments = listOf(
                navArgument("zoneId") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            val viewModel: ZoneDetailViewModel = viewModel()
            val zoneId = backStackEntry.arguments?.getLong("zoneId") ?: 0L
            
            ZoneDetailScreen(
                viewModel = viewModel,
                zoneId = zoneId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToEdit = { id ->
                    navController.navigate(Screen.AddEditZone.createRoute(id))
                }
            )
        }
        
        composable(Screen.Statistics.route) {
            val viewModel: StatisticsViewModel = viewModel()
            StatisticsScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.Settings.route) {
            val viewModel: SettingsViewModel = viewModel()
            SettingsScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onDataCleared = {
                    // Clear all navigation and restart from main
                    navController.navigate(Screen.Main.route) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}

