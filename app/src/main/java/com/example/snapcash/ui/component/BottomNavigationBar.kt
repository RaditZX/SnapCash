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
import androidx.compose.material3.MaterialTheme
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
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.surface,
            modifier = Modifier.height(100.dp)
        ) {
            NavigationBarItem(
                modifier = Modifier.offset(y = 5.dp),
                icon = {
                    Icon(
                        Icons.Default.Home,
                        contentDescription = "Home",
                        tint = if (currentRoute == "dashboard") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                label = { Text("Home", color = if (currentRoute == "dashboard") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant) },
                selected = currentRoute == "dashboard",
                colors = NavigationBarItemDefaults.colors(
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
                        colorFilter = ColorFilter.tint(if (currentRoute == "tambah/pengeluaran" || currentRoute == "tambah/pemasukan") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                },
                label = { Text("Catat", color = if (currentRoute == "tambah/pengeluaran" || currentRoute == "tambah/pemasukan") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant) },
                selected = currentRoute == "tambah/pengeluaran" || currentRoute == "tambah/pemasukan",
                colors = NavigationBarItemDefaults.colors(
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
                        colorFilter = ColorFilter.tint(if (currentRoute == "history") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                },
                label = { Text("History", color = if (currentRoute == "history") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant) },
                selected = currentRoute == "history",
                colors = NavigationBarItemDefaults.colors(
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
                        tint = if (currentRoute == "profile") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                label = { Text("Profile", color = if (currentRoute == "profile") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant) },
                selected = currentRoute == "profile",
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent
                ),
                onClick = { navController.navigate("profile") }
            )
        }

        FloatingActionButton(
            onClick = { navController.navigate("camera") },
            containerColor = MaterialTheme.colorScheme.primary,
            shape = CircleShape,
            modifier = Modifier
                .size(100.dp)
                .align(Alignment.Center)
                .border(4.dp, MaterialTheme.colorScheme.outlineVariant, shape = CircleShape)
        ) {
            Icon(Icons.Default.AddCircle, contentDescription = "Middle", tint = MaterialTheme.colorScheme.onPrimary)
        }
    }
}