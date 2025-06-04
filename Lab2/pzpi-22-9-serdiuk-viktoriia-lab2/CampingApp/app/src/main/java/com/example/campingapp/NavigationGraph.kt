package com.example.campingapp

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.campingapp.api.ApiService
import com.example.campingapp.screens.AuthorizationScreen
import com.example.campingapp.screens.HomeScreen
import com.example.campingapp.screens.RegistrationScreen
import com.example.campingapp.screens.ProfileScreen
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.campingapp.components.AppBackground
import com.example.campingapp.screens.LocationDetailsScreen

@Composable
fun NavigationGraph(navController: NavHostController, apiService: ApiService) {
    NavHost(navController = navController, startDestination = "register") {
        composable("register") {
            AppBackground {
                RegistrationScreen(navController = navController, apiService = apiService)
            }
        }
        composable("auth") { AppBackground { AuthorizationScreen(navController = navController) } }

        composable("home") {
            AppBackground {
                val context = LocalContext.current
                HomeScreen(navController = navController, context = context)
            }
        }
        composable("profile") { AppBackground { ProfileScreen(navController = navController) } }

        composable(
            route = "locationDetails/{locationId}",
            arguments = listOf(navArgument("locationId") { type = NavType.IntType })
        ) { backStackEntry ->
            val locationId = backStackEntry.arguments?.getInt("locationId") ?: -1
            AppBackground { LocationDetailsScreen(locationId = locationId) }
        }
    }
}

