package com.example.campingapp.api

import java.util.Date

data class Booking(
    val start_date: Date,
    val end_date: Date,
    val total_price: Float,
    val payment_status: String,
    val payment_date: Date,
    val user_id: Int,
    val location_id: Int
)