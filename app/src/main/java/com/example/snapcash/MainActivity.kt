package com.example.snapcash

import com.example.snapcash.ui.AppNavHost
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.snapcash.ui.theme.SnapCashTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SnapCashTheme {
                AppNavHost()
            }
        }
    }
}
