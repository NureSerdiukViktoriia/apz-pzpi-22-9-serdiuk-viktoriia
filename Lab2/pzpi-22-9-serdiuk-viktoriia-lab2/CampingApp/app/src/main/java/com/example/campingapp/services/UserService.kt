package com.example.campingapp.services

import android.content.Context
import com.example.campingapp.api.RetrofitInstance
import com.example.campingapp.api.UserData

suspend fun fetchUser(context: Context, userId: String): UserData {
    val apiService = RetrofitInstance.getApiService(context)
    return apiService.getUser(userId)
}

suspend fun deleteUser(context: Context, userId: String) {
    val apiService = RetrofitInstance.getApiService(context)
    apiService.deleteUser(userId)
}

suspend fun updateUser(context: Context, userId: String, userData: UserData) {
    val apiService = RetrofitInstance.getApiService(context)
    apiService.updateUser(userId, userData)
}
