package com.example.snapcash.ui

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
import com.example.snapcash.ui.screen.Auth.LoginScreen
import com.example.snapcash.ui.screen.Auth.RegisterScreen
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController()
) {
    val sidebarState = rememberDrawerState(DrawerValue.Closed)
    val sidebarScope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerContent = {
            Surface(
                modifier = Modifier.width(LocalConfiguration.current.screenWidthDp.dp * 3 / 4),
                color = MaterialTheme.colorScheme.surface
            ) {
                SidebarContent(
                    navController = navController,
                    sidebarScope = sidebarScope,
                    sidebarState = sidebarState
                )
            }
        },
        drawerState = sidebarState
    ) {
        Scaffold(
            bottomBar = { BottomNavigationBar(navController) },
        )  { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = "signIn",
                modifier = Modifier.padding(paddingValues)
            ) {

                composable("signIn"){
                    LoginScreen(navController)
                }

                composable("signUp"){
                    RegisterScreen(navController)
                }

                composable("dashboard") {
                    DashboardScreen(
                        navController = navController,
                        openSidebar = { sidebarScope.launch { sidebarState.open() } }
                    )
                }
                composable("catat") {
                    CatatScreen(navController = navController)
                }
                composable("history") {
                    HistoryScreen(navController = navController)
                }
                composable("profile") {
                    ProfileScreen(navController = navController)
                }
                composable("middle") {
                    // Bisa diarahkan ke fitur lain, misalnya layar transaksi cepat
                }
                composable("settings") {
                    SettingScreen(navController = navController)
                }
                composable("aboutus") {
                    AboutUsScreen(navController = navController)
                }
                composable("help") {
                    HelpScreen(navController = navController)
                }
            }
        }
    }
}
