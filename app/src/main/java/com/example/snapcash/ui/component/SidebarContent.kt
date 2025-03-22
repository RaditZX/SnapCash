package com.example.snapcash.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun SidebarContent(
    navController: NavController,
    sidebarScope: CoroutineScope,
    sidebarState: DrawerState
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Home",
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    sidebarScope.launch { sidebarState.close() } // ✅ Tutup sidebar dengan aman
                    navController.navigate("dashboard")
                }
                .padding(8.dp)
        )

        Text(
            text = "Pemasukan",
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    sidebarScope.launch { sidebarState.close() } // ✅ Tutup sidebar dengan aman
                    navController.navigate("form_pemasukan")
                }
                .padding(8.dp)
        )

        Text(
            text = "Pengeluaran",
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    sidebarScope.launch { sidebarState.close() } // ✅ Tutup sidebar dengan aman
                    navController.navigate("form_pengeluaran")
                }
                .padding(8.dp)
        )
    }
}
