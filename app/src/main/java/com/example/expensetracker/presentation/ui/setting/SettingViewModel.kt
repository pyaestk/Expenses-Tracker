package com.example.expensetracker.presentation.ui.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class AppThemeMode {
    LIGHT, DARK, SYSTEM
}

data class SettingsUiState(
    val themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    val currency: String = "USD ($)",
    val currencySymbol: String = "$"
)


class SettingsViewModel(
    private val userPreferences: UserPreferencesRepository
) : ViewModel() {

    // Combine flows from Repository to create UI State
    val uiState: StateFlow<SettingsUiState> = combine(
        userPreferences.themeMode,
        userPreferences.currencySymbol
    ) { themeString, currencySymbol ->

        val mode = try { AppThemeMode.valueOf(themeString) } catch (e: Exception) { AppThemeMode.SYSTEM }

        // Find the full display string for the symbol (optional polish)
        val displayCurrency = currencyList.find { it.contains(currencySymbol) } ?: "USD ($currencySymbol)"

        SettingsUiState(
            themeMode = mode,
            currency = displayCurrency,
            currencySymbol = currencySymbol
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SettingsUiState()
    )

    val currencyList = listOf("USD ($)", "EUR (€)", "GBP (£)", "JPY (¥)", "INR (₹)", "THB (฿)")

    fun setTheme(mode: AppThemeMode) {
        viewModelScope.launch {
            userPreferences.saveTheme(mode.name)
        }
    }

    fun setCurrency(currency: String) {
        val symbol = currency.substringAfter("(").substringBefore(")")
        viewModelScope.launch {
            userPreferences.saveCurrency(symbol)
        }
    }
}