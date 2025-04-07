package com.example.snapcash

import android.content.pm.ActivityInfo
import android.os.Build
import com.example.snapcash.ui.AppNavHost
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresExtension
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import com.example.snapcash.ui.AppEntry
import com.example.snapcash.ui.screen.SplashScreen
import com.example.snapcash.ui.theme.SnapCashTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            SnapCashTheme {
                var showSplash by remember { mutableStateOf(true) }

                if (showSplash) {
                    SplashScreen { showSplash = false }
                }else {
                    AppEntry(
                        navController = rememberNavController(),
                        context = LocalContext.current
                    )
                }
            }
        }
    }
}
