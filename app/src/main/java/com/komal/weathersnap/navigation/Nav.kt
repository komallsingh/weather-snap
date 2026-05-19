package com.komal.weathersnap.navigation

import com.komal.weathersnap.Screens.AllReportsScreen
import com.komal.weathersnap.Screens.CameraScreen
import com.komal.weathersnap.Screens.ReportScreen
import com.komal.weathersnap.Screens.WeatherHomeScreen
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController


sealed class Screen(val route: String) {
    data object WeatherHome : Screen("weather_home")
    data object Report : Screen("report")
    data object Camera : Screen("camera")
    data object AllReports : Screen("all_reports")
}
@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.WeatherHome.route
    ) {

        composable(Screen.WeatherHome.route) {
            WeatherHomeScreen(
                navController = navController
            )
        }

        composable(Screen.Report.route) {
            ReportScreen(
                navController = navController
            )
        }

        composable(Screen.Camera.route) {
            CameraScreen(
                navController = navController
            )
        }

        composable(Screen.AllReports.route) {
            AllReportsScreen(
                navController = navController
            )
        }
    }
}
