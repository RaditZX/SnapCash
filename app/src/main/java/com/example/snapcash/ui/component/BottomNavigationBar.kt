package com.example.snapcash.ui.component

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        BottomNavItem.Dashboard,
        BottomNavItem.Form_Pemasukan,
        BottomNavItem.Form_Pengeluaran
    )

    NavigationBar {
        val navBackStackEntry = navController.currentBackStackEntryAsState().value
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.route
                    )
                },
                label = { Text(getLabelForNavItem(item)) },
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun getLabelForNavItem(item: BottomNavItem): String {
    return when (item) {
        is BottomNavItem.Dashboard -> "dashboard"
        is BottomNavItem.Form_Pemasukan ->"form_pemasukan"
        is BottomNavItem.Form_Pengeluaran -> "form_pengeluaran"
    }
}
