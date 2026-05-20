package com.komal.weathersnap.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.komal.weathersnap.Screens.AllReportsScreen
import com.komal.weathersnap.Screens.CameraScreen
import com.komal.weathersnap.Screens.ReportScreen
import com.komal.weathersnap.Screens.WeatherHomeScreen
import com.komal.weathersnap.model.SharedViewModel

sealed class Screen(val route: String) {
    object WeatherHome  : Screen("weather_home")
    object Report       : Screen("create_report")
    object Camera       : Screen("camera")
    object AllReports   : Screen("all_reports")
}

@Composable
fun AppNavHost(navController: NavHostController = rememberNavController()) {

    // ONE SharedViewModel instance for the whole app — scoped to NavHost
    val sharedVm: SharedViewModel = hiltViewModel()

    NavHost(
        navController    = navController,
        startDestination = Screen.WeatherHome.route
    ) {
        composable(Screen.WeatherHome.route) {
            WeatherHomeScreen(
                navController = navController,
                sharedVm      = sharedVm          // pass down
            )
        }
        composable(Screen.Report.route) {
            ReportScreen(
                navController = navController,
                sharedVm      = sharedVm          // same instance
            )
        }
        composable(Screen.Camera.route) {
            CameraScreen(
                navController = navController,
                sharedVm      = sharedVm          // same instance
            )
        }
        composable(Screen.AllReports.route) {
            AllReportsScreen(navController = navController)
        }
    }
}