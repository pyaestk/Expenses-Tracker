package com.example.saveit

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.saveit.presentation.ui.navigation.BottomNavItem
import com.example.saveit.presentation.ui.navigation.BottomNavigationBar
import com.example.saveit.presentation.ui.setting.AppThemeMode
import com.example.saveit.presentation.ui.setting.SettingsViewModel
import com.example.saveit.ui.navigation.ExpenseTrackerNavGraph
import com.example.saveit.ui.theme.ExpenseTrackerTheme
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settingsViewModel: SettingsViewModel = koinViewModel()

            val settingsState by settingsViewModel.uiState.collectAsStateWithLifecycle()


            val isDarkTheme = when (settingsState.themeMode) {
                AppThemeMode.LIGHT -> false
                AppThemeMode.DARK -> true
                AppThemeMode.SYSTEM -> isSystemInDarkTheme()
            }

            val view = LocalView.current
            if (!view.isInEditMode) {
                SideEffect {
                    val window = (view.context as Activity).window
                    // If Dark Theme is ON, we want Light Status Bar Icons (false = light icons, true = dark icons)
                    WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isDarkTheme
                }
            }

            ExpenseTrackerTheme(darkTheme = isDarkTheme) {
                val navController = rememberNavController()

                val items = listOf(
                    BottomNavItem.Home,
                    BottomNavItem.Analytics,
                    BottomNavItem.Budget,
                    BottomNavItem.Settings
                )
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                val showBottomBar = items.any { it.route == currentRoute }

                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background),
                    bottomBar = {
                        AnimatedVisibility(
                            visible = showBottomBar,
                            enter = slideInVertically { it },
                            exit = slideOutVertically { it }
                        ){
                            BottomNavigationBar(
                                items,
                                currentRoute,
                                navController
                            )
                        }
                    }
                ) { paddingValues ->
                    Box(modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding())) {
                        ExpenseTrackerNavGraph(navController = navController)
                    }
                }
            }
        }
    }
}