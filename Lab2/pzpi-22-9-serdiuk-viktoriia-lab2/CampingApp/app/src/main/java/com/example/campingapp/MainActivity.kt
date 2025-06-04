package com.example.campingapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import com.example.campingapp.api.RetrofitInstance
import com.example.campingapp.components.AppBackground
import com.example.campingapp.ui.theme.CampingAppTheme
import com.jakewharton.threetenabp.AndroidThreeTen


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidThreeTen.init(this)
        setContent {
            CampingAppTheme {
                AppBackground {
                    val navController = rememberNavController()
                    val context = LocalContext.current
                    val apiService = RetrofitInstance.getApiService(context)
                    NavigationGraph(navController = navController, apiService = apiService)
                }
            }
        }
    }
}