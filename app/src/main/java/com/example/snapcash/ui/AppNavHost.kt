package com.example.snapcash.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.snapcash.ui.component.BottomNavigationBar
import com.example.snapcash.ui.component.SidebarContent
import com.example.snapcash.ui.screen.*
import kotlinx.coroutines.launch

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController()
) {
    val sidebarState = rememberDrawerState(DrawerValue.Closed) // ✅ Gunakan rememberDrawerState
    val sidebarScope = rememberCoroutineScope() // ✅ Tambahkan scope untuk sidebar

    ModalNavigationDrawer(
        drawerContent = {
            Surface(
                modifier = Modifier.width(LocalConfiguration.current.screenWidthDp.dp / 2),
                color = MaterialTheme.colorScheme.surface
            ) {
                SidebarContent(
                    navController = navController,
                    sidebarScope = sidebarScope, // ✅ Kirim scope ke SidebarContent
                    sidebarState = sidebarState // ✅ Kirim state ke SidebarContent
                )
            }
        },
        drawerState = sidebarState
    ) {
        Scaffold(
            bottomBar = { BottomNavigationBar(navController) }
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = "dashboard",
                modifier = Modifier.padding(paddingValues)
            ) {
                composable("dashboard") {
                    DashboardScreen(
                        navController = navController,
                        openSidebar = { sidebarScope.launch { sidebarState.open() } } // ✅ Gunakan scope untuk membuka sidebar
                    )
                }
                composable("form_pemasukan") {
                    PemasukanEntryScreen(navController = navController)
                }
                composable("form_pengeluaran") {
                    PengeluaranEntryScreen(navController = navController)
                }
            }
        }
    }
}
