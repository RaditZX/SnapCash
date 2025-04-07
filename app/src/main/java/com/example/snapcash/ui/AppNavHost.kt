package com.example.snapcash.ui

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresExtension
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
import androidx.compose.runtime.setValue
import com.example.snapcash.data.OnboardingPrefs
import com.example.snapcash.ui.screen.Upload.CameraScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers


@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@Composable
fun AppEntry(navController: NavHostController, context: Context) {
    var startDestination by remember { mutableStateOf("onBoarding") }

    LaunchedEffect(Unit) {
        val isOnboardingShown = OnboardingPrefs.isOnboardingShown(context)
        startDestination = if (isOnboardingShown) "signIn" else "onBoarding"
    }

    AppNavHost(navController, startDestination)
}

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    startDestination: String
) {
    val sidebarState = rememberDrawerState(DrawerValue.Closed)
    val sidebarScope = rememberCoroutineScope()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    ModalNavigationDrawer(
        drawerContent = {
            Surface(
                modifier = Modifier.width(LocalConfiguration.current.screenWidthDp.dp / 2),
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
            bottomBar = {
                if (currentRoute !in listOf("signIn", "signUp","onBoarding")) {
                    BottomNavigationBar(navController)
                }
            },
        )  { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = startDestination,
                modifier = Modifier.padding(paddingValues)
            ) {

                composable("onBoarding") {
                    OnboardingScreen(
                        onFinish = {
                            // Simpan status onboarding sudah selesai
                            CoroutineScope(Dispatchers.IO).launch {
                                OnboardingPrefs.setOnboardingShown(navController.context)
                            }
                            navController.navigate("SignIn") {
                                popUpTo("onBoarding") { inclusive = true }
                            }
                        },
                        navController = navController
                    )
                }
                composable("signIn"){
                    LoginScreen(navController)
                }

                composable("signUp"){
                    RegisterScreen(navController)
                }

                composable("camera"){
                    CameraScreen(navController = navController)
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
            }
        }
    }
}
