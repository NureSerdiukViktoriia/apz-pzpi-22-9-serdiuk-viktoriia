package com.example.campingapp.services

import android.content.Context
import com.example.campingapp.api.Location
import com.example.campingapp.api.RetrofitInstance

suspend fun fetchLocation(context: Context, locationId: Int): Location {
    val apiService = RetrofitInstance.getApiService(context)
    return apiService.getLocation(locationId)
}
