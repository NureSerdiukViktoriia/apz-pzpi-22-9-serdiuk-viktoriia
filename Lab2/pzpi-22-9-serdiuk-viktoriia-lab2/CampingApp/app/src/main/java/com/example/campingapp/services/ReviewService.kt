package com.example.campingapp.services

import android.content.Context
import com.example.campingapp.api.Review
import com.example.campingapp.api.ReviewRequest
import com.example.campingapp.api.RetrofitInstance

suspend fun createReview(
    context: Context, token: String, reviewRequest: ReviewRequest
): Review {
    val apiService = RetrofitInstance.getApiService(context)
    val response = apiService.createReview("Bearer $token", reviewRequest)
    if (response.isSuccessful) {
        return response.body() ?: throw Exception("Порожній текст відповіді")
    } else {
        throw Exception("Не вдалося створити відгук: ${response.code()} ${response.message()}")
    }
}

suspend fun getReviewsByLocationId(
    context: Context, locationId: Int
): List<Review> {
    val apiService = RetrofitInstance.getApiService(context)
    val response = apiService.getReviewsByLocationId(locationId)
    if (response.isSuccessful) {
        return response.body() ?: emptyList()
    } else {
        throw Exception("Не вдалося отримати відгуки: ${response.code()} ${response.message()}")
    }
}

suspend fun updateReview(
    context: Context, token: String, reviewId: Int, reviewRequest: ReviewRequest
): Review {
    val apiService = RetrofitInstance.getApiService(context)
    val response = apiService.updateReview("Bearer $token", reviewId, reviewRequest)
    if (response.isSuccessful) {
        return response.body() ?: throw Exception("Порожній текст відповіді")
    } else {
        throw Exception("Не вдалося оновити відгук: ${response.code()} ${response.message()}")
    }
}

suspend fun deleteReview(
    context: Context, token: String, reviewId: Int
) {
    val apiService = RetrofitInstance.getApiService(context)
    val response = apiService.deleteReview("Bearer $token", reviewId)
    if (!response.isSuccessful) {
        throw Exception("Не вдалося видалити відгук: ${response.code()} ${response.message()}")
    }
}
