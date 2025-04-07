package com.example.snapcash.ui.component

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.border
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.Alignment
import com.example.snapcash.ui.theme.*
import androidx.compose.foundation.isSystemInDarkTheme

@Composable
fun BottomNavigationBar(navController: NavController) {
    val currentRoute = navController.currentBackStackEntry?.destination?.route

    Box {
        val backgroundColor = if (isSystemInDarkTheme()) Color(0xFF1E1E1E) else night
        val navPrimaryColor  = Color(0xFF2D6CE9)

        NavigationBar(
            containerColor = backgroundColor,
            modifier = Modifier.height(68.dp).offset(y=21.dp) // Menyesuaikan tinggi navbar
        ) {
            NavigationBarItem(
                modifier = Modifier.offset(y = 5.dp),
                icon = {
                    Icon(
                        Icons.Default.Home,
                        contentDescription = "Home",

                        )
                },
                label = { Text("Home") },
                selected = currentRoute == "dashboard",
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = navPrimaryColor,
                    unselectedIconColor = Color.Gray,
                    selectedTextColor = navPrimaryColor,          // 👈 change label color when selected
                    unselectedTextColor = Color.Gray,
                    indicatorColor = Color.Transparent
                ),
                onClick = { navController.navigate("dashboard") }
            )
            NavigationBarItem(
                modifier = Modifier.offset(y = 5.dp),
                icon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "Catat",
                    )
                },
                label = { Text("Catat" ) },
                selected = currentRoute == "catat",
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = navPrimaryColor,
                    unselectedIconColor = Color.Gray,
                    selectedTextColor = navPrimaryColor,          // 👈 change label color when selected
                    unselectedTextColor = Color.Gray,
                    indicatorColor = Color.Transparent
                ),
                onClick = { navController.navigate("catat") }
            )
            Spacer(modifier = Modifier.weight(1f))
            NavigationBarItem(
                modifier = Modifier.offset(y = 5.dp),
                icon = {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = "History",
                    )
                },
                label = { Text("History") },
                selected = currentRoute == "history",
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = navPrimaryColor,
                    unselectedIconColor = Color.Gray,
                    selectedTextColor = navPrimaryColor,          // 👈 change label color when selected
                    unselectedTextColor = Color.Gray,
                    indicatorColor = Color.Transparent
                ),
                onClick = { navController.navigate("history") }
            )
            NavigationBarItem(
                modifier = Modifier.offset(y = 5.dp),
                icon = {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = "Profile",
                    )
                },
                label = { Text("Profile") },
                selected = currentRoute == "profile",
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = navPrimaryColor,
                    unselectedIconColor = Color.Gray,
                    selectedTextColor = navPrimaryColor,          // 👈 change label color when selected
                    unselectedTextColor = Color.Gray,
                    indicatorColor = Color.Transparent
                ),
                onClick = { navController.navigate("profile") }
            )
        }

        FloatingActionButton(
            onClick = { navController.navigate("camera") },
            containerColor = Color(0xFF2D6CE9),
            shape = CircleShape,
            modifier = Modifier
                .size(90.dp)
                .offset(y = (-10).dp)
                .align(Alignment.Center)
                .border(
                    width = 4.dp,
                    color = Color(0xFFA9C0FF), // 👈 stroke color
                    shape = CircleShape
                )
        ) {
            Icon(Icons.Default.AddCircle, contentDescription = "Middle", tint = Color.White)
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