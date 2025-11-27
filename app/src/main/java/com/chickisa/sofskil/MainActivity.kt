package com.chickisa.sofskil

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.chickisa.sofskil.ui.navigation.AppNavigation
import com.chickisa.sofskil.ui.theme.ChickSanityTheme
import com.chickisa.sofskil.ui.viewmodel.SettingsViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Enable edge-to-edge display
        WindowCompat.setDecorFitsSystemWindows(window, false)
        
        setContent {
            ChickSanityApp()
        }
    }
}

@Composable
fun ChickSanityApp() {
    val settingsViewModel: SettingsViewModel = viewModel()
    val themeMode by settingsViewModel.themeMode.collectAsState()
    
    val darkTheme = when (themeMode) {
        "dark" -> true
        else -> false // Always light by default
    }
    
    ChickSanityTheme(darkTheme = darkTheme) {
        val navController = rememberNavController()
        AppNavigation(navController = navController)
    }
}
