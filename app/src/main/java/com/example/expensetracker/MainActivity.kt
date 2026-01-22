package com.example.expensetracker

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.navigation.compose.rememberNavController
import com.example.expensetracker.presentation.ui.navigation.BottomNavigationBar
import com.example.expensetracker.presentation.ui.setting.AppThemeMode
import com.example.expensetracker.presentation.ui.setting.SettingsViewModel
import com.example.expensetracker.ui.navigation.ExpenseTrackerNavGraph
import com.example.expensetracker.ui.theme.ExpenseTrackerTheme
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
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background),
                    bottomBar = { BottomNavigationBar(navController = navController) }
                ) { paddingValues ->
                    Box(modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding())) {
                        ExpenseTrackerNavGraph(navController = navController)
                    }
                }
            }
        }
    }
}