package com.example.snapcash.ui.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: Int) {
    object Dashboard : BottomNavItem("dashboard", Icons.Filled.Home, 1)
    object Form_Pemasukan : BottomNavItem("form_pemasukan", Icons.Filled.DateRange, 2)
    object Form_Pengeluaran : BottomNavItem("form_pengeluaran", Icons.Filled.AddCircle, 3)
}