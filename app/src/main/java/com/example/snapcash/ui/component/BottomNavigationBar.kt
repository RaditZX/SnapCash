package com.example.snapcash.ui.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomNavigationBar(navController: NavController) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Dashboard") },
            label = { Text("Dashboard") },
            selected = currentRoute == "dashboard",
            onClick = {
                navController.navigate("dashboard") {
                    popUpTo("dashboard") { inclusive = true }
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Add, contentDescription = "Pemasukan") },
            label = { Text("Pemasukan") },
            selected = currentRoute == "form_pemasukan",
            onClick = {
                navController.navigate("form_pemasukan") {
                    popUpTo("dashboard") { inclusive = false }
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Create, contentDescription = "Pengeluaran") },
            label = { Text("Pengeluaran") },
            selected = currentRoute == "form_pengeluaran",
            onClick = {
                navController.navigate("form_pengeluaran") {
                    popUpTo("dashboard") { inclusive = false }
                }
            }
        )
    }
}
