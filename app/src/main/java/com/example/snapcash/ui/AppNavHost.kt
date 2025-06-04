package com.example.snapcash.ui


import android.content.Context
import android.os.Build
import androidx.annotation.RequiresExtension
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.snapcash.data.FilterModel
import com.example.snapcash.data.OnboardingPrefs
import com.example.snapcash.data.Transaction
import com.example.snapcash.ui.component.BottomNavigationBar
import com.example.snapcash.ui.component.FilterBottomSheet
import com.example.snapcash.ui.component.SidebarContent
import com.example.snapcash.ui.screen.Auth.LoginScreen
import com.example.snapcash.ui.screen.Auth.RegisterScreen
import com.example.snapcash.ui.screen.DashboardScreen
import com.example.snapcash.ui.screen.EditProfileScreen
import com.example.snapcash.ui.screen.HistoryScreen
import com.example.snapcash.ui.screen.OnboardingScreen
import com.example.snapcash.ui.screen.PemasukanEntryScreen
import com.example.snapcash.ui.screen.PengeluaranEntryScreen
import com.example.snapcash.ui.screen.ProfileScreen
import com.example.snapcash.ui.screen.Upload.CameraScreen
import  com.example.snapcash.ui.screen.ListKategoriScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
    val bottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val coroutineScope = rememberCoroutineScope()
    var filterModel by remember { mutableStateOf(FilterModel()) }
    var isPemasukan by remember {mutableStateOf(false)}
    var dataTransaction by remember { mutableStateOf(listOf<Transaction>()) }
    var periode by remember {mutableStateOf("")}
    ModalNavigationDrawer(
        drawerContent = {
            Surface(
                modifier = Modifier.width(LocalConfiguration.current.screenWidthDp.dp * 3 / 4),
                color = Color(0xFF0D0F13)
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


        ModalBottomSheetLayout(
            sheetState = bottomSheetState,
            sheetBackgroundColor = Color.Transparent,
            sheetContent = {
                FilterBottomSheet(onDismiss = {
                    coroutineScope.launch { bottomSheetState.hide() }
                }, filterData = { newFilterData ->
                    filterModel = newFilterData
                }, isPemasukan = isPemasukan , navController = navController, dataTransaction = dataTransaction, periode = periode)
            }
        ) {
            Scaffold(
                bottomBar = {
                    if (currentRoute !in listOf("signIn", "signUp", "onBoarding")) {
                        BottomNavigationBar(navController)
                    }
                },
            ) { paddingValues ->
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
                    composable("signIn") {
                        LoginScreen(navController)
                    }

                    composable("signUp") {
                        RegisterScreen(navController)
                    }

                    composable("camera") {
                        CameraScreen(navController = navController)
                    }

                    composable("dashboard") {
                        DashboardScreen(
                            navController = navController,
                            openSidebar = { sidebarScope.launch { sidebarState.open() } }
                        )
                    }
                    composable("tambah/pengeluaran") {
                        PengeluaranEntryScreen(navController = navController, id = null,  preview = false)
                    }
                    composable("kategori") {
                        ListKategoriScreen(navController = navController)
                    }
                    composable("tambah/pemasukan") {
                        PemasukanEntryScreen(navController = navController, id = null,  preview = false)
                    }
                    composable("update/pengeluaran/{id}") { backStackEntry ->
                        val id = backStackEntry.arguments?.getString("id").toString()
                        PengeluaranEntryScreen(navController = navController, id = id, preview = false)
                    }
                    composable("update/pemasukan/{id}") { backStackEntry ->
                        val id = backStackEntry.arguments?.getString("id").toString()
                        PemasukanEntryScreen(navController = navController, id = id, preview = false)
                    }
                    composable("preview/pengeluaran/{id}") { backStackEntry ->
                        val id = backStackEntry.arguments?.getString("id").toString()
                        PengeluaranEntryScreen(navController = navController, id, preview = true )
                    }
                    composable("preview/pemasukan/{id}") { backStackEntry ->
                        val id = backStackEntry.arguments?.getString("id").toString()
                        PemasukanEntryScreen(navController = navController, id = id, preview = true)
                    }

                    composable("history") {
                        HistoryScreen(
                            navController = navController,
                            onFilterClick = {
                                coroutineScope.launch { bottomSheetState.show() }
                            },
                            isPemasukan = { value ->
                                coroutineScope.launch {
                                    isPemasukan = value
                                }
                            },
                            filterData = filterModel,
                            dataTransaction = {
                                value ->
                                coroutineScope.launch {
                                    dataTransaction = value
                                }
                            },
                            periode = {
                                value ->
                                coroutineScope.launch {
                                    periode = value
                                }

                            }
                        )

                    }
                    composable("profile") {
                        ProfileScreen(navController = navController)
                    }
                    composable("profile/edit") {
                        EditProfileScreen(navController = navController)
                    }

                }
            }
        }
    }
}
