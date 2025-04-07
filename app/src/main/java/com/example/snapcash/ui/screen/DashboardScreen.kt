package com.example.snapcash.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun DashboardScreen(navController: NavController, openSidebar: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {

        IconButton(
            onClick = openSidebar,
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(imageVector = Icons.Default.Menu, contentDescription = "Sidebar Menu")
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            BasicText(text = "Dashboard Screen", style = MaterialTheme.typography.headlineLarge)
        }
    }
}
