package com.example.snapcash.ui.component

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.Alignment
import com.example.snapcash.ui.screen.CatatScreen
import com.example.snapcash.ui.screen.DashboardScreen
import com.example.snapcash.ui.screen.HistoryScreen
import com.example.snapcash.ui.screen.ProfileScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.snapcash.ui.theme.*
import kotlinx.coroutines.launch
import androidx.compose.foundation.isSystemInDarkTheme

@Composable
fun BottomNavigationBar(navController: NavController) {
    val currentRoute = navController.currentBackStackEntry?.destination?.route

    Box {
        val backgroundColor = if (isSystemInDarkTheme()) Color.Black else night

        NavigationBar(
            containerColor = backgroundColor,
            modifier = Modifier.height(68.dp)
        ) {
            NavigationBarItem(
                icon = { Icon(Icons.Default.Home, contentDescription = "Home", modifier = Modifier.offset(y = 12.dp)) },
                label = { Text("Home", modifier = Modifier.offset(y = 12.dp)) },
                selected = currentRoute == "home",
                colors = NavigationBarItemDefaults.colors(selectedIconColor = Color.Blue, unselectedIconColor = Color.Gray),
                onClick = { navController.navigate("dashboard") }
            )
            NavigationBarItem(
                icon = { Icon(Icons.Default.Search, contentDescription = "Catat", modifier = Modifier.offset(y = 12.dp)) },
                label = { Text("Catat", modifier = Modifier.offset(y = 12.dp)) },
                selected = currentRoute == "catat",
                colors = NavigationBarItemDefaults.colors(selectedIconColor = Color.Blue, unselectedIconColor = Color.Gray),
                onClick = { navController.navigate("catat") }
            )
            Spacer(modifier = Modifier.weight(1f))
            NavigationBarItem(
                icon = { Icon(Icons.Default.CheckCircle, contentDescription = "History", modifier = Modifier.offset(y = 12.dp)) },
                label = { Text("History", modifier = Modifier.offset(y = 12.dp)) },
                selected = currentRoute == "history",
                colors = NavigationBarItemDefaults.colors(selectedIconColor = Color.Blue, unselectedIconColor = Color.Gray),
                onClick = { navController.navigate("history") }
            )
            NavigationBarItem(
                icon = { Icon(Icons.Default.Person, contentDescription = "Profile", modifier = Modifier.offset(y = 12.dp)) },
                label = { Text("Profile", modifier = Modifier.offset(y = 12.dp)) },
                selected = currentRoute == "profile",
                colors = NavigationBarItemDefaults.colors(selectedIconColor = Color.Blue, unselectedIconColor = Color.Gray),
                onClick = { navController.navigate("profile") }
            )
        }

        FloatingActionButton(
            onClick = { navController.navigate("middle") },
            containerColor = MaterialTheme.colorScheme.primary,
            shape = CircleShape,
            modifier = Modifier
                .size(70.dp)
                .offset(y = (-30).dp)
                .align(Alignment.Center)
        ) {
            Icon(Icons.Default.AddCircle, contentDescription = "Middle", tint = MaterialTheme.colorScheme.onPrimary)
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val sidebarState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val sidebarScope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerContent = {
            SidebarContent(navController, sidebarScope, sidebarState)
        },
        drawerState = sidebarState
    ) {
        Scaffold(
            bottomBar = { BottomNavigationBar(navController) }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "home",
                modifier = Modifier.padding(innerPadding)
            ) {
                composable("home") {
                    DashboardScreen(navController) {
                        sidebarScope.launch { sidebarState.open() }
                    }
                }
                composable("catat") { CatatScreen(navController) }
                composable("history") { HistoryScreen(navController) }
                composable("profile") { ProfileScreen(navController) }
                composable("middle") { /* Bisa arahkan ke screen lain atau tampilkan dialog */ }
            }
        }
    }
}


@Preview("default", "rectangle")
@Preview("dark theme", "rectangle", uiMode = UI_MODE_NIGHT_YES)
@Preview("large font", "rectangle", fontScale = 2f)
@Composable
fun BottomNavigationBarPreview() {
    val navController = rememberNavController()
    BottomNavigationBar(navController)
}