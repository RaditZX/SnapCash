package com.example.snapcash.ui.component

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.snapcash.R
import com.example.snapcash.ui.theme.night

@Composable
fun BottomNavigationBar(navController: NavController) {
    val currentRoute = navController.currentBackStackEntry?.destination?.route

    Box {
        val backgroundColor = if (isSystemInDarkTheme()) Color(0xFF1E1E1E) else night
        val navPrimaryColor  = Color(0xFF2D6CE9)

        NavigationBar(
            containerColor = backgroundColor,
            modifier = Modifier.height(100.dp)
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
                    selectedTextColor = navPrimaryColor,
                    unselectedTextColor = Color.Gray,
                    indicatorColor = Color.Transparent
                ),
                onClick = { navController.navigate("dashboard") }
            )
            NavigationBarItem(
                modifier = Modifier.offset(y = 5.dp).offset(x = (-10).dp),
                icon = {
                        Image(
                            painter = painterResource(R.drawable.baseline_sticky_note_2_24),
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(Color.DarkGray)
                        )
                },
                label = { Text("Catat" ) },
                selected = currentRoute == "tambah/pengeluaran" || currentRoute == "tambah/pemasukan",
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = navPrimaryColor,
                    unselectedIconColor = Color.Gray,
                    selectedTextColor = navPrimaryColor,
                    unselectedTextColor = Color.Gray,
                    indicatorColor = Color.Transparent
                ),
                onClick = { navController.navigate("tambah/pengeluaran") }
            )
            Spacer(modifier = Modifier.weight(1f))
            NavigationBarItem(
                modifier = Modifier.offset(y = 5.dp).offset(x = 10.dp),
                icon = {
                    Image(
                        painter = painterResource(R.drawable.baseline_history_24),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(Color.DarkGray)
                    )
                },
                label = { Text("History") },
                selected = currentRoute == "history",
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = navPrimaryColor,
                    unselectedIconColor = Color.Gray,
                    selectedTextColor = navPrimaryColor,
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
                    selectedTextColor = navPrimaryColor,
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
                .size(100.dp)
                .align(Alignment.Center)
                .border(
                    width = 4.dp,
                    color = Color(0xFFA9C0FF),
                    shape = CircleShape
                )
        ) {
            Icon(Icons.Default.AddCircle, contentDescription = "Middle", tint = Color.White)
        }
    }
}