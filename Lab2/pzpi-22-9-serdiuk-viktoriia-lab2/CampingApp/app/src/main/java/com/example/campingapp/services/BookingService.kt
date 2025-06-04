package com.example.campingapp.services

import android.content.Context
import com.example.campingapp.api.Booking
import com.example.campingapp.api.BookingResponse
import com.example.campingapp.api.RetrofitInstance

suspend fun createBooking(
    context: Context, token: String, bookingRequest: Booking
): BookingResponse {
    val apiService = RetrofitInstance.getApiService(context)
    val response = apiService.createBookings("Bearer $token", bookingRequest)
    if (response.isSuccessful) {
        return response.body() ?: throw Exception("Порожній текст відповіді")
    } else {
        throw Exception("Не вдалося створити бронювання: ${response.code()} ${response.message()}")
    }
}