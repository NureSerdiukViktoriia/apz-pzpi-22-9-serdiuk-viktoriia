package com.example.campingapp.api

import java.util.Date

data class Review(
    val id: Int,
    val user_id: Int,
    val user_email: String,
    val location_id: Int,
    val rating: Int,
    val message: String,
    val date: Date
)