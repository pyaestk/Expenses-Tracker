package com.example.saveit.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Extension for singleton DataStore
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class UserPreferencesRepository(private val context: Context) {

    private val THEME_KEY = stringPreferencesKey("theme_mode")
    private val CURRENCY_KEY = stringPreferencesKey("currency_symbol")

    // Default to "$" if nothing is saved
    val currencySymbol: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[CURRENCY_KEY] ?: "$"
        }

    // Save full string "USD ($)" or just the code/mode if you prefer
    // For this app, we saved the mode as a String in SettingsViewModel
    val themeMode: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[THEME_KEY] ?: "SYSTEM"
        }

    suspend fun saveCurrency(symbol: String) {
        context.dataStore.edit { preferences ->
            preferences[CURRENCY_KEY] = symbol
        }
    }

    suspend fun saveTheme(mode: String) {
        context.dataStore.edit { preferences ->
            preferences[THEME_KEY] = mode
        }
    }
}