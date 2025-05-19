package com.example.snapcash.ui.screen

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import co.yml.charts.ui.linechart.LineChart
import com.example.snapcash.ViewModel.DashboardViewModel
import com.example.snapcash.ui.component.LineChartDashboard
import com.example.snapcash.ui.component.ProgressCircleChart
import androidx.compose.foundation.horizontalScroll

@Composable
fun DashboardScreen(
    navController: NavController,
    openSidebar: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {

    val isLoading by viewModel.isLoading
    val data = viewModel.dashboardData.value

    LaunchedEffect(Unit) {
        viewModel.getDashboardAnalytics(tahun = 2025)
    }

    val scrollState = rememberScrollState()
    val pagerState = rememberPagerState(pageCount = { 2 })

    val chartItems = listOf(
        ChartItem("HOUSE", 122.00f, Color(0xFFF53844)),
        ChartItem("CAR", 4528.00f, Color(0xFF2D6CE9)),
        ChartItem("FOOD", 201.00f, Color(0xFF20BF55)),
        ChartItem("EXTRA", 1000.00f, Color(0xFFFFA500))
    )
    val pages = chartItems.chunked(3)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        IconButton(onClick = openSidebar) {
            Icon(imageVector = Icons.Default.Menu, contentDescription = "Sidebar Menu")
        }

        Text(
            text = "Welcome Back DWIKA 100!",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            textAlign = TextAlign.Center
        )

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else if (data != null) {
            Text("Total: Rp ${data.total}", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("Perubahan Total: Rp ${data.perubahanTotal}", color = Color.White)
            Text("Perubahan Persentase: ${data.perubahanPersentase}%", color = Color.White)
            Text("Total Tahun Sebelumnya: Rp ${data.totalTahunSebelumnya ?: 0}", color = Color.White)
            Spacer(modifier = Modifier.height(16.dp))

            Text("Total per Kategori:", color = Color.White, fontWeight = FontWeight.Bold)
            data.TotalByKategori.forEach { (kategori, total) ->
                Text("- $kategori: Rp $total", color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Total per Tahun:", color = Color.White, fontWeight = FontWeight.Bold)
            data.TotalByRange.forEach { (tahun, total) ->
                Text("- $tahun: Rp $total", color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Grafik Total per Tahun:", color = Color.White, fontWeight = FontWeight.Bold)
            LineChartDashboard(data.TotalByRange)
        } else {
            Text("Tidak ada data.", color = Color.White)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Progress Circle Charts", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        val scrollStateHorizontal = rememberScrollState()
        val totalByKategori = data?.TotalByKategori ?: emptyMap()

        Row(
            modifier = Modifier
                .horizontalScroll(scrollStateHorizontal)
                .padding(bottom = 16.dp)
        ) {
            totalByKategori.forEach { (kategori, total) ->
                ProgressCircleChart(
                    label = kategori,
                    value = total.toFloat(),
                    total = totalByKategori.values.sum().toFloat(),
                    color = Color(0xFF2D6CE9),
                    modifier = Modifier
                        .padding(end = 12.dp)
                        .width(100.dp)
                )
            }
        }

    }
}

data class ChartItem(
    val label: String,
    val value: Float,
    val color: Color
)