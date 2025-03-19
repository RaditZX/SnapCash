import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.snapcash.ui.screen.DashboardScreen
import com.example.snapcash.ui.screen.PengeluaranEntryScreen
import com.example.snapcash.ui.component.BottomNavigationBar

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController()
) {
    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "dashboard",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("dashboard") {
                DashboardScreen(navController = navController)
            }
            composable("form_pemasukan") {
                PemasukanEntryScreen(navController = navController)
            }
            composable("form_pengeluaran") {
                PengeluaranEntryScreen(navController = navController)
            }
        }
    }
}