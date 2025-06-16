package com.example.snapcash

import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresExtension
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.navigation.compose.rememberNavController
import com.example.snapcash.ui.AppEntry
import com.example.snapcash.ui.screen.SplashScreen
import com.example.snapcash.ui.theme.SnapCashTheme
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val themeViewModel: ThemeViewModel = hiltViewModel()
            val isDarkTheme by themeViewModel.isDarkTheme.collectAsStateWithLifecycle()
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            SnapCashTheme(darkTheme = if (isDarkTheme) true else false) {
                var showSplash by remember { mutableStateOf(true) }

                if (showSplash) {
                    SplashScreen { showSplash = false }
                }else {
                    AppEntry(
                        navController = rememberNavController(),
                        context = LocalContext.current,
                        isDarkTheme = isDarkTheme,
                        onThemeToggle = { themeViewModel.toggleTheme() }
                    )
                }
            }
        }
    }
}

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "theme_settings")
// 5. Alternative: Menggunakan ViewModel (Recommended)
@HiltViewModel
class ThemeViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val dataStore = context.dataStore
    private val THEME_KEY = booleanPreferencesKey("is_dark_theme")

    // StateFlow untuk theme
    val isDarkTheme = dataStore.data.map { preferences ->
        preferences[THEME_KEY] ?: false
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = true
    )

    // Function untuk toggle theme
    fun toggleTheme() {
        viewModelScope.launch {
            val currentTheme = isDarkTheme.value
            dataStore.edit { preferences ->
                preferences[THEME_KEY] = !currentTheme
            }
        }
    }

    // Function untuk set theme langsung
    fun setTheme(isDark: Boolean) {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[THEME_KEY] = isDark
            }
        }
    }
}

