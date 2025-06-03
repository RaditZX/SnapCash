package com.example.snapcash.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import com.example.snapcash.R
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.List
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun SidebarContent(
    navController: NavController,
    sidebarScope: CoroutineScope,
    sidebarState: DrawerState
) {
    Column(
        modifier = Modifier
            .background(Color(0xFF0F1418))
    ) {
        // Profile Section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF2D6CE9 ))
                .padding(28.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(60.dp)
                        .background(Color.Gray, CircleShape)
                        .clickable {
                            sidebarScope.launch { sidebarState.close() }
                            navController.navigate("profile")
                        }
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text("Lorem Ipsum", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("LoremIpsum@gmail.com", color = Color.White, fontSize = 14.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Menu Items
        SidebarItem("Home", Icons.Filled.Home, navController, sidebarScope, sidebarState, "home")
        SidebarItem("Catat", Icons.Filled.Edit, navController, sidebarScope, sidebarState, "catat")
        SidebarItem("History", Icons.Filled.Refresh, navController, sidebarScope, sidebarState, "history")
        SidebarItem("Profile", Icons.Filled.Person, navController, sidebarScope, sidebarState, "profile")
        SidebarItem("List Kategori", Icons.Filled.List,navController,sidebarScope,sidebarState, "kategori")
        Spacer(modifier = Modifier.weight(1f))

        // Bottom Items
        SidebarItem("Settings", Icons.Filled.Settings, navController, sidebarScope, sidebarState, "settings")
        SidebarItem("About Us", Icons.Filled.ThumbUp, navController, sidebarScope, sidebarState, "aboutus")
        SidebarItem("Help", Icons.Filled.Info, navController, sidebarScope, sidebarState, "help")
        SidebarItem("Logout", Icons.Filled.ExitToApp, navController, sidebarScope, sidebarState, "home")

        Spacer(modifier = Modifier.height(16.dp))
    }
}


@Composable
fun SidebarItem(
    title: String,
    icon: ImageVector,
    navController: NavController,
    sidebarScope: CoroutineScope,
    sidebarState: DrawerState,
    route: String
) {
    Row(
        modifier = Modifier
            .clickable {
                sidebarScope.launch { sidebarState.close() }
                navController.navigate(route)
            }
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(title, color = Color.White, fontSize = 16.sp)
    }
}

