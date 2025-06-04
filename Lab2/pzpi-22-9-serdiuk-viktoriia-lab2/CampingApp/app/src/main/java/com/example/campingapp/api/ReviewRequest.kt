package com.example.campingapp.api

import java.util.Date

data class ReviewRequest(
    val user_id: Int,
    val location_id: Int,
    val rating: Int,
    val message: String,
    val date: Date
)